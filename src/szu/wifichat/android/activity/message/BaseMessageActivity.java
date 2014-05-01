package szu.wifichat.android.activity.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import szu.wifichat.android.BaseActivity;
import szu.wifichat.android.BaseDialog;
import szu.wifichat.android.activity.OtherProfileActivity;
import szu.wifichat.android.adapter.ChatAdapter;
import szu.wifichat.android.dialog.SimpleListDialog;
import szu.wifichat.android.dialog.SimpleListDialog.onSimpleListItemClickListener;
import szu.wifichat.android.entity.Message;
import szu.wifichat.android.entity.NearByPeople;
import szu.wifichat.android.file.explore.FileState;
import szu.wifichat.android.popupwindow.ChatPopupWindow;
import szu.wifichat.android.popupwindow.ChatPopupWindow.OnChatPopupItemClickListener;
import szu.wifichat.android.socket.UDPSocketThread;
import szu.wifichat.android.sql.SqlDBOperate;
import szu.wifichat.android.tcp.socket.TcpClient;
import szu.wifichat.android.tcp.socket.TcpService;
import szu.wifichat.android.util.AudioRecorderUtils;
import szu.wifichat.android.util.ImageUtils;
import szu.wifichat.android.view.ChatListView;
import szu.wifichat.android.view.EmoteInputView;
import szu.wifichat.android.view.EmoticonsEditText;
import szu.wifichat.android.view.HeaderLayout;
import szu.wifichat.android.view.ScrollLayout;
import szu.wifichat.android.view.HeaderLayout.onMiddleImageButtonClickListener;
import szu.wifichat.android.view.HeaderLayout.onRightImageButtonClickListener;
import szu.wifichat.android.view.ScrollLayout.OnScrollToScreenListener;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import szu.wifichat.android.R;

