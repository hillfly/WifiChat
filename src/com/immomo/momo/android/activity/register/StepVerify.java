package com.immomo.momo.android.activity.register;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.immomo.momo.android.BaseDialog;
import com.immomo.momo.android.R;
import com.immomo.momo.android.util.TextUtils;
import com.immomo.momo.android.view.HandyTextView;

public class StepVerify extends RegisterStep implements OnClickListener,
		TextWatcher {

	private HandyTextView mHtvPhoneNumber;
	private EditText mEtVerifyCode;
	private Button mBtnResend;
	private HandyTextView mHtvNoCode;

	private static final String PROMPT = "验证码已经发送到* ";
	private static final String DEFAULT_VALIDATE_CODE = "852369";

	private boolean mIsChange = true;
	private String mVerifyCode;

	private int mReSendTime = 60;
	private BaseDialog mBaseDialog;

	public StepVerify(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
		handler.sendEmptyMessage(0);
	}

	@Override
	public void initViews() {
		mHtvPhoneNumber = (HandyTextView) findViewById(R.id.reg_verify_htv_phonenumber);
		mHtvPhoneNumber.setText(PROMPT + getPhoneNumber());
		mEtVerifyCode = (EditText) findViewById(R.id.reg_verify_et_verifycode);
		mBtnResend = (Button) findViewById(R.id.reg_verify_btn_resend);
		mBtnResend.setEnabled(false);
		mBtnResend.setText("重发(60)");
		mHtvNoCode = (HandyTextView) findViewById(R.id.reg_verify_htv_nocode);
		TextUtils.addUnderlineText(mContext, mHtvNoCode, 0, mHtvNoCode
				.getText().toString().length());
//		TextUtils.addHyperlinks(mHtvNoCode, 0, mHtvNoCode
//				.getText().toString().length(), new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						System.out.println("123");
//					}
//				});
	}

	@Override
	public void initEvents() {
		mBtnResend.setOnClickListener(this);
		mHtvNoCode.setOnClickListener(this);
		mEtVerifyCode.addTextChangedListener(this);
	}

	@Override
	public void doNext() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog("正在验证,请稍后...");
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
					if (DEFAULT_VALIDATE_CODE.equals(mVerifyCode)) {
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
					mBaseDialog = BaseDialog.getDialog(mContext, "提示", "验证码错误",
							"确认", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mEtVerifyCode.requestFocus();
									dialog.dismiss();
								}

							});
					mBaseDialog.show();
				}
			}

		});
	}

	@Override
	public boolean validate() {
		if (isNull(mEtVerifyCode)) {
			showCustomToast("请输入验证码");
			mEtVerifyCode.requestFocus();
			return false;
		}
		mVerifyCode = mEtVerifyCode.getText().toString().trim();
		return true;
	}

	@Override
	public boolean isChange() {
		return mIsChange;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_verify_btn_resend:
			handler.sendEmptyMessage(0);
			break;

		case R.id.reg_verify_htv_nocode:
			showCustomToast("抱歉,暂时不支持此操作");
			break;
		}
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

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mReSendTime > 1) {
				mReSendTime--;
				mBtnResend.setEnabled(false);
				mBtnResend.setText("重发(" + mReSendTime + ")");
				handler.sendEmptyMessageDelayed(0, 1000);
			} else {
				mReSendTime = 60;
				mBtnResend.setEnabled(true);
				mBtnResend.setText("重    发");
			}
		}
	};

}
