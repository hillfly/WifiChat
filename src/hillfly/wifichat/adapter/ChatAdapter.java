package hillfly.wifichat.adapter;

import hillfly.wifichat.BaseApplication;
import hillfly.wifichat.BaseObjectListAdapter;
import hillfly.wifichat.R;
import hillfly.wifichat.bean.Entity;
import hillfly.wifichat.bean.Message;
import hillfly.wifichat.bean.Users;
import hillfly.wifichat.file.FileState;
import hillfly.wifichat.socket.tcp.TcpClient;
import hillfly.wifichat.socket.tcp.TcpService;
import hillfly.wifichat.util.FileUtils;
import hillfly.wifichat.util.ImageUtils;
import hillfly.wifichat.util.SessionUtils;
import hillfly.wifichat.view.EmoticonsTextView;
import hillfly.wifichat.view.HandyTextView;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

public class ChatAdapter extends BaseObjectListAdapter {

    private static final int TYPE_LEFT_TEXT = 0;
    private static final int TYPE_LEFT_IMAGE = 1;
    private static final int TYPE_LEFT_VOICE = 2;
    private static final int TYPE_LEFT_FILE = 3;

    private static final int TYPE_RIGHT_TEXT = 4;
    private static final int TYPE_RIGHT_IMAGE = 5;
    private static final int TYPE_RIGHT_VOICE = 6;
    private static final int TYPE_RIGHT_FILE = 7;

    private static final int ITEM_POSITION = -1;

    private ListView mListView;
    private int mAvatarId;
    private Users mChatUser;

    public ChatAdapter(Context context, List<? extends Entity> datas) {
        super(context, datas);
        TcpClient.setHandler(mHandler); // 绑定handler
        TcpService.setHandler(mHandler);

    }

    public void setData(List<? extends Entity> datas) {
        super.setData(datas);
    }

    public void setListView(ListView view) {
        this.mListView = view;
    }

