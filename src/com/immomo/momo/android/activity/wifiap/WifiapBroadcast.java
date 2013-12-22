package com.immomo.momo.android.activity.wifiap;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WifiapBroadcast extends BroadcastReceiver {
	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

	public void onReceive(Context paramContext, Intent paramIntent) {
		if (paramIntent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
			Log.i("WTScanResults", "android.net.wifi.SCAN_RESULTS");
			for (int j = 0; j < ehList.size(); j++)
				((EventHandler) ehList.get(j)).scanResultsAvailable();
		} else if (paramIntent.getAction().equals(
				"android.net.wifi.WIFI_STATE_CHANGED")) {
			Log.e("WTScanResults", "android.net.wifi.WIFI_STATE_CHANGED");
			for (int j = 0; j < ehList.size(); j++)
				((EventHandler) ehList.get(j)).wifiStatusNotification();
		} else if (paramIntent.getAction().equals(
				"android.net.wifi.STATE_CHANGE")) {
			Log.e("WTScanResults", "android.net.wifi.STATE_CHANGE");
			for (int i = 0; i < ehList.size(); i++)
				((EventHandler) ehList.get(i)).handleConnectChange();
		}
	}

	public static abstract interface EventHandler {
		public abstract void handleConnectChange();

		public abstract void scanResultsAvailable();

		public abstract void wifiStatusNotification();
	}
}