public abstract class BaseMessageActivity extends BaseActivity implements
        OnScrollToScreenListener, OnClickListener, OnTouchListener,
        TextWatcher, OnChatPopupItemClickListener {

    protected HeaderLayout mHeaderLayout;
    protected ChatListView mClvList;
    protected ScrollLayout mLayoutScroll;
    protected LinearLayout mLayoutRounds;
    protected EmoteInputView mInputView;

    protected ImageButton mIbTextDitorPlus;
    protected ImageButton mIbTextDitorKeyBoard;
    protected ImageButton mIbTextDitorEmote;
    protected EmoticonsEditText mEetTextDitorEditer;
    protected Button mBtnTextDitorSend;
    protected ImageView mIvTextDitorAudio;
    protected ImageView mIvAvatar;

    protected ImageButton mIbAudioDitorPlus;
    protected ImageButton mIbAudioDitorKeyBoard;
    protected ImageView mIvAudioDitorAudioBtn;

    protected LinearLayout mLayoutFullScreenMask;
    protected LinearLayout mLayoutMessagePlusBar;
    protected LinearLayout mLayoutMessagePlusPicture;
    protected LinearLayout mLayoutMessagePlusCamera;
    protected LinearLayout mLayoutMessagePlusLocation;
    protected LinearLayout mLayoutMessagePlusGift;

    protected List<Message> mMessagesList = new ArrayList<Message>(); // 消息列表
    protected ChatAdapter mAdapter;
    protected NearByPeople mPeople; // 聊天的对象
    // protected UserDAO mUserDAO; // 数据库用户信息操作实例
    // protected ChattingDAO mChattingDAO; // 数据库聊天信息操作实例
    protected SqlDBOperate mDBOperate;// 新增数据库类可以操作用户数据库和聊天信息数据库

    protected Bitmap mRoundsSelected;
    protected Bitmap mRoundsNormal;

    private ChatPopupWindow mChatPopupWindow;
    private int mWidth;
    private int mHeaderHeight;

    protected SimpleListDialog mDialog;
    protected int mCheckId = 0;

    protected BaseDialog mSynchronousDialog;

    protected String mCameraImagePath;

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
    protected Thread mRecordThread;

    protected int recordState = 0; // 录音状态
    protected float recodeTime = 0.0f; // 录音时长
    protected double voiceValue = 0.0; // 录音的音量值
    protected boolean isMove = false; // 手指是否移动
    protected float downY;

    // 文件传输变量
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
        initViews();
        initEvents();
        mUDPSocketThread = UDPSocketThread.getInstance(mApplication, this); // 获取对象
        // mUserDAO = new UserDAO(this); // 实例化数据库用户操作类
        // mChattingDAO = new ChattingDAO(this); // 实例化数据库聊天信息操作类
        mDBOperate = new SqlDBOperate(this); // 新增数据库操作类，可以操作用户表和聊天信息表

    }

    protected class OnMiddleImageButtonClickListener implements
            onMiddleImageButtonClickListener {

        @Override
        public void onClick() {
            Intent intent = new Intent(BaseMessageActivity.this,
                    OtherProfileActivity.class);
            intent.putExtra(NearByPeople.ENTITY_PEOPLE, mPeople);
            startActivity(intent);
            finish();
        }
    }

    protected class OnRightImageButtonClickListener implements
            onRightImageButtonClickListener {

        @Override
        public void onClick() {
            mChatPopupWindow.showAtLocation(mHeaderLayout, Gravity.RIGHT
                    | Gravity.TOP, -10, mHeaderHeight + 10);
        }
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
        mLayoutMessagePlusLocation.setEnabled(true);
        mLayoutMessagePlusGift.setEnabled(true);
        Animation animation = AnimationUtils.loadAnimation(
                BaseMessageActivity.this, R.anim.controller_enter);
        mLayoutMessagePlusBar.setAnimation(animation);
        mLayoutMessagePlusBar.setVisibility(View.VISIBLE);
        mLayoutFullScreenMask.setVisibility(View.VISIBLE);
    }

    protected void hidePlusBar() {
        mLayoutFullScreenMask.setEnabled(false);
        mLayoutMessagePlusBar.setEnabled(false);
        mLayoutMessagePlusPicture.setEnabled(false);
        mLayoutMessagePlusCamera.setEnabled(false);
        mLayoutMessagePlusLocation.setEnabled(false);
        mLayoutMessagePlusGift.setEnabled(false);
        mLayoutFullScreenMask.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(
                BaseMessageActivity.this, R.anim.controller_exit);
        animation.setInterpolator(AnimationUtils.loadInterpolator(
                BaseMessageActivity.this,
                android.R.anim.anticipate_interpolator));
        mLayoutMessagePlusBar.setAnimation(animation);
        mLayoutMessagePlusBar.setVisibility(View.GONE);
    }

    protected void initRounds() {
        mRoundsSelected = ImageUtils.getRoundBitmap(BaseMessageActivity.this,
                getResources().getColor(R.color.msg_short_line_selected));
        mRoundsNormal = ImageUtils.getRoundBitmap(BaseMessageActivity.this,
                getResources().getColor(R.color.msg_short_line_normal));
        int mChildCount = mLayoutScroll.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            ImageView imageView = (ImageView) LayoutInflater.from(
                    BaseMessageActivity.this).inflate(
                    R.layout.include_message_shortline, null);
            imageView.setImageBitmap(mRoundsNormal);
            mLayoutRounds.addView(imageView);
        }
        ((ImageView) mLayoutRounds.getChildAt(0)).setImageBitmap(mRoundsSelected);
    }

    protected void initPopupWindow() {
        mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                130, getResources().getDisplayMetrics());
        mHeaderHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 48,
                getResources().getDisplayMetrics());
        mChatPopupWindow = new ChatPopupWindow(this, mWidth,
                LayoutParams.WRAP_CONTENT);
        mChatPopupWindow.setOnChatPopupItemClickListener(this);
    }

    protected void initSynchronousDialog() {
        mSynchronousDialog = BaseDialog.getDialog(BaseMessageActivity.this,
                R.string.dialog_tips, "成为陌陌会员即可同步好友聊天记录", "查看详情",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }, "取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        mSynchronousDialog.setButton1Background(R.drawable.btn_default_popsubmit);
    }

    protected class OnVoiceModeDialogItemClickListener implements
            onSimpleListItemClickListener {

        @Override
        public void onItemClick(int position) {
            mCheckId = position;
        }
    }

}
