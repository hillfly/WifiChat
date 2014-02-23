package com.immomo.momo.android.activity.maintabs;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

import com.immomo.momo.android.R;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintabs);
        initViews();
        initTabs();
    }

    private void initViews() {
        mTabHost = getTabHost(); // 从TabActivity上面获取放置Tab的TabHost
    }

    private void initTabs() {

        // from(MainTabActivity.this)从这个TabActivity获取LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(MainTabActivity.this);

        // 附近
        // common_bottombar_tab_nearby存放该Tab布局，inflate可将xml实例化成View
        View nearbyView = inflater.inflate(R.layout.common_bottombar_tab_nearby, null);

        // 创建TabHost.TabSpec的对象，并设置该对象的tag，最后关联该Tab的View
        TabHost.TabSpec nearbyTabSpec = mTabHost.newTabSpec(NearByActivity.class.getName())
                .setIndicator(nearbyView);
        nearbyTabSpec.setContent(new Intent(MainTabActivity.this, // 跳转activity
                NearByActivity.class));

        mTabHost.addTab(nearbyTabSpec); // 添加该Tab, addTab(TabHost.TabSpec
                                        // mTabSpec)

        // 消息
        View sessionListView = inflater.inflate(R.layout.common_bottombar_tab_chat, null);
        TabHost.TabSpec sessionListTabSpec = mTabHost.newTabSpec(
                SessionListActivity.class.getName()).setIndicator(sessionListView);
        sessionListTabSpec.setContent(new Intent(MainTabActivity.this, SessionListActivity.class));
        mTabHost.addTab(sessionListTabSpec);

        // 设置
        View userSettingView = inflater.inflate(R.layout.common_bottombar_tab_profile, null);
        TabHost.TabSpec userSettingTabSpec = mTabHost.newTabSpec(
                UserSettingActivity.class.getName()).setIndicator(userSettingView);
        userSettingTabSpec.setContent(new Intent(MainTabActivity.this, UserSettingActivity.class));
        mTabHost.addTab(userSettingTabSpec);

    }
}
