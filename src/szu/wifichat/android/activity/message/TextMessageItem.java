package szu.wifichat.android.activity.message;

import szu.wifichat.android.entity.Message;
import szu.wifichat.android.view.EmoticonsTextView;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;

import szu.wifichat.android.R;

public class TextMessageItem extends MessageItem implements OnLongClickListener {

    private EmoticonsTextView mEtvContent;

    public TextMessageItem(Message paramMsg, Context paramContext) {
        super(paramMsg, paramContext);
    }

    @Override
    protected void onInitViews() {
        View view = mInflater.inflate(R.layout.message_text, null);
        mLayoutMessageContainer.addView(view);
        mEtvContent = (EmoticonsTextView) view.findViewById(R.id.message_etv_msgtext);
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
