package com.immomo.momo.android.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.BaseObjectListAdapter;
import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.FeedProfileActivity;
import com.immomo.momo.android.dialog.FlippingLoadingDialog;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.immomo.momo.android.entity.Entity;
import com.immomo.momo.android.entity.Feed;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.entity.NearByPeopleProfile;
import com.immomo.momo.android.popupwindow.OtherFeedListPopupWindow;
import com.immomo.momo.android.popupwindow.OtherFeedListPopupWindow.onOtherFeedListPopupItemClickListner;
import com.immomo.momo.android.view.EmoticonsTextView;
import com.immomo.momo.android.view.HandyTextView;

@SuppressWarnings("deprecation")
public class OtherFeedListAdapter extends BaseObjectListAdapter implements
		onSimpleListItemClickListener, onOtherFeedListPopupItemClickListner {

	private NearByPeopleProfile mProfile;
	private NearByPeople mPeople;
	private OtherFeedListPopupWindow mPopupWindow;
	private int mWidthAndHeight;
	private int mPosition;
	private SimpleListDialog mDialog;

	public OtherFeedListAdapter(NearByPeopleProfile profile,
			NearByPeople people, BaseApplication application, Context context,
			List<? extends Entity> datas) {
		super(application, context, datas);
		mProfile = profile;
		mPeople = people;
		mWidthAndHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 120, context.getResources()
						.getDisplayMetrics());
		mPopupWindow = new OtherFeedListPopupWindow(context, mWidthAndHeight,
				mWidthAndHeight);
		mPopupWindow.setOnOtherFeedListPopupItemClickListner(this);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_feed, null);
			holder = new ViewHolder();
			holder.root = (RelativeLayout) convertView
					.findViewById(R.id.feed_item_layout_root);
			holder.avatar = (ImageView) convertView
					.findViewById(R.id.feed_item_iv_avatar);
			holder.time = (HandyTextView) convertView
					.findViewById(R.id.feed_item_htv_time);
			holder.name = (HandyTextView) convertView
					.findViewById(R.id.feed_item_htv_name);
			holder.content = (EmoticonsTextView) convertView
					.findViewById(R.id.feed_item_etv_content);
			holder.contentImage = (ImageView) convertView
					.findViewById(R.id.feed_item_iv_content);
			holder.more = (ImageButton) convertView
					.findViewById(R.id.feed_item_ib_more);
			holder.comment = (LinearLayout) convertView
					.findViewById(R.id.feed_item_layout_comment);
			holder.commentCount = (HandyTextView) convertView
					.findViewById(R.id.feed_item_htv_commentcount);
			holder.site = (HandyTextView) convertView
					.findViewById(R.id.feed_item_htv_site);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Feed feed = (Feed) getItem(position);
		holder.avatar.setImageBitmap(mApplication.getAvatar(mProfile
				.getAvatar()));
		holder.name.setText(mProfile.getName());
		holder.time.setText(feed.getTime());
		holder.content.setText(feed.getContent());
		if (feed.getContentImage() == null) {
			holder.contentImage.setVisibility(View.GONE);
		} else {
			holder.contentImage.setVisibility(View.VISIBLE);
			holder.contentImage.setImageBitmap(mApplication.getStatusPhoto(feed
					.getContentImage()));
		}
		holder.site.setText(feed.getSite());
		holder.commentCount.setText(feed.getCommentCount() + "");
		holder.comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPosition = position;
				Intent intent = new Intent(mContext, FeedProfileActivity.class);
				intent.putExtra("entity_profile", mProfile);
				intent.putExtra("entity_people", mPeople);
				intent.putExtra("entity_feed", (Feed) getItem(mPosition));
				mContext.startActivity(intent);
			}
		});
		holder.more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPosition = position;
				int[] location = new int[2];
				arg0.getLocationOnScreen(location);
				mPopupWindow.showAtLocation(arg0, Gravity.NO_GRAVITY,
						location[0], location[1] - mWidthAndHeight + 30);
			}
		});
		holder.root.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPosition = position;
				Intent intent = new Intent(mContext, FeedProfileActivity.class);
				intent.putExtra("entity_profile", mProfile);
				intent.putExtra("entity_people", mPeople);
				intent.putExtra("entity_feed", (Feed) getItem(mPosition));
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		RelativeLayout root;
		ImageView avatar;
		HandyTextView time;
		HandyTextView name;
		EmoticonsTextView content;
		ImageView contentImage;
		ImageButton more;
		LinearLayout comment;
		HandyTextView commentCount;
		HandyTextView site;
	}

	@Override
	public void onItemClick(int position) {
		final FlippingLoadingDialog dialog = new FlippingLoadingDialog(
				mContext, "正在提交,请稍后...");
		dialog.show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				dialog.dismiss();
				showCustomToast("举报的信息已提交");
			}
		}, 1500);
	}

	@Override
	public void onCopy(View v) {
		Feed feed = (Feed) getItem(mPosition);
		String text = feed.getContent();
		ClipboardManager m = (ClipboardManager) mContext
				.getSystemService(Context.CLIPBOARD_SERVICE);
		m.setText(text);
		showCustomToast("已成功复制文本");
	}

	@Override
	public void onReport(View v) {
		String[] codes = mContext.getResources().getStringArray(
				R.array.reportfeed_items);
		mDialog = new SimpleListDialog(mContext);
		mDialog.setTitle("举报留言");
		mDialog.setTitleLineVisibility(View.GONE);
		mDialog.setAdapter(new SimpleListDialogAdapter(mContext, codes));
		mDialog.setOnSimpleListItemClickListener(this);
		mDialog.show();
	}
}
