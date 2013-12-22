package com.immomo.momo.android.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.immomo.momo.android.BaseDialog;
import com.immomo.momo.android.R;
import com.immomo.momo.android.util.NetWorkUtils;
import com.immomo.momo.android.util.NetWorkUtils.NetWorkState;

public class WebDialog extends BaseDialog {

	private WebView mWebView;
	private View mLoadingView;

	private NetWorkUtils mNetWorkUtils;
	private OnWebDialogErrorListener mOnWebDialogErrorListener;

	public WebDialog(Context context) {
		super(context);
		mNetWorkUtils = new NetWorkUtils(context);
		setDialogContentView(R.layout.include_dialog_web);
		mLoadingView = findViewById(R.id.dialog_web_loading_indicator);
		mWebView = (WebView) findViewById(R.id.dialog_web_webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
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

	public void init(CharSequence title, CharSequence button1,
			OnClickListener listener) {
		super.setTitle(title);
		super.setButton1(button1, listener);
	}

	public void loadUrl(String url) {
		if (url == null) {
			if (mOnWebDialogErrorListener != null) {
				mOnWebDialogErrorListener.urlError();
			}
			return;
		}
		if (mNetWorkUtils.getConnectState() == NetWorkState.NONE) {
			if (mOnWebDialogErrorListener != null) {
				mOnWebDialogErrorListener.networkError();
			}
			return;
		}
		mWebView.loadUrl(url);
		mWebView.getSettings().setLayoutAlgorithm(
				WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	}

	public void setOnWebDialogErrorListener(OnWebDialogErrorListener listener) {
		mOnWebDialogErrorListener = listener;
	}

	public interface OnWebDialogErrorListener {

		void urlError();

		void networkError();
	}
}
