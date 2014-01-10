package com.immomo.momo.android.activity.wifiap;

/**
 * WifiAp常量
 * 
 * @author _Hill3
 * 
 */
public class WifiApConst {
    // Wifi状态 粗略
    public static final int CLOSE = 0x001;
    public static final int SEARCH = 0x002;
    public static final int CREATE = 0x003;
    public static final int NOTHING = 0x004;

    // Wifi状态 详细
    public static final int ApSearchTimeOut = 0;// 搜索超时
    public static final int ApScanResult = 1;// 搜索到wifi返回结果
    public static final int ApConnectResult = 2;// 连接上wifi热点
    public static final int ApCreateAPResult = 3;// 创建热点结果
    public static final int ApUserResult = 4;// 用户上线人数更新命令(待定)
    public static final int ApConnected = 5;// 点击连接后断开wifi，3.5秒后刷新adapter

    // WifiAP 参数
    public static final String PACKAGE_NAME = "com.immomo.momo.android.activity";
    public static final String FIRST_OPEN_KEY = "version";
    public static final String WIFI_AP_HEADER = "Chat_"; // way_
    public static final String WIFI_AP_PASSWORD = "wifichat123";
}