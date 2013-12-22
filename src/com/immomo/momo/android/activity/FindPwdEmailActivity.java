package com.immomo.momo.android.activity;

import java.util.regex.Pattern;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.BaseDialog;
import com.immomo.momo.android.R;

public class FindPwdEmailActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener {

	private EditText mEtEmail;
	private Button mBtnBack;
	private Button mBtnSubmit;

	private String mEmail;
	private static final String DEFAULT_EMAIL = "86930007@qq.com";

	private BaseDialog mDialog;
	private boolean mIsSuccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwdemail);
		initViews();
		initEvents();
	}

	@Override
	protected void initViews() {
		mEtEmail = (EditText) findViewById(R.id.findpwdemail_et_email);
		mBtnBack = (Button) findViewById(R.id.findpwdemail_btn_back);
		mBtnSubmit = (Button) findViewById(R.id.findpwdemail_btn_submit);
	}

	@Override
	protected void initEvents() {
		mBtnBack.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.findpwdemail_btn_back:
			finish();
			break;

		case R.id.findpwdemail_btn_submit:
			submit();
			break;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (mIsSuccess) {
			finish();
		} else {
			mEtEmail.requestFocus();
		}

	}

	private boolean isNull(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}

	private boolean matchEmail(String text) {
		if (Pattern.compile("\\w[\\w.-]*@[\\w.]+\\.\\w+").matcher(text)
				.matches()) {
			return true;
		}
		return false;
	}

	private boolean validateEmail() {
		mEmail = null;
		if (isNull(mEtEmail)) {
			showCustomToast("请输入邮箱账号");
			mEtEmail.requestFocus();
			return false;
		}
		String email = mEtEmail.getText().toString().trim();
		if (matchEmail(email)) {
			mEmail = email;
			return true;
		}
		showCustomToast("邮箱地址格式不正确");
		mEtEmail.requestFocus();
		return false;
	}

	private void submit() {
		if (!validateEmail()) {
			return;
		}
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
					if (DEFAULT_EMAIL.equals(mEmail)) {
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
				mIsSuccess = result;
				if (result) {
					mDialog = BaseDialog.getDialog(FindPwdEmailActivity.this,
							"提示", "重置密码的链接稍后将发送到您的邮箱,请注意查收", "确认",
							FindPwdEmailActivity.this);
					mDialog.show();
				} else {
					mDialog = BaseDialog
							.getDialog(FindPwdEmailActivity.this, "提示",
									"您填写的邮箱没有注册", "确认",
									FindPwdEmailActivity.this);
					mDialog.show();
				}
			}

		});
	}
}
