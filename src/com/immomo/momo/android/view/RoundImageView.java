package com.immomo.momo.android.view;

import com.immomo.momo.android.util.PhotoUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {

	public RoundImageView(Context context) {
		super(context);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		bm = PhotoUtils.toRoundCorner(bm, 3);
		super.setImageBitmap(bm);
	}

	public void setImageBitmap(Bitmap bm, int pixels) {
		bm = PhotoUtils.toRoundCorner(bm, pixels);
		super.setImageBitmap(bm);
	}
}
