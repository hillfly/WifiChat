package com.immomo.momo.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.BaseObjectListAdapter;
import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Entity;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.util.PhotoUtils;
import com.immomo.momo.android.view.HandyTextView;

public class NearByPeopleAdapter extends BaseObjectListAdapter {

	public NearByPeopleAdapter(BaseApplication application, Context context,
			List<? extends Entity> datas) {
		super(application, context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_user, null);
			holder = new ViewHolder();

			holder.mIvAvatar = (ImageView) convertView
					.findViewById(R.id.user_item_iv_avatar);
			holder.mIvVip = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_vip);
			holder.mIvGroupRole = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_group_role);
			holder.mIvIndustry = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_industry);
			holder.mIvWeibo = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_weibo);
			holder.mIvTxWeibo = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_txweibo);
			holder.mIvRenRen = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_renren);
			holder.mIvDevice = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_device);
			holder.mIvRelation = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_relation);
			holder.mIvMultipic = (ImageView) convertView
					.findViewById(R.id.user_item_iv_icon_multipic);

			holder.mHtvName = (HandyTextView) convertView
					.findViewById(R.id.user_item_htv_name);
			holder.mLayoutGender = (LinearLayout) convertView
					.findViewById(R.id.user_item_layout_gender);
			holder.mIvGender = (ImageView) convertView
					.findViewById(R.id.user_item_iv_gender);
			holder.mHtvAge = (HandyTextView) convertView
					.findViewById(R.id.user_item_htv_age);
			holder.mHtvDistance = (HandyTextView) convertView
					.findViewById(R.id.user_item_htv_distance);
			holder.mHtvTime = (HandyTextView) convertView
					.findViewById(R.id.user_item_htv_time);
			holder.mHtvSign = (HandyTextView) convertView
					.findViewById(R.id.user_item_htv_sign);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NearByPeople people = (NearByPeople) getItem(position);
		holder.mIvAvatar.setImageBitmap(mApplication.getAvatar(people
				.getAvatar()));
		holder.mHtvName.setText(people.getName());
		holder.mLayoutGender.setBackgroundResource(people.getGenderBgId());
		holder.mIvGender.setImageResource(people.getGenderId());
		holder.mHtvAge.setText(people.getAge() + "");
		holder.mHtvDistance.setText(people.getDistance());
		holder.mHtvTime.setText(people.getTime());
		holder.mHtvSign.setText(people.getSign());
		if (people.getIsVip() != 0) {
			holder.mIvVip.setVisibility(View.VISIBLE);
		} else {
			holder.mIvVip.setVisibility(View.GONE);
		}
		if (people.getIsGroupRole() != 0) {
			holder.mIvGroupRole.setVisibility(View.VISIBLE);
			if (people.getIsGroupRole() == 1) {
				holder.mIvGroupRole
						.setImageResource(R.drawable.ic_userinfo_groupowner);
			}
		} else {
			holder.mIvIndustry.setVisibility(View.GONE);
		}
		if (!android.text.TextUtils.isEmpty(people.getIndustry())) {
			holder.mIvIndustry.setVisibility(View.VISIBLE);
			holder.mIvIndustry.setImageBitmap(PhotoUtils.getIndustry(mContext,
					people.getIndustry()));
		} else {
			holder.mIvIndustry.setVisibility(View.GONE);
		}
		if (people.getIsbindWeibo() != 0) {
			holder.mIvWeibo.setVisibility(View.VISIBLE);
			if (people.getIsbindWeibo() == 1) {
				holder.mIvWeibo.setImageResource(R.drawable.ic_userinfo_weibov);
			}
		} else {
			holder.mIvWeibo.setVisibility(View.GONE);
		}
		if (people.getIsbindTxWeibo() != 0) {
			holder.mIvTxWeibo.setVisibility(View.VISIBLE);
			if (people.getIsbindTxWeibo() == 1) {
				holder.mIvTxWeibo
						.setImageResource(R.drawable.ic_userinfo_tweibov);
			}
		} else {
			holder.mIvTxWeibo.setVisibility(View.GONE);
		}

		if (people.getIsbindRenRen() != 0) {
			holder.mIvRenRen.setVisibility(View.VISIBLE);
		} else {
			holder.mIvRenRen.setVisibility(View.GONE);
		}
		if (people.getDevice() != 0) {
			holder.mIvDevice.setVisibility(View.VISIBLE);
			if (people.getDevice() == 1) {
				holder.mIvDevice
						.setImageResource(R.drawable.ic_userinfo_android);
			}
			if (people.getDevice() == 2) {
				holder.mIvDevice.setImageResource(R.drawable.ic_userinfo_apple);
			}
		} else {
			holder.mIvDevice.setVisibility(View.GONE);
		}
		if (people.getIsRelation() != 0) {
			holder.mIvRelation.setVisibility(View.VISIBLE);
		} else {
			holder.mIvRelation.setVisibility(View.GONE);
		}
		if (people.getIsMultipic() != 0) {
			holder.mIvMultipic.setVisibility(View.VISIBLE);
		} else {
			holder.mIvMultipic.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {

		ImageView mIvAvatar;
		ImageView mIvVip;
		ImageView mIvGroupRole;
		ImageView mIvIndustry;
		ImageView mIvWeibo;
		ImageView mIvTxWeibo;
		ImageView mIvRenRen;
		ImageView mIvDevice;
		ImageView mIvRelation;
		ImageView mIvMultipic;
		HandyTextView mHtvName;
		LinearLayout mLayoutGender;
		ImageView mIvGender;
		HandyTextView mHtvAge;
		HandyTextView mHtvDistance;
		HandyTextView mHtvTime;
		HandyTextView mHtvSign;
	}
}
