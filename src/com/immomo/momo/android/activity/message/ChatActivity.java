package com.immomo.momo.android.activity.message;

import java.io.File;
import java.io.IOException;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.ChatAdapter;
import com.immomo.momo.android.adapter.CheckListDialogAdapter;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.Message.CONTENT_TYPE;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.socket.IPMSGConst;
import com.immomo.momo.android.socket.OnActiveChatActivityListenner;
import com.immomo.momo.android.tcp.socket.TcpClient;
import com.immomo.momo.android.util.AudioRecorderUtils;
import com.immomo.momo.android.util.DateUtils;
import com.immomo.momo.android.util.FileUtils;
import com.immomo.momo.android.util.PhotoUtils;
import com.immomo.momo.android.util.SessionUtils;
import com.immomo.momo.android.view.ChatListView;
import com.immomo.momo.android.view.EmoteInputView;
import com.immomo.momo.android.view.EmoticonsEditText;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;
import com.immomo.momo.android.view.ScrollLayout;

public class ChatActivity extends BaseMessageActivity implements OnActiveChatActivityListenner {

    private static final String TAG = "SZU_ChatActivity";
    public static String IMAG_PATH;
    public static String VOICE_PATH;
    public static String FILE_PATH;

    // 录音变量
    // private static final int MAX_RECORD_TIME = 30; // 最长录制时间，单位秒，0为无时间限制
    private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
    private static final int RECORD_OFF = 0; // 不在录音
    private static final int RECORD_ON = 1; // 正在录音
    private String RECORD_FILENAME; // 录音文件名

    private TextView mTvRecordDialogTxt;
    private ImageView mIvRecVolume;

    private Dialog mRecordDialog;
    private AudioRecorderUtils mAudioRecorder;
    private Thread mRecordThread;

