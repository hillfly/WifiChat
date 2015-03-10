package hillfly.wifichat.activity.wifiap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiapBroadcast extends BroadcastReceiver {

    private NetworkInfo mNetworkInfo;
    private NetWorkChangeListener mListener;

    public WifiapBroadcast(NetWorkChangeListener listener) {
        mListener = listener;
    }

    public void onReceive(Context paramContext, Intent paramIntent) {

        // wifi开关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(paramIntent.getAction())) {
            int wifiState = paramIntent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                case WifiManager.WIFI_STATE_ENABLED:
                    mListener.wifiStatusChange();
                    break;
            }
        }

        // 连接wifi
        else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(paramIntent.getAction())) {
            mNetworkInfo = paramIntent.getParcelableExtra("networkInfo");

            /**
             * 当 DetailedState 变化为 CONNECTED 时，说明已连接成功，则通知Handler更新
             * 可避免WifiapActivity里出现重复获取IP的问题
             */
            if (mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                mListener.WifiConnected();
            }
        }

    }

    public static abstract interface NetWorkChangeListener {

        /** Wifi连接成功 **/
        public abstract void WifiConnected();
        
        /** Wifi状态改变 **/
        public abstract void wifiStatusChange();
    }
}