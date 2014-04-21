package com.immomo.momo.android.activity.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.util.SessionUtils;
import com.immomo.momo.android.view.HandyTextView;

public abstract class MessageItem {

    protected static Context mContext;
    protected View mRootView;

    /**
     * TimeStampContainer
     */
    private RelativeLayout mLayoutTimeStampContainer;
    private HandyTextView mHtvTimeStampTime;

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
            case VOICE:
                messageItem = new VoiceMessageItem(msg, context);
                break;
            default:
                break;

        // case VOICE:
        // messageItem = new VoiceMessageItem(msg, context);
        // break;

        // case FILE:
        // break;

        // case MAP:
        // messageItem = new MapMessageItem(msg, context);
        // break;
        }
        messageItem.init(SessionUtils.isItself(msg.getSenderIMEI()));
        return messageItem;
    }

    private void init(boolean paramIsSelfMsg) {
        Log.i("SZU MessgaeItem", "paramIsSelfMsg:" + paramIsSelfMsg);
        if (paramIsSelfMsg) {
            mRootView = mInflater.inflate(R.layout.message_group_send_template, null);
            mBackground = R.drawable.bg_message_box_send;
            initViews(mRootView);
        }
        else {
            mRootView = mInflater.inflate(R.layout.message_group_receive_template, null);
            mBackground = R.drawable.bg_message_box_receive;
            initReceiveViews(mRootView);
        }
    }

    /**
     * 初始化发送信息的Views
     * 
     * @param view
     */
    protected void initViews(View view) {
        Log.i("szu MessageItem", "进入initViews()");
        mLayoutTimeStampContainer = (RelativeLayout) view
                .findViewById(R.id.message_layout_timecontainer);
        mHtvTimeStampTime = (HandyTextView) view.findViewById(R.id.message_timestamp_htv_time);

        mLayoutLeftContainer = (RelativeLayout) view
                .findViewById(R.id.message_layout_leftcontainer);
        mLayoutStatus = (LinearLayout) view.findViewById(R.id.message_layout_status);
        mHtvStatus = (HandyTextView) view.findViewById(R.id.message_htv_status);

        mLayoutMessageContainer = (LinearLayout) view
                .findViewById(R.id.message_layout_messagecontainer);
        mLayoutMessageContainer.setBackgroundResource(mBackground);

        mLayoutRightContainer = (LinearLayout) view
                .findViewById(R.id.message_layout_rightcontainer);
        mIvPhotoView = (ImageView) view.findViewById(R.id.message_iv_userphoto);
        onInitViews();
    }

    /**
     * 初始化接收信息的Views
     * 
     * @param view
     */
    protected void initReceiveViews(View view) {
        Log.i("SZU MessageItem", "进入initReceiveViews()");
        mLayoutTimeStampContainer = (RelativeLayout) view
                .findViewById(R.id.message_layout_timecontainer);
        mHtvTimeStampTime = (HandyTextView) view.findViewById(R.id.message_timestamp_htv_time);

        mLayoutLeftContainer = (RelativeLayout) view
                .findViewById(R.id.message_layout_leftcontainer);

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
        if (SessionUtils.isItself(mMsg.getSenderIMEI())) { // 若为自己发的信息，就初始化消息状态
            fillStatus();
        }
        else {
            mLayoutLeftContainer.setVisibility(View.VISIBLE);
        }
        fillMessage();
        fillPhotoView();
    }

    protected void fillMessage() {
        onFillMessage();
    }

    protected void fillTimeStamp() {
        Log.i("SZU MessageItem", "进入fillTimeStamp() mMsg:" + (mMsg != null));
        mLayoutTimeStampContainer.setVisibility(View.VISIBLE);
        if (!(TextUtils.isEmpty(mMsg.getSendTime()))) {
            mHtvTimeStampTime.setText(mMsg.getSendTime());
        }
    }

    protected void fillStatus() {
        Log.i("SZU MessageItem", "进入fillStatus()");
        mLayoutLeftContainer.setVisibility(View.VISIBLE);
        mLayoutStatus.setBackgroundResource(R.drawable.bg_message_status_sended);
        mHtvStatus.setText("送达");
    }

    protected void fillPhotoView() {
        Log.i("MessageItem", "进入fillPhotoView()");
        BaseApplication mBaseApplication = BaseApplication.getInstance();
        mLayoutRightContainer.setVisibility(View.VISIBLE);
        Bitmap bitmap = null;
        if (SessionUtils.isItself(mMsg.getSenderIMEI())) {
            bitmap = mBaseApplication.getAvatar(NearByPeople.AVATAR + SessionUtils.getAvatar());
        }
        else {
            NearByPeople m = mBaseApplication.getOnlineUser(mMsg.getSenderIMEI()); // 获取用户对象
            bitmap = mBaseApplication.getAvatar(NearByPeople.AVATAR + m.getAvatar());
        }
        mIvPhotoView.setImageBitmap(bitmap);
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
