package hillfly.wifichat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

/**
 * @fileName DateUtils.java
 * @package szu.wifichat.android.util
 * @description 时间日期工具类
 * @author _Hill3
 */
public class DateUtils {

    public static String FORMATTIMESTR = "yyyy年MM月dd日 HH:mm:ss"; // 时间格式化格式

    /**
     * 获取yyyyMMdd格式日期
     * 
     * @param time
     * @return
     */
    public static Date getDate(String time) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            date = format.parse(time);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatDate(Context context, long date) {
        @SuppressWarnings("deprecation")
        int format_flags = android.text.format.DateUtils.FORMAT_NO_NOON_MIDNIGHT
                | android.text.format.DateUtils.FORMAT_ABBREV_ALL
                | android.text.format.DateUtils.FORMAT_CAP_AMPM
                | android.text.format.DateUtils.FORMAT_SHOW_DATE
                | android.text.format.DateUtils.FORMAT_SHOW_DATE
                | android.text.format.DateUtils.FORMAT_SHOW_TIME;
        return android.text.format.DateUtils.formatDateTime(context, date, format_flags);
    }

    /**
     * 返回此时时间
     * 
     * @return String: XXX年XX月XX日 XX:XX:XX
     */
    public static String getNowtime() {
        return new SimpleDateFormat(FORMATTIMESTR).format(new Date());
    }

    /**
     * 格式化输出指定时间点与现在的差
     * 
     * @param paramTime
     *            指定的时间点
     * @return 格式化后的时间差，类似 X秒前、X小时前、X年前
     */
    public static String getBetweentime(String paramTime) {
        String returnStr = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATTIMESTR);
        try {
            Date nowData = new Date();
            Date mDate = dateFormat.parse(paramTime);
            long betweenForSec = Math.abs(mDate.getTime() - nowData.getTime()) / 1000; // 秒
            if (betweenForSec < 60) {
                returnStr = betweenForSec + "秒前";
            }
            else if (betweenForSec < (60 * 60)) {
                returnStr = betweenForSec / 60 + "分钟前";
            }
            else if (betweenForSec < (60 * 60 * 24)) {
                returnStr = betweenForSec / (60 * 60) + "小时前";
            }
            else if (betweenForSec < (60 * 60 * 24 * 30)) {
                returnStr = betweenForSec / (60 * 60 * 24) + "天前";
            }
            else if (betweenForSec < (60 * 60 * 24 * 30 * 12)) {
                returnStr = betweenForSec / (60 * 60 * 24 * 30) + "个月前";
            }
            else
                returnStr = betweenForSec / (60 * 60 * 24 * 30 * 12) + "年前";
        }
        catch (ParseException e) {
            returnStr = "TimeError"; // 错误提示
        }
        return returnStr;
    }
}
