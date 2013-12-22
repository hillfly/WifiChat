package com.immomo.momo.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.message.ChatActivity;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.entity.NearByPeopleProfile;
import com.immomo.momo.android.util.JsonResolveUtils;
import com.immomo.momo.android.util.PhotoUtils;
import com.immomo.momo.android.view.EmoticonsTextView;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;
import com.immomo.momo.android.view.RoundImageView;
import com.immomo.momo.android.view.UserPhotosView;
import com.immomo.momo.android.view.UserPhotosView.onPagerPhotoItemClickListener;

public class OtherProfileActivity extends BaseActivity implements
		OnClickListener, onPagerPhotoItemClickListener {

	private HeaderLayout mHeaderLayout;// 标题栏
	private LinearLayout mLayoutChat;// 对话
	private LinearLayout mLayoutUnfollow;// 取消关注
	private LinearLayout mLayoutFollow;// 关注
	private LinearLayout mLayoutReport;// 拉黑/举报

	private UserPhotosView mUpvPhotos;// 照片

	private LinearLayout mLayoutGender;// 性别根布局
	private ImageView mIvGender;// 性别
	private HandyTextView mHtvAge;// 年龄
	private HandyTextView mHtvConstellation;// 星座
	private HandyTextView mHtvDistance;// 距离
	private HandyTextView mHtvTime;// 时间
	private RelativeLayout mLayoutFeed;// 状态根布局
	private LinearLayout mLayoutFeedPicture;// 状态图片布局
	private RoundImageView mRivFeedPicture;// 状态图片
	private HandyTextView mHtvFeedSignature;// 状态签名
	private HandyTextView mHtvFeedDistance;// 状态距离

	private RelativeLayout mLayoutVip;// VIP
	private LinearLayout mLayoutSign;// 签名
	private EmoticonsTextView mEtvSign;// 签名内容
	private ImageView mIvIndustryIcon;// 职业图标

	private LinearLayout mLayoutBindInfo;// 绑定
	private LinearLayout mLayoutBindSina;// 绑定新浪
	private LinearLayout mLayoutBindTx;// 绑定腾讯
	private LinearLayout mLayoutBindRenRen;// 绑定人人

	private LinearLayout mLayoutJoinGroup;// 群组

	private String mUid;// ID
	private String mName;// 姓名
	private String mAvatar;// 头像
	private NearByPeople mPeople;// 用户实体
	private NearByPeopleProfile mProfile;// 资料

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otherprofile);
		initViews();
		initEvents();
		init();
	}

	@Override
	protected void initViews() {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.otherprofile_header);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mLayoutChat = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_chat);
		mLayoutUnfollow = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_unfollow);
		mLayoutFollow = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_follow);
		mLayoutReport = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_report);

		mUpvPhotos = (UserPhotosView) findViewById(R.id.otherprofile_upv_photos);

		mLayoutGender = (LinearLayout) findViewById(R.id.otherprofile_layout_gender);
		mIvGender = (ImageView) findViewById(R.id.otherprofile_iv_gender);
		mHtvAge = (HandyTextView) findViewById(R.id.otherprofile_htv_age);
		mHtvConstellation = (HandyTextView) findViewById(R.id.otherprofile_htv_constellation);
		mHtvDistance = (HandyTextView) findViewById(R.id.otherprofile_htv_distance);
		mHtvTime = (HandyTextView) findViewById(R.id.otherprofile_htv_time);

		mLayoutFeed = (RelativeLayout) findViewById(R.id.otherprofile_layout_feed);
		mLayoutFeedPicture = (LinearLayout) findViewById(R.id.otherprofile_layout_feed_pic);
		mRivFeedPicture = (RoundImageView) findViewById(R.id.otherprofile_riv_feed_pic);
		mHtvFeedSignature = (HandyTextView) findViewById(R.id.otherprofile_htv_feed_sign);
		mHtvFeedDistance = (HandyTextView) findViewById(R.id.otherprofile_htv_feed_distance);

		mLayoutVip = (RelativeLayout) findViewById(R.id.otherprofile_info_layout_vip);
		mLayoutSign = (LinearLayout) findViewById(R.id.otherprofile_info_layout_sign);
		mEtvSign = (EmoticonsTextView) findViewById(R.id.otherprofile_info_htv_sign);
		mIvIndustryIcon = (ImageView) findViewById(R.id.industry_iv_icon);

		mLayoutBindInfo = (LinearLayout) findViewById(R.id.otherprofile_bindinfo_layout);
		mLayoutBindSina = (LinearLayout) findViewById(R.id.otherprofile_bindinfo_layout_sina);
		mLayoutBindTx = (LinearLayout) findViewById(R.id.otherprofile_bindinfo_layout_tx);
		mLayoutBindRenRen = (LinearLayout) findViewById(R.id.otherprofile_bindinfo_layout_renren);

		mLayoutJoinGroup = (LinearLayout) findViewById(R.id.otherprofile_joingroup_layout_container);
	}

	@Override
	protected void initEvents() {
		mLayoutChat.setOnClickListener(this);
		mLayoutFollow.setOnClickListener(this);
		mLayoutUnfollow.setOnClickListener(this);
		mLayoutReport.setOnClickListener(this);
		mUpvPhotos.setOnPagerPhotoItemClickListener(this);
		mLayoutFeed.setOnClickListener(this);
	}

	private void init() {
		mUid = getIntent().getStringExtra("uid");
		mName = getIntent().getStringExtra("name");
		mAvatar = getIntent().getStringExtra("avatar");
		mPeople = getIntent().getParcelableExtra("entity_people");
		mHeaderLayout.setDefaultTitle(mName, null);
		getProfile();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.otherprofile_bottom_layout_chat:
			intent = new Intent(OtherProfileActivity.this, ChatActivity.class);
			intent.putExtra("entity_people", mPeople);
			intent.putExtra("entity_profile", mProfile);
			startActivity(intent);
			break;

		case R.id.otherprofile_bottom_layout_unfollow:
			System.out.println("取消关注");
			break;

		case R.id.otherprofile_bottom_layout_follow:
			System.out.println("关注");
			break;

		case R.id.otherprofile_bottom_layout_report:
			System.out.println("拉黑/举报");
			break;

		case R.id.otherprofile_layout_feed:
			intent = new Intent(OtherProfileActivity.this,
					OtherFeedListActivity.class);
			intent.putExtra("entity_profile", mProfile);
			intent.putExtra("entity_people", mPeople);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		Intent intent = new Intent(OtherProfileActivity.this,
				ImageBrowserActivity.class);
		intent.putExtra(ImageBrowserActivity.IMAGE_TYPE,
				ImageBrowserActivity.TYPE_ALBUM);
		intent.putExtra("position", position);
		intent.putExtra("entity_profile", mProfile);
		startActivity(intent);
		overridePendingTransition(R.anim.zoom_enter, 0);
	}

	private void getProfile() {
		if (mProfile == null) {
			putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					showLoadingDialog("正在加载,请稍后...");
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					mProfile = new NearByPeopleProfile();
					return JsonResolveUtils.resolveNearbyProfile(
							OtherProfileActivity.this, mProfile, mUid);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					mProfile.setAvatar(mAvatar);// 在其他模拟用户中赋当前用户的头像
					dismissLoadingDialog();
					if (!result) {
						showCustomToast("数据加载失败...");
					} else {
						initProfile();
					}
				}

			});
		}
	}

	private void initProfile() {
		mLayoutGender.setBackgroundResource(mProfile.getGenderBgId());
		mIvGender.setImageResource(mProfile.getGenderId());
		mHtvAge.setText(mProfile.getAge() + "");
		mHtvConstellation.setText(mProfile.getConstellation());
		mHtvDistance.setText(mProfile.getDistance());
		mHtvTime.setText(mProfile.getTime());
		if (mProfile.isHasSign()) {
			mLayoutFeed.setVisibility(View.VISIBLE);
			mHtvFeedSignature.setText(mProfile.getSign());
			if (mProfile.getSignPicture() != null) {
				mLayoutFeedPicture.setVisibility(View.VISIBLE);
				mRivFeedPicture.setImageBitmap(
						mApplication.getStatusPhoto(mProfile.getSignPicture()),
						5);
			} else {
				mLayoutFeedPicture.setVisibility(View.GONE);
			}
			mHtvFeedDistance.setText("(" + mProfile.getSignDistance() + ")");
		} else {
			mLayoutFeed.setVisibility(View.GONE);
		}

		mUpvPhotos.setPhotos(mApplication, mProfile.getPhotos());

		if (mPeople.getIsVip() == 1) {
			mLayoutVip.setVisibility(View.VISIBLE);
		} else {
			mLayoutVip.setVisibility(View.GONE);
		}

		if (TextUtils.isEmpty(mPeople.getSign())) {
			mLayoutSign.setVisibility(View.GONE);
		} else {
			mLayoutSign.setVisibility(View.VISIBLE);
			mEtvSign.setText(mPeople.getSign());
		}
		mIvIndustryIcon.setImageBitmap(PhotoUtils.getIndustry(
				OtherProfileActivity.this, "医"));
		if (mPeople.getIsbindWeibo() == 0 && mPeople.getIsbindTxWeibo() == 0
				&& mPeople.getIsbindRenRen() == 0) {
			mLayoutBindInfo.setVisibility(View.GONE);
		} else {
			mLayoutBindInfo.setVisibility(View.VISIBLE);
			if (mPeople.getIsbindWeibo() != 0) {
				mLayoutBindSina.setVisibility(View.VISIBLE);
			}
			if (mPeople.getIsbindTxWeibo() != 0) {
				mLayoutBindTx.setVisibility(View.VISIBLE);
			}
			if (mPeople.getIsbindRenRen() != 0) {
				mLayoutBindRenRen.setVisibility(View.VISIBLE);
			}
		}
		initJoinGroup();
	}

	private void initJoinGroup() {
		View group = LayoutInflater.from(OtherProfileActivity.this).inflate(
				R.layout.otherprofile_joingroup_item, null);
		mLayoutJoinGroup.addView(group);
		ImageView mIvAvatar = (ImageView) group
				.findViewById(R.id.joingroup_item_avatar);
		EmoticonsTextView mEtvName = (EmoticonsTextView) group
				.findViewById(R.id.joingroup_item_name);
		HandyTextView mHtvOwner = (HandyTextView) group
				.findViewById(R.id.joingroup_item_owner);

		mIvAvatar.setImageBitmap(mApplication.getAvatar("nearby_group_1"));
		mEtvName.setText("℡一群二B的小青年");
		mHtvOwner.setVisibility(View.VISIBLE);
	}

}
