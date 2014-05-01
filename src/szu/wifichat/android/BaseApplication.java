package szu.wifichat.android;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import szu.wifichat.android.entity.Message;
import szu.wifichat.android.entity.NearByPeople;
import szu.wifichat.android.file.explore.FileState;
import szu.wifichat.android.util.FileUtils;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import szu.wifichat.android.R;

public class BaseApplication extends Application {

    /** mEmoticons 表情 **/
    public static Map<String, Integer> mEmoticonsId;
    public static List<String> mEmoticons;
    public static List<String> mEmoticons_Zem;
    public static List<String> mEmoticons_Zemoji;

    /** 缓存 **/
    private Map<String, SoftReference<Bitmap>> mAvatarCache;
    private HashMap<String, String> mLastMsgCache; // 最后一条消息缓存，以IMEI为KEY
    private ArrayList<NearByPeople> mUnReadPeople; // 未读用户队列
    private HashMap<String, String> mLocalUserSession; // 本机用户Session信息
    private HashMap<String, NearByPeople> mOnlineUsers; // 在线用户集合，以IMEI为KEY

    public static HashMap<String, FileState> sendFileStates;// 存放文件状态
    public static HashMap<String, FileState> recieveFileStates;// 存放文件状态

    /** 屏幕长宽 **/
    public double mLongitude;
    public double mLatitude;

    // 本地图像、声音、文件存储路径
    public static String IMAG_PATH;
    public static String VOICE_PATH;
    public static String FILE_PATH;
    public static String SAVE_PATH;

    // 静音、震动开关
    private static boolean isSOUND;
    private static boolean isVIBRATE;

    private static BaseApplication instance; // 唯一实例

