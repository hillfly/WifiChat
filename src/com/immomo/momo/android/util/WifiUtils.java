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

/**
 * Wifi 工具类
 * 
 * <p>封装了Wifi的基础操作方法，方便获取Wifi连接信息以及操作Wifi</p>
 */

public class WifiUtils {
    private static WifiUtils wiFiAdmin = null;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiInfo mWifiInfo;
    private DhcpInfo mDhcpInfo;
    private List<ScanResult> mWifiList;
    WifiManager.WifiLock mWifiLock;
    public WifiManager mWifiManager;

    public WifiUtils(Context paramContext) {
        this.mWifiManager = ((WifiManager) paramContext
                .getSystemService(Context.WIFI_SERVICE));
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
        this.mDhcpInfo = this.mWifiManager.getDhcpInfo();
    }

    public static WifiUtils getInstance(Context paramContext) {
        if (wiFiAdmin == null)
            wiFiAdmin = new WifiUtils(paramContext);
        return wiFiAdmin;
    }

    private WifiConfiguration isExsits(String paramString) {
        Iterator localIterator = this.mWifiManager.getConfiguredNetworks()
                .iterator();
        WifiConfiguration localWifiConfiguration;
        do {
            if (!localIterator.hasNext())
                return null;
            localWifiConfiguration = (WifiConfiguration) localIterator.next();
        } while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
        return localWifiConfiguration;
    }

    public void AcquireWifiLock() {
        this.mWifiLock.acquire();
    }

    public void CreatWifiLock() {
        this.mWifiLock = this.mWifiManager.createWifiLock("Test");
    }

    public void OpenWifi() {
        if (!this.mWifiManager.isWifiEnabled())
            this.mWifiManager.setWifiEnabled(true);
    }

    public void ReleaseWifiLock() {
        if (this.mWifiLock.isHeld())
            this.mWifiLock.release();
    }

    public void addNetwork(WifiConfiguration paramWifiConfiguration) {
        int i = this.mWifiManager.addNetwork(paramWifiConfiguration);
        this.mWifiManager.enableNetwork(i, true);
    }

    public void closeWifi() {
        this.mWifiManager.setWifiEnabled(false);
    }

    public void connectConfiguration(int paramInt) {
        if (paramInt > this.mWifiConfiguration.size())
            return;
        this.mWifiManager
                .enableNetwork(((WifiConfiguration) this.mWifiConfiguration
                        .get(paramInt)).networkId, true);
    }

    public void createWiFiAP(WifiConfiguration paramWifiConfiguration,
            boolean paramBoolean) {
        try {
            Class localClass = this.mWifiManager.getClass();
            Class[] arrayOfClass = new Class[2];
            arrayOfClass[0] = WifiConfiguration.class;
            arrayOfClass[1] = Boolean.TYPE;
            Method localMethod = localClass.getMethod("setWifiApEnabled",
                    arrayOfClass);
            WifiManager localWifiManager = this.mWifiManager;
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
        if (paramString3.equals("wt")) {
            localWifiConfiguration1.SSID = ("\"" + ssid + "\"");
            WifiConfiguration localWifiConfiguration2 = isExsits(ssid);
            if (localWifiConfiguration2 != null)
                mWifiManager.removeNetwork(localWifiConfiguration2.networkId);
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
        this.mWifiManager.disableNetwork(paramInt);
    }

    public String getApSSID() {
        try {
            Method localMethod = this.mWifiManager.getClass()
                    .getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (localMethod == null)
                return null;
            Object localObject1 = localMethod.invoke(this.mWifiManager,
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
        if (this.mWifiInfo == null)
            return "NULL";
        return this.mWifiInfo.getBSSID();
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.mWifiConfiguration;
    }

    public String getLocalIPAddress() {
        if (this.mDhcpInfo == null)
            return "NULL";
        return intToIp(this.mDhcpInfo.ipAddress);
    }  
    
    public String getServerIPAddress() {
        if (this.mDhcpInfo == null)
            return "NULL";
        return intToIp(this.mDhcpInfo.serverAddress);
    }

    public String getMacAddress() {
        if (this.mWifiInfo == null)
            return "NULL";
        return this.mWifiInfo.getMacAddress();
    }    

    public int getNetworkId() {
        if (this.mWifiInfo == null)
            return 0;
        return this.mWifiInfo.getNetworkId();
    }

    public int getWifiApState() {
        try {
            int i = ((Integer) this.mWifiManager.getClass()
                    .getMethod("getWifiApState", new Class[0])
                    .invoke(this.mWifiManager, new Object[0])).intValue();
            return i;
        } catch (Exception localException) {
        }
        return 4;
    }

    public WifiInfo getWifiInfo() {
        return this.mWifiManager.getConnectionInfo();
    }

    public List<ScanResult> getWifiList() {
        return this.mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0;; i++) {
            if (i >= 2)
                return localStringBuilder;
            localStringBuilder.append("Index_" + new Integer(i + 1).toString()
                    + ":");
            localStringBuilder.append(((ScanResult) this.mWifiList.get(i))
                    .toString());
            localStringBuilder.append("/n");
        }
    }

    public void setWifiList() {
        this.mWifiList = this.mWifiManager.getScanResults();
    }

    public void startScan() {
        this.mWifiManager.startScan();
    }

    public String intToIp(int paramIntip) {
        return (paramIntip & 0xFF) + "." + ((paramIntip >> 8) & 0xFF) + "."
                + ((paramIntip >> 16) & 0xFF) + "." + ((paramIntip >> 24) & 0xFF);
    }
}