package com.immomo.momo.android.activity;

import com.immomo.momo.android.R;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class FindPwdTabsActivity extends TabActivity {
	private HeaderLayout mHeaderLayout;
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwdtabs);
		initViews();
		initTabs();
	}

	protected void initViews() {
		mTabHost = getTabHost();
		mHeaderLayout = (HeaderLayout) findViewById(R.id.findpwdtabs_header);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle("找回密码", null);
	}

	protected void initTabs() {
		LayoutInflater inflater = LayoutInflater.from(FindPwdTabsActivity.this);
		View phoneView = inflater.inflate(
				R.layout.common_tabbar_item_lightblue, null);
		((HandyTextView) phoneView.findViewById(R.id.tabbar_item_htv_label))
				.setText("手机号码");
		TabHost.TabSpec phoneTabSpec = mTabHost.newTabSpec(
				FindPwdPhoneActivity.class.getName()).setIndicator(phoneView);
		phoneTabSpec.setContent(new Intent(FindPwdTabsActivity.this,
				FindPwdPhoneActivity.class));
		mTabHost.addTab(phoneTabSpec);

		View emailView = inflater.inflate(
				R.layout.common_tabbar_item_lightblue, null);
		((HandyTextView) emailView.findViewById(R.id.tabbar_item_htv_label))
				.setText("电子邮箱");
		emailView.findViewById(R.id.tabbar_item_ligthblue_driver_left)
				.setVisibility(View.VISIBLE);
		TabHost.TabSpec emailTabSpec = mTabHost.newTabSpec(
				FindPwdEmailActivity.class.getName()).setIndicator(emailView);
		emailTabSpec.setContent(new Intent(FindPwdTabsActivity.this,
				FindPwdEmailActivity.class));
		mTabHost.addTab(emailTabSpec);

	}
}
