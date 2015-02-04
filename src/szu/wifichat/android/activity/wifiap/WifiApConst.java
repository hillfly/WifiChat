package szu.wifichat.android.activity.wifiap;

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
    public static final int ApConnected = 5;// 连接或断开wifi，3.5秒后刷新adapter
    public static final int ApConnectting = 6;// 连接热点中

    // WifiAP 参数
    public static final String PACKAGE_NAME = "szu.wifichat.android.activity";
    public static final String WIFI_AP_HEADER = "Chat_";
    public static final String WIFI_AP_PASSWORD = "wifichat123";
}