package szu.wifichat.android.activity.message;


import szu.wifichat.android.entity.Message;
import szu.wifichat.android.util.FileUtils;
import szu.wifichat.android.util.ImageUtils;
import szu.wifichat.android.view.HandyTextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import szu.wifichat.android.R;

public class FileMessageItem extends MessageItem implements
        OnLongClickListener, OnClickListener {

    public static final String FILE_UPDATE_ACTION = "szu.wifichat.android.message.file.update";
    public static final String FILE_FINISH_UPDATE_ATCTION = "szu.wifichat.android.message.file.finishupdate";
    private static final String TAG = "SZU_ImageMessageItem";
    private ImageView mIvImage;
    private LinearLayout mLayoutLoading;
    private ImageView mIvLoading;
    private HandyTextView mHtvLoadingText;

    private AnimationDrawable mAnimation;
    private int mProgress = 0;
    private Bitmap mBitmap;

    private String filePath;

    private ImageItemBroadcastReceiver imageItemBroadcastReceiver;
    private IntentFilter intentFilter;

    public FileMessageItem(Message msg, Context context) {
        super(msg, context);
        this.filePath = new String(msg.getMsgContent());
        regBroadcastRecv();
    }

    @Override
    protected void onInitViews() {
        View view = mInflater.inflate(R.layout.message_file, null);
        mLayoutMessageContainer.addView(view);
        mIvImage = (ImageView) view.findViewById(R.id.message_file_iv_msgimage);
        mLayoutLoading = (LinearLayout) view.findViewById(R.id.message_file_layout_loading);
        mIvLoading = (ImageView) view.findViewById(R.id.message_file_iv_loading);
        mHtvLoadingText = (HandyTextView) view.findViewById(R.id.message_file_htv_loading_text);
        mIvImage.setOnClickListener(this);
        mIvImage.setOnLongClickListener(this);
    }

    @Override
    protected void onFillMessage() {
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
    	Intent intent = new Intent();
    	intent.setType("*/*");
		intent.setData(Uri.parse("file://"+FileUtils.getPathByFullPath(filePath)));
		mContext.startActivity(intent);
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
        mAnimation.addFrame(ImageUtils.getDrawableFromId(
                mContext.getResources(), R.drawable.ic_loading_msgplus_01), 100);
        mAnimation.addFrame(ImageUtils.getDrawableFromId(
                mContext.getResources(), R.drawable.ic_loading_msgplus_02), 100);
        mAnimation.addFrame(ImageUtils.getDrawableFromId(
                mContext.getResources(), R.drawable.ic_loading_msgplus_03), 100);
        mAnimation.addFrame(ImageUtils.getDrawableFromId(
                mContext.getResources(), R.drawable.ic_loading_msgplus_04), 100);
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
            if (mAnimation.isRunning()) {
                mAnimation.stop();
                mAnimation = null;
            }
        }
//        mBitmap = ImageUtils.createBitmap(mMsg.getMsgContent(), 100, 100);
        mLayoutLoading.setVisibility(View.GONE);
        mHtvLoadingText.setVisibility(View.GONE);
        mIvImage.setVisibility(View.VISIBLE);
        if (mBitmap != null) {
//            mIvImage.setImageBitmap(mBitmap);
        	mIvImage.setImageResource(R.drawable.file_icon_default);
        }
    }

    // 设置图像进度
    public void setProgress(int progress) {
        if (progress != 100) {
            mProgress = progress;
            mHandler.sendEmptyMessage(1);
        } else
            mHandler.sendEmptyMessage(2);

    }

    private void updateLoadingProgress() {
        mHtvLoadingText.setText(mProgress + "%");
    }

    // 广播接收器注册
    private void regBroadcastRecv() {
        imageItemBroadcastReceiver = new ImageItemBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(FILE_UPDATE_ACTION);
        intentFilter.addAction(FILE_FINISH_UPDATE_ATCTION);
        mContext.registerReceiver(imageItemBroadcastReceiver, intentFilter);
    }

    private class ImageItemBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(FILE_UPDATE_ACTION)) {

                Log.d(TAG, "文件路径:" + filePath);
                int i = intent.getIntExtra(filePath, -1);
                Log.d(TAG, "收到文件更新广播，" + "进度大小：" + i);
                if (i < 100 && i > 0)
                    setProgress(i);
            } else if (intent.getAction().equals(FILE_FINISH_UPDATE_ATCTION)) {
                Log.d(TAG, "文件更新完毕");
                int i = intent.getIntExtra(filePath, -1);
                if (i == 100)
                    mHandler.sendEmptyMessage(2);
            }

        }

    }
}
