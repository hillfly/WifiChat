package com.immomo.momo.android.activity.maintabs;

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
import com.immomo.momo.android.util.JsonResolveUtils;
import com.immomo.momo.android.view.MoMoRefreshListView;
import com.immomo.momo.android.view.MoMoRefreshListView.OnCancelListener;
import com.immomo.momo.android.view.MoMoRefreshListView.OnRefreshListener;

public class NearByPeopleFragment extends BaseFragment implements
        OnItemClickListener, OnRefreshListener, OnCancelListener {

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
        NearByPeople people = mApplication.mNearByPeoples.get(position);
        String uid = null;
        String name = null;
        String avatar = null;
        if (position > 3) {
            uid = "momo_p_other";
        } else {
            uid = people.getUid();
        }
        name = people.getName();
        avatar = people.getAvatar();
        Intent intent = new Intent(mContext, OtherProfileActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("name", name);
        intent.putExtra("avatar", avatar);
        intent.putExtra("entity_people", people);
        startActivity(intent);
    }

    private void getPeoples() {
        if (mApplication.mNearByPeoples.isEmpty()) {
            putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showLoadingDialog("正在加载,请稍后...");
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    return JsonResolveUtils.resolveNearbyPeople(mApplication);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    dismissLoadingDialog();
                    if (!result) {
                        showCustomToast("数据加载失败...");
                    } else {
                        mAdapter = new NearByPeopleAdapter(mApplication,
                                mContext, mApplication.mNearByPeoples);
                        mMmrlvList.setAdapter(mAdapter);
                    }
                }

            });
        } else {
            mAdapter = new NearByPeopleAdapter(mApplication, mContext,
                    mApplication.mNearByPeoples);
            mMmrlvList.setAdapter(mAdapter);
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
