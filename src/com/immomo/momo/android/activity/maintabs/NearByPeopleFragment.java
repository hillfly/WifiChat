package com.immomo.momo.android.activity.maintabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.BaseFragment;
import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.OtherProfileActivity;
import com.immomo.momo.android.adapter.NearByPeopleAdapter;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.view.MoMoRefreshListView;
import com.immomo.momo.android.view.MoMoRefreshListView.OnCancelListener;
import com.immomo.momo.android.view.MoMoRefreshListView.OnRefreshListener;

public class NearByPeopleFragment extends BaseFragment implements
        OnItemClickListener, OnRefreshListener, OnCancelListener {

    private static List<NearByPeople> mNearByPeoples;

    private MoMoRefreshListView mMmrlvList;
    private NearByPeopleAdapter mAdapter;

    public NearByPeopleFragment() {
        super();
    }

    public NearByPeopleFragment(BaseApplication application, Activity activity, Context context) {
        super(application, activity, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    private void getPeoples() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在加载,请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                refreshUserList(mApplication);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (!result) {
                    showCustomToast("数据加载失败...");
                } else {
                    mAdapter = new NearByPeopleAdapter(mApplication, mContext, mNearByPeoples);
                    mMmrlvList.setAdapter(mAdapter);
                }
            }

        });

    }

    /**
     * 将HashMap转成ArrayList 以便ListView刷新
     * 
     * @param application
     */
    private void refreshUserList(BaseApplication application) {
        Map<String, NearByPeople> mMap = application.OnlineUsers;
        mNearByPeoples = new ArrayList<NearByPeople>(mMap.size());
        for (Map.Entry<String, NearByPeople> entry : mMap.entrySet()) {
            mNearByPeoples.add(entry.getValue());
        }
    }

    @Override
    public void onCancel() {
        clearAsyncTask();
        mMmrlvList.onRefreshComplete();
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
                mMmrlvList.onRefreshComplete();
            }
        });
    }

    public void onManualRefresh() {
        mMmrlvList.onManualRefresh();
    }
}
