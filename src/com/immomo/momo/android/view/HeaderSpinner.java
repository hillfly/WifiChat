package com.immomo.momo.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.immomo.momo.android.R;

public class HeaderSpinner extends LinearLayout {

	private View mView;
	private RelativeLayout mLayoutRoot;
	private HandyTextView mHtvText;
	private RotatingImageView mRivArrow;

	private LayoutInflater mInflater;

	private boolean mIsSelect;
	private onSpinnerClickListener mOnSpinnerClickListener;

	public HeaderSpinner(Context context) {
		super(context);
		init(context);
	}

	public HeaderSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HeaderSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mView = mInflater.inflate(R.layout.common_headerbar_spinner, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mView, params);
		initViews();
		initEvents();
	}

	private void initViews() {
		mLayoutRoot = (RelativeLayout) findViewBySpinnerId(R.id.header_spinner_layout_root);
		mHtvText = (HandyTextView) findViewBySpinnerId(R.id.header_spinner_htv_text);
		mRivArrow = (RotatingImageView) findViewBySpinnerId(R.id.header_spinner_riv_arrow);
	}

	private void initEvents() {
		mLayoutRoot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnSpinnerClickListener != null) {
					mIsSelect = !mIsSelect;
					initSpinnerState(mIsSelect);
					mOnSpinnerClickListener.onClick(mIsSelect);
				}
			}
		});
	}

	public View findViewBySpinnerId(int id) {
		return mView.findViewById(id);
	}

	public void setText(CharSequence text) {
		if (text != null) {
			mHtvText.setText(text);
		}
	}

	public void initSpinnerState(boolean isSelect) {
		mIsSelect = isSelect;
		if (mIsSelect) {
			mLayoutRoot.setSelected(true);
			mRivArrow.setDegress(-180);
		} else {
			mLayoutRoot.setSelected(false); // "附近"失去焦点
			mRivArrow.setDegress(360); 
			// 旋转三角箭头(左 下 右 上 : 90 180 270 360),并关闭Pop,刷新View 
		}
	}

	public void setOnSpinnerClickListener(onSpinnerClickListener l) {
		mOnSpinnerClickListener = l;
	}

	public interface onSpinnerClickListener {
		void onClick(boolean isSelect);
	}
}
