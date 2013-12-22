package com.immomo.momo.android.activity;

import java.util.regex.Pattern;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.SimpleListDialogAdapter;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.immomo.momo.android.util.TextUtils;
import com.immomo.momo.android.view.HandyTextView;

public class FindPwdPhoneActivity extends BaseActivity implements
		OnClickListener, onSimpleListItemClickListener {

	private HandyTextView mHtvAreaCode;
	private EditText mEtPhone;
	private Button mBtnBack;
	private Button mBtnNext;

	private static final String DEFAULT_PHONE = "+8612345678901";
	private String mAreaCode = "+86";
	private String mPhone;
	private SimpleListDialog mSimpleListDialog;
	private String[] mCountryCodes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwdphone);
		initViews();
		initEvents();
	}

	@Override
	protected void initViews() {
		mHtvAreaCode = (HandyTextView) findViewById(R.id.findpwdphone_htv_areacode);
		mEtPhone = (EditText) findViewById(R.id.findpwdphone_et_phone);
		mBtnBack = (Button) findViewById(R.id.findpwdphone_btn_back);
		mBtnNext = (Button) findViewById(R.id.findpwdphone_btn_next);
	}

	@Override
	protected void initEvents() {
		mHtvAreaCode.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
	}

	private boolean matchPhone(String text) {
		if (Pattern.compile("(\\d{11})|(\\+\\d{3,})").matcher(text).matches()) {
			return true;
		}
		return false;
	}

	private boolean isNull(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}

	private boolean validatePhone() {
		mPhone = null;
		if (isNull(mEtPhone)) {
			showCustomToast("请输入电话号码");
			mEtPhone.requestFocus();
			return false;
		}
		String phone = mHtvAreaCode.getText().toString().trim()
				+ mEtPhone.getText().toString().trim();
		if (matchPhone(phone)) {
			if (phone.length() < 3) {
				showCustomToast("电话格式不正确");
				mEtPhone.requestFocus();
				return false;
			}
			if (Pattern.compile("(\\d{3,})|(\\+\\d{3,})").matcher(phone)
					.matches()) {
				mPhone = phone;
				return true;
			}
		}
		showCustomToast("电话格式不正确");
		mEtPhone.requestFocus();
		return false;
	}

	private void next() {
		if (validatePhone()) {
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
						if (DEFAULT_PHONE.equals(mPhone)) {
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
						startActivity(ResetPwdPhoneActivity.class);
						finish();
					} else {
						showCustomToast("您的手机尚未注册陌陌账号");
					}
				}

			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.findpwdphone_htv_areacode:
			mCountryCodes = getResources()
					.getStringArray(R.array.onlinestate_type);
			mSimpleListDialog = new SimpleListDialog(FindPwdPhoneActivity.this);
			mSimpleListDialog.setTitle("选择国家区号");
			mSimpleListDialog.setTitleLineVisibility(View.GONE);
			mSimpleListDialog.setAdapter(new SimpleListDialogAdapter(
					FindPwdPhoneActivity.this, mCountryCodes));
			mSimpleListDialog
					.setOnSimpleListItemClickListener(FindPwdPhoneActivity.this);
			mSimpleListDialog.show();
			break;

		case R.id.findpwdphone_btn_back:
			finish();
			break;

		case R.id.findpwdphone_btn_next:
			next();
			break;
		}
	}

	@Override
	public void onItemClick(int position) {
		String text = TextUtils.getCountryCodeBracketsInfo(
				mCountryCodes[position], mAreaCode);
		mHtvAreaCode.setText(text);
	}

}
