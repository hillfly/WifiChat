package com.immomo.momo.android.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.immomo.momo.android.BasePopupWindow;
import com.immomo.momo.android.R;

/**
 * @fileName NearByPopupWindow.java
 * @package com.immomo.momo.android.popupwindow
 * @description 附近PopupWindow类
 * @author 任东卫
 * @email 86930007@qq.com
 * @version 1.0
 */
public class NearByPopupWindow extends BasePopupWindow {

	private LinearLayout mLayoutRoot;// 根布局
	private RadioGroup mRgGender;// 性别
	private RadioGroup mRgTime;// 时间
	private Button mBtnSubmit;// 确认
	private Button mBtnCancel;// 取消

	public NearByPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(
				R.layout.include_dialog_nearby_filter, null),
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setAnimationStyle(R.style.Popup_Animation_PushDownUp);
	}

	@Override
	public void initViews() {
		mLayoutRoot = (LinearLayout) findViewById(R.id.dialog_nearby_layout_root);
		mRgGender = (RadioGroup) findViewById(R.id.dialog_nearby_rg_gender);

		mRgTime = (RadioGroup) findViewById(R.id.dialog_nearby_rg_time);
		mBtnSubmit = (Button) findViewById(R.id.dialog_nearby_btn_submit);
		mBtnCancel = (Button) findViewById(R.id.dialog_nearby_btn_cancel);
	}

	@Override
	public void initEvents() {
		mLayoutRoot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		mBtnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (mOnSubmitClickListener != null) {
					mOnSubmitClickListener.onClick();
				}
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		mRgGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 暂时不做任何操作
			}
		});
		mRgTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 暂时不做任何操作
			}
		});
	}

	@Override
	public void init() {
		// 设置默认项
		mRgGender.check(R.id.dialog_nearby_rb_gender_all);
		mRgTime.check(R.id.dialog_nearby_rb_time_fifteenminutes);
	}

}
