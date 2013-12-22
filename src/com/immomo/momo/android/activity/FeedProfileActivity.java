package com.immomo.momo.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.FeedProfileCommentsAdapter;
import com.immomo.momo.android.adapter.SimpleListDialogAdapter;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.immomo.momo.android.entity.Feed;
import com.immomo.momo.android.entity.FeedComment;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.entity.NearByPeopleProfile;
import com.immomo.momo.android.popupwindow.OtherFeedListPopupWindow;
import com.immomo.momo.android.popupwindow.OtherFeedListPopupWindow.onOtherFeedListPopupItemClickListner;
import com.immomo.momo.android.util.DateUtils;
import com.immomo.momo.android.util.JsonResolveUtils;
import com.immomo.momo.android.util.PhotoUtils;
import com.immomo.momo.android.view.EmoteInputView;
import com.immomo.momo.android.view.EmoticonsEditText;
import com.immomo.momo.android.view.EmoticonsTextView;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;
import com.immomo.momo.android.view.HeaderLayout.onRightImageButtonClickListener;

@SuppressWarnings("deprecation")
public class FeedProfileActivity extends BaseActivity implements
		OnItemClickListener, onRightImageButtonClickListener,
		onOtherFeedListPopupItemClickListner, OnClickListener, OnTouchListener {

	private HeaderLayout mHeaderLayout;
	private ListView mLvList;
	private EmoticonsTextView mEtvEditerTitle;
	private ImageView mIvEmote;
	private Button mBtnSend;
	private EmoticonsEditText mEetEditer;

	private View mHeaderView;
	private ImageView mIvAvatar;
	private TextView mTvTime;
	private EmoticonsTextView mEtvName;
	private LinearLayout mLayoutGender;
	private ImageView mIvGender;
	private HandyTextView mHtvAge;
	private ImageView mIvVip;
	private ImageView mIvGroupRole;
	private ImageView mIvIndustry;
	private ImageView mIvWeibo;
	private ImageView mIvTxWeibo;
	private ImageView mIvRenRen;
	private ImageView mIvDevice;
	private ImageView mIvRelation;
	private ImageView mIvMultipic;
	private EmoticonsTextView mEtvContent;
	private ImageView mIvContent;
	private LinearLayout mLayoutComment;
	private TextView mTvCommentCount;
	private TextView mTvDistance;
	private RelativeLayout mLayoutLoading;
	private TextView mTvLoading;
	private ImageView mIvLoading;
	private EmoteInputView mInputView;

	private FeedProfileCommentsAdapter mAdapter;

	private NearByPeopleProfile mProfile;
	private NearByPeople mPeople;
	private Feed mFeed;
	private OtherFeedListPopupWindow mPopupWindow;
	private int mWidthAndHeight;
	private int mHeaderHeight;
	private SimpleListDialog mDialog;
	private Animation mLoadingAnimation;

	private List<FeedComment> mComments = new ArrayList<FeedComment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedprofile);
		initViews();
		initEvents();
		init();
	}

	@Override
	public void onBackPressed() {
		if (mInputView.isShown()) {
			mInputView.setVisibility(View.GONE);
		} else {
			finish();
		}
	}

	@Override
	protected void initViews() {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.feedprofile_header);
		mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
		mHeaderLayout.setTitleRightImageButton("留言内容", null,
				R.drawable.ic_topbar_more, this);
		mLvList = (ListView) findViewById(R.id.feedprofile_lv_list);
		mEtvEditerTitle = (EmoticonsTextView) findViewById(R.id.feedprofile_etv_editertitle);
		mIvEmote = (ImageView) findViewById(R.id.feedprofile_iv_emote);
		mBtnSend = (Button) findViewById(R.id.feedprofile_btn_send);
		mEetEditer = (EmoticonsEditText) findViewById(R.id.feedprofile_eet_editer);
		mInputView = (EmoteInputView) findViewById(R.id.feedprofile_eiv_input);

		mHeaderView = LayoutInflater.from(FeedProfileActivity.this).inflate(
				R.layout.header_feed, null);
		mIvAvatar = (ImageView) mHeaderView
				.findViewById(R.id.header_feed_iv_avatar);
		mTvTime = (TextView) mHeaderView.findViewById(R.id.header_feed_tv_time);
		mEtvName = (EmoticonsTextView) mHeaderView
				.findViewById(R.id.header_feed_etv_name);
		mLayoutGender = (LinearLayout) mHeaderView
				.findViewById(R.id.header_feed_layout_gender);
		mIvGender = (ImageView) mHeaderView
				.findViewById(R.id.header_feed_iv_gender);
		mHtvAge = (HandyTextView) mHeaderView
				.findViewById(R.id.header_feed_htv_age);
		mIvVip = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_vip);
		mIvGroupRole = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_group_role);
		mIvIndustry = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_industry);
		mIvWeibo = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_weibo);
		mIvTxWeibo = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_txweibo);
		mIvRenRen = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_renren);
		mIvDevice = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_device);
		mIvRelation = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_relation);
		mIvMultipic = (ImageView) mHeaderView
				.findViewById(R.id.user_item_iv_icon_multipic);
		mEtvContent = (EmoticonsTextView) mHeaderView
				.findViewById(R.id.header_feed_etv_content);
		mIvContent = (ImageView) mHeaderView
				.findViewById(R.id.header_feed_iv_content);
		mLayoutComment = (LinearLayout) mHeaderView
				.findViewById(R.id.header_feed_layout_comment);
		mTvCommentCount = (TextView) mHeaderView
				.findViewById(R.id.header_feed_tv_commentcount);
		mTvDistance = (TextView) mHeaderView
				.findViewById(R.id.header_feed_tv_distance);
		mLayoutLoading = (RelativeLayout) mHeaderView
				.findViewById(R.id.header_feed_layout_loading);
		mTvLoading = (TextView) mHeaderView
				.findViewById(R.id.header_feed_tv_loading);
		mIvLoading = (ImageView) mHeaderView
				.findViewById(R.id.header_feed_iv_loading);
	}

	@Override
	protected void initEvents() {
		mLvList.setOnItemClickListener(this);
		mLayoutComment.setOnClickListener(this);
		mEetEditer.setOnTouchListener(this);
		mBtnSend.setOnClickListener(this);
		mIvEmote.setOnClickListener(this);
	}

	private void init() {
		mProfile = getIntent().getParcelableExtra("entity_profile");
		mPeople = getIntent().getParcelableExtra("entity_people");
		mFeed = getIntent().getParcelableExtra("entity_feed");
		initPopupWindow();
		initHeaderView();
		mInputView.setEditText(mEetEditer);
		mLvList.addHeaderView(mHeaderView);
		mAdapter = new FeedProfileCommentsAdapter(mApplication,
				FeedProfileActivity.this, mComments);
		mLvList.setAdapter(mAdapter);
		getComments();
	}

	private void initPopupWindow() {
		mWidthAndHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 120, getResources()
						.getDisplayMetrics());
		mHeaderHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 48, getResources()
						.getDisplayMetrics());
		mPopupWindow = new OtherFeedListPopupWindow(this, mWidthAndHeight,
				mWidthAndHeight);
		mPopupWindow.setOnOtherFeedListPopupItemClickListner(this);
	}

	private void initHeaderView() {
		mIvAvatar.setImageBitmap(mApplication.getAvatar(mProfile.getAvatar()));
		mEtvName.setText(mProfile.getName());
		mTvTime.setText(mFeed.getTime());
		mLayoutGender.setBackgroundResource(mProfile.getGenderBgId());
		mIvGender.setImageResource(mProfile.getGenderId());
		mHtvAge.setText(mProfile.getAge() + "");
		if (mPeople.getIsVip() != 0) {
			mIvVip.setVisibility(View.VISIBLE);
		} else {
			mIvVip.setVisibility(View.GONE);
		}
		if (mPeople.getIsGroupRole() != 0) {
			mIvGroupRole.setVisibility(View.VISIBLE);
			if (mPeople.getIsGroupRole() == 1) {
				mIvGroupRole
						.setImageResource(R.drawable.ic_userinfo_groupowner);
			}
		} else {
			mIvIndustry.setVisibility(View.GONE);
		}
		if (!android.text.TextUtils.isEmpty(mPeople.getIndustry())) {
			mIvIndustry.setVisibility(View.VISIBLE);
			mIvIndustry.setImageBitmap(PhotoUtils.getIndustry(
					FeedProfileActivity.this, mPeople.getIndustry()));
		} else {
			mIvIndustry.setVisibility(View.GONE);
		}
		if (mPeople.getIsbindWeibo() != 0) {
			mIvWeibo.setVisibility(View.VISIBLE);
			if (mPeople.getIsbindWeibo() == 1) {
				mIvWeibo.setImageResource(R.drawable.ic_userinfo_weibov);
			}
		} else {
			mIvWeibo.setVisibility(View.GONE);
		}
		if (mPeople.getIsbindTxWeibo() != 0) {
			mIvTxWeibo.setVisibility(View.VISIBLE);
			if (mPeople.getIsbindTxWeibo() == 1) {
				mIvTxWeibo.setImageResource(R.drawable.ic_userinfo_tweibov);
			}
		} else {
			mIvTxWeibo.setVisibility(View.GONE);
		}

		if (mPeople.getIsbindRenRen() != 0) {
			mIvRenRen.setVisibility(View.VISIBLE);
		} else {
			mIvRenRen.setVisibility(View.GONE);
		}
		if (mPeople.getDevice() != 0) {
			mIvDevice.setVisibility(View.VISIBLE);
			if (mPeople.getDevice() == 1) {
				mIvDevice.setImageResource(R.drawable.ic_userinfo_android);
			}
			if (mPeople.getDevice() == 2) {
				mIvDevice.setImageResource(R.drawable.ic_userinfo_apple);
			}
		} else {
			mIvDevice.setVisibility(View.GONE);
		}
		if (mPeople.getIsRelation() != 0) {
			mIvRelation.setVisibility(View.VISIBLE);
		} else {
			mIvRelation.setVisibility(View.GONE);
		}
		if (mPeople.getIsMultipic() != 0) {
			mIvMultipic.setVisibility(View.VISIBLE);
		} else {
			mIvMultipic.setVisibility(View.GONE);
		}
		mEtvContent.setText(mFeed.getContent());
		if (mFeed.getContentImage() == null) {
			mIvContent.setVisibility(View.GONE);
		} else {
			mIvContent.setVisibility(View.VISIBLE);
			mIvContent.setImageBitmap(mApplication.getStatusPhoto(mFeed
					.getContentImage()));
		}
		mTvDistance.setText(mFeed.getSite());
		mTvCommentCount.setText(mFeed.getCommentCount() + "");
	}

	@Override
	public void onClick() {
		mPopupWindow.showAtLocation(mHeaderLayout, Gravity.RIGHT | Gravity.TOP,
				-10, mHeaderHeight + 10);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg3 != -1) {
			FeedComment comment = mComments.get((int) arg3);
			String[] codes = new String[] { "回复", "复制文本", "举报" };
			mDialog = new SimpleListDialog(this);
			mDialog.setTitle("提示");
			mDialog.setTitleLineVisibility(View.GONE);
			mDialog.setAdapter(new SimpleListDialogAdapter(this, codes));
			mDialog.setOnSimpleListItemClickListener(new OnReplyDialogItemClickListener(
					comment));
			mDialog.show();
		}
	}

	@Override
	public void onCopy(View v) {
		String text = mFeed.getContent();
		copy(text);
	}

	@Override
	public void onReport(View v) {
		report();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_feed_layout_comment:
			mEtvEditerTitle.setText(null);
			mEtvEditerTitle.setVisibility(View.GONE);
			mEetEditer.requestFocus();
			showKeyBoard();
			break;

		case R.id.feedprofile_iv_emote:
			mEetEditer.requestFocus();
			if (mInputView.isShown()) {
				showKeyBoard();
			} else {
				hideKeyBoard();
				mInputView.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.feedprofile_btn_send:
			String content = mEetEditer.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				showCustomToast("请输入评论内容");
				mEetEditer.requestFocus();
			} else {
				String reply = null;
				if (mEtvEditerTitle.isShown()) {
					reply = mEtvEditerTitle.getText().toString().trim();
				}
				content = TextUtils.isEmpty(reply) ? content : reply + content;
				FeedComment comment = new FeedComment("测试用户",
						"nearby_people_other", content, DateUtils.formatDate(
								FeedProfileActivity.this,
								System.currentTimeMillis()));
				mComments.add(0, comment);
				mAdapter.notifyDataSetChanged();
			}
			mEtvEditerTitle.setText(null);
			mEtvEditerTitle.setVisibility(View.GONE);
			mEetEditer.setText(null);
			mInputView.setVisibility(View.GONE);
			hideKeyBoard();
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (v.getId() == R.id.feedprofile_eet_editer) {
			showKeyBoard();
		}
		return false;
	}

	private void copy(String text) {
		ClipboardManager m = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		m.setText(text);
		showCustomToast("已成功复制文本");
		mEetEditer.requestFocus();
	}

	private void report() {
		String[] codes = getResources()
				.getStringArray(R.array.reportfeed_items);
		mDialog = new SimpleListDialog(this);
		mDialog.setTitle("举报留言");
		mDialog.setTitleLineVisibility(View.GONE);
		mDialog.setAdapter(new SimpleListDialogAdapter(this, codes));
		mDialog.setOnSimpleListItemClickListener(new OnReportDialogItemClickListener());
		mDialog.show();
	}

	private class OnReportDialogItemClickListener implements
			onSimpleListItemClickListener {

		@Override
		public void onItemClick(int position) {
			showLoadingDialog("正在提交,请稍后...");
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					dismissLoadingDialog();
					showCustomToast("举报的信息已提交");
				}
			}, 1500);
		}
	}

	private class OnReplyDialogItemClickListener implements
			onSimpleListItemClickListener {

		private FeedComment mComment;

		public OnReplyDialogItemClickListener(FeedComment comment) {
			mComment = comment;
		}

		@Override
		public void onItemClick(int position) {
			switch (position) {
			case 0:
				mEtvEditerTitle.setVisibility(View.VISIBLE);
				mEtvEditerTitle.setText("回复" + mComment.getName() + " :");
				mEetEditer.requestFocus();
				showKeyBoard();
				break;

			case 1:
				String text = mComment.getContent();
				copy(text);
				break;

			case 2:
				report();
				break;
			}
		}

	}

	private void startLoading() {
		if (!mLayoutLoading.isShown()) {
			mLayoutLoading.setVisibility(View.VISIBLE);
		}
		if (mIvLoading != null) {
			mIvLoading.setVisibility(View.VISIBLE);
			mTvLoading.setText("评论加载中");
			mLoadingAnimation = AnimationUtils.loadAnimation(
					FeedProfileActivity.this, R.anim.loading);
			mIvLoading.startAnimation(mLoadingAnimation);
		}
	}

	private void refreshCommentTitle() {
		if (mComments != null) {
			if (mComments.size() > 0 && !mAdapter.isEmpty()) {
				mLayoutLoading.setVisibility(View.GONE);
			} else {
				mIvLoading.clearAnimation();
				mIvLoading.setVisibility(View.GONE);
				mTvLoading.setText("暂无评论");
				mLayoutLoading.setVisibility(View.VISIBLE);
			}
		}
	}

	private void showKeyBoard() {
		if (mInputView.isShown()) {
			mInputView.setVisibility(View.GONE);
		}
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.showSoftInput(mEetEditer, 0);
	}

	private void hideKeyBoard() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(FeedProfileActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void getComments() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				startLoading();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return JsonResolveUtils.resoleFeedComment(
						FeedProfileActivity.this, mComments);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (!result) {
					showCustomToast("数据加载失败...");
					mLayoutLoading.setVisibility(View.GONE);
					mIvLoading.clearAnimation();
				} else {
					refreshCommentTitle();
					mAdapter.notifyDataSetChanged();
				}
			}

		});
	}

}
