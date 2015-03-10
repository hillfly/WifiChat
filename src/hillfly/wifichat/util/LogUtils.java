package hillfly.wifichat.util;

import android.util.Log;

/**
 * @fileName LogUtils.java
 * @package szu.wifichat.android.util
 * @description 日志操作类
 * @author _Hill3
 */

public class LogUtils {

    private static boolean isShow = true; // 是否打印日志

    public static void setLogStatus(boolean flag){
        isShow = flag;
    }

    public static void d(String tag, String msg) {
        if (isShow)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isShow)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isShow)
            Log.v(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isShow)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isShow)
            Log.w(tag, msg);
    }

    public static void wtf(String tag, String msg) {
        if (isShow)
            Log.wtf(tag, msg);
    }

}
