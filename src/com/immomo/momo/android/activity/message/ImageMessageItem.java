package com.immomo.momo.android.activity.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.ImageBrowserActivity;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.util.PhotoUtils;
import com.immomo.momo.android.view.HandyTextView;

public class ImageMessageItem extends MessageItem implements
		OnLongClickListener, OnClickListener {

	private ImageView mIvImage;
	private LinearLayout mLayoutLoading;
	private ImageView mIvLoading;
	private HandyTextView mHtvLoadingText;

	private AnimationDrawable mAnimation;
	private int mProgress;
	private Bitmap mBitmap;

	public ImageMessageItem(Message msg, Context context) {
		super(msg, context);
	}

	@Override
	protected void onInitViews() {
		View view = mInflater.inflate(R.layout.message_image, null);
		mLayoutMessageContainer.addView(view);
		mIvImage = (ImageView) view.findViewById(R.id.message_iv_msgimage);
		mLayoutLoading = (LinearLayout) view
				.findViewById(R.id.message_layout_loading);
		mIvLoading = (ImageView) view.findViewById(R.id.message_iv_loading);
		mHtvLoadingText = (HandyTextView) view
				.findViewById(R.id.message_htv_loading_text);
		mIvImage.setOnClickListener(this);
		mIvImage.setOnLongClickListener(this);
	}

	@Override
	protected void onFillMessage() {
		mBitmap = PhotoUtils.getBitmapFromFile(mMsg.getContent());
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mContext, ImageBrowserActivity.class);
		intent.putExtra(ImageBrowserActivity.IMAGE_TYPE,
				ImageBrowserActivity.TYPE_PHOTO);
		intent.putExtra("path", mMsg.getContent());
		mContext.startActivity(intent);
		((ChatActivity) mContext).overridePendingTransition(R.anim.zoom_enter,
				0);
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				startLoadingAnimation();
				break;

			case 1:
				updateLoadingProgress();
				break;

			case 2:
				stopLoadingAnimation();
				break;
			}
		}

	};

	private void startLoadingAnimation() {
		mAnimation = new AnimationDrawable();
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_01), 300);
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_02), 300);
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_03), 300);
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_04), 300);
		mAnimation.setOneShot(false);
		mIvImage.setVisibility(View.GONE);
		mLayoutLoading.setVisibility(View.VISIBLE);
		mIvLoading.setVisibility(View.VISIBLE);
		mHtvLoadingText.setVisibility(View.VISIBLE);
		mIvLoading.setImageDrawable(mAnimation);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mAnimation.start();
			}
		});
		mHandler.sendEmptyMessage(1);
	}

	private void stopLoadingAnimation() {
		if (mAnimation != null) {
			mAnimation.stop();
			mAnimation = null;
		}
		mLayoutLoading.setVisibility(View.GONE);
		mHtvLoadingText.setVisibility(View.GONE);
		mIvImage.setVisibility(View.VISIBLE);
		if (mBitmap != null) {
			mIvImage.setImageBitmap(mBitmap);
		}
	}

	private void updateLoadingProgress() {
		if (mProgress < 100) {
			mProgress++;
			mHtvLoadingText.setText(mProgress + "%");
			mHandler.sendEmptyMessageDelayed(1, 100);
		} else {
			mProgress = 0;
			mHandler.sendEmptyMessage(2);
		}
	}

	@SuppressWarnings("deprecation")
	private Drawable getDrawable(int id) {
		return new BitmapDrawable(BitmapFactory.decodeResource(
				mContext.getResources(), id));
	}
}
