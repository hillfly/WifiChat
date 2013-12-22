package com.immomo.momo.android.activity.register;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.SimpleListDialogAdapter;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.dialog.WebDialog;
import com.immomo.momo.android.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.immomo.momo.android.dialog.WebDialog.OnWebDialogErrorListener;
import com.immomo.momo.android.jni.JniManager;
import com.immomo.momo.android.util.TextUtils;
import com.immomo.momo.android.view.HandyTextView;

public class StepPhone extends RegisterStep implements OnClickListener,
		TextWatcher, onSimpleListItemClickListener, OnWebDialogErrorListener {

	private HandyTextView mHtvAreaCode;
	private EditText mEtPhone;
	private HandyTextView mHtvNotice;
	private HandyTextView mHtvNote;

	private String mAreaCode = "+86";
	private SimpleListDialog mSimpleListDialog;
	private String[] mCountryCodes;

	private static final String DEFAULT_PHONE = "+8612345678901";
	private String mPhone;
	private boolean mIsChange = true;

	private WebDialog mWebDialog;

	public StepPhone(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
	}

	public String getPhoneNumber() {
		return "(" + mAreaCode + ")" + mPhone;
	}

	@Override
	public void initViews() {
		mHtvAreaCode = (HandyTextView) findViewById(R.id.reg_phone_htv_areacode);
		mEtPhone = (EditText) findViewById(R.id.reg_phone_et_phone);
		mHtvNotice = (HandyTextView) findViewById(R.id.reg_phone_htv_notice);
		mHtvNote = (HandyTextView) findViewById(R.id.reg_phone_htv_note);
		TextUtils.addHyperlinks(mHtvNote, 8, 15, this);
	}

	@Override
	public void initEvents() {
		mHtvAreaCode.setOnClickListener(this);
		mEtPhone.addTextChangedListener(this);
	}

	@Override
	public void doNext() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog("正在验证手机号,请稍后...");
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
					if (DEFAULT_PHONE.equals(mAreaCode+mPhone)) {
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
					mIsChange = false;
					mOnNextActionListener.next();
				} else {
					showCustomToast("手机号码不可用或已被注册");
				}
			}
		});
	}

	@Override
	public boolean validate() {
		mPhone = null;
		if (isNull(mEtPhone)) {
			showCustomToast("请填写手机号码");
			mEtPhone.requestFocus();
			return false;
		}
		String phone = mEtPhone.getText().toString().trim();
		if (matchPhone(mAreaCode + phone)) {
			mPhone = phone;
			return true;
		}
		showCustomToast("手机号码格式不正确");
		mEtPhone.requestFocus();
		return false;
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
		if (s.toString().length() > 0) {
			mHtvNotice.setVisibility(View.VISIBLE);
			char[] chars = s.toString().toCharArray();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < chars.length; i++) {
				if (i % 4 == 2) {
					buffer.append(chars[i] + "  ");
				} else {
					buffer.append(chars[i]);
				}
			}
			mHtvNotice.setText(buffer.toString());
		} else {
			mHtvNotice.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_phone_htv_areacode:
			mCountryCodes = mContext.getResources().getStringArray(
					R.array.onlinestate_type);
			mSimpleListDialog = new SimpleListDialog(mContext);
			mSimpleListDialog.setTitle("选择国家区号");
			mSimpleListDialog.setTitleLineVisibility(View.GONE);
			mSimpleListDialog.setAdapter(new SimpleListDialogAdapter(mContext,
					mCountryCodes));
			mSimpleListDialog.setOnSimpleListItemClickListener(StepPhone.this);
			mSimpleListDialog.show();
			break;

		default:
			mWebDialog = new WebDialog(mContext);
			mWebDialog.init("用户协议", "确认",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mWebDialog.dismiss();
						}
					});
			mWebDialog.setOnWebDialogErrorListener(StepPhone.this);
			mWebDialog
					.loadUrl(JniManager.getInstance().getAgreementDialogUrl());
			mWebDialog.show();
			break;
		}
	}

	@Override
	public void onItemClick(int position) {
		String text = TextUtils.getCountryCodeBracketsInfo(
				mCountryCodes[position], mAreaCode);
		mAreaCode = text;
		mHtvAreaCode.setText(text);
	}

	@Override
	public void urlError() {
		showCustomToast("网页地址错误,请检查");
	}

	@Override
	public void networkError() {
		showCustomToast("当前网络不可用,请检查");
	}
}
