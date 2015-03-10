package hillfly.wifichat.activity.maintabs;

import hillfly.wifichat.BaseFragment;
import hillfly.wifichat.R;
import hillfly.wifichat.activity.message.ChatActivity;
import hillfly.wifichat.adapter.FriendsAdapter;
import hillfly.wifichat.bean.Users;
import hillfly.wifichat.view.MultiListView;
import hillfly.wifichat.view.MultiListView.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsFragment extends BaseFragment implements OnItemClickListener, OnRefreshListener {

    private static List<Users> mUsersList; // 在线用户列表

    private MultiListView mListView;
    private FriendsAdapter mAdapter;

    public FriendsFragment() {
    }

    public FriendsFragment(Context context) {
        super(context);
    }

    @Override
    public View
            onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friends, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void initViews() {
        mListView = (MultiListView) findViewById(R.id.friends_list);
    }

    @Override
    protected void initEvents() {
        mAdapter = new FriendsAdapter(getActivity(), mUsersList);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(this);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void init() {
    }

    @Override
    public void onRefresh() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mUDPListener.refreshUsers();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mListView.onRefreshComplete();

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Users people = mUsersList.get((int) id);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Users.ENTITY_PEOPLE, people);
        startActivity(intent);
    }

    /**
     * 将用户表HashMap转成ArrayList
     * 
     * @param application
     */
    public void initMaptoList() {
        HashMap<String, Users> mMap = mUDPListener.getOnlineUserMap();
        mUsersList = new ArrayList<Users>(mMap.size());
        for (Map.Entry<String, Users> entry : mMap.entrySet()) {
            mUsersList.add(entry.getValue());
        }
    }

    /** 刷新用户在线列表UI **/
    public void refreshAdapter() {
        mAdapter.setData(mUsersList); // Adapter加载List数据
        mAdapter.notifyDataSetChanged();
    }

    /** 设置显示起始位置 **/
    public void setLvSelection(int position) {
        mListView.setSelection(position);
    }

}
