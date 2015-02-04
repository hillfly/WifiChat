package szu.wifichat.android.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import szu.wifichat.android.activity.wifiap.TimerCheck;
import szu.wifichat.android.activity.wifiap.WifiApConst;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

/**
 * Wifi 工具类
 * 
 * 封装了Wifi的基础操作方法，方便获取Wifi连接信息以及操作Wifi
 */

public class WifiUtils {
    public static final String TAG = "SZU_WifiUtils";

    private static WifiUtils mWifiUtils = null;
    private WifiInfo mWifiInfo;
    private DhcpInfo mDhcpInfo;
    private List<ScanResult> mWifiList;
    private WifiManager.WifiLock mWifiLock;
    public WifiManager mWifiManager;
    private NetworkInfo mNetworkInfo;
    private Context mContext;

    private WifiUtils(Context paramContext) {
        mContext = paramContext;
        mWifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
        mDhcpInfo = mWifiManager.getDhcpInfo();
        mWifiInfo = mWifiManager.getConnectionInfo();
        mNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public static WifiUtils getInstance(Context paramContext) {
        if (mWifiUtils == null)
            mWifiUtils = new WifiUtils(paramContext);
        return mWifiUtils;
    }

    public void setNewWifiManagerInfo() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = wifiManager.getConnectionInfo();
        mDhcpInfo = wifiManager.getDhcpInfo();
    }

    public void startWifiAp(String ssid, String passwd, final Handler handler) {

        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }

        startAp(ssid, passwd);

        TimerCheck timerCheck = new TimerCheck() {

            @Override
            public void doTimerCheckWork() {
                // TODO Auto-generated method stub

                if (isWifiApEnabled()) {
                    LogUtils.v(TAG, "WifiAp enabled success!");
                    Message msg = handler.obtainMessage(WifiApConst.ApCreateAPResult);
                    handler.sendMessage(msg);
                    this.exit();
                }
                else {
                    LogUtils.v(TAG, "WifiAp enabled failed!");
                }
            }

            @Override
            public void doTimeOutWork() {
                // TODO Auto-generated method stub
                this.exit();
            }
        };
        timerCheck.start(15, 1000);

    }

    public void startAp(String ssid, String passwd) {
        Method method1 = null;
        try {
            method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();

            netConfig.SSID = ssid;
            netConfig.preSharedKey = passwd;

            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            method1.invoke(mWifiManager, netConfig, true);

        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void closeWifiAp() {
        if (isWifiApEnabled()) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);

                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);

                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                method2.invoke(mWifiManager, config, false);
            }
            catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);

        }
        catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getWifiApStateInt() {
        try {
            int i = ((Integer) mWifiManager.getClass().getMethod("getWifiApState", new Class[0])
                    .invoke(mWifiManager, new Object[0])).intValue();
            return i;
        }
        catch (Exception localException) {
        }
        return 4;
    }

    /**
     * 判断是否连接上wifi
     * 
     * @return boolean值(isConnect),对应已连接(true)和未连接(false)
     */
    public boolean isWifiConnect() {
        return mNetworkInfo.isConnected();
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

    public void removeNetwork(int netId) {
        if (mWifiManager != null) {
            mWifiManager.removeNetwork(netId);
        }
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
            Object localObject1 = localMethod.invoke(mWifiManager, new Object[0]);
            if (localObject1 == null)
                return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null)
                return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
            if (localField1 == null)
                return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)
                return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null)
                return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        }
        catch (Exception localException) {
        }
        return null;
    }

    private WifiConfiguration isExsits(String paramString) {
        Iterator<WifiConfiguration> localIterator = mWifiManager.getConfiguredNetworks().iterator();
        WifiConfiguration localWifiConfiguration;
        do {
            if (!localIterator.hasNext())
                return null;
            localWifiConfiguration = (WifiConfiguration) localIterator.next();
        }
        while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
        return localWifiConfiguration;
    }

    public WifiConfiguration createWifiInfo(String ssid, String paramString2, int paramInt,
            String paramString3) {
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
            }
            else if (paramInt == 2) {
                localWifiConfiguration1.hiddenSSID = true;
                localWifiConfiguration1.wepKeys[0] = ("\"" + paramString2 + "\"");
            }
            else {
                localWifiConfiguration1.preSharedKey = ("\"" + paramString2 + "\"");
                localWifiConfiguration1.hiddenSSID = true;
                localWifiConfiguration1.allowedAuthAlgorithms.set(0);
                localWifiConfiguration1.allowedGroupCiphers.set(2);
                localWifiConfiguration1.allowedKeyManagement.set(1);
                localWifiConfiguration1.allowedPairwiseCiphers.set(1);
                localWifiConfiguration1.allowedGroupCiphers.set(3);
                localWifiConfiguration1.allowedPairwiseCiphers.set(2);
            }
        }
        else {
            localWifiConfiguration1.SSID = ssid;
            localWifiConfiguration1.allowedAuthAlgorithms.set(1);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            localWifiConfiguration1.allowedKeyManagement.set(0);
            localWifiConfiguration1.wepTxKeyIndex = 0;
            if (paramInt == 1) {
                localWifiConfiguration1.wepKeys[0] = "";
                localWifiConfiguration1.allowedKeyManagement.set(0);
                localWifiConfiguration1.wepTxKeyIndex = 0;
            }
            else if (paramInt == 2) {
                localWifiConfiguration1.hiddenSSID = true;
                localWifiConfiguration1.wepKeys[0] = paramString2;
            }
            else if (paramInt == 3) {
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

    public String getLocalIPAddress() {        
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress mInetAddress = enumIpAddr.nextElement();
                    if (!mInetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(mInetAddress.getHostAddress())) {
                        return mInetAddress.getHostAddress();                        
                    }
                }
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getServerIPAddress() {
        setNewWifiManagerInfo();
        if (mDhcpInfo == null)
            return "NULL";
        return intToIp(mDhcpInfo.gateway);
    }

    public static String getBroadcastAddress() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum
                    .hasMoreElements();) {
                NetworkInterface ni = niEnum.nextElement();
                if (!ni.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                        if (interfaceAddress.getBroadcast() != null) {
                            return interfaceAddress.getBroadcast().toString().substring(1);
                        }
                    }
                }
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
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
            localStringBuilder.append("Index_" + Integer.valueOf(i + 1).toString() + ":");
            localStringBuilder.append(((ScanResult) mWifiList.get(i)).toString());
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
                + ((paramIntip >> 16) & 0xFF) + "." + ((paramIntip >> 24) & 0xFF);
    }
}