package com.immomo.momo.android.activity.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.view.HandyTextView;

public abstract class MessageItem {

    protected Context mContext;
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
        messageItem.init(msg.getIsSelfMsg());
        return messageItem;
    }

    private void init(boolean paramIsSelfMsg) {
        if (paramIsSelfMsg) {
            mRootView = mInflater.inflate(R.layout.message_group_send_template,
                    null);
            mBackground = R.drawable.bg_message_box_send;
        } else {
            mBackground = R.drawable.bg_message_box_receive;
        }
        if (mRootView != null) {
            initViews(mRootView);
        }
    }

    protected void initViews(View view) {
        mLayoutTimeStampContainer = (RelativeLayout) view.findViewById(R.id.message_layout_timecontainer);
        mHtvTimeStampTime = (HandyTextView) view.findViewById(R.id.message_timestamp_htv_time);

        mLayoutLeftContainer = (RelativeLayout) view.findViewById(R.id.message_layout_leftcontainer);
        mLayoutStatus = (LinearLayout) view.findViewById(R.id.message_layout_status);
        mHtvStatus = (HandyTextView) view.findViewById(R.id.message_htv_status);

        mLayoutMessageContainer = (LinearLayout) view.findViewById(R.id.message_layout_messagecontainer);
        mLayoutMessageContainer.setBackgroundResource(mBackground);

        mLayoutRightContainer = (LinearLayout) view.findViewById(R.id.message_layout_rightcontainer);
        mIvPhotoView = (ImageView) view.findViewById(R.id.message_iv_userphoto);
        onInitViews();
    }

    public void fillContent() {
        fillTimeStamp();
        fillStatus();
        fillMessage();
        fillPhotoView();
    }

    protected void fillMessage() {
        onFillMessage();
    }

    protected void fillTimeStamp() {
        mLayoutTimeStampContainer.setVisibility(View.VISIBLE);
        if (mMsg.getSendTime().length() != 0) {
            mHtvTimeStampTime.setText(mMsg.getSendTime());
        }
    }

    protected void fillStatus() {
        mLayoutLeftContainer.setVisibility(View.VISIBLE);
        mLayoutStatus.setBackgroundResource(R.drawable.bg_message_status_sended);
        mHtvStatus.setText("送达");
    }

    protected void fillPhotoView() {
        BaseApplication mBaseApplication = BaseApplication.getInstance();
        mLayoutRightContainer.setVisibility(View.VISIBLE);
        mIvPhotoView.setImageBitmap(mBaseApplication.getAvatar(NearByPeople.AVATAR
                + mBaseApplication.OnlineUsers.get(mMsg.getSenderIMEI())
                                              .getAvatar()));
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
