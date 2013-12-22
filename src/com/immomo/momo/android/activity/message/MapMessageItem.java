package com.immomo.momo.android.activity.message;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.util.ImageMapUtils;
import com.immomo.momo.android.util.ImageMapUtils.OnMapDownloadStateListener;
import com.immomo.momo.android.util.PhotoUtils;

public class MapMessageItem extends MessageItem implements OnClickListener,
		OnMapDownloadStateListener {
	private ImageView mIvImage;
	private LinearLayout mLayoutLoading;
	private ImageView mIvLoading;
	private AnimationDrawable mAnimation;
	private BaseApplication mApplication;
	private ImageMapUtils mImageMapUtils;

	public MapMessageItem(Message msg, Context context) {
		super(msg, context);
		mApplication = ((BaseApplication) ((Activity) mContext)
				.getApplication());
		mImageMapUtils = ImageMapUtils.create(context);
	}

	@Override
	protected void onInitViews() {
		View view = mInflater.inflate(R.layout.message_map, null);
		mLayoutMessageContainer.addView(view);
		mIvImage = (ImageView) view.findViewById(R.id.message_iv_mapimage);
		mLayoutLoading = (LinearLayout) view
				.findViewById(R.id.message_layout_maploading);
		mIvLoading = (ImageView) view.findViewById(R.id.message_iv_maploading);
		mIvImage.setOnClickListener(this);
		mLayoutMessageContainer.setOnClickListener(this);
	}

	@Override
	protected void onFillMessage() {
		downloadMap();
	}

	@Override
	public void onClick(View v) {
		System.out.println("dianji");
	}

	private void startLoadingAnimation() {
		mAnimation = new AnimationDrawable();
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_01), 300);
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_02), 300);
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_03), 300);
		mAnimation.addFrame(getDrawable(R.drawable.ic_loading_msgplus_04), 300);
		mAnimation.setOneShot(false);
		mLayoutLoading.setVisibility(View.VISIBLE);
		mIvLoading.setVisibility(View.VISIBLE);
		mIvLoading.setImageDrawable(mAnimation);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mAnimation.start();
			}
		});
	}

	private void stopLoadingAnimation(String path) {
		if (mAnimation != null) {
			mAnimation.stop();
			mAnimation = null;
		}
		mLayoutLoading.setVisibility(View.GONE);
		mIvImage.setVisibility(View.VISIBLE);
		if (path != null) {
			mIvImage.setImageBitmap(PhotoUtils.getBitmapFromFile(path));
		}
	}

	private void downloadMap() {
		mImageMapUtils.setOnMapDownloadStateListener(this);
		mImageMapUtils.display(mApplication.mLongitude, mApplication.mLatitude);
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
				String path=msg.obj.toString();
				stopLoadingAnimation(path);
				break;
			}
		}

	};

	@SuppressWarnings("deprecation")
	private Drawable getDrawable(int id) {
		return new BitmapDrawable(BitmapFactory.decodeResource(
				mContext.getResources(), id));
	}

	@Override
	public void onStart() {
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public void onFinish(String path) {
		android.os.Message msg = mHandler.obtainMessage();
		msg.what = 1;
		msg.obj = path;
		msg.sendToTarget();
	}
}
