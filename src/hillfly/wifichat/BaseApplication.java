package hillfly.wifichat;

import hillfly.wifichat.file.FileState;
import hillfly.wifichat.util.FileUtils;
import hillfly.wifichat.util.LogUtils;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.Service;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

public class BaseApplication extends Application {

    public static boolean isDebugmode = false;
    private boolean isPrintLog = true;

    /** 静音、震动默认开关 **/
    private static boolean isSlient = false;
    private static boolean isVIBRATE = true;

    /** 新消息提醒 **/
    private static int notiSoundPoolID;
    private static SoundPool notiMediaplayer;
    private static Vibrator notiVibrator;

    /** 缓存 **/
    private Map<String, SoftReference<Bitmap>> mAvatarCache;

    public static HashMap<String, FileState> sendFileStates;
    public static HashMap<String, FileState> recieveFileStates;

    /** 本地图像、缩略图、声音、文件存储路径 **/
    public static String IMAG_PATH;
    public static String THUMBNAIL_PATH;
    public static String VOICE_PATH;
    public static String FILE_PATH;
    public static String SAVE_PATH;
    public static String CAMERA_IMAGE_PATH;

    /** mEmoticons 表情 **/
    public static Map<String, Integer> mEmoticonsId;
    public static List<String> mEmoticons;
    public static List<String> mEmoticons_Zem;

    private static BaseApplication instance;

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

        sendFileStates = new HashMap<String, FileState>();
        recieveFileStates = new HashMap<String, FileState>();
        mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();

        ActivitiesManager.init(getApplicationContext()); // 初始化活动管理器
        LogUtils.setLogStatus(isPrintLog); // 设置是否显示日志

        initEmoticons();
        initNotification();
        initFolder();

    }

    private void initEmoticons() {
        mEmoticonsId = new HashMap<String, Integer>();
        mEmoticons = new ArrayList<String>();
        mEmoticons_Zem = new ArrayList<String>();

        // 预载表情
        for (int i = 1; i < 64; i++) {
            String emoticonsName = "[zem" + i + "]";
            int emoticonsId = getResources().getIdentifier("zem" + i, "drawable", getPackageName());
            mEmoticons.add(emoticonsName);
            mEmoticons_Zem.add(emoticonsName);
            mEmoticonsId.put(emoticonsName, emoticonsId);
        }
    }

    private void initNotification() {
        notiMediaplayer = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        notiSoundPoolID = notiMediaplayer.load(this, R.raw.crystalring, 1);
        notiVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtils.e("BaseApplication", "onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtils.e("BaseApplication", "onTerminate");
    }

    // 函数创建文件存储目录
    private void initFolder() {
        if (null == IMAG_PATH) {
            SAVE_PATH = FileUtils.getSDPath();// 获取SD卡的根目录路径,如果不存在就返回Null
            if (null == SAVE_PATH) {
                SAVE_PATH = instance.getFilesDir().toString();// 获取内置存储区目录
            }
            SAVE_PATH += File.separator + instance.getString(R.string.app_name);
            IMAG_PATH = SAVE_PATH + File.separator + "image";
            THUMBNAIL_PATH = SAVE_PATH + File.separator + "thumbnail";
            VOICE_PATH = SAVE_PATH + File.separator + "voice";
            FILE_PATH = SAVE_PATH + File.separator + "file";
            CAMERA_IMAGE_PATH = IMAG_PATH + File.separator;
            if (!FileUtils.isFileExists(IMAG_PATH))
                FileUtils.createDirFile(BaseApplication.IMAG_PATH);
            if (!FileUtils.isFileExists(THUMBNAIL_PATH))
                FileUtils.createDirFile(BaseApplication.THUMBNAIL_PATH);
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

    /* 设置声音提醒 */
    public static boolean getSoundFlag() {
        return !isSlient;
    }

    public static void setSoundFlag(boolean pIsSlient) {
        isSlient = pIsSlient;
    }

    /* 设置震动提醒 */
    public static boolean getVibrateFlag() {
        return isVIBRATE;
    }

    public static void setVibrateFlag(boolean pIsvibrate) {
        isVIBRATE = pIsvibrate;
    }

    /**
     * 新消息提醒 - 声音提醒、振动提醒
     */
    public static void playNotification() {
        if (!isSlient) {
            notiMediaplayer.play(notiSoundPoolID, 1, 1, 0, 0, 1);
        }
        if (isVIBRATE) {
            notiVibrator.vibrate(200);
        }

    }
}
