package com.immomo.momo.android.activity.message;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.view.EmoticonsTextView;

public class TextMessageItem extends MessageItem implements OnLongClickListener {

	private EmoticonsTextView mEtvContent;

	public TextMessageItem(Message msg, Context context) {
		super(msg, context);
	}
	

	@Override
	protected void onInitViews() {
		View view = mInflater.inflate(R.layout.message_text, null);
		mLayoutMessageContainer.addView(view);
		mEtvContent = (EmoticonsTextView) view
				.findViewById(R.id.message_etv_msgtext);
		mEtvContent.setText(mMsg.getContent());
		mEtvContent.setOnLongClickListener(this);
		mLayoutMessageContainer.setOnLongClickListener(this);
	}

	@Override
	protected void onFillMessage() {

	}

	@Override
	public boolean onLongClick(View v) {
		System.out.println("长按");
		return true;
	}

}
