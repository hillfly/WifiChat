package com.immomo.momo.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.immomo.momo.android.dialog.FlippingLoadingDialog;
import com.immomo.momo.android.util.NetWorkUtils;
import com.immomo.momo.android.view.HandyTextView;

public abstract class BaseFragment extends Fragment {
	protected BaseApplication mApplication;
	protected Activity mActivity;
	protected Context mContext;
	protected View mView;
	protected NetWorkUtils mNetWorkUtils;
	protected FlippingLoadingDialog mLoadingDialog;

	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;

	protected List<AsyncTask<Void, Void, Boolean>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();

	public BaseFragment() {
		super();
	}

	public BaseFragment(BaseApplication application, Activity activity,
			Context context) {
		mApplication = application;
		mActivity = activity;
		mContext = context;
		mNetWorkUtils = new NetWorkUtils(context);
		mLoadingDialog = new FlippingLoadingDialog(context, "请求提交中");
		/**
		 * 获取屏幕宽度、高度、密度
		 */
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initViews();
		initEvents();
		init();
		return mView;
	}

	@Override
	public void onDestroy() {
		clearAsyncTask();
		super.onDestroy();
	}

	protected abstract void initViews();

	protected abstract void initEvents();

	protected abstract void init();

	public View findViewById(int id) {
		return mView.findViewById(id);
	}

	protected void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Boolean>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}

	protected void showLoadingDialog(String text) {
		if (text != null) {
			mLoadingDialog.setText(text);
		}
		mLoadingDialog.show();
	}

	protected void dismissLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(mContext).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(mContext);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(mContext, cls);
		startActivity(intent);
	}
}
