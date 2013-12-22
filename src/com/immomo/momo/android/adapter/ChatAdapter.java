package com.immomo.momo.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.BaseObjectListAdapter;
import com.immomo.momo.android.activity.message.MessageItem;
import com.immomo.momo.android.entity.Entity;
import com.immomo.momo.android.entity.Message;

public class ChatAdapter extends BaseObjectListAdapter {

	public ChatAdapter(BaseApplication application, Context context,
			List<? extends Entity> datas) {
		super(application, context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message msg = (Message) getItem(position);
		MessageItem messageItem = MessageItem.getInstance(msg, mContext);
		messageItem.fillContent();
		View view = messageItem.getRootView();
		return view;
	}
}
