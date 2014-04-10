package com.immomo.momo.android.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Wifi 工具类
 * 
 * 封装了Wifi的基础操作方法，方便获取Wifi连接信息以及操作Wifi
 */

public class WifiUtils {
	private static WifiUtils wiFiAdmin = null;
	private List<WifiConfiguration> mWifiConfiguration;
	private WifiInfo mWifiInfo;
	private DhcpInfo mDhcpInfo;
	private List<ScanResult> mWifiList;
	WifiManager.WifiLock mWifiLock;
	public WifiManager mWifiManager;

	private WifiUtils(Context paramContext) {
		mWifiManager = (WifiManager) paramContext
				.getSystemService(Context.WIFI_SERVICE);
		mDhcpInfo = mWifiManager.getDhcpInfo();
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public static WifiUtils getInstance(Context paramContext) {
		if (wiFiAdmin == null)
			wiFiAdmin = new WifiUtils(paramContext);
		return wiFiAdmin;
	}

	private WifiConfiguration isExsits(String paramString) {
		Iterator<WifiConfiguration> localIterator = mWifiManager
				.getConfiguredNetworks().iterator();
		WifiConfiguration localWifiConfiguration;
		do {
			if (!localIterator.hasNext())
				return null;
			localWifiConfiguration = (WifiConfiguration) localIterator.next();
		} while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
		return localWifiConfiguration;
	}

	public void AcquireWifiLock() {
		mWifiLock.acquire();
	}

	public void CreatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	public void OpenWifi() {
		if (!mWifiManager.isWifiEnabled())
			mWifiManager.setWifiEnabled(true);
	}

	public void ReleaseWifiLock() {
		if (mWifiLock.isHeld())
			mWifiLock.release();
	}

	public void addNetwork(WifiConfiguration paramWifiConfiguration) {
		int i = mWifiManager.addNetwork(paramWifiConfiguration);
		mWifiManager.enableNetwork(i, true);
	}

	public void closeWifi() {
		mWifiManager.setWifiEnabled(false);
	}

	public void connectConfiguration(int paramInt) {
		if (paramInt > mWifiConfiguration.size())
			return;
		mWifiManager
				.enableNetwork(
						((WifiConfiguration) mWifiConfiguration.get(paramInt)).networkId,
						true);
	}

	public void removeNetwork(int netId) {
		if (mWifiManager != null) {
			mWifiManager.removeNetwork(netId);
		}
	}

	public void createWiFiAP(WifiConfiguration paramWifiConfiguration,
			boolean paramBoolean) {
		try {
			Class<? extends WifiManager> localClass = mWifiManager.getClass();
			Class[] arrayOfClass = new Class[2];
			arrayOfClass[0] = WifiConfiguration.class;
			arrayOfClass[1] = Boolean.TYPE;
			Method localMethod = localClass.getMethod("setWifiApEnabled",
					arrayOfClass);
			WifiManager localWifiManager = mWifiManager;
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = paramWifiConfiguration;
			arrayOfObject[1] = Boolean.valueOf(paramBoolean);
			localMethod.invoke(localWifiManager, arrayOfObject);
			return;
		} catch (Exception localException) {
		}
	}

	public WifiConfiguration createWifiInfo(String ssid, String paramString2,
			int paramInt, String paramString3) {
		WifiConfiguration localWifiConfiguration1 = new WifiConfiguration();
		localWifiConfiguration1.allowedAuthAlgorithms.clear();
		localWifiConfiguration1.allowedGroupCiphers.clear();
		localWifiConfiguration1.allowedKeyManagement.clear();
		localWifiConfiguration1.allowedPairwiseCiphers.clear();
		localWifiConfiguration1.allowedProtocols.clear();
		if ("wt".equals(paramString3)) {
			localWifiConfiguration1.SSID = ("\"" + ssid + "\"");
			WifiConfiguration localWifiConfiguration2 = isExsits(ssid);
			if (localWifiConfiguration2 != null)
				removeNetwork(localWifiConfiguration2.networkId);
			if (paramInt == 1) {
				localWifiConfiguration1.wepKeys[0] = "";
				localWifiConfiguration1.allowedKeyManagement.set(0);
				localWifiConfiguration1.wepTxKeyIndex = 0;
			} else if (paramInt == 2) {
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.wepKeys[0] = ("\"" + paramString2 + "\"");
			} else {
				localWifiConfiguration1.preSharedKey = ("\"" + paramString2 + "\"");
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.allowedAuthAlgorithms.set(0);
				localWifiConfiguration1.allowedGroupCiphers.set(2);
				localWifiConfiguration1.allowedKeyManagement.set(1);
				localWifiConfiguration1.allowedPairwiseCiphers.set(1);
				localWifiConfiguration1.allowedGroupCiphers.set(3);
				localWifiConfiguration1.allowedPairwiseCiphers.set(2);
			}
		} else {
			localWifiConfiguration1.SSID = ssid;
			localWifiConfiguration1.allowedAuthAlgorithms.set(1);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP40);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			localWifiConfiguration1.allowedKeyManagement.set(0);
			localWifiConfiguration1.wepTxKeyIndex = 0;
			if (paramInt == 1) {
				localWifiConfiguration1.wepKeys[0] = "";
				localWifiConfiguration1.allowedKeyManagement.set(0);
				localWifiConfiguration1.wepTxKeyIndex = 0;
			} else if (paramInt == 2) {
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.wepKeys[0] = paramString2;
			} else if (paramInt == 3) {
				localWifiConfiguration1.preSharedKey = paramString2;
				localWifiConfiguration1.allowedAuthAlgorithms.set(0);
				localWifiConfiguration1.allowedProtocols.set(1);
				localWifiConfiguration1.allowedProtocols.set(0);
				localWifiConfiguration1.allowedKeyManagement.set(1);
				localWifiConfiguration1.allowedPairwiseCiphers.set(2);
				localWifiConfiguration1.allowedPairwiseCiphers.set(1);
			}
		}
		return localWifiConfiguration1;
	}

