package com.immomo.momo.android.activity.maintabs;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.BaseFragment;
import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.NearByGroupAdapter;
import com.immomo.momo.android.util.JsonResolveUtils;
import com.immomo.momo.android.view.MoMoRefreshExpandableList;
import com.immomo.momo.android.view.MoMoRefreshListView.OnCancelListener;
import com.immomo.momo.android.view.MoMoRefreshListView.OnRefreshListener;

public class NearByGroupFragment extends BaseFragment implements
		OnClickListener, OnItemClickListener, OnRefreshListener,
		OnCancelListener {
	private LinearLayout mLayoutCover;
	private MoMoRefreshExpandableList mMmrelvList;
	private NearByGroupAdapter mAdapter;

	public NearByGroupFragment() {
		super();
	}

	public NearByGroupFragment(BaseApplication application, Activity activity,
			Context context) {
		super(application, activity, context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_nearbygroup, container,
				false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void initViews() {
		mLayoutCover = (LinearLayout) findViewById(R.id.nearby_group_layout_cover);
		mMmrelvList = (MoMoRefreshExpandableList) findViewById(R.id.nearby_group_mmrelv_list);
	}

	@Override
	protected void initEvents() {
		mLayoutCover.setOnClickListener(this);
		mMmrelvList.setOnItemClickListener(this);
		mMmrelvList.setOnRefreshListener(this);
		mMmrelvList.setOnCancelListener(this);
	}

	@Override
	protected void init() {
		getGroups();
	}

	private void getGroups() {
		if (mApplication.mNearByGroups.isEmpty()) {
			putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					showLoadingDialog("正在加载,请稍后...");
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					return JsonResolveUtils.resolveNearbyGroup(mApplication);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					dismissLoadingDialog();
					if (!result) {
						showCustomToast("数据加载失败...");
					} else {
						mAdapter = new NearByGroupAdapter(mApplication,
								mContext, mApplication.mNearByGroups);
						mMmrelvList.setAdapter(mAdapter);
						mMmrelvList.setPinnedHeaderView(mActivity
								.getLayoutInflater().inflate(
										R.layout.include_nearby_group_header,
										mMmrelvList, false));
					}
				}
			});
		} else {
			mAdapter = new NearByGroupAdapter(mApplication, mContext,
					mApplication.mNearByGroups);
			mMmrelvList.setAdapter(mAdapter);
			mMmrelvList.setPinnedHeaderView(mActivity.getLayoutInflater()
					.inflate(R.layout.include_nearby_group_header, mMmrelvList,
							false));
		}
	}

	@Override
	public void onRefresh() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {

				}
				return null;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				mMmrelvList.onRefreshComplete();
			}
		});

	}

	@Override
	public void onCancel() {
		clearAsyncTask();
		mMmrelvList.onRefreshComplete();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void onClick(View v) {
		if (mMmrelvList.ismHeaderViewVisible()) {
			mAdapter.onPinnedHeaderClick(mMmrelvList.getFirstItemPosition());
		} else {
			mAdapter.onPinnedHeaderClick(1);
		}
	}
}
