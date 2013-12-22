package com.immomo.momo.android.activity;

import android.os.Bundle;
import android.webkit.WebSettings;

import com.immomo.momo.android.BaseWebActivity;
import com.immomo.momo.android.jni.JniManager;
import com.immomo.momo.android.util.NetWorkUtils.NetWorkState;

public class HelpActivity extends BaseWebActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mNetWorkUtils.getConnectState() != NetWorkState.NONE) {
			mWebView.loadUrl(JniManager.getInstance().getHelpUrl());
			mWebView.getSettings().setLayoutAlgorithm(
					WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		} else {
			showCustomToast("当前网络不可用,请检查");
		}
	}

	@Override
	protected void onResume() {
		AboutTabsActivity.mHeaderLayout.setDefaultTitle("用户帮助", null);
		super.onResume();
	}
}