	public void disconnectWifi(int paramInt) {
		mWifiManager.disableNetwork(paramInt);
	}

	public String getApSSID() {
		try {
			Method localMethod = mWifiManager.getClass().getDeclaredMethod(
					"getWifiApConfiguration", new Class[0]);
			if (localMethod == null)
				return null;
			Object localObject1 = localMethod.invoke(mWifiManager,
					new Object[0]);
			if (localObject1 == null)
				return null;
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
			if (localWifiConfiguration.SSID != null)
				return localWifiConfiguration.SSID;
			Field localField1 = WifiConfiguration.class
					.getDeclaredField("mWifiApProfile");
			if (localField1 == null)
				return null;
			localField1.setAccessible(true);
			Object localObject2 = localField1.get(localWifiConfiguration);
			localField1.setAccessible(false);
			if (localObject2 == null)
				return null;
			Field localField2 = localObject2.getClass()
					.getDeclaredField("SSID");
			localField2.setAccessible(true);
			Object localObject3 = localField2.get(localObject2);
			if (localObject3 == null)
				return null;
			localField2.setAccessible(false);
			String str = (String) localObject3;
			return str;
		} catch (Exception localException) {
		}
		return null;
	}

	public String getBSSID() {
		if (mWifiInfo == null)
			return "NULL";
		return mWifiInfo.getBSSID();
	}

	public String getSSID() {
		if (mWifiInfo == null)
			return "NULL";
		return mWifiInfo.getSSID();
	}

	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	public String getLocalIPAddress() {
		if (mDhcpInfo == null)
			return "NULL";
		Log.i("Wifi", intToIp(mDhcpInfo.ipAddress));
		return intToIp(mDhcpInfo.ipAddress);
	}

	public String getServerIPAddress() {
		if (mDhcpInfo == null)
			return "NULL";
		return intToIp(mDhcpInfo.serverAddress);
	}

	public String getMacAddress() {
		if (mWifiInfo == null)
			return "NULL";
		return mWifiInfo.getMacAddress();
	}

	public int getNetworkId() {
		if (mWifiInfo == null)
			return 0;
		return mWifiInfo.getNetworkId();
	}

	public int getWifiApState() {
		try {
			int i = ((Integer) mWifiManager.getClass()
					.getMethod("getWifiApState", new Class[0])
					.invoke(mWifiManager, new Object[0])).intValue();
			return i;
		} catch (Exception localException) {
		}
		return 4;
	}

	public WifiInfo getWifiInfo() {
		return mWifiManager.getConnectionInfo();
	}

	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	public StringBuilder lookUpScan() {
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = 0;; i++) {
			if (i >= 2)
				return localStringBuilder;
			localStringBuilder.append("Index_"
					+ Integer.valueOf(i + 1).toString() + ":");
			localStringBuilder.append(((ScanResult) mWifiList.get(i))
					.toString());
			localStringBuilder.append("/n");
		}
	}

	public void setWifiList() {
		mWifiList = mWifiManager.getScanResults();
	}

	public void startScan() {
		mWifiManager.startScan();
	}

	public String intToIp(int paramIntip) {
		return (paramIntip & 0xFF) + "." + ((paramIntip >> 8) & 0xFF) + "."
				+ ((paramIntip >> 16) & 0xFF) + "."
				+ ((paramIntip >> 24) & 0xFF);
	}
}