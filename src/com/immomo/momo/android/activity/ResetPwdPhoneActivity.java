package com.immomo.momo.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.util.TextUtils;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;

public class ResetPwdPhoneActivity extends BaseActivity implements
		OnClickListener {

	private HeaderLayout mHeaderLayout;
	private HandyTextView mHtvContent;
	private EditText mEtValidateCode;
	private EditText mEtNewPwd;
	private EditText mEtReNewPwd;
	private Button mBtnBack;
	private Button mBtnSubmit;

	private static final String DEFAULT_VALIDATE_CODE = "852369";
	private String mValidateCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpwdphone);
		initViews();
		initEvents();
	}

	@Override
	protected void initViews() {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.resetpwdphone_header);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle("密码重置", null);
		mHtvContent = (HandyTextView) findViewById(R.id.resetpwdphone_htv_content);
		TextUtils.addHyperlinks(mHtvContent, 23, 35, this);
		mEtValidateCode = (EditText) findViewById(R.id.resetpwdphone_et_validatecode);
		mEtNewPwd = (EditText) findViewById(R.id.resetpwdphone_et_newpwd);
		mEtReNewPwd = (EditText) findViewById(R.id.resetpwdphone_et_renewpwd);
		mBtnBack = (Button) findViewById(R.id.resetpwdphone_btn_back);
		mBtnSubmit = (Button) findViewById(R.id.resetpwdphone_btn_submit);
	}

	@Override
	protected void initEvents() {
		mBtnBack.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
	}

	private boolean isNull(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}

	private boolean validateValidateCode() {
		mValidateCode = null;
		if (isNull(mEtValidateCode)) {
			showCustomToast("请输入验证码");
			mEtValidateCode.requestFocus();
			return false;
		}
		mValidateCode = mEtValidateCode.getText().toString().trim();
		return true;
	}

	private boolean validatePwd() {
		String pwd = null;
		String rePwd = null;
		if (isNull(mEtNewPwd)) {
			showCustomToast("请输入密码");
			mEtNewPwd.requestFocus();
			return false;
		} else {
			pwd = mEtNewPwd.getText().toString().trim();
			if (pwd.length() < 6) {
				showCustomToast("密码不能小于6位");
				mEtNewPwd.requestFocus();
				return false;
			}
		}
		if (isNull(mEtReNewPwd)) {
			showCustomToast("请重复输入一次密码");
			mEtReNewPwd.requestFocus();
			return false;
		} else {
			rePwd = mEtReNewPwd.getText().toString().trim();
			if (!pwd.equals(rePwd)) {
				showCustomToast("两次输入的密码不一致");
				mEtReNewPwd.requestFocus();
				return false;
			}
		}

		return true;
	}

	private void submit() {
		if (validateValidateCode()) {
			if (validatePwd()) {
				putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						showLoadingDialog("请稍后,正在提交...");
					}

					@Override
					protected Boolean doInBackground(Void... params) {
						try {
							Thread.sleep(2000);
							if (DEFAULT_VALIDATE_CODE.equals(mValidateCode)) {
								return true;
							}
						} catch (InterruptedException e) {

						}
						return false;
					}

					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						dismissLoadingDialog();
						if (result) {
							showCustomToast("密码修改成功");
							finish();
						} else {
							showCustomToast("验证码输入错误或已经过期,请检查或重新获取验证码");
							mValidateCode = null;
							mEtValidateCode.setText(null);
							mEtValidateCode.requestFocus();
						}
					}

				});
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.resetpwdphone_btn_back:
			finish();
			break;

		case R.id.resetpwdphone_btn_submit:
			submit();
			break;

		default:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android-dir/mms-sms");
			intent.putExtra("address", "106902291602");
			intent.putExtra("sms_body", "MMCZ");
			startActivity(intent);
			break;
		}
	}

}
