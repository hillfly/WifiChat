package hillfly.wifichat;

import hillfly.wifichat.dialog.FlippingLoadingDialog;
import hillfly.wifichat.socket.udp.UDPMessageListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
    protected UDPMessageListener mUDPListener;
    protected View mView;
    protected FlippingLoadingDialog mLoadingDialog;

    protected List<AsyncTask<Void, Void, Boolean>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();

    public BaseFragment() {
        super();
    }

    public BaseFragment(Context context) {
        mUDPListener = UDPMessageListener.getInstance(context);
    }

    @Override
    public View
            onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initViews();
        initEvents();
        init();
        return mView;
    }

    @Override
    public void onDestroy() {
        clearAsyncTask();
        mUDPListener = null;
        mLoadingDialog = null;
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

    public void clearAsyncTask() {
        Iterator<AsyncTask<Void, Void, Boolean>> iterator = mAsyncTasks.iterator();
        while (iterator.hasNext()) {
            AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();
            if (asyncTask != null && !asyncTask.isCancelled()) {
                asyncTask.cancel(true);
            }
        }
        mAsyncTasks.clear();
    }

    protected void showLoadingDialog(String text) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new FlippingLoadingDialog(getActivity(), "请求提交中");
        }
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

    /** 短暂显示Toast提示(来自res) **/
    protected void showShortToast(int resId) {
        Toast.makeText(getActivity(), getString(resId), Toast.LENGTH_SHORT).show();
    }

    protected void showShortToast(CharSequence charStr) {
        Toast.makeText(getActivity(), charStr, Toast.LENGTH_SHORT).show();
    }

    /** 通过Class跳转界面 **/
    protected void startActivity(Context context, Class<?> cls) {
        Intent in = new Intent(context, cls);
        startActivity(in);
    }
}
