package hillfly.wifichat.activity.message;

import hillfly.wifichat.BaseActivity;
import hillfly.wifichat.BaseApplication;
import hillfly.wifichat.adapter.ChatAdapter;
import hillfly.wifichat.bean.Message;
import hillfly.wifichat.bean.Users;
import hillfly.wifichat.bean.Message.CONTENT_TYPE;
import hillfly.wifichat.file.FileState;
import hillfly.wifichat.socket.tcp.TcpClient;
import hillfly.wifichat.socket.tcp.TcpService;
import hillfly.wifichat.socket.udp.IPMSGConst;
import hillfly.wifichat.socket.udp.UDPMessageListener;
import hillfly.wifichat.sql.SqlDBOperate;
import hillfly.wifichat.util.AudioRecorderUtils;
import hillfly.wifichat.util.DateUtils;
import hillfly.wifichat.util.FileUtils;
import hillfly.wifichat.util.ImageUtils;
import hillfly.wifichat.view.EmoteInputView;
import hillfly.wifichat.view.EmoticonsEditText;
import hillfly.wifichat.view.MultiListView;
import hillfly.wifichat.view.ScrollLayout;
import hillfly.wifichat.view.ScrollLayout.OnScrollToScreenListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import hillfly.wifichat.R;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseMessageActivity extends BaseActivity implements OnScrollToScreenListener,
        OnClickListener, OnLongClickListener, OnTouchListener, TextWatcher {

    protected static final int FILE_SELECT_CODE = 4;
    protected static String IMAG_PATH;
    protected static String THUMBNAIL_PATH;
    protected static String VOICE_PATH;
    protected static String FILE_PATH;

    protected MultiListView mClvList;
    protected ScrollLayout mLayoutScroll;
    protected LinearLayout mLayoutRounds;
    protected EmoteInputView mInputView;

    protected ImageButton mIbTextDitorPlus;
    protected ImageButton mIbTextDitorKeyBoard;
    protected ImageButton mIbTextDitorEmote;
    protected EmoticonsEditText mEetTextDitorEditer;
    protected Button mBtnTextDitorSend;
    protected ImageView mIvTextDitorAudio;

    protected ImageButton mIbAudioDitorPlus;
    protected ImageButton mIbAudioDitorKeyBoard;
    protected ImageView mIvAudioDitorAudioBtn;

    // 更多 +
    protected LinearLayout mLayoutFullScreenMask;
    protected LinearLayout mLayoutMessagePlusBar;
    protected LinearLayout mLayoutMessagePlusPicture;
    protected LinearLayout mLayoutMessagePlusCamera;
    protected LinearLayout mLayoutMessagePlusFile;

    protected Bitmap mRoundsSelected;
    protected Bitmap mRoundsNormal;

    protected List<Message> mMessagesList; // 消息列表
    protected ChatAdapter mAdapter;
    protected Users mChatUser; // 聊天的对象
    protected String mCameraImagePath;
    protected SqlDBOperate mDBOperate;

    // 录音变量
    protected String mVoicePath;
    // private static final int MAX_RECORD_TIME = 30; // 最长录制时间，单位秒，0为无时间限制
    protected static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
    protected static final int RECORD_OFF = 0; // 不在录音
    protected static final int RECORD_ON = 1; // 正在录音
    protected String RECORD_FILENAME; // 录音文件名

    protected TextView mTvRecordDialogTxt;
    protected ImageView mIvRecVolume;

    protected Dialog mRecordDialog;
    protected AudioRecorderUtils mAudioRecorder;
    protected MediaPlayer mMediaPlayer;
    protected Thread mRecordThread;

    protected boolean isPlay = false; // 播放状态
    protected int recordState = 0; // 录音状态
    protected float recodeTime = 0.0f; // 录音时长
    protected double voiceValue = 0.0; // 录音的音量值
    protected boolean isMove = false; // 手指是否移动
    protected float downY;

    // 文件传输变量
    protected String sendFilePath; // 文件路径
    protected TcpClient tcpClient = null;
    protected TcpService tcpService = null;
    protected HashMap<String, FileState> sendFileStates;
    protected HashMap<String, FileState> reciveFileStates;

    protected String mNickName;
    protected String mIMEI;
    protected int mID;
    protected int mSenderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mUDPListener = UDPMessageListener.getInstance(getApplicationContext());
        initViews();
        initEvents();
    }

    protected void showKeyBoard() {
        if (mInputView.isShown()) {
            mInputView.setVisibility(View.GONE);
        }
        mEetTextDitorEditer.requestFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(
                mEetTextDitorEditer, 0);
    }

    protected void hideKeyBoard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                BaseMessageActivity.this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void showPlusBar() {
        mLayoutFullScreenMask.setEnabled(true);
        mLayoutMessagePlusBar.setEnabled(true);
        mLayoutMessagePlusPicture.setEnabled(true);
        mLayoutMessagePlusCamera.setEnabled(true);
        mLayoutMessagePlusFile.setEnabled(true);
        Animation animation = AnimationUtils.loadAnimation(BaseMessageActivity.this,
                R.anim.controller_enter);
        mLayoutMessagePlusBar.setAnimation(animation);
        mLayoutMessagePlusBar.setVisibility(View.VISIBLE);
        mLayoutFullScreenMask.setVisibility(View.VISIBLE);
    }

    protected void hidePlusBar() {
        mLayoutFullScreenMask.setEnabled(false);
        mLayoutMessagePlusBar.setEnabled(false);
        mLayoutMessagePlusPicture.setEnabled(false);
        mLayoutMessagePlusCamera.setEnabled(false);
        mLayoutMessagePlusFile.setEnabled(false);
        mLayoutFullScreenMask.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(BaseMessageActivity.this,
                R.anim.controller_exit);
        animation.setInterpolator(AnimationUtils.loadInterpolator(BaseMessageActivity.this,
                android.R.anim.anticipate_interpolator));
        mLayoutMessagePlusBar.setAnimation(animation);
        mLayoutMessagePlusBar.setVisibility(View.GONE);
    }

    protected void initRounds() {
        mRoundsSelected = ImageUtils.getRoundBitmap(BaseMessageActivity.this, getResources()
                .getColor(R.color.msg_short_line_selected));
        mRoundsNormal = ImageUtils.getRoundBitmap(BaseMessageActivity.this, getResources()
                .getColor(R.color.msg_short_line_normal));
        int mChildCount = mLayoutScroll.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            ImageView imageView = (ImageView) LayoutInflater.from(BaseMessageActivity.this)
                    .inflate(R.layout.include_message_shortline, null);
            imageView.setImageBitmap(mRoundsNormal);
            mLayoutRounds.addView(imageView);
        }
        ((ImageView) mLayoutRounds.getChildAt(0)).setImageBitmap(mRoundsSelected);
    }

    public void refreshAdapter() {
        mAdapter.setData(mMessagesList);
        mAdapter.notifyDataSetChanged();
        setLvSelection(mMessagesList.size());
    }

    public void setLvSelection(int position) {
        mClvList.setSelection(position);
    }

    /*
     * createSavePath 存储目录初始化
     */
    protected void initfolder() {
        if (null != BaseApplication.IMAG_PATH) {
            String imei = mChatUser.getIMEI();
            IMAG_PATH = BaseApplication.IMAG_PATH + File.separator + imei;
            THUMBNAIL_PATH = BaseApplication.THUMBNAIL_PATH + File.separator + imei;
            VOICE_PATH = BaseApplication.VOICE_PATH + File.separator + imei;
            FILE_PATH = BaseApplication.FILE_PATH + File.separator + imei;
            if (!FileUtils.isFileExists(IMAG_PATH))
                FileUtils.createDirFile(IMAG_PATH);
            if (!FileUtils.isFileExists(THUMBNAIL_PATH))
                FileUtils.createDirFile(THUMBNAIL_PATH);
            if (!FileUtils.isFileExists(VOICE_PATH))
                FileUtils.createDirFile(VOICE_PATH);
            if (!FileUtils.isFileExists(FILE_PATH))
                FileUtils.createDirFile(FILE_PATH);
        }
    }

    public void sendMessage(String content, CONTENT_TYPE type) {
        String nowtime = DateUtils.getNowtime();
        Message msg = new Message(mIMEI, nowtime, content, type);
        mMessagesList.add(msg);
        mUDPListener.addLastMsgCache(mChatUser.getIMEI(), msg); // 更新消息缓存

        switch (type) {
            case TEXT:
                UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mChatUser.getIpaddress(),
                        msg);
                break;

            case IMAGE:
                UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_REQUEST_IMAGE_DATA,
                        mChatUser.getIpaddress());
                break;

            case VOICE:
                UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_REQUEST_VOICE_DATA,
                        mChatUser.getIpaddress());
                break;

            case FILE:
                Message fileMsg = msg.clone();
                fileMsg.setMsgContent(FileUtils.getNameByPath(msg.getMsgContent()));
                UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mChatUser.getIpaddress(),
                        fileMsg);
                break;

        }

        mDBOperate.addChattingInfo(mID, mSenderID, nowtime, content, type);
    }

    // 录音时显示Dialog
    protected void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(BaseMessageActivity.this, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
            case 1:
                mIvRecVolume.setImageResource(R.drawable.record_cancel);
                mTvRecordDialogTxt.setText(getString(R.string.chat_dialog_record_cancel_up));
                break;

            default:
                mIvRecVolume.setImageResource(R.drawable.record_animate_01);
                mTvRecordDialogTxt.setText(getString(R.string.chat_dialog_record_cancel_move));
                break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }

    // 录音Dialog图片随声音大小切换
    protected void setDialogImage() {
        if (voiceValue < 800.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_01);
        }
        else if (voiceValue > 800.0 && voiceValue < 1200.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_02);
        }
        else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_03);
        }
        else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_04);
        }
        else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_05);
        }
        else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_06);
        }
        else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_07);
        }
        else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_08);
        }
        else if (voiceValue > 4000.0 && voiceValue < 5000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_09);
        }
        else if (voiceValue > 5000.0 && voiceValue < 6000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_10);
        }
        else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_11);
        }
        else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_12);
        }
        else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_13);
        }
        else if (voiceValue > 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_14);
        }
    }

    // 录音时间太短时Toast显示
    protected void showWarnToast(int toastTextId) {
        showWarnToast(getString(toastTextId));
    }

    protected void showWarnToast(String toastText) {
        Toast toast = new Toast(BaseMessageActivity.this);
        LinearLayout linearLayout = new LinearLayout(BaseMessageActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        ImageView imageView = new ImageView(BaseMessageActivity.this);
        imageView.setImageResource(R.drawable.voice_to_short);

        TextView mTv = new TextView(BaseMessageActivity.this);
        mTv.setText(toastText);
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setBackgroundResource(R.drawable.record_bg);

        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /** 调用文件选择软件来选择文件 **/
    protected void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.text_file_send_select)),
                    FILE_SELECT_CODE);
        }
        catch (ActivityNotFoundException ex) {
            Toast.makeText(BaseMessageActivity.this, R.string.toast_file_manager_unavailable,
                    Toast.LENGTH_SHORT).show();
        }
    }

}
