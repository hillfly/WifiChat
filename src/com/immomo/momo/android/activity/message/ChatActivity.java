package com.immomo.momo.android.activity.message;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.ChatAdapter;
import com.immomo.momo.android.adapter.CheckListDialogAdapter;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.Message.CONTENT_TYPE;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.socket.IPMSGConst;
import com.immomo.momo.android.socket.OnActiveChatActivityListenner;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        changeActiveChatActivity(this); // 注册到ReceiveMsgListener容器
    }

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
        PhotoUtils.deleteImageFile();
        super.onDestroy();
    }

    @Override
    public void finish() {
        removeActiveChatActivity(); // 移除监听
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

        mLayoutFullScreenMask.setOnTouchListener(this);
        mLayoutMessagePlusPicture.setOnClickListener(this);
        mLayoutMessagePlusCamera.setOnClickListener(this);
        mLayoutMessagePlusLocation.setOnClickListener(this);
        mLayoutMessagePlusGift.setOnClickListener(this);

    }

    private void init() {
        mNickName = SessionUtils.getNickname();
        mIMEI = SessionUtils.getIMEI();
        mPeople = getIntent().getParcelableExtra(NearByPeople.ENTITY_PEOPLE);
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
        if (v.getId() == R.id.chat_textditor_eet_editer) {
            mIbTextDitorKeyBoard.setVisibility(View.GONE);
            mIbTextDitorEmote.setVisibility(View.VISIBLE);
            showKeyBoard();
        }
        if (v.getId() == R.id.fullscreen_mask) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hidePlusBar();
            }
        }
        return false;
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
                mCameraImagePath = null;
                break;

            case PhotoUtils.INTENT_REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
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

    @Override
    public boolean isThisActivityMsg(Message msg) {
        Log.i("SZU_ChatActivity", "进入isThisActivityMsg(), msg:" + (msg != null));
        // TODO 待完成
        if (mPeople.getIMEI().equals(msg.getSenderIMEI())) { // 若消息与本activity有关，则接收
            mMessagesList.add(msg); // 将此消息添加到显示聊天list中
            mApplication.addLastMsgCache(msg.getSenderIMEI(), msg.getMsgContent()); // 更新消息缓存
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
                Log.i("SZU_ChatActivity", "refreshAdapter()");
                // super.processMessage(null); // 调用父类方法进行响铃提醒
                break;

            case IPMSGConst.IPMSG_RELEASEFILES: { // 拒绝接受文件,停止发送文件线程
            }
                break;

            case IPMSGConst.FILESENDSUCCESS: { // 文件发送成功
            }
                break;

        } // end of switch
    }

    public void sendMessage(String content, CONTENT_TYPE type) {
        Message msg = new Message(mIMEI, DateUtils.getNowtime(), content, type);
        mMessagesList.add(msg);
        mUDPSocketThread.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mPeople.getIpaddress(), msg);
        mApplication.addLastMsgCache(mPeople.getIMEI(), content); // 更新消息缓存
    }
}
