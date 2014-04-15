package com.immomo.momo.android.activity.maintabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow.OnDismissListener;

import com.immomo.momo.android.BasePopupWindow.onSubmitClickListener;
import com.immomo.momo.android.R;
import com.immomo.momo.android.popupwindow.NearByPopupWindow;
import com.immomo.momo.android.socket.IPMSGConst;
import com.immomo.momo.android.util.WifiUtils;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;
import com.immomo.momo.android.view.HeaderLayout.SearchState;
import com.immomo.momo.android.view.HeaderLayout.onMiddleImageButtonClickListener;
import com.immomo.momo.android.view.HeaderLayout.onSearchListener;
import com.immomo.momo.android.view.HeaderSpinner;
import com.immomo.momo.android.view.HeaderSpinner.onSpinnerClickListener;
import com.immomo.momo.android.view.SwitcherButton.SwitcherButtonState;
import com.immomo.momo.android.view.SwitcherButton.onSwitcherButtonClickListener;

public class NearByActivity extends TabItemActivity {

	private HeaderLayout mHeaderLayout;
	private HeaderSpinner mHeaderSpinner;
	private NearByPeopleFragment mPeopleFragment;
	private NearByGroupFragment mGroupFragment;

	private NearByPopupWindow mPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby);
		initPopupWindow();
		initViews();
		initEvents();
		init();
	}

	@Override
	// 初始化顶栏
	protected void initViews() {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.nearby_header);
		mHeaderLayout.initSearch(new OnSearchClickListener());
		mHeaderSpinner = mHeaderLayout.setTitleNearBy("附近",
				new OnSpinnerClickListener(), "群组",
				R.drawable.ic_topbar_search,
				new OnMiddleImageButtonClickListener(), "个人", "群组",
				new OnSwitcherButtonClickListener());
		mHeaderLayout.init(HeaderStyle.TITLE_NEARBY_PEOPLE);
	}

	@Override
	protected void initEvents() {

	}

	@Override
	protected void init() {
		mPeopleFragment = new NearByPeopleFragment(mApplication, this, this);
		mGroupFragment = new NearByGroupFragment(mApplication, this, this);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.nearby_layout_content, mPeopleFragment).commit();
	}

	// "附近"弹出框
	private void initPopupWindow() {
		mPopupWindow = new NearByPopupWindow(this);
		mPopupWindow.setOnSubmitClickListener(new onSubmitClickListener() {

			@Override
			public void onClick() { // 刷新列表
				mPeopleFragment.onManualRefresh();
			}
		});
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() { // 弹出框被关闭时的相关操作
				mHeaderSpinner.initSpinnerState(false);
			}
		});
	}

	public class OnSpinnerClickListener implements onSpinnerClickListener {

		@Override
		public void onClick(boolean isSelect) {
			if (isSelect) { // 打开弹出框
				mPopupWindow
						.showViewTopCenter(findViewById(R.id.nearby_layout_root));
			} else {
				mPopupWindow.dismiss(); // 关闭弹出框
			}
		}
	}

	public class OnSearchClickListener implements onSearchListener {

		@Override
		public void onSearch(EditText et) {
			String s = et.getText().toString().trim();
			if (TextUtils.isEmpty(s)) {
				showCustomToast("请输入搜索关键字");
				et.requestFocus();
			} else {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(NearByActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						mHeaderLayout.changeSearchState(SearchState.SEARCH);
					}

					@Override
					protected Boolean doInBackground(Void... params) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return false;
					}

					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						mHeaderLayout.changeSearchState(SearchState.INPUT);
						showCustomToast("未找到搜索的群");
					}
				});
			}
		}

	}

	public class OnMiddleImageButtonClickListener implements
			onMiddleImageButtonClickListener {

		@Override
		public void onClick() {
			mHeaderLayout.showSearch(); // 打开搜索栏
		}
	}

	public class OnSwitcherButtonClickListener implements
			onSwitcherButtonClickListener {

		/**
		 * 个人、群组切换
		 * 
		 * @param state
		 *            : LEFT RIGHT
		 */
		@Override
		public void onClick(SwitcherButtonState state) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.setCustomAnimations(R.anim.fragment_fadein,
					R.anim.fragment_fadeout);
			switch (state) {
			case LEFT:
				mHeaderLayout.init(HeaderStyle.TITLE_NEARBY_PEOPLE);
				ft.replace(R.id.nearby_layout_content, mPeopleFragment)
						.commit();
				break;

			case RIGHT:
				mHeaderLayout.init(HeaderStyle.TITLE_NEARBY_GROUP);
				ft.replace(R.id.nearby_layout_content, mGroupFragment).commit();
				break;
			}
		}

	}

	@Override
	public void processMessage(android.os.Message msg) {
			mPeopleFragment.refreshAdapter();
	}

	@Override
	public void onBackPressed() {
		if (mHeaderLayout.searchIsShowing()) {
			clearAsyncTask();
			mHeaderLayout.dismissSearch();
			mHeaderLayout.clearSearch();
			mHeaderLayout.changeSearchState(SearchState.INPUT);
		} else {
			super.onBackPressed();
		}
	}

}
