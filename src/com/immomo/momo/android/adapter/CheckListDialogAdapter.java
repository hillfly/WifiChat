package com.immomo.momo.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.immomo.momo.android.BaseArrayListAdapter;
import com.immomo.momo.android.R;
import com.immomo.momo.android.view.HandyTextView;

public class CheckListDialogAdapter extends BaseArrayListAdapter {
	private int mCheckId;

	public CheckListDialogAdapter(int checkId, Context context,
			List<String> datas) {
		super(context, datas);
		mCheckId = checkId;
	}

	public CheckListDialogAdapter(int checkId, Context context, String... datas) {
		super(context, datas);
		mCheckId = checkId;
	}

	public void setCheckId(int checkId) {
		mCheckId = checkId;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.listitem_dialog, null);
		}
		((HandyTextView) arg1.findViewById(R.id.listitem_dialog_text))
				.setText((CharSequence) getItem(arg0));
		ImageView checkd = ((ImageView) arg1
				.findViewById(R.id.listitem_dialog_icon));
		if (mCheckId == arg0) {
			checkd.setVisibility(View.VISIBLE);
		} else {
			checkd.setVisibility(View.GONE);
		}
		return arg1;
	}
}
