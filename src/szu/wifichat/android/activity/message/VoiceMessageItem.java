package szu.wifichat.android.activity.message;

import java.io.IOException;

import szu.wifichat.android.entity.Message;
import szu.wifichat.android.util.ImageUtils;
import szu.wifichat.android.util.SessionUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import szu.wifichat.android.R;

public class VoiceMessageItem extends MessageItem implements OnClickListener {

    public static final String VOICE_UPDATE_ACTION = "szu.wifichat.android.message.voice.update";
    public static final String VOICE_FINISH_UPDATE_ATCTION = "szu.wifichat.android.message.voice.finishupdate";
    private static final String TAG = "SZU_VoiceMessageItem";
    private LinearLayout mLyVoiceMessage;
    private ImageView mIvImage;
    private ImageView mIvLoading;

    private Message mMessage;

    private AnimationDrawable mAnimation;
    private String filePath;

    private VoiceItemBroadcastReceiver imageItemBroadcastReceiver;
    private IntentFilter intentFilter;

    private MediaPlayer mMediaPlayer;
    private boolean isPlay = false; // 播放状态

    public VoiceMessageItem(Message msg, Context context) {
        super(msg, context);
        mMessage = msg;
        this.filePath = new String(msg.getMsgContent());
        regBroadcastRecv();
    }

    @Override
    protected void onInitViews() {
        View view = mInflater.inflate(R.layout.message_voice, null);
        mLayoutMessageContainer.addView(view);
        mIvImage = (ImageView) view.findViewById(R.id.voice_message_iv_msgimage);
        mLyVoiceMessage = (LinearLayout) view.findViewById(R.id.voice_message);

        if (SessionUtils.isItself(mMessage.getSenderIMEI())) {
            mIvImage.setImageResource(R.drawable.voicerecord_right);
        } else {
            mIvImage.setImageResource(R.drawable.voicerecord_left);
        }

        mIvLoading = (ImageView) view.findViewById(R.id.voice_message_iv_loading);

        mLyVoiceMessage.setOnClickListener(this);
    }

    @Override
    protected void onFillMessage() {
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        // 播放录音

        if (!isPlay) {
            Log.i(TAG, "onClick");
            mMediaPlayer = new MediaPlayer();
            try {
                Log.i(TAG, filePath);
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mIvImage.setImageResource(R.drawable.voicerecord_stop);
                isPlay = true;
                mMediaPlayer.start();
                Log.i(TAG, "Mediaplayer statrt()");
                // 设置播放结束时监听
                mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (isPlay) {
                            mIvImage.setImageResource(R.drawable.voicerecord_right);
                            isPlay = false;
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            Log.i(TAG, "Mediaplayer release()");
                        }
                    }
                });
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.i(TAG, "else");
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                isPlay = false;
            } else {
                isPlay = false;
                mMediaPlayer.release();
            }
            mIvImage.setImageResource(R.drawable.voicerecord_right);
        }

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
                mContext.getResources(), R.drawable.ic_loading_msgplus_01), 300);
        mAnimation.addFrame(ImageUtils.getDrawableFromId(
                mContext.getResources(), R.drawable.ic_loading_msgplus_02), 300);
        mAnimation.addFrame(ImageUtils.getDrawableFromId(
                mContext.getResources(), R.drawable.ic_loading_msgplus_03), 300);
        mAnimation.addFrame(ImageUtils.getDrawableFromId(
                mContext.getResources(), R.drawable.ic_loading_msgplus_04), 300);
        mAnimation.setOneShot(false);
        mIvImage.setVisibility(View.GONE);
        mIvLoading.setVisibility(View.VISIBLE);

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
        mIvLoading.setVisibility(View.GONE);
        mIvImage.setVisibility(View.VISIBLE);
    }

    // 设置进度
    public void setProgress(int progress) {
        if (progress == 100)
            mHandler.sendEmptyMessage(2);
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
                int i = intent.getIntExtra(filePath, -1);
                Log.d(TAG, "收到录音更新广播" + "进度大小" + i);
                if (i < 100 && i > 0)
                    setProgress(i);
            } else if (intent.getAction().equals(VOICE_FINISH_UPDATE_ATCTION)) {
                Log.d(TAG, "录音更新完毕");
                int i = intent.getIntExtra(filePath, -1);
                if (i == 100)
                    mHandler.sendEmptyMessage(2);

            }

        }

    }
}
