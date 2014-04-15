package com.immomo.momo.android.activity.message;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.view.EmoticonsTextView;

public class TextMessageItem extends MessageItem implements OnLongClickListener {

    private EmoticonsTextView mEtvContent;

    public TextMessageItem(Message paramMsg, Context paramContext) {
        super(paramMsg, paramContext);
    }

    @Override
    protected void onInitViews() {
        Log.i("TextMessageItem", "进入onInitViews()");
        View view = mInflater.inflate(R.layout.message_text, null);
        Log.i("TextMessageItem", "view:" + (view != null) + " mLayoutMessageContainer:"
                + (mLayoutMessageContainer != null));
        mLayoutMessageContainer.addView(view);
        mEtvContent = (EmoticonsTextView) view.findViewById(R.id.message_etv_msgtext);
        Log.i("TextMessageItem", "MsgContent:" + mMsg.getMsgContent());
        mEtvContent.setText(mMsg.getMsgContent());
        mEtvContent.setOnLongClickListener(this);
        mLayoutMessageContainer.setOnLongClickListener(this);
    }

    @Override
    protected void onFillMessage() {

    }

    @Override
    public boolean onLongClick(View v) {
        Log.d("SZU_TextMessageItem", "onLongClick");
        return true;
    }

}
