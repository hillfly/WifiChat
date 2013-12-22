package com.immomo.momo.android.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.BaseObjectListAdapter;
import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Entity;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByGroups;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.MoMoRefreshExpandableList.PinnedHeaderAdapter;

public class NearByGroupAdapter extends BaseObjectListAdapter implements
		SectionIndexer, PinnedHeaderAdapter {

	private List<NearByGroup> mNearByGroups = new ArrayList<NearByGroup>();
	private int[] mPositions;
	private int mCount = 0;

	@SuppressWarnings("unchecked")
	public NearByGroupAdapter(BaseApplication application, Context context,
			List<? extends Entity> datas) {
		super(application, context, datas);
		mNearByGroups = (List<NearByGroup>) datas;
		mPositions = new int[mNearByGroups.size()];
		int position = 0;
		for (int i = 0; i < mNearByGroups.size(); i++) {
			mPositions[i] = position;
			position += mNearByGroups.get(i).getGroupCount();
		}
		mCount = position;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_nearby_group,
					null);
			holder = new ViewHolder();
			holder.mLayoutHeader = (LinearLayout) convertView
					.findViewById(R.id.nearby_group_item_layout_header);
			holder.mHtvHeaderAddress = (HandyTextView) convertView
					.findViewById(R.id.nearby_group_item_header_htv_address);
			holder.mHtvHeaderCount = (HandyTextView) convertView
					.findViewById(R.id.nearby_group_item_header_htv_count);
			holder.mIvAvatar = (ImageView) convertView
					.findViewById(R.id.nearby_group_item_iv_avatar);
			holder.mIvParty = (ImageView) convertView
					.findViewById(R.id.nearby_group_item_iv_party);
			holder.mIvNew = (ImageView) convertView
					.findViewById(R.id.nearby_group_item_iv_new);
			holder.mHtvName = (HandyTextView) convertView
					.findViewById(R.id.nearby_group_item_htv_name);
			holder.mHtvMember = (HandyTextView) convertView
					.findViewById(R.id.nearby_group_item_htv_member);
			holder.mIvLevel = (ImageView) convertView
					.findViewById(R.id.nearby_group_item_iv_level);
			holder.mHtvSign = (HandyTextView) convertView
					.findViewById(R.id.nearby_group_item_htv_sign);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int section = getSectionForPosition(position);
		int positionForSection = getPositionForSection(section);
		final NearByGroup group = mNearByGroups.get(section);
		NearByGroups groups = group.getmGroups().get(
				position - positionForSection);
		if (position == positionForSection) {
			holder.mLayoutHeader.setVisibility(View.VISIBLE);
			holder.mHtvHeaderAddress.setText(group.getAddress() + "    "
					+ group.getDistance());
			holder.mHtvHeaderCount.setText("显示全部" + group.getGroupCount()
					+ "个群组");
			holder.mLayoutHeader.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					System.out.println(group.getAddress() + "    "
							+ group.getDistance());
				}
			});
		} else {
			holder.mLayoutHeader.setVisibility(View.GONE);
		}
		holder.mIvAvatar.setImageBitmap(mApplication.getAvatar(groups
				.getAvatar()));
		if (groups.getIsParty() == 0) {
			holder.mIvParty.setVisibility(View.GONE);
		} else {
			holder.mIvParty.setVisibility(View.VISIBLE);
		}
		if (groups.getIsNew() == 0) {
			holder.mIvNew.setVisibility(View.GONE);
		} else {
			holder.mIvNew.setVisibility(View.VISIBLE);
		}
		holder.mHtvName.setText(groups.getName());
		if (groups.getMemberCount() > groups.getMemberTotal() - 8) {
			holder.mHtvMember.setTextColor(mContext.getResources().getColor(
					R.color.red));
		} else {
			holder.mHtvMember.setTextColor(mContext.getResources().getColor(
					R.color.font_value));
		}
		holder.mHtvMember.setText(groups.getMemberCount() + "/"
				+ groups.getMemberTotal());
		if (groups.getLevel() > 0) {
			holder.mIvLevel.setVisibility(View.VISIBLE);
			if (groups.getIsVip() == 0) {
				switch (groups.getLevel()) {
				case 1:
					holder.mIvLevel
							.setImageResource(R.drawable.ic_group_level1);
					break;
				case 2:
					holder.mIvLevel
							.setImageResource(R.drawable.ic_group_level2);
					break;

				case 3:
					holder.mIvLevel
							.setImageResource(R.drawable.ic_group_level3);
					break;
				default:
					holder.mIvLevel.setImageResource(R.drawable.transparent);
					break;
				}
			} else {
				switch (groups.getLevel()) {
				case 1:
					holder.mIvLevel
							.setImageResource(R.drawable.ic_group_vip_level1);
					break;
				case 2:
					holder.mIvLevel
							.setImageResource(R.drawable.ic_group_vip_level2);
					break;

				case 3:
					holder.mIvLevel
							.setImageResource(R.drawable.ic_group_vip_level3);
					break;
				default:
					holder.mIvLevel.setImageResource(R.drawable.transparent);
					break;
				}
			}
		} else {
			holder.mIvLevel.setVisibility(View.GONE);
		}
		holder.mHtvSign.setText(groups.getSign());
		return convertView;
	}

	class ViewHolder {
		LinearLayout mLayoutHeader;
		HandyTextView mHtvHeaderAddress;
		HandyTextView mHtvHeaderCount;
		ImageView mIvAvatar;
		ImageView mIvParty;
		ImageView mIvNew;
		HandyTextView mHtvName;
		HandyTextView mHtvMember;
		ImageView mIvLevel;
		HandyTextView mHtvSign;
	}

	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position - 1;
		if (realPosition < 0) {
			return PINNED_HEADER_GONE;
		}
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;

	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		int realPosition = position - 1;
		int section = getSectionForPosition(realPosition);
		NearByGroup group = mNearByGroups.get(section);
		((HandyTextView) header
				.findViewById(R.id.nearby_group_header_htv_address))
				.setText(group.getAddress() + "    " + group.getDistance());
		((HandyTextView) header
				.findViewById(R.id.nearby_group_header_htv_count))
				.setText("显示全部" + group.getGroupCount() + "个群组");
	}

	@Override
	public int getPositionForSection(int section) {
		if (section < 0 || section >= mNearByGroups.size()) {
			return -1;
		}
		return mPositions[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= mCount) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions, position);
		return index >= 0 ? index : -index - 2;
	}

	@Override
	public Object[] getSections() {
		return mNearByGroups.toArray();
	}

	public void onPinnedHeaderClick(int position) {
		int realPosition = position - 1;
		int section = getSectionForPosition(realPosition);
		final NearByGroup group = mNearByGroups.get(section);
		System.out.println(group.getAddress() + "    " + group.getDistance());
	}

}
