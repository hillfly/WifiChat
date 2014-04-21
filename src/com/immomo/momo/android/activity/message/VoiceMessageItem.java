package com.immomo.momo.android.activity.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.view.HandyTextView;

public class VoiceMessageItem extends MessageItem implements OnLongClickListener, OnClickListener {

    public static final String VOICE_UPDATE_ACTION = "com.immomo.momo.android.message.voice.update";
    public static final String VOICE_FINISH_UPDATE_ATCTION = "com.immomo.momo.android.message.voice.finishupdate";
    private static final String TAG = "SZU_VoiceMessageItem";
    private ImageView mIvImage;
    private LinearLayout mLayoutLoading;
    private ImageView mIvLoading;
    private HandyTextView mHtvLoadingText;

    private AnimationDrawable mAnimation;
    private int mProgress = 0;

    private String filePath;

    private VoiceItemBroadcastReceiver imageItemBroadcastReceiver;
    private IntentFilter intentFilter;

    public VoiceMessageItem(Message msg, Context context) {
        super(msg, context);
        this.filePath = new String(msg.getMsgContent());
        regBroadcastRecv();
    }

    @Override
    protected void onInitViews() {
        View view = mInflater.inflate(R.layout.message_voice, null);
        mLayoutMessageContainer.addView(view);
        mIvImage = (ImageView) view.findViewById(R.id.voice_message_iv_msgimage);
        mLayoutLoading = (LinearLayout) view.findViewById(R.id.voice_message_layout_loading);
        mIvLoading = (ImageView) view.findViewById(R.id.voice_message_iv_loading);
        mHtvLoadingText = (HandyTextView) view.findViewById(R.id.voice_message_htv_loading_text);
        mIvImage.setOnClickListener(this);
        mIvImage.setOnLongClickListener(this);
    }

    @Override
    protected void onFillMessage() {
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        // 播放录音
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
            if (mAnimation.isRunning()) {
                mAnimation.stop();
                mAnimation = null;
            }
        }
        mLayoutLoading.setVisibility(View.GONE);
        mHtvLoadingText.setVisibility(View.GONE);
        mIvImage.setVisibility(View.VISIBLE);
    }

    // 设置进度
    public void setProgress(int progress) {
        if (progress != 100) {
            mProgress = progress;
            mHandler.sendEmptyMessage(1);
        }
        else
            mHandler.sendEmptyMessage(2);

    }

    private void updateLoadingProgress() {

        mHtvLoadingText.setText(mProgress + "%");
    }

    @SuppressWarnings("deprecation")
    private Drawable getDrawable(int id) {
        return new BitmapDrawable(BitmapFactory.decodeResource(mContext.getResources(), id));
    }

    // 广播接收器注册
    private void regBroadcastRecv() {
        imageItemBroadcastReceiver = new VoiceItemBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(VOICE_UPDATE_ACTION);
        intentFilter.addAction(VOICE_FINISH_UPDATE_ATCTION);
        mContext.registerReceiver(imageItemBroadcastReceiver, intentFilter);
    }

    private class VoiceItemBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(VOICE_UPDATE_ACTION)) {

                Log.d(TAG, "录音路径:" + filePath);
                int i = intent.getExtras().getInt(filePath);
                Log.d(TAG, "收到录音更新广播" + "进度大小" + i);
                if (i < 100 && i > 0)
                    setProgress(i);
            }
            else if (intent.getAction().equals(VOICE_FINISH_UPDATE_ATCTION)) {
                Log.d(TAG, "录音更新完毕");
                int i = intent.getIntExtra(filePath, -1);
                if (i == 100)
                    mHandler.sendEmptyMessage(2);

            }

        }

    }
}
