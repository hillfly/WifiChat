package szu.wifichat.android.activity.maintabs;

import java.util.List;

import szu.wifichat.android.BaseApplication;
import szu.wifichat.android.BaseFragment;
import szu.wifichat.android.activity.message.ChatActivity;
import szu.wifichat.android.adapter.NearByPeopleAdapter;
import szu.wifichat.android.entity.NearByPeople;
import szu.wifichat.android.view.MoMoRefreshListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import szu.wifichat.android.R;

public class SessionPeopleFragment extends BaseFragment implements OnItemClickListener {

    private static List<NearByPeople> mSessionPeoples; // 未读消息用户列表

    private MoMoRefreshListView mMmrlvList;
    private NearByPeopleAdapter mAdapter;

    public SessionPeopleFragment() {
        super();
    }

    public SessionPeopleFragment(BaseApplication application, Activity activity, Context context) {
        super(application, activity, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_nearbypeople, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews() {
        mMmrlvList = (MoMoRefreshListView) findViewById(R.id.nearby_people_mmrlv_list);
    }

    @Override
    protected void initEvents() {
        mMmrlvList.setOnItemClickListener(this);
    }

    @Override
    protected void init() {
        mSessionPeoples = mApplication.getUnReadPeopleList();
        mAdapter = new NearByPeopleAdapter(mApplication, mContext, mSessionPeoples);
        mMmrlvList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        int position = (int) arg3;
        NearByPeople people = mSessionPeoples.get(position);
        mApplication.removeUnReadPeople(people); // 移除未读用户
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(NearByPeople.ENTITY_PEOPLE, people);
        startActivity(intent);
    }

    /** 刷新用户在线列表UI **/
    public void refreshAdapter() {
        mSessionPeoples = mApplication.getUnReadPeopleList();
        mAdapter.setData(mSessionPeoples); // Adapter加载List数据
        mAdapter.notifyDataSetChanged();
        mMmrlvList.setSelection(0);
    }

    /** 设置显示起始位置 **/
    public void setLvSelection(int position) {
        mMmrlvList.setSelection(position);
    }
}
