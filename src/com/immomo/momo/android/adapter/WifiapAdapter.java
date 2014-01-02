package com.immomo.momo.android.adapter;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.WifiapActivity;
import com.immomo.momo.android.util.WifiUtils;

public class WifiapAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ScanResult> mList;
    private Context mContext;
    private final String TAG = "SZU_WifiapAdapter";

    public WifiapAdapter(Context context, List<ScanResult> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    // 新加的一个函数，用来更新数据
    public void setData(List<ScanResult> list) {
        this.mList = list;
        Log.i(TAG, "m_listWifi size = " + mList.size());
        notifyDataSetChanged();
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
        final ScanResult localScanResult = mList.get(position);
        final WifiUtils wifiAdmin = WifiUtils.getInstance(mContext);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater
                    .inflate(R.layout.activity_wifiap_item, null);
            viewHolder.textVName = ((TextView) convertView
                    .findViewById(R.id.name_text_wtitem));
            viewHolder.textConnect = ((TextView) convertView
                    .findViewById(R.id.connect_text_wtitem));
            viewHolder.linearLConnectOk = ((LinearLayout) convertView
                    .findViewById(R.id.connect_ok_layout_wtitem));
            viewHolder.progressBConnecting = ((ProgressBar) convertView
                    .findViewById(R.id.connecting_progressBar_wtitem));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 点击连接处理事件
        viewHolder.textConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiConfiguration localWifiConfiguration = wifiAdmin
                        .createWifiInfo(localScanResult.SSID,
                                WifiapActivity.WIFI_AP_PASSWORD, 3, "wt");
                wifiAdmin.addNetwork(localWifiConfiguration);
                viewHolder.textConnect.setVisibility(View.GONE);
                viewHolder.progressBConnecting.setVisibility(View.VISIBLE);
                viewHolder.linearLConnectOk.setVisibility(View.GONE);
                Handler localHandler = ((WifiapActivity) mContext).handler;
                localHandler.sendEmptyMessageDelayed(
                        WifiapActivity.m_nApConnected, 3500L);
            }
        });
        // 点击断开处理事件
        viewHolder.linearLConnectOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                wifiAdmin
                        .disconnectWifi(wifiAdmin.getWifiInfo().getNetworkId());
                viewHolder.textConnect.setVisibility(View.GONE);
                viewHolder.progressBConnecting.setVisibility(View.VISIBLE);
                viewHolder.linearLConnectOk.setVisibility(View.GONE);
                Handler localHandler = ((WifiapActivity) mContext).handler;
                localHandler.sendEmptyMessageDelayed(
                        WifiapActivity.m_nApConnected, 3500L);
            }
        });

        viewHolder.textConnect.setVisibility(View.GONE);
        viewHolder.progressBConnecting.setVisibility(View.GONE);
        viewHolder.linearLConnectOk.setVisibility(View.GONE);
        viewHolder.textVName.setText(localScanResult.SSID);
        WifiInfo localWifiInfo = WifiUtils.getInstance(mContext).getWifiInfo();// 正连接的wifi信息
        if (localWifiInfo != null) {
            try {
                if ((localWifiInfo.getSSID() != null)
                        && (localWifiInfo.getSSID()
                                .equals(localScanResult.SSID))) {
                    viewHolder.linearLConnectOk.setVisibility(View.VISIBLE);
                    return convertView;
                }
            } catch (NullPointerException localNullPointerException) {
                localNullPointerException.printStackTrace();
                return convertView;
            }
            viewHolder.textConnect.setVisibility(View.VISIBLE);
        }

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
