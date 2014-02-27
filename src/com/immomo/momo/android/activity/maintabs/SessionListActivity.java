package com.immomo.momo.android.activity.maintabs;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;

import com.immomo.momo.android.R;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;

public class SessionListActivity extends TabItemActivity {

    private HeaderLayout mHeaderLayout;
    private SessionPeopleFragment mPeopleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionlist);
        initViews();
        init();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.session_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("消息", null);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void init() {
        mPeopleFragment = new SessionPeopleFragment(mApplication, this, this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.session_layout_content, mPeopleFragment).commit();
    }

    @Override
    public void processMessage(Message msg) {
        mPeopleFragment.refreshAdapter();
    }

}