    private int recordState = 0; // 录音状态
    private float recodeTime = 0.0f; // 录音时长
    private double voiceValue = 0.0; // 录音的音量值
    private boolean isMove = false; // 手指是否移动
    private float downY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        changeActiveChatActivity(this); // 注册到changeActiveChatActivity

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 监听返回键
    @Override
    public void onBackPressed() {
        if (mLayoutMessagePlusBar.isShown()) {
            hidePlusBar();
        }
        else if (mInputView.isShown()) {
            mIbTextDitorKeyBoard.setVisibility(View.GONE);
            mIbTextDitorEmote.setVisibility(View.VISIBLE);
            mInputView.setVisibility(View.GONE);
        }
        else if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            mIbTextDitorKeyBoard.setVisibility(View.VISIBLE);
            mIbTextDitorEmote.setVisibility(View.GONE);
            hideKeyBoard();
        }
        else if (mLayoutScroll.getCurScreen() == 1) {
            mLayoutScroll.snapToScreen(0);
        }
        else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // PhotoUtils.deleteImageFile();
        super.onDestroy();
    }

    @Override
    public void finish() {

        removeActiveChatActivity(); // 移除监听
        // if (mUserDAO != null) // 关闭数据库连接
        // mUserDAO.close();
        // if (mChattingDAO != null)
        // mChattingDAO.close();
        if (null != mDBOperate) {
            mDBOperate.close();
            mDBOperate = null;
        }
        try {
            mAudioRecorder.stop();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        super.finish();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.chat_header);
        mHeaderLayout.init(HeaderStyle.TITLE_CHAT);
        mIvAvatar = (ImageView) findViewById(R.id.header_iv_logo);

        mClvList = (ChatListView) findViewById(R.id.chat_clv_list);
        mLayoutScroll = (ScrollLayout) findViewById(R.id.chat_slayout_scroll);
        mLayoutRounds = (LinearLayout) findViewById(R.id.chat_layout_rounds);
        mInputView = (EmoteInputView) findViewById(R.id.chat_eiv_inputview);

        mIbTextDitorPlus = (ImageButton) findViewById(R.id.chat_textditor_ib_plus);
        mIbTextDitorKeyBoard = (ImageButton) findViewById(R.id.chat_textditor_ib_keyboard);
        mIbTextDitorEmote = (ImageButton) findViewById(R.id.chat_textditor_ib_emote);
        mIvTextDitorAudio = (ImageView) findViewById(R.id.chat_textditor_iv_audio);
        mBtnTextDitorSend = (Button) findViewById(R.id.chat_textditor_btn_send);
        mEetTextDitorEditer = (EmoticonsEditText) findViewById(R.id.chat_textditor_eet_editer);

        mIbAudioDitorPlus = (ImageButton) findViewById(R.id.chat_audioditor_ib_plus);
        mIbAudioDitorKeyBoard = (ImageButton) findViewById(R.id.chat_audioditor_ib_keyboard);
        mIvAudioDitorAudioBtn = (ImageView) findViewById(R.id.chat_audioditor_iv_audiobtn);

        mLayoutFullScreenMask = (LinearLayout) findViewById(R.id.fullscreen_mask);
        mLayoutMessagePlusBar = (LinearLayout) findViewById(R.id.message_plus_layout_bar);
        mLayoutMessagePlusPicture = (LinearLayout) findViewById(R.id.message_plus_layout_picture);
        mLayoutMessagePlusCamera = (LinearLayout) findViewById(R.id.message_plus_layout_camera);
        mLayoutMessagePlusLocation = (LinearLayout) findViewById(R.id.message_plus_layout_location);
        mLayoutMessagePlusGift = (LinearLayout) findViewById(R.id.message_plus_layout_gift);

    }

    @Override
    protected void initEvents() {
        mLayoutScroll.setOnScrollToScreen(this);
        mIbTextDitorPlus.setOnClickListener(this);
        mIbTextDitorEmote.setOnClickListener(this);
        mIbTextDitorKeyBoard.setOnClickListener(this);
        mBtnTextDitorSend.setOnClickListener(this);
        mIvTextDitorAudio.setOnClickListener(this);
        mEetTextDitorEditer.addTextChangedListener(this);
        mEetTextDitorEditer.setOnTouchListener(this);
        mIbAudioDitorPlus.setOnClickListener(this);
        mIbAudioDitorKeyBoard.setOnClickListener(this);

        mIvAudioDitorAudioBtn.setOnTouchListener(this);

        mLayoutFullScreenMask.setOnTouchListener(this);
        mLayoutMessagePlusPicture.setOnClickListener(this);
        mLayoutMessagePlusCamera.setOnClickListener(this);
        mLayoutMessagePlusLocation.setOnClickListener(this);
        mLayoutMessagePlusGift.setOnClickListener(this);

    }

    private void init() {
        mMessagesList = mDBOperate.getScrollMessageOfChattingInfo(0, 10);
        sendFileStates = BaseApplication.sendFileStates;// 存储文件收发状态
        reciveFileStates = BaseApplication.recieveFileStates;
        mID = SessionUtils.getLocalUserID();
        mNickName = SessionUtils.getNickname();
        mIMEI = SessionUtils.getIMEI();
        mPeople = getIntent().getParcelableExtra(NearByPeople.ENTITY_PEOPLE);
        createSavePath();// 创建保存的文件夹目录
        // mSenderID = mUserDAO.getID(mPeople.getIMEI());//获取聊天对象IMEI,旧
        mSenderID = mDBOperate.getIDByIMEI(mPeople.getIMEI());// 获取聊天对象IMEI
        mHeaderLayout.setTitleChat(
                mApplication.getIDfromDrawable(NearByPeople.AVATAR + mPeople.getAvatar()),
                R.drawable.bg_chat_dis_active, mPeople.getNickname(), mPeople.getLogintime(),
                R.drawable.ic_topbar_profile, new OnMiddleImageButtonClickListener(),
                R.drawable.ic_topbar_more, new OnRightImageButtonClickListener());
        mInputView.setEditText(mEetTextDitorEditer);
        initRounds();
        initPopupWindow();
        initSynchronousDialog();

        mAdapter = new ChatAdapter(mApplication, ChatActivity.this, mMessagesList);
        mClvList.setAdapter(mAdapter);
        // refreshAdapter();
    }

    @Override
    public void doAction(int whichScreen) {
        switch (whichScreen) {
            case 0:
                ((ImageView) mLayoutRounds.getChildAt(0)).setImageBitmap(mRoundsSelected);
                ((ImageView) mLayoutRounds.getChildAt(1)).setImageBitmap(mRoundsNormal);
                break;

            case 1:
                ((ImageView) mLayoutRounds.getChildAt(1)).setImageBitmap(mRoundsSelected);
                ((ImageView) mLayoutRounds.getChildAt(0)).setImageBitmap(mRoundsNormal);
                mIbTextDitorKeyBoard.setVisibility(View.GONE);
                mIbTextDitorEmote.setVisibility(View.VISIBLE);
                if (mInputView.isShown()) {
                    mInputView.setVisibility(View.GONE);
                }
                hideKeyBoard();
                break;
        }
    }

    public void refreshAdapter() {
        mAdapter.setData(mMessagesList);
        mAdapter.notifyDataSetChanged();
        setLvSelection(mMessagesList.size());
    }

    public void setLvSelection(int position) {
        mClvList.setSelection(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_textditor_ib_plus:
                if (!mLayoutMessagePlusBar.isShown()) {
                    showPlusBar();
                }
                break;

            case R.id.chat_textditor_ib_emote:
                mIbTextDitorKeyBoard.setVisibility(View.VISIBLE);
                mIbTextDitorEmote.setVisibility(View.GONE);
                mEetTextDitorEditer.requestFocus();
                if (mInputView.isShown()) {
                    hideKeyBoard();
                }
                else {
                    hideKeyBoard();
                    mInputView.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.chat_textditor_ib_keyboard:
                mIbTextDitorKeyBoard.setVisibility(View.GONE);
                mIbTextDitorEmote.setVisibility(View.VISIBLE);
                showKeyBoard();
                break;

            case R.id.chat_textditor_btn_send:
                String content = mEetTextDitorEditer.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    mEetTextDitorEditer.setText(null);
                    sendMessage(content, CONTENT_TYPE.TEXT);
                    refreshAdapter();
                }
                break;

            case R.id.chat_textditor_iv_audio:
                mLayoutScroll.snapToScreen(1);
                break;

            case R.id.chat_audioditor_ib_plus:
                if (!mLayoutMessagePlusBar.isShown()) {
                    showPlusBar();
                }
                break;

            case R.id.chat_audioditor_ib_keyboard:
                mLayoutScroll.snapToScreen(0);
                break;

            case R.id.message_plus_layout_picture:
                // Intent intent=new Intent();
                // intent.setAction(ImageMessageItem.IMAGE_FINISH_UPDATE_ATCTION);
                // // intent.putExtra(message.getMsgContent(), 100);
                //
                // sendBroadcast(intent);
                PhotoUtils.selectPhoto(ChatActivity.this);
                hidePlusBar();
                break;

            case R.id.message_plus_layout_camera:
                mCameraImagePath = PhotoUtils.takePicture(ChatActivity.this);
                hidePlusBar();
                break;

            // case R.id.message_plus_layout_location:
            // mMessages.add(new Message("nearby_people_other", System
            // .currentTimeMillis(), "0.12km", null, CONTENT_TYPE.MAP,
            // MESSAGE_TYPE.SEND));
            // mAdapter.notifyDataSetChanged();
            // mClvList.setSelection(mMessages.size());
            // hidePlusBar();
            // break;

            case R.id.message_plus_layout_gift:
                hidePlusBar();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "ACTION:" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下按钮
                Log.i(TAG, "ACTION DOWN");

                if (recordState == RECORD_OFF) {
                    if (v.getId() == R.id.chat_textditor_eet_editer) {
                        mIbTextDitorKeyBoard.setVisibility(View.GONE);
                        mIbTextDitorEmote.setVisibility(View.VISIBLE);
                        showKeyBoard();
                    }

                    if (v.getId() == R.id.fullscreen_mask) {
                        hidePlusBar();
                    }

                    if (v.getId() == R.id.chat_audioditor_iv_audiobtn) {
                        Log.i(TAG, "start Record");
                        downY = event.getY();

                        mAudioRecorder = new AudioRecorderUtils();

                        RECORD_FILENAME = System.currentTimeMillis()
                                + com.immomo.momo.android.util.TextUtils.getRandomNumStr(3);
                        mAudioRecorder.setVoicePath(VOICE_PATH, RECORD_FILENAME);
                        recordState = RECORD_ON;
                        try {
                            mAudioRecorder.start();
                            recordTimethread();
                            showVoiceDialog(0);
                        }
                        catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: // 滑动手指
                float moveY = event.getY();
                Log.i(TAG, "ACTION MOVE");
                if (moveY - downY < -50) {
                    isMove = true;
                    showVoiceDialog(1);
                }
                else if (moveY - downY < -20) {
                    isMove = false;
                    showVoiceDialog(0);
                }
                break;

            case MotionEvent.ACTION_UP: // 松开手指
                Log.i(TAG, "ACTION UP");
                if (recordState == RECORD_ON) {
                    recordState = RECORD_OFF;
                    if (mRecordDialog.isShowing()) {
                        mRecordDialog.dismiss();
                    }
                    try {
                        mRecordThread.interrupt();
                        mAudioRecorder.stop();
                        voiceValue = 0.0;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!isMove) {
                        if (recodeTime < MIN_RECORD_TIME) {
                            showWarnToast("时间太短  录音失败");
                        }
                        else {
                            // mTvRecordTxt.setText("录音时间：" + ((int)
                            // recodeTime));
                            // mTvRecordPath.setText("文件路径：" + getAmrPath());
                        }
                    }
                    isMove = false;

                    sendMessage(mAudioRecorder.getVoicePath(), CONTENT_TYPE.VOICE);

                }
                break;

        }

        return true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            mIvTextDitorAudio.setVisibility(View.VISIBLE);
            mBtnTextDitorSend.setVisibility(View.GONE);
        }
        else {
            mIvTextDitorAudio.setVisibility(View.GONE);
            mBtnTextDitorSend.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onVoiceModeClick() {
        String[] modes = getResources().getStringArray(R.array.chat_audio_type);
        mDialog = new SimpleListDialog(this);
        mDialog.setTitle("语音收听方式");
        mDialog.setTitleLineVisibility(View.GONE);
        mDialog.setAdapter(new CheckListDialogAdapter(mCheckId, this, modes));
        mDialog.setOnSimpleListItemClickListener(new OnVoiceModeDialogItemClickListener());
        mDialog.show();
    }

    @Override
    public void onCreateClick() {

    }

    @Override
    public void onSynchronousClick() {
        mSynchronousDialog.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoUtils.INTENT_REQUEST_CODE_ALBUM:
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (data.getData() == null) {
                        return;
                    }
                    if (!FileUtils.isSdcardExist()) {
                        showCustomToast("SD卡不可用,请检查");
                        return;
                    }
                    Uri uri = data.getData();
                    String[] proj = { MediaStore.Images.Media.DATA };
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                            String path = cursor.getString(column_index);
                            mCameraImagePath = path;
                            Bitmap bitmap = PhotoUtils.getBitmapFromFile(path);
                            if (PhotoUtils.bitmapIsLarge(bitmap)) {
                                PhotoUtils.cropPhoto(this, this, path);
                            }
                            else {
                                if (path != null) {
                                    sendMessage(path, CONTENT_TYPE.IMAGE);
                                    refreshAdapter();
                                    setLvSelection(mMessagesList.size());
                                }
                            }
                        }
                    }
                }
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (mCameraImagePath != null) {
                        mCameraImagePath = PhotoUtils.savePhotoToSDCard(PhotoUtils
                                .CompressionPhoto(mScreenWidth, mCameraImagePath, 2));
                        PhotoUtils.fliterPhoto(this, this, mCameraImagePath);
                    }
                }
                // mCameraImagePath = null;
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    mCameraImagePath = path;
                    if (path != null) {
                        sendMessage(path, CONTENT_TYPE.IMAGE);
                        refreshAdapter();
                        setLvSelection(mMessagesList.size());
                    }
                }
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_FLITER:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    if (path != null) {
                        sendMessage(path, CONTENT_TYPE.IMAGE);
                        refreshAdapter();
                        setLvSelection(mMessagesList.size());
                    }
                }
                break;
        }
    }

    // 程序在开始运行的时候,调用以下函数创建存储图片语音文件目录
    private void createSavePath() {
        if (null != BaseApplication.IMAG_PATH) {
            String imei = mPeople.getIMEI();
            IMAG_PATH = BaseApplication.IMAG_PATH + File.separator + imei;
            VOICE_PATH = BaseApplication.VOICE_PATH + File.separator + imei;
            FILE_PATH = BaseApplication.FILE_PATH + File.separator + imei;
            if (!FileUtils.isFileExists(IMAG_PATH))
                FileUtils.createDirFile(IMAG_PATH);// 如果目录不存在则创建目录
            if (!FileUtils.isFileExists(VOICE_PATH))
                FileUtils.createDirFile(VOICE_PATH);
            if (!FileUtils.isFileExists(FILE_PATH))
                FileUtils.createDirFile(FILE_PATH);
        }
    }

    @Override
    public boolean isThisActivityMsg(Message msg) {
        Log.i("SZU_ChatActivity", "进入isThisActivityMsg(), msg:" + (msg != null));
        // TODO 待完成
        if (mPeople.getIMEI().equals(msg.getSenderIMEI())) { // 若消息与本activity有关，则接收
            mMessagesList.add(msg); // 将此消息添加到显示聊天list中
            mDBOperate.addChattingInfo(mSenderID, mID, msg.getSendTime(), msg.getMsgContent(),
                    msg.getContentType());
            sendEmptyMessage(IPMSGConst.IPMSG_SENDMSG);
            return true;
        }
        return false;
    }

    @Override
    public void processMessage(android.os.Message msg) {
        Log.i("SZU_ChatActivity", "进入processMessage()");
        // TODO 待完成
        switch (msg.what) {
            case IPMSGConst.IPMSG_SENDMSG:
                refreshAdapter(); // 刷新ListView
                // super.processMessage(null); // 调用父类方法进行响铃提醒
                break;

            case IPMSGConst.IPMSG_RELEASEFILES: { // 拒绝接受文件,停止发送文件线程
            }
                break;

            case IPMSGConst.FILESENDSUCCESS: { // 文件发送成功
            }

                break;
            case IPMSGConst.IPMSG_RECIEVEIMAGEDATA: {// 图片开始发送
                Log.d(TAG, "接收方确认文件请求,发送文件为" + mCameraImagePath);
                tcpClient = TcpClient.getInstance(ChatActivity.this);
                tcpClient.startSend();
                tcpClient.sendFile(mCameraImagePath, mPeople.getIpaddress(),
                        Message.CONTENT_TYPE.IMAGE);
            }
                break;
            case IPMSGConst.IPMSG_GETIMAGESUCCESS: { // 图片发送成功
                Log.d("SZU_ChatActivity", "接收成功");
                refreshAdapter(); // 刷新ListView
            }
                break;
        } // end of switch
    }

    public void sendMessage(String content, CONTENT_TYPE type) {
        String nowtime = DateUtils.getNowtime();
        Message msg = new Message(mIMEI, nowtime, content, type);
        mMessagesList.add(msg);
        switch (type) {
            case TEXT:
                mUDPSocketThread.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mPeople.getIpaddress(), msg);
                break;

            case IMAGE:                
                Message imageMsg = msg.clone();
                imageMsg.setMsgContent(FileUtils.getNameByPath(msg.getMsgContent()));
                mUDPSocketThread.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mPeople.getIpaddress(), imageMsg);
                break;
                
            case VOICE:
                Message voiceMsg = msg.clone();
                voiceMsg.setMsgContent(FileUtils.getNameByPath(msg.getMsgContent()));
                mUDPSocketThread.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mPeople.getIpaddress(), voiceMsg);
                break;
                
            case FILE:
                break;       
           
        }      
       
        mDBOperate.addChattingInfo(mID, mSenderID, nowtime, content, type);// 新增方法
        mApplication.addLastMsgCache(mPeople.getIMEI(), msg); // 更新消息缓存
    }

    // 录音计时线程
    void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    // 录音时显示Dialog
    void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(ChatActivity.this, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
            case 1:
                Log.i(TAG, "showVoiceDialog | flag:1");
                mIvRecVolume.setImageResource(R.drawable.record_cancel);
                mTvRecordDialogTxt.setText("松开手指可取消录音");
                break;

            default:
                Log.i(TAG, "showVoiceDialog | default");
                mIvRecVolume.setImageResource(R.drawable.record_animate_01);
                mTvRecordDialogTxt.setText("向上滑动可取消录音");
                break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }

    // 录音线程
    private Runnable recordThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                // 限制录音时长
                // if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
                // imgHandle.sendEmptyMessage(0);
                // } else
                {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        // 获取音量，更新dialog
                        if (!isMove) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            recordHandler.sendEmptyMessage(1);
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            setDialogImage();
        }
    };

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
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
    void showWarnToast(String toastText) {
        Toast toast = new Toast(ChatActivity.this);
        LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // 定义一个ImageView
        ImageView imageView = new ImageView(ChatActivity.this);
        imageView.setImageResource(R.drawable.voice_to_short); // 图标

        TextView mTv = new TextView(ChatActivity.this);
        mTv.setText(toastText);
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);// 字体颜色

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);// 内容居中
        linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
        toast.show();
    }

}
