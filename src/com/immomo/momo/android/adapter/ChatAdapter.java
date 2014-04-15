package com.immomo.momo.android.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
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
	
	public void setData(List<? extends Entity> datas){
	    super.setData(datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message msg = (Message) getItem(position);
		Log.i("SZU ChatAdapter", "msg:" + (msg != null));
		MessageItem messageItem = MessageItem.getInstance(msg, mContext);
		Log.i("SZU ChatAdapter", "messageItem:" + (messageItem != null));
		messageItem.fillContent();
		View view = messageItem.getRootView();
		return view;
	}
}
