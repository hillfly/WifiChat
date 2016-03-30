package hillfly.wifichat.util;

import java.util.Hashtable;

import android.content.Context;
import android.util.Log;

/**
 * @fileName Logger.java
 * @description 日志操作类
 * @author _Hill3
 */
public class Logger {

    private static boolean logFlag;
    private static String tag;
    private static int logLevel;
    private static Hashtable<String, Logger> sLoggerTable;

    private Logger() {

    }

    /**
     * Logger初始化
     * 
     * @param mContext
     *            上下文
     * @param 是否输出日志标识
     * @param 输出日志等级
     * 
     */
    public static Logger initLogger(Context mContext, boolean flag, int level) {
        tag = AppUtils.getAppName(mContext);
        logFlag = flag;
        logLevel = level;
        sLoggerTable = new Hashtable<String, Logger>();
        
        return getLogger(mContext.getClass());
    }

    /**
     * 
     * @param className
     * @return
     */
    public static Logger getLogger(Class<?> cls) {
        String className = cls.getName();
        Logger classLogger = (Logger) sLoggerTable.get(className);
        if (classLogger == null) {
            classLogger = new Logger();
            sLoggerTable.put(className, classLogger);
        }
        return classLogger;
    }

    /**
     * Get The Current Function Name
     * 
     * @return
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":"
                    + st.getLineNumber() + " " + st.getMethodName() + " ]";
        }
        return null;
    }

    /**
     * The Log Level:i
     * 
     * @param str
     */
    public void i(Object str) {
        if (logFlag) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(tag, name + " - " + str);
                }
                else {
                    Log.i(tag, str.toString());
                }
            }
        }

    }

    /**
     * The Log Level:d
     * 
     * @param str
     */
    public void d(Object str) {
        if (logFlag) {
            if (logLevel <= Log.DEBUG) {
                String name = getFunctionName();
                if (name != null) {
                    Log.d(tag, name + " - " + str);
                }
                else {
                    Log.d(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:V
     * 
     * @param str
     */
    public void v(Object str) {
        if (logFlag) {
            if (logLevel <= Log.VERBOSE) {
                String name = getFunctionName();
                if (name != null) {
                    Log.v(tag, name + " - " + str);
                }
                else {
                    Log.v(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:w
     * 
     * @param str
     */
    public void w(Object str) {
        if (logFlag) {
            if (logLevel <= Log.WARN) {
                String name = getFunctionName();
                if (name != null) {
                    Log.w(tag, name + " - " + str);
                }
                else {
                    Log.w(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     * 
     * @param str
     */
    public void e(Object str) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                String name = getFunctionName();
                if (name != null) {
                    Log.e(tag, name + " - " + str);
                }
                else {
                    Log.e(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     * 
     * @param ex
     */
    public void e(Exception ex) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                Log.e(tag, "error", ex);
            }
        }
    }

    /**
     * The Log Level:e
     * 
     * @param log
     * @param tr
     */
    public void e(String log, Throwable tr) {
        if (logFlag) {
            String line = getFunctionName();
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + line + ":] "
                    + log + "\n", tr);
        }
    }
}
