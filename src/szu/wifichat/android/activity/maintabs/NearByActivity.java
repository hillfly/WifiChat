package szu.wifichat.android.activity.maintabs;

import szu.wifichat.android.view.HeaderLayout;
import szu.wifichat.android.view.HeaderLayout.HeaderStyle;
import szu.wifichat.android.view.HeaderLayout.SearchState;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import szu.wifichat.android.R;

public class NearByActivity extends TabItemActivity {

    private HeaderLayout mHeaderLayout;
    private NearByPeopleFragment mPeopleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        initViews();
        initEvents();
        init();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.nearby_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(getString(R.string.maintab_text_nearby), null);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void init() {
        mPeopleFragment = new NearByPeopleFragment(mApplication, this, this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nearby_layout_content, mPeopleFragment).commit();
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
        }
        else {
            super.onBackPressed();
        }
    }

}
