package com.immomo.momo.android.activity.register;

import java.util.regex.Pattern;

import com.immomo.momo.android.BaseApplication;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;

public abstract class RegisterStep {
	protected RegisterActivity mActivity;
	protected Context mContext;
	private View mContentRootView;
	protected onNextActionListener mOnNextActionListener;

	public RegisterStep(RegisterActivity activity, View contentRootView) {
		mActivity = activity;
		mContext = (Context) mActivity;
		mContentRootView = contentRootView;
		initViews();
		initEvents();
	}

	public abstract void initViews();

	public abstract void initEvents();

	public abstract boolean validate();

	public abstract boolean isChange();

	public View findViewById(int id) {
		return mContentRootView.findViewById(id);
	}

	public void doPrevious() {

	}

	public void doNext() {

	}

	public void nextAnimation() {

	}

	public void preAnimation() {

	}

	protected boolean isNull(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}

	protected boolean matchPhone(String text) {
		if (Pattern.compile("(\\d{11})|(\\+\\d{3,})").matcher(text).matches()) {
			return true;
		}
		return false;
	}

	protected boolean matchEmail(String text) {
		if (Pattern.compile("\\w[\\w.-]*@[\\w.]+\\.\\w+").matcher(text)
				.matches()) {
			return true;
		}
		return false;
	}

	protected String getPhoneNumber() {
		return mActivity.getPhoneNumber();
	}

	protected void showCustomToast(String text) {
		mActivity.showCustomToast(text);
	}

	protected void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		mActivity.putAsyncTask(asyncTask);
	}

	protected void showLoadingDialog(String text) {
		mActivity.showLoadingDialog(text);
	}

	protected void dismissLoadingDialog() {
		mActivity.dismissLoadingDialog();
	}

	protected int getScreenWidth() {
		return mActivity.getScreenWidth();
	}

	protected BaseApplication getBaseApplication() {
		return mActivity.getBaseApplication();
	}

	public void setOnNextActionListener(onNextActionListener listener) {
		mOnNextActionListener = listener;
	}

	public interface onNextActionListener {
		void next();
	}
}
