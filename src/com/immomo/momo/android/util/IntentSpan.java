package com.immomo.momo.android.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

public class IntentSpan extends ClickableSpan {
	private final OnClickListener mOnClickListener;

	public IntentSpan(View.OnClickListener listener) {
		mOnClickListener = listener;
	}

	public void onClick(View view) {
		mOnClickListener.onClick(view);
	}

	public void updateDrawState(TextPaint ds) {
		super.updateDrawState(ds);
		ds.setUnderlineText(true);
	}
}
