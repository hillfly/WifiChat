package hillfly.wifichat.adapter;

import hillfly.wifichat.R;
import hillfly.wifichat.common.BaseObjectListAdapter;
import hillfly.wifichat.model.Entity;
import hillfly.wifichat.model.Users;
import hillfly.wifichat.util.DateUtils;
import hillfly.wifichat.util.ImageUtils;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FriendsAdapter extends BaseObjectListAdapter {

    public FriendsAdapter(Context context, List<? extends Entity> datas) {
        super(context, datas);
    }

    public void setData(List<? extends Entity> datas) {
        super.setData(datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_chat_user, null);
            holder = new ViewHolder();

            holder.mIvAvatar = (ImageView) convertView.findViewById(R.id.user_item_iv_avatar);
            holder.mIvDevice = (ImageView) convertView.findViewById(R.id.user_item_iv_icon_device);
            holder.mHtvName = (TextView) convertView.findViewById(R.id.user_item_htv_name);
            holder.mLayoutGender = (LinearLayout) convertView
                    .findViewById(R.id.user_item_layout_gender);
            holder.mIvGender = (ImageView) convertView.findViewById(R.id.user_item_iv_gender);
            holder.mHtvAge = (TextView) convertView.findViewById(R.id.user_item_htv_age);
            holder.mHtvTime = (TextView) convertView.findViewById(R.id.user_item_htv_time);
            holder.mHtvLastMsg = (TextView) convertView
                    .findViewById(R.id.user_item_htv_lastmsg);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Users people = (Users) getItem(position);
        int avatarId = ImageUtils.getImageID(Users.AVATAR + people.getAvatar());
        Picasso.with(mContext).load(avatarId).into(holder.mIvAvatar);
        holder.mHtvName.setText(people.getNickname());
        holder.mLayoutGender.setBackgroundResource(people.getGenderBgId());
        holder.mIvGender.setImageResource(people.getGenderId());
        holder.mHtvAge.setText(people.getAge() + "");
        holder.mHtvTime.setText(DateUtils.getBetweentime(people.getLogintime()));
        holder.mHtvLastMsg.setText(mUDPListener.getLastMsgCache(people.getIMEI()));
        holder.mIvDevice.setImageResource(R.drawable.ic_userinfo_android);
        return convertView;
    }

    class ViewHolder {
        ImageView mIvAvatar;
        ImageView mIvDevice;
        TextView mHtvName;
        LinearLayout mLayoutGender;
        ImageView mIvGender;
        TextView mHtvAge;
        TextView mHtvTime;
        TextView mHtvLastMsg;
    }
}
