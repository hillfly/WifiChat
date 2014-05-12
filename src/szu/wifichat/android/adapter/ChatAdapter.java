package szu.wifichat.android.adapter;

import java.util.List;

import szu.wifichat.android.BaseApplication;
import szu.wifichat.android.BaseObjectListAdapter;
import szu.wifichat.android.activity.message.FileMessageItem;
import szu.wifichat.android.activity.message.ImageMessageItem;
import szu.wifichat.android.activity.message.MessageItem;
import szu.wifichat.android.activity.message.VoiceMessageItem;
import szu.wifichat.android.entity.Entity;
import szu.wifichat.android.entity.Message;
import szu.wifichat.android.file.explore.FileState;
import szu.wifichat.android.util.FileUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ChatAdapter extends BaseObjectListAdapter {

    public ChatAdapter(BaseApplication application, Context context, List<? extends Entity> datas) {
        super(application, context, datas);
    }

    public void setData(List<? extends Entity> datas) {
        super.setData(datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message msg = (Message) getItem(position);
        MessageItem messageItem = MessageItem.getInstance(msg, mContext);
        messageItem.fillContent();

        View view = messageItem.getRootView();
        switch (msg.getContentType()) {
            case IMAGE:
                String imagPath = msg.getMsgContent();
                if (FileUtils.isFileExists(imagPath)) {
                    ImageMessageItem imageMessageItem = (ImageMessageItem) messageItem;
                    imageMessageItem.setProgress(100);
                }
                break;
            case VOICE:
                String voicePath = msg.getMsgContent();
                if (FileUtils.isFileExists(voicePath)) {
                    VoiceMessageItem voiceMessageItem = (VoiceMessageItem) messageItem;
                    voiceMessageItem.setProgress(100);
                }
                break;
            case FILE:
                String filePath = msg.getMsgContent();
                if (FileUtils.isFileExists(filePath)) {
                    FileMessageItem fileMessageItem = (FileMessageItem) messageItem;
                    FileState fs = BaseApplication.sendFileStates.get(filePath);
                    if (null == fs) {
                        fs = BaseApplication.recieveFileStates.get(filePath);
                        if (null == fs)
                            fileMessageItem.setProgress(100);
                        else {
                            fileMessageItem.setProgress(fs.percent);
                            fs = null;
                        }
                    }
                    else {
                        fileMessageItem.setProgress(fs.percent);
                        fs = null;
                    }
                }
                break;
            default:
                break;
        }

        return view;
    }

}
