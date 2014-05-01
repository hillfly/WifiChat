package szu.wifichat.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Random;

import szu.wifichat.android.view.HandyTextView;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.widget.EditText;


/**
 * @fileName TextUtils.java
 * @package szu.wifichat.android.util
 * @description 文本工具类
 */
public class TextUtils {
    /**
     * 添加下划线
     * 
     * @param context
     *            上下文
     * @param textView
     *            添加下划线的TextView
     * @param start
     *            添加下划线开始的位置
     * @param end
     *            添加下划线结束的位置
     */
    public static void addUnderlineText(final Context context, final HandyTextView textView,
            final int start, final int end) {
        textView.setFocusable(true);
        textView.setClickable(true);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(textView
                .getText().toString().trim());
        spannableStringBuilder.setSpan(new UnderlineSpan(), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableStringBuilder);
    }

    /**
     * 获取括号中的国家区号
     * 
     * @param text
     *            带有括号的国家区号
     * @param defaultText
     *            默认的国家区号(在获取错误时返回该值)
     * @return
     */
    public static String getCountryCodeBracketsInfo(String text, String defaultText) {
        if (text.contains("(") && text.contains(")")) {
            int leftBrackets = text.indexOf("(");
            int rightBrackets = text.lastIndexOf(")");
            if (leftBrackets < rightBrackets) {
                return "+" + text.substring(leftBrackets + 1, rightBrackets);
            }
        }
        if (defaultText != null) {
            return defaultText;
        }
        else {
            return text;
        }
    }

    /**
     * 根据月日获取星座
     * 
     * @param month
     *            月
     * @param day
     *            日
     * @return
     */
    public static String getConstellation(int month, int day) {
        String[] constellationArr = { "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座",
                "天秤座", "天蝎座", "射手座", "魔羯座" };
        int[] constellationEdgeDay = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        // default to return 摩羯座
        return constellationArr[11];
    }

    /**
     * 根据年月日获取年龄
     * 
     * @param year
     *            年
     * @param month
     *            月
     * @param day
     *            日
     * @return
     */
    public static int getAge(int year, int month, int day) {
        int age = 0;
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == year) {
            if (calendar.get(Calendar.MONTH) == month) {
                if (calendar.get(Calendar.DAY_OF_MONTH) >= day) {
                    age = calendar.get(Calendar.YEAR) - year + 1;
                }
                else {
                    age = calendar.get(Calendar.YEAR) - year;
                }
            }
            else if (calendar.get(Calendar.MONTH) > month) {
                age = calendar.get(Calendar.YEAR) - year + 1;
            }
            else {
                age = calendar.get(Calendar.YEAR) - year;
            }
        }
        else {
            age = calendar.get(Calendar.YEAR) - year;
        }
        if (age < 0) {
            return 0;
        }
        return age;
    }

    /**
     * 判断文本框的内容是否为空
     * 
     * @param editText
     *            需要判断是否为空的EditText对象
     * @return boolean 返回是否为空,空(true),非空(false)
     */
    public static boolean isNull(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 返回指定长度的一串数字
     * 
     * @param NumLen 数字串位数
     * @return
     */
    public static String getRandomNumStr(int NumLen) {
        Random random = new Random(System.currentTimeMillis());
        StringBuffer str = new StringBuffer();
        int i, num;
        for (i = 0; i < NumLen; i++) {
            num = random.nextInt(10); // 0-10的随机数
            str.append(num);
        }
        return str.toString();
    }

    /**
     * 获取Assets中的json文本
     * 
     * @param context
     *            上下文
     * @param name
     *            文本名称
     * @return
     */
    public static String getJson(Context context, String name) {
        // TODO 待清除
        if (name != null) {
            String path = "json/" + name;
            InputStream is = null;
            try {
                is = context.getAssets().open(path);
                return readTextFile(is);
            }
            catch (IOException e) {
                return null;
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                }
                catch (IOException e) {

                }
            }
        }
        return null;
    }

    /**
     * 从输入流中获取文本
     * 
     * @param inputStream
     *            文本输入流
     * @return
     */
    public static String readTextFile(InputStream inputStream) {
        // TODO 待清除
        String readedStr = "";
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                readedStr += tmp;
            }
            br.close();
            inputStream.close();
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        catch (IOException e) {
            return null;
        }

        return readedStr;
    }

}
