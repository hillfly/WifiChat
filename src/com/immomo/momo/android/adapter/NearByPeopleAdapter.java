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
import com.immomo.momo.android.view.HandyTextView;

public class NearByPeopleAdapter extends BaseObjectListAdapter {

    public NearByPeopleAdapter(BaseApplication application, Context context, List<? extends Entity> datas) {
        super(application, context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_user, null);
            holder = new ViewHolder();

            holder.mIvAvatar = (ImageView) convertView.findViewById(R.id.user_item_iv_avatar);
            holder.mIvGroupRole = (ImageView) convertView.findViewById(R.id.user_item_iv_icon_group_role);
            holder.mIvDevice = (ImageView) convertView.findViewById(R.id.user_item_iv_icon_device);
            holder.mHtvName = (HandyTextView) convertView.findViewById(R.id.user_item_htv_name);
            holder.mLayoutGender = (LinearLayout) convertView.findViewById(R.id.user_item_layout_gender);
            holder.mIvGender = (ImageView) convertView.findViewById(R.id.user_item_iv_gender);
            holder.mHtvAge = (HandyTextView) convertView.findViewById(R.id.user_item_htv_age);
            holder.mHtvTime = (HandyTextView) convertView.findViewById(R.id.user_item_htv_time);
            holder.mHtvSign = (HandyTextView) convertView.findViewById(R.id.user_item_htv_sign);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NearByPeople people = (NearByPeople) getItem(position);
        holder.mIvAvatar.setImageBitmap(mApplication.getAvatar(NearByPeople.AVATAR
                + people.getAvatar()));
        holder.mHtvName.setText(people.getNickname());
        holder.mLayoutGender.setBackgroundResource(people.getGenderBgId());
        holder.mIvGender.setImageResource(people.getGenderId());
        holder.mHtvAge.setText(people.getAge() + "");
        holder.mHtvTime.setText(people.getLogintime());
//        holder.mHtvSign.setText(people.getSign());
        holder.mIvDevice.setImageResource(R.drawable.ic_userinfo_android);         
        return convertView;
    }

    class ViewHolder {
        ImageView mIvAvatar;
        ImageView mIvGroupRole;
        ImageView mIvDevice;
        HandyTextView mHtvName;
        LinearLayout mLayoutGender;
        ImageView mIvGender;
        HandyTextView mHtvAge;
        HandyTextView mHtvTime;
        HandyTextView mHtvSign;
    }
}