    public void setChatUser(Users user) {
        this.mChatUser = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message msg = (Message) getItem(position);
        if (SessionUtils.isLocalUser(msg.getSenderIMEI())) {
            String avatarFileName = Users.AVATAR + SessionUtils.getAvatar();
            mAvatarId = ImageUtils.getImageID(avatarFileName);
        }
        else {
            Users users = mUDPListener.getOnlineUser(msg.getSenderIMEI());
            String avatarFileName = Users.AVATAR + users.getAvatar();
            mAvatarId = ImageUtils.getImageID(avatarFileName);
        }
        int messageType = getItemViewType(position);

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            switch (messageType) {
                case TYPE_LEFT_TEXT:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_text, null);

                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);
                    holder.mEtvTextContent = (EmoticonsTextView) holder.mView
                            .findViewById(R.id.message_etv_msgtext);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_LEFT_IMAGE:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_image, null);

                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);
                    holder.mIvImageContent = (ImageView) holder.mView
                            .findViewById(R.id.message_iv_msgimage);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_LEFT_VOICE:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_voice, null);

                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);
                    holder.mIvVoiceImage = (ImageView) holder.mView
                            .findViewById(R.id.voice_message_iv_msgimage);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_LEFT_FILE:
                    convertView = mInflater.inflate(R.layout.message_group_receive_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.left_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_file, null);

                    holder.mHtvLoadingProcess = (HandyTextView) holder.mView
                            .findViewById(R.id.message_file_htv_loading_text);
                    holder.mIvLeftAvatar = (ImageView) convertView
                            .findViewById(R.id.left_message_iv_userphoto);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_TEXT:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_text, null);

                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);
                    holder.mEtvTextContent = (EmoticonsTextView) holder.mView
                            .findViewById(R.id.message_etv_msgtext);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_IMAGE:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_image, null);

                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);
                    holder.mIvImageContent = (ImageView) holder.mView
                            .findViewById(R.id.message_iv_msgimage);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_VOICE:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_voice, null);

                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);
                    holder.mIvVoiceImage = (ImageView) holder.mView
                            .findViewById(R.id.voice_message_iv_msgimage);
                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;

                case TYPE_RIGHT_FILE:
                    convertView = mInflater.inflate(R.layout.message_group_send_template, null);
                    holder.mHtvTimeStampTime = (HandyTextView) convertView
                            .findViewById(R.id.message_timestamp_htv_time);
                    holder.mLayoutMessageContainer = (LinearLayout) convertView
                            .findViewById(R.id.right_message_layout_messagecontainer);
                    holder.mView = mInflater.inflate(R.layout.message_file, null);

                    holder.mHtvLoadingProcess = (HandyTextView) holder.mView
                            .findViewById(R.id.message_file_htv_loading_text);
                    holder.mIvRightAvatar = (ImageView) convertView
                            .findViewById(R.id.right_message_iv_userphoto);

                    holder.mLayoutMessageContainer.addView(holder.mView);
                    break;
            }
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (messageType) {
            case TYPE_LEFT_TEXT:
                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                holder.mEtvTextContent.setText(msg.getMsgContent());
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvLeftAvatar);
                break;

            case TYPE_LEFT_IMAGE:
                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvLeftAvatar);
                Picasso.with(mContext).load(getThumbnailPath(msg)).into(holder.mIvImageContent);
                break;

            case TYPE_LEFT_VOICE:
                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                holder.mIvVoiceImage.setImageResource(R.drawable.voicerecord_left);
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvLeftAvatar);
                break;

            case TYPE_LEFT_FILE:
                holder.mHtvLoadingProcess.setTag(FileUtils.getNameByPath(msg.getMsgContent()));
                holder.mHtvLoadingProcess.setTag(ITEM_POSITION, position);

                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvLeftAvatar);
                if (FileUtils.isFileExists(msg.getMsgContent())) {
                    holder.mHtvLoadingProcess.setVisibility(View.GONE);
                }
                else {
                    holder.mHtvLoadingProcess.setVisibility(View.VISIBLE);
                    holder.mHtvLoadingProcess.setText(msg.getPercent() + "");

                }
                break;

            case TYPE_RIGHT_TEXT:
                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                holder.mEtvTextContent.setText(msg.getMsgContent());
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvRightAvatar);
                break;

            case TYPE_RIGHT_IMAGE:
                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvRightAvatar);
                Picasso.with(mContext).load(getThumbnailPath(msg)).into(holder.mIvImageContent);
                break;

            case TYPE_RIGHT_VOICE:
                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvRightAvatar);
                Picasso.with(mContext).load(R.drawable.voicerecord_right)
                        .into(holder.mIvVoiceImage);
                break;

            case TYPE_RIGHT_FILE:

                holder.mHtvLoadingProcess.setTag(FileUtils.getNameByPath(msg.getMsgContent()));
                holder.mHtvLoadingProcess.setTag(ITEM_POSITION, position);

                holder.mHtvTimeStampTime.setText(msg.getSendTime());
                Picasso.with(mContext).load(mAvatarId).into(holder.mIvRightAvatar);
                if (FileUtils.isFileExists(msg.getMsgContent())) {
                    holder.mHtvLoadingProcess.setVisibility(View.GONE);
                }
                else {
                    holder.mHtvLoadingProcess.setVisibility(View.VISIBLE);
                    holder.mHtvLoadingProcess.setText(msg.getPercent() + "");

                }
                break;
        }

        return convertView;
    }

    /**
     * 根据数据源的position返回需要显示的的layout的type
     * 
     * */
    @Override
    public int getItemViewType(int position) {

        Message msg = (Message) getItem(position);
        int type = -1;
        if (SessionUtils.isLocalUser(msg.getSenderIMEI())) {
            switch (msg.getContentType()) {
                case TEXT:
                    type = 4;
                    break;

                case IMAGE:
                    type = 5;
                    break;

                case VOICE:
                    type = 6;
                    break;

                case FILE:
                    type = 7;
                    break;
            }

        }
        else {
            switch (msg.getContentType()) {
                case TEXT:
                    type = 0;
                    break;

                case IMAGE:
                    type = 1;
                    break;

                case VOICE:
                    type = 2;
                    break;

                case FILE:
                    type = 3;
                    break;
            }
        }
        return type;
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message paramMsg) {
            updateView(paramMsg);
        }
    };

    private void updateView(android.os.Message paramMsg) {
        FileState file = (FileState) paramMsg.obj;

        if (mListView != null) {
            HandyTextView htvView = (HandyTextView) mListView.findViewWithTag(FileUtils
                    .getNameByPath(file.fileName));

            if (htvView != null) {
                int itemPosition = (Integer) htvView.getTag(ITEM_POSITION);
                int visiblePos = mListView.getFirstVisiblePosition();
                int offset = itemPosition - visiblePos;

                Message itemMessage = (Message) getItem(itemPosition);
                itemMessage.setPercent(file.percent);

                // 只有在可见区域才更新
                if (offset < 0)
                    return;

                else {
                    if (file.percent != 100) {
                        htvView.setText(file.percent + "");
                    }
                    else {
                        htvView.setVisibility(View.GONE);
                    }
                }

            }
        }
    }

    @SuppressWarnings("unused")
    private Bitmap getThumbnailBitmap(Message msg) {
        String imagePath = BaseApplication.THUMBNAIL_PATH + File.separator;
        if (SessionUtils.isLocalUser(msg.getSenderIMEI())) {
            imagePath = imagePath + mChatUser.getIMEI() + File.separator
                    + FileUtils.getNameByPath(msg.getMsgContent());
        }
        else {
            imagePath = imagePath + msg.getSenderIMEI() + File.separator
                    + FileUtils.getNameByPath(msg.getMsgContent());
        }

        Bitmap bitmap = ImageUtils.getBitmapFromPath(imagePath);

        return bitmap;
    }

    private String getThumbnailPath(Message msg) {
        StringBuffer imagePath = new StringBuffer();
        imagePath.append("file://").append(BaseApplication.THUMBNAIL_PATH).append(File.separator);
        if (SessionUtils.isLocalUser(msg.getSenderIMEI())) {
            imagePath.append(mChatUser.getIMEI()).append(File.separator)
                    .append(FileUtils.getNameByPath(msg.getMsgContent()));
        }
        else {
            imagePath.append(msg.getSenderIMEI()).append(File.separator)
                    .append(FileUtils.getNameByPath(msg.getMsgContent()));
        }

        return imagePath.toString();
    }

    /**
     * 返回所有的layout的数量
     * 
     * */
    @Override
    public int getViewTypeCount() {
        return 8;
    }

    static class ViewHolder {

        private HandyTextView mHtvTimeStampTime; // 时间
        private LinearLayout mLayoutMessageContainer; // 消息容器
        private View mView;

        private EmoticonsTextView mEtvTextContent; // 文本内容
        private ImageView mIvImageContent; // 图像内容
        private ImageView mIvVoiceImage; // 声音图像
        private HandyTextView mHtvLoadingProcess; // 下载进度条

        private ImageView mIvLeftAvatar; // 左边的头像
        private ImageView mIvRightAvatar; // 右边的头像

    }

}
