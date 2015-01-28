package szu.wifichat.android.activity.wifiap;

import java.util.ArrayList;

import szu.wifichat.android.util.LogUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiapBroadcast extends BroadcastReceiver {

    private static final String TAG = "SZU_WifiapBroadcase";

    private static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
    private NetworkInfo mNetworkInfo;

    public void onReceive(Context paramContext, Intent paramIntent) {
      
        if (paramIntent.getAction().equals(    // 搜索到wifi热点
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            LogUtils.d(TAG, WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            for (int j = 0; j < ehList.size(); j++)
                ((EventHandler) ehList.get(j)).scanResultsAvailable();

           
        } else if (paramIntent.getAction().equals(    // wifi打开或关闭
                WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            LogUtils.d(TAG, WifiManager.WIFI_STATE_CHANGED_ACTION + " | "
                    + paramIntent.getIntExtra("wifi_state", -1) + " | "
                    + paramIntent.getIntExtra("previous_wifi_state", -1));
            for (int j = 0; j < ehList.size(); j++)
                ((EventHandler) ehList.get(j)).wifiStatusNotification();

           
        } else if (paramIntent.getAction().equals(   // 连接 SSID    		
                WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            mNetworkInfo = paramIntent.getParcelableExtra("networkInfo");
            LogUtils.d(TAG, WifiManager.NETWORK_STATE_CHANGED_ACTION + " | "
                    + mNetworkInfo.getDetailedState());

            // 当 DetailedState 变化为 CONNECTED 时，说明已连接成功，则通知Handler更新
            // 可避免WifiapActivity里出现重复获取IP的问题
            if (mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                for (int i = 0; i < ehList.size(); i++)
                    ((EventHandler) ehList.get(i)).handleConnectChange();
            }
        }

    }

    public void addehList(EventHandler paramEventHandler) {
        if (paramEventHandler != null) {
            ehList.add(paramEventHandler);
        }
    }

    public void removeehList(EventHandler paramEventHandler) {
        if (paramEventHandler != null) {
            ehList.remove(paramEventHandler);
        }
    }

    /** 使用Handler更新前台网络环境数据的接口 **/
    public static abstract interface EventHandler {

        /** Wifi连接发生变化时，更新前台数据 **/
        public abstract void handleConnectChange();

        /** 扫描热点结束，更新前台扫描结果 **/
        public abstract void scanResultsAvailable();

        /** Wifi状态更新 **/
        public abstract void wifiStatusNotification();
    }
}