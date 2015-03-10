package hillfly.wifichat.activity.maintabs;

import hillfly.wifichat.BaseFragment;
import hillfly.wifichat.R;
import hillfly.wifichat.activity.MainTabActivity;
import hillfly.wifichat.activity.message.ChatActivity;
import hillfly.wifichat.adapter.FriendsAdapter;
import hillfly.wifichat.bean.Users;
import hillfly.wifichat.socket.udp.IPMSGConst;
import hillfly.wifichat.view.MultiListView;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MessageFragment extends BaseFragment implements OnItemClickListener {

    private List<Users> mSessionPeoples; // 未读消息用户列表

    private MultiListView mListView;
    private FriendsAdapter mAdapter;
    private TextView mTvListEmpty;

    public MessageFragment() {

    }

    public MessageFragment(Context context) {
        super(context);
    }

    @Override
    public View
            onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    protected void initViews() {
        mListView = (MultiListView) findViewById(R.id.message_list);
        mTvListEmpty = (TextView) findViewById(R.id.message_empty);
    }

    @Override
    protected void initEvents() {
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mTvListEmpty);
    }

    @Override
    protected void init() {
        mSessionPeoples = mUDPListener.getUnReadPeopleList();
        mAdapter = new FriendsAdapter(getActivity(), mSessionPeoples);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        int position = (int) arg3;
        Users people = mSessionPeoples.get(position);
        mUDPListener.removeUnReadPeople(people); // 移除未读用户
        ((MainTabActivity) getActivity()).handler.sendEmptyMessage(IPMSGConst.IPMSG_READMSG);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Users.ENTITY_PEOPLE, people);
        startActivity(intent);
    }

    public void refreshAdapter() {
        mSessionPeoples = mUDPListener.getUnReadPeopleList();
        mAdapter.setData(mSessionPeoples);
        mAdapter.notifyDataSetChanged();
    }

    /** 设置显示起始位置 **/
    public void setLvSelection(int position) {
        mListView.setSelection(position);
    }
}
