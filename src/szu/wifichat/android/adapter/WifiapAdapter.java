package szu.wifichat.android.adapter;

import java.util.List;

import szu.wifichat.android.R;
import szu.wifichat.android.activity.WifiapActivity;
import szu.wifichat.android.activity.wifiap.WifiApConst;
import szu.wifichat.android.util.WifiUtils;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WifiapAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> mList;
    private Context mContext;
    private WifiUtils mWifiUtils;

    public WifiapAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
        this.mWifiUtils = WifiUtils.getInstance(mContext);
    }

    // 新加的一个函数，用来更新数据
    public void setData(List<String> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String apSSID = mList.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.activity_wifiap_item, null);
            viewHolder.textVName = ((TextView) convertView.findViewById(R.id.name_text_wtitem));
            viewHolder.textConnect = ((TextView) convertView.findViewById(R.id.connect_text_wtitem));
            viewHolder.linearLConnectOk = ((LinearLayout) convertView
                    .findViewById(R.id.connect_ok_layout_wtitem));
            viewHolder.progressBConnecting = ((ProgressBar) convertView
                    .findViewById(R.id.connecting_progressBar_wtitem));
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 点击连接处理事件
        viewHolder.textConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiConfiguration localWifiConfiguration = mWifiUtils.createWifiInfo(apSSID,
                        WifiApConst.WIFI_AP_PASSWORD, 3, "wt");
                mWifiUtils.addNetwork(localWifiConfiguration);
                viewHolder.textConnect.setVisibility(View.GONE);
                viewHolder.progressBConnecting.setVisibility(View.VISIBLE);
                viewHolder.linearLConnectOk.setVisibility(View.GONE);

                // Handler localHandler = ((WifiapActivity) mContext).handler;
                // localHandler.sendEmptyMessageDelayed(WifiApConst.ApConnected,
                // 3500L);
            }
        });
        // 点击断开处理事件
        viewHolder.linearLConnectOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int netWorkID = mWifiUtils.getWifiInfo().getNetworkId();
                mWifiUtils.disconnectWifi(netWorkID);
                mWifiUtils.removeNetwork(netWorkID);
                mWifiUtils.mWifiManager.saveConfiguration();

                viewHolder.textConnect.setVisibility(View.GONE);
                viewHolder.progressBConnecting.setVisibility(View.VISIBLE);
                viewHolder.linearLConnectOk.setVisibility(View.GONE);
                Handler localHandler = ((WifiapActivity) mContext).handler;
                localHandler.sendEmptyMessageDelayed(WifiApConst.ApConnected, 3500L);
            }
        });

        viewHolder.textConnect.setVisibility(View.GONE);
        viewHolder.progressBConnecting.setVisibility(View.GONE);
        viewHolder.linearLConnectOk.setVisibility(View.GONE);
        viewHolder.textVName.setText(apSSID);

        WifiUtils mWifiUtils = WifiUtils.getInstance(mContext);
        mWifiUtils.setNewWifiManagerInfo();

        if (!TextUtils.isEmpty(mWifiUtils.getSSID()) && mWifiUtils.getSSID().equals(apSSID)) {
            viewHolder.linearLConnectOk.setVisibility(View.VISIBLE);
            return convertView;
        }
        viewHolder.textConnect.setVisibility(View.VISIBLE);

        return convertView;
    }

    public final class ViewHolder {
        public LinearLayout linearLConnectOk;
        public ProgressBar progressBConnecting;
        public TextView textConnect;
        public TextView textVName;

        public ViewHolder() {
        }
    }
}
