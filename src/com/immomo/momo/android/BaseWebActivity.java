package com.immomo.momo.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BaseWebActivity extends BaseActivity {

	private View mLoadingView;
	protected WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baseweb);
		initViews();
		initEvents();
	}

	@Override
	protected void initViews() {
		mLoadingView = findViewById(R.id.baseweb_loading_indicator);
		mWebView = (WebView) findViewById(R.id.baseweb_webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
	}

	@Override
	protected void initEvents() {
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}

		});
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWebView.loadUrl(url);
				return true;
			}

			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, android.net.http.SslError error) {
				handler.proceed();
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				showProgress();
			}

			private void showProgress() {
				mLoadingView.setVisibility(View.VISIBLE);
			}

			private void dismissProgress() {
				mLoadingView.setVisibility(View.GONE);
			}

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				dismissProgress();
			}
		});
	}
}
