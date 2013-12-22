package com.immomo.momo.android.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.immomo.momo.android.BasePopupWindow;
import com.immomo.momo.android.R;
import com.immomo.momo.android.view.HandyTextView;

public class ChatPopupWindow extends BasePopupWindow implements OnClickListener {
	private HandyTextView mHtvVoiceMode;
	private HandyTextView mHtvCreate;
	private HandyTextView mHtvSynchronous;

	private onChatPopupItemClickListener mOnChatPopupItemClickListener;

	public ChatPopupWindow(Context context, int width, int height) {
		super(LayoutInflater.from(context).inflate(
				R.layout.include_dialog_chat, null), width, height);
		setAnimationStyle(R.style.Popup_Animation_Alpha);
	}

	@Override
	public void initViews() {
		mHtvVoiceMode = (HandyTextView) findViewById(R.id.dialog_chat_htv_voicemode);
		mHtvCreate = (HandyTextView) findViewById(R.id.dialog_chat_htv_create);
		mHtvSynchronous = (HandyTextView) findViewById(R.id.dialog_chat_htv_synchronous);
	}

	@Override
	public void initEvents() {
		mHtvVoiceMode.setOnClickListener(this);
		mHtvCreate.setOnClickListener(this);
		mHtvSynchronous.setOnClickListener(this);
	}

	@Override
	public void init() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_chat_htv_voicemode:
			if (mOnChatPopupItemClickListener != null) {
				mOnChatPopupItemClickListener.onVoiceModeClick();
			}
			break;

		case R.id.dialog_chat_htv_create:
			if (mOnChatPopupItemClickListener != null) {
				mOnChatPopupItemClickListener.onCreateClick();
			}
			break;

		case R.id.dialog_chat_htv_synchronous:
			if (mOnChatPopupItemClickListener != null) {
				mOnChatPopupItemClickListener.onSynchronousClick();
			}
			break;
		}
		dismiss();
	}

	public void setOnChatPopupItemClickListener(
			onChatPopupItemClickListener listener) {
		mOnChatPopupItemClickListener = listener;
	}

	public interface onChatPopupItemClickListener {
		void onVoiceModeClick();

		void onCreateClick();

		void onSynchronousClick();
	}
}
