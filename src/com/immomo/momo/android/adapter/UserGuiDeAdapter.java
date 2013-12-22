package com.immomo.momo.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.immomo.momo.android.R;

public class UserGuiDeAdapter extends PagerAdapter {

	private Context mContext;
	private TypedArray mContents;
	private TypedArray mBottoms;
	private TypedArray mBackgrounds;
	private TypedValue mTypedValue;

	public UserGuiDeAdapter(Context context) {
		mContext = context;
		mContents = context.getResources().obtainTypedArray(
				R.array.guide_content);
		mBottoms = context.getResources()
				.obtainTypedArray(R.array.guide_bottom);
		mBackgrounds = context.getResources().obtainTypedArray(
				R.array.guide_backgroud);
		mTypedValue = new TypedValue();
	}

	@Override
	public int getCount() {
		return mContents.length();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.include_userguide_item, null);
		ImageView mIvContent = (ImageView) view
				.findViewById(R.id.userguide_iv_content);
		ImageView mIvBottom = (ImageView) view
				.findViewById(R.id.userguide_iv_bottom);
		if (position < getCount()) {
			mIvContent.setImageDrawable(mContents.getDrawable(position));
			mIvBottom.setImageDrawable(mBottoms.getDrawable(position));
		}
		if (position == 0 || position == getCount() - 1) {
			if (mBackgrounds.getValue(position, mTypedValue)
					&& mTypedValue.resourceId != 0) {
				mIvContent.setBackgroundDrawable(mBackgrounds
						.getDrawable(position));
			}
		}
		if (position == getCount() - 1) {
			Button mBtnEnter = (Button) view
					.findViewById(R.id.userguide_btn_enter);
			mBtnEnter.setVisibility(View.VISIBLE);
			mBtnEnter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((Activity) mContext).finish();
				}
			});
		}
		container.addView(view);
		return view;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
