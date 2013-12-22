package com.immomo.momo.android.activity.register;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.R;
import com.immomo.momo.android.dialog.EditTextDialog;
import com.immomo.momo.android.util.PhotoUtils;
import com.immomo.momo.android.view.HandyTextView;

public class StepPhoto extends RegisterStep implements OnClickListener {

	private HandyTextView mHtvRecommendation;
	private ImageView mIvUserPhoto;
	private LinearLayout mLayoutSelectPhoto;
	private LinearLayout mLayoutTakePicture;
	private LinearLayout mLayoutAvatars;

	private View[] mMemberBlocks;
	private String[] mAvatars = new String[] { "welcome_0", "welcome_1",
			"welcome_2", "welcome_3", "welcome_4", "welcome_5" };
	private String[] mDistances = new String[] { "0.84km", "1.02km", "1.34km",
			"1.88km", "2.50km", "2.78km" };
	private String mTakePicturePath;
	private Bitmap mUserPhoto;

	private EditTextDialog mEditTextDialog;

	public StepPhoto(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
		initAvatarsItem();
	}

	private void initAvatarsItem() {
		initMemberBlocks();
		for (int i = 0; i < mMemberBlocks.length; i++) {
			((ImageView) mMemberBlocks[i]
					.findViewById(R.id.welcome_item_iv_avatar))
					.setImageBitmap(getBaseApplication().getAvatar(mAvatars[i]));
			((HandyTextView) mMemberBlocks[i]
					.findViewById(R.id.welcome_item_htv_distance))
					.setText(mDistances[i]);
		}
	}

	private void initMemberBlocks() {
		mMemberBlocks = new View[6];
		mMemberBlocks[0] = findViewById(R.id.reg_photo_include_member_avatar_block0);
		mMemberBlocks[1] = findViewById(R.id.reg_photo_include_member_avatar_block1);
		mMemberBlocks[2] = findViewById(R.id.reg_photo_include_member_avatar_block2);
		mMemberBlocks[3] = findViewById(R.id.reg_photo_include_member_avatar_block3);
		mMemberBlocks[4] = findViewById(R.id.reg_photo_include_member_avatar_block4);
		mMemberBlocks[5] = findViewById(R.id.reg_photo_include_member_avatar_block5);

		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources()
						.getDisplayMetrics());
		int widthAndHeight = (getScreenWidth() - margin * 12) / 6;
		for (int i = 0; i < mMemberBlocks.length; i++) {
			ViewGroup.LayoutParams params = mMemberBlocks[i].findViewById(
					R.id.welcome_item_iv_avatar).getLayoutParams();
			params.width = widthAndHeight;
			params.height = widthAndHeight;
			mMemberBlocks[i].findViewById(R.id.welcome_item_iv_avatar)
					.setLayoutParams(params);
		}
		mLayoutAvatars.invalidate();
	}

	public void setUserPhoto(Bitmap bitmap) {
		if (bitmap != null) {
			mUserPhoto = bitmap;
			mIvUserPhoto.setImageBitmap(mUserPhoto);
			return;
		}
		showCustomToast("未获取到图片");
		mUserPhoto = null;
		mIvUserPhoto.setImageResource(R.drawable.ic_common_def_header);
	}

	public String getTakePicturePath() {
		return mTakePicturePath;
	}

	@Override
	public void initViews() {
		mHtvRecommendation = (HandyTextView) findViewById(R.id.reg_photo_htv_recommendation);
		mIvUserPhoto = (ImageView) findViewById(R.id.reg_photo_iv_userphoto);
		mLayoutSelectPhoto = (LinearLayout) findViewById(R.id.reg_photo_layout_selectphoto);
		mLayoutTakePicture = (LinearLayout) findViewById(R.id.reg_photo_layout_takepicture);
		mLayoutAvatars = (LinearLayout) findViewById(R.id.reg_photo_layout_avatars);
	}

	@Override
	public void initEvents() {
		mHtvRecommendation.setOnClickListener(this);
		mLayoutSelectPhoto.setOnClickListener(this);
		mLayoutTakePicture.setOnClickListener(this);
	}

	@Override
	public boolean validate() {
		if (mUserPhoto == null) {
			showCustomToast("请添加头像");
			return false;
		}
		return true;
	}

	@Override
	public void doNext() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog("请稍后,正在提交...");
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
					return true;
				} catch (InterruptedException e) {

				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				dismissLoadingDialog();
				if (result) {
					mActivity.finish();
				}
			}

		});
	}

	@Override
	public boolean isChange() {
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_photo_htv_recommendation:
			mEditTextDialog = new EditTextDialog(mContext);
			mEditTextDialog.setTitle("填写推荐人");
			mEditTextDialog.setButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mEditTextDialog.cancel();
						}
					}, "确认", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String text = mEditTextDialog.getText();
							if (text == null) {
								mEditTextDialog.requestFocus();
								showCustomToast("请输入推荐人号码");
							} else {
								mEditTextDialog.dismiss();
								showCustomToast("您输入的推荐人号码为:" + text);
							}
						}
					});
			mEditTextDialog.show();
			break;

		case R.id.reg_photo_layout_selectphoto:
			PhotoUtils.selectPhoto(mActivity);
			break;

		case R.id.reg_photo_layout_takepicture:
			mTakePicturePath = PhotoUtils.takePicture(mActivity);
			break;
		}
	}

}
