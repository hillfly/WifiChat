package com.immomo.momo.android.activity.message;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.Message.MESSAGE_TYPE;
import com.immomo.momo.android.util.DateUtils;
import com.immomo.momo.android.view.HandyTextView;

public abstract class MessageItem {

	protected Context mContext;
	protected View mRootView;

	/**
	 * TimeStampContainer
	 */
	private RelativeLayout mLayoutTimeStampContainer;
	private HandyTextView mHtvTimeStampTime;
	private HandyTextView mHtvTimeStampDistance;

	/**
	 * LeftContainer
	 */
	private RelativeLayout mLayoutLeftContainer;
	private LinearLayout mLayoutStatus;
	private HandyTextView mHtvStatus;

	/**
	 * MessageContainer
	 */
	protected LinearLayout mLayoutMessageContainer;

	/**
	 * RightContainer
	 */
	private LinearLayout mLayoutRightContainer;
	private ImageView mIvPhotoView;

	protected LayoutInflater mInflater;
	protected Message mMsg;

	protected int mBackground;

	public MessageItem(Message msg, Context context) {
		mMsg = msg;
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public static MessageItem getInstance(Message msg, Context context) {
		MessageItem messageItem = null;
		switch (msg.getContentType()) {
		case TEXT:
			messageItem = new TextMessageItem(msg, context);
			break;

		case IMAGE:
			messageItem = new ImageMessageItem(msg, context);
			break;

		case MAP:
			messageItem = new MapMessageItem(msg, context);
			break;

		case VOICE:
			messageItem = new VoiceMessageItem(msg, context);
			break;

		}
		messageItem.init(msg.getMessageType());
		return messageItem;
	}

	private void init(MESSAGE_TYPE messageType) {
		switch (messageType) {
		case RECEIVER:
			mBackground = R.drawable.bg_message_box_receive;
			break;

		case SEND:
			mRootView = mInflater.inflate(R.layout.message_group_send_template,
					null);
			mBackground = R.drawable.bg_message_box_send;
			break;
		}
		if (mRootView != null) {
			initViews(mRootView);
		}
	}

	protected void initViews(View view) {
		mLayoutTimeStampContainer = (RelativeLayout) view
				.findViewById(R.id.message_layout_timecontainer);
		mHtvTimeStampTime = (HandyTextView) view
				.findViewById(R.id.message_timestamp_htv_time);
		mHtvTimeStampDistance = (HandyTextView) view
				.findViewById(R.id.message_timestamp_htv_distance);

		mLayoutLeftContainer = (RelativeLayout) view
				.findViewById(R.id.message_layout_leftcontainer);
		mLayoutStatus = (LinearLayout) view
				.findViewById(R.id.message_layout_status);
		mHtvStatus = (HandyTextView) view.findViewById(R.id.message_htv_status);

		mLayoutMessageContainer = (LinearLayout) view
				.findViewById(R.id.message_layout_messagecontainer);
		mLayoutMessageContainer.setBackgroundResource(mBackground);

		mLayoutRightContainer = (LinearLayout) view
				.findViewById(R.id.message_layout_rightcontainer);
		mIvPhotoView = (ImageView) view.findViewById(R.id.message_iv_userphoto);
		onInitViews();
	}

	public void fillContent() {
		fillTimeStamp();
		fillStatus();
		fillMessage();
		fillPhotoView();
	}

	protected void fillMessage() {
		onFillMessage();
	}

	protected void fillTimeStamp() {
		mLayoutTimeStampContainer.setVisibility(View.VISIBLE);
		if (mMsg.getTime() != 0) {
			mHtvTimeStampTime.setText(DateUtils.formatDate(mContext,
					mMsg.getTime()));
		}
		if (mMsg.getDistance() != null) {
			mHtvTimeStampDistance.setText(mMsg.getDistance());
		} else {
			mHtvTimeStampDistance.setText("未知");
		}
	}

	protected void fillStatus() {
		mLayoutLeftContainer.setVisibility(View.VISIBLE);
		mLayoutStatus
				.setBackgroundResource(R.drawable.bg_message_status_sended);
		mHtvStatus.setText("送达");
	}

	protected void fillPhotoView() {
		mLayoutRightContainer.setVisibility(View.VISIBLE);
		mIvPhotoView.setImageBitmap(((BaseApplication) ((Activity) mContext)
				.getApplication()).getAvatar(mMsg.getAvatar()));
	}

	protected void refreshAdapter() {
		((ChatActivity) mContext).refreshAdapter();
	}

	public View getRootView() {
		return mRootView;
	}

	protected abstract void onInitViews();

	protected abstract void onFillMessage();
}
