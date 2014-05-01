package szu.wifichat.android.activity.maintabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import szu.wifichat.android.BaseApplication;
import szu.wifichat.android.BaseFragment;
import szu.wifichat.android.activity.OtherProfileActivity;
import szu.wifichat.android.adapter.NearByPeopleAdapter;
import szu.wifichat.android.entity.NearByPeople;
import szu.wifichat.android.view.MoMoRefreshListView;
import szu.wifichat.android.view.MoMoRefreshListView.OnCancelListener;
import szu.wifichat.android.view.MoMoRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import szu.wifichat.android.R;

public class NearByPeopleFragment extends BaseFragment implements
		OnItemClickListener, OnRefreshListener, OnCancelListener {

	private static List<NearByPeople> mNearByPeoples; // 在线用户列表

	private MoMoRefreshListView mMmrlvList;
	private NearByPeopleAdapter mAdapter;

	public NearByPeopleFragment() {
		super();
	}

	public NearByPeopleFragment(BaseApplication application, Activity activity,
			Context context) {
		super(application, activity, context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_nearbypeople, container,
				false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void initViews() {
		mMmrlvList = (MoMoRefreshListView) findViewById(R.id.nearby_people_mmrlv_list);
	}

	@Override
	protected void initEvents() {
		mMmrlvList.setOnItemClickListener(this);
		mMmrlvList.setOnRefreshListener(this);
		mMmrlvList.setOnCancelListener(this);
	}

	@Override
	protected void init() {
		getPeoples();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = (int) arg3;
		NearByPeople people = mNearByPeoples.get(position);
		Intent intent = new Intent(mContext, OtherProfileActivity.class);
		intent.putExtra(NearByPeople.ENTITY_PEOPLE, people);
		startActivity(intent);
	}

	@Override
	public void onCancel() {
		clearAsyncTask();
		mMmrlvList.onRefreshComplete();
	}

	/**
	 * 将用户表HashMap转成ArrayList 以便加载ListView Adapter
	 * 
	 * @param application
	 */
	private void initMaptoList() {
		HashMap<String, NearByPeople> mMap = mApplication.getOnlineUserMap();
		Log.d("SZU_NearByPeopleFragment", "HashMap size:" + mMap.size());
		mNearByPeoples = new ArrayList<NearByPeople>(mMap.size());
		for (Map.Entry<String, NearByPeople> entry : mMap.entrySet()) {
			mNearByPeoples.add(entry.getValue());
		}
		Log.d("SZU_NearByPeopleFragment",
				"ArrayList size:" + mNearByPeoples.size());
	}

	/** 刷新用户在线列表UI **/
	public void refreshAdapter() {
		Log.i("SZU NearBypeopleFragment","refreshAdapter");
		mAdapter.setData(mNearByPeoples); // Adapter加载List数据
		mAdapter.notifyDataSetChanged();
	}

	/** 设置显示起始位置 **/
	public void setLvSelection(int position) {
		mMmrlvList.setSelection(position);
	}

	/** 获取在线用户 */
	private void getPeoples() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog("正在加载,请稍后...");
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				initMaptoList();
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				dismissLoadingDialog();
				if (result) {
					mAdapter = new NearByPeopleAdapter(mApplication, mContext,
							mNearByPeoples);
					mMmrlvList.setAdapter(mAdapter);
				} else {
					showCustomToast("数据加载失败...");
				}
			}

		});

	}

	/**
	 * 下拉刷新数据处理
	 * 
	 * @see szu.wifichat.android.view.MoMoRefreshListView.OnRefreshListener#onRefresh()
	 */
	@Override
	public void onRefresh() {
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(1000); // 停顿1S
					if (mApplication.getOnlineUserMap().isEmpty()) { // 若在线用户非空，则刷新
						return false;
					}
					initMaptoList();
					return true;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				mMmrlvList.onRefreshComplete();
				refreshAdapter();
				setLvSelection(0);
				super.onPostExecute(result);
			}
		});
	}

	public void onManualRefresh() {
		mMmrlvList.onManualRefresh();
	}
}
