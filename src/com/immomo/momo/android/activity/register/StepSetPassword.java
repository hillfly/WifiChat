package com.immomo.momo.android.activity.register;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.immomo.momo.android.R;

public class StepSetPassword extends RegisterStep implements TextWatcher {

	private EditText mEtPwd;
	private EditText mEtRePwd;

	private boolean mIsChange = true;

	public StepSetPassword(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
	}

	@Override
	public void initViews() {
		mEtPwd = (EditText) findViewById(R.id.reg_setpwd_et_pwd);
		mEtRePwd = (EditText) findViewById(R.id.reg_setpwd_et_repwd);
	}

	@Override
	public void initEvents() {
		mEtPwd.addTextChangedListener(this);
		mEtRePwd.addTextChangedListener(this);
	}

	@Override
	public void doNext() {
		mIsChange = false;
		mOnNextActionListener.next();
	}

	@Override
	public boolean validate() {
		String pwd = null;
		String rePwd = null;
		if (isNull(mEtPwd)) {
			showCustomToast("请输入密码");
			mEtPwd.requestFocus();
			return false;
		} else {
			pwd = mEtPwd.getText().toString().trim();
			if (pwd.length() < 6) {
				showCustomToast("密码不能小于6位");
				mEtPwd.requestFocus();
				return false;
			}
		}
		if (isNull(mEtRePwd)) {
			showCustomToast("请重复输入一次密码");
			mEtRePwd.requestFocus();
			return false;
		} else {
			rePwd = mEtRePwd.getText().toString().trim();
			if (!pwd.equals(rePwd)) {
				showCustomToast("两次输入的密码不一致");
				mEtRePwd.requestFocus();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isChange() {
		return mIsChange;
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mIsChange = true;
	}

}