    /**
     * <p>
     * 获取BaseApplication实例
     * <p>
     * 单例模式，返回唯一实例
     * 
     * @return instance
     */
    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }

        mEmoticonsId = new HashMap<String, Integer>();
        mEmoticons = new ArrayList<String>();
        mEmoticons_Zem = new ArrayList<String>();
        mEmoticons_Zemoji = new ArrayList<String>();

        sendFileStates = new HashMap<String, FileState>();
        recieveFileStates = new HashMap<String, FileState>();

        mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
        mLocalUserSession = new HashMap<String, String>(15); // 存储用户登陆信息

        // 预载表情
        for (int i = 1; i < 64; i++) {
            String emoticonsName = "[zem" + i + "]";
            int emoticonsId = getResources().getIdentifier("zem" + i, "drawable", getPackageName());
            mEmoticons.add(emoticonsName);
            mEmoticons_Zem.add(emoticonsName);
            mEmoticonsId.put(emoticonsName, emoticonsId);
        }
        for (int i = 1; i < 59; i++) {
            String emoticonsName = "[zemoji" + i + "]";
            int emoticonsId = getResources().getIdentifier("zemoji_e" + i, "drawable",
                    getPackageName());
            mEmoticons.add(emoticonsName);
            mEmoticons_Zemoji.add(emoticonsName);
            mEmoticonsId.put(emoticonsName, emoticonsId);
        }

        createFilePath();

        // 默认启动响铃与振动提醒
        isSOUND = true;
        isVIBRATE = true;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("BaseApplication", "onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("BaseApplication", "onTerminate");
    }

    public HashMap<String, String> getUserSession() {
        return mLocalUserSession;
    }

    /** 初始化相关基本参数 */
    public void initParam() {
        initOnlineUserMap(); // 初始化用户列表
        initUnReadMessages(); // 初始化未读消息
        initLastMsgCache(); // 初始化消息缓存
    }

    // mOnlineUsers setter getter
    public void initOnlineUserMap() {
        mOnlineUsers = new LinkedHashMap<String, NearByPeople>();
    }

    // 函数创建文件存储目录
    private void createFilePath() {
        if (null == IMAG_PATH) {
            SAVE_PATH = FileUtils.getSDPath();// 获取SD卡的根目录路径,如果不存在就返回Null
            if (null == SAVE_PATH) {
                SAVE_PATH = instance.getFilesDir().toString();// 获取内置存储区的地址
            }
            String appName = instance.getString(R.string.app_name);
            SAVE_PATH += File.separator + appName;
            IMAG_PATH = SAVE_PATH + File.separator + "image";
            VOICE_PATH = SAVE_PATH + File.separator + "voice";
            FILE_PATH = SAVE_PATH + File.separator + "file";
            if (!FileUtils.isFileExists(IMAG_PATH))
                FileUtils.createDirFile(BaseApplication.IMAG_PATH);// 如果目录不存在则创建目录
            if (!FileUtils.isFileExists(VOICE_PATH))
                FileUtils.createDirFile(BaseApplication.VOICE_PATH);
            if (!FileUtils.isFileExists(FILE_PATH))
                FileUtils.createDirFile(BaseApplication.FILE_PATH);

        }
    }

    public void addAvatarCache(String paramAvatarName, Bitmap bitmap) {
        mAvatarCache.put(paramAvatarName, new SoftReference<Bitmap>(bitmap));
    }

    public Reference<Bitmap> getAvatarCache(String paramAvatarName) {
        return mAvatarCache.get(paramAvatarName);
    }

    public void removeAvatarCache(String paramAvatarName) {
        mAvatarCache.remove(paramAvatarName);
    }

    public Map<String, SoftReference<Bitmap>> getAvatarCache() {
        return mAvatarCache;
    }

    public void addOnlineUser(String paramIMEI, NearByPeople paramObject) {
        mOnlineUsers.put(paramIMEI, paramObject);
    }

    public NearByPeople getOnlineUser(String paramIMEI) {
        return mOnlineUsers.get(paramIMEI);
    }

    /**
     * 移除在线用户
     * 
     * @param paramIMEI
     *            需要移除的用户IMEI
     * @param paramtype
     *            操作类型，0:清空在线列表，1:移除指定用户
     */
    public void removeOnlineUser(String paramIMEI, int paramtype) {
        if (paramtype == 1) {
            mOnlineUsers.remove(paramIMEI);
        }
        else if (paramtype == 0) {
            mOnlineUsers.clear();
        }
    }

    public HashMap<String, NearByPeople> getOnlineUserMap() {
        return mOnlineUsers;
    }

    // mLastMsgCache setter getter
    /** 初始化消息缓存 */
    public void initLastMsgCache() {
        mLastMsgCache = new HashMap<String, String>();
    }

    /**
     * 新增用户缓存
     * 
     * @param paramIMEI
     *            新增记录的对应用户IMEI
     * @param paramMsg
     *            需要缓存的消息对象
     */
    public void addLastMsgCache(String paramIMEI, Message msg) {
        StringBuffer content = new StringBuffer();
        switch (msg.getContentType()) {
            case FILE:
                content.append("<FILE>: ").append(msg.getMsgContent());
                break;
            case IMAGE:
                content.append("<IMAGE>: ").append(msg.getMsgContent());
                break;
            case VOICE:
                content.append("<VOICE>: ").append(msg.getMsgContent());
                break;
            default:
                content.append(msg.getMsgContent());
                break;
        }
        if (msg.getMsgContent().isEmpty()) {
            content.append(" ");
        }
        mLastMsgCache.put(paramIMEI, content.toString());
    }

    /**
     * 获取消息缓存
     * 
     * @param paramIMEI
     *            需要获取消息缓存记录的用户IMEI
     * @return
     */
    public String getLastMsgCache(String paramIMEI) {
        return mLastMsgCache.get(paramIMEI);
    }

    /**
     * 移除消息缓存
     * 
     * @param paramIMEI
     *            需要清除缓存的用户IMEI
     */
    public void removeLastMsgCache(String paramIMEI) {
        mLastMsgCache.remove(paramIMEI);
    }

    // mUnReadMessags setter getter
    /** 初始化未读消息队列 */
    public void initUnReadMessages() {
        mUnReadPeople = new ArrayList<NearByPeople>();
    }

    /**
     * 新增未读消息用户
     * 
     * @param people
     */
    public void addUnReadPeople(NearByPeople people) {
        Log.i("BaseActivity", "进入到 UnReadMessages()");
        if (!mUnReadPeople.contains(people))
            mUnReadPeople.add(people);
    }

    /**
     * 获取未读消息用户队列
     * 
     * @return
     */
    public ArrayList<NearByPeople> getUnReadPeopleList() {
        return mUnReadPeople;
    }

    /**
     * 获取未读用户数
     * 
     * @return
     */
    public int getUnReadPeopleSize() {
        return mUnReadPeople.size();
    }

    /**
     * 移除指定未读用户
     * 
     * @param people
     */
    public void removeUnReadPeople(NearByPeople people) {
        if (mUnReadPeople.contains(people))
            mUnReadPeople.remove(people);
    }

    /* 设置声音提醒 */
    public static boolean getSoundFlag() {
        return isSOUND;
    }

    public static void setSoundFlag(boolean sound_flag) {
        isSOUND = sound_flag;
    }

    /* 设置震动提醒 */
    public static boolean getVibrateFlag() {
        return isVIBRATE;
    }

    public static void setVibrateFlag(boolean vibrate_flag) {
        isVIBRATE = vibrate_flag;
    }
}
