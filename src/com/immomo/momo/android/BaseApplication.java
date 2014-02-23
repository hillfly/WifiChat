package com.immomo.momo.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByPeople;

public class BaseApplication extends Application {

    /** mEmoticons 表情 **/
    public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
    public static List<String> mEmoticons = new ArrayList<String>();
    public static List<String> mEmoticons_Zem = new ArrayList<String>();
    public static List<String> mEmoticons_Zemoji = new ArrayList<String>();

    /** 缓存 **/
    private Map<String, SoftReference<Bitmap>> mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
    private HashMap<String, String> mLastMsgCache; // 最后一条消息，以IMEI为KEY

    public List<NearByGroup> mNearByGroups = new ArrayList<NearByGroup>(); // 群列表
    private ArrayList<Message> mUnReadMessags; // 未读消息队列
    private HashMap<String, String> mLocalUserSession; // 本机用户Session信息
    private HashMap<String, NearByPeople> mOnlineUsers; // 在线用户集合，以IMEI为KEY  

    public double mLongitude;
    public double mLatitude;
    private Bitmap mDefaultAvatar;

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
        mLocalUserSession = new HashMap<String, String>(13); // 存储用户登陆信息
        mDefaultAvatar = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_common_def_header);
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
    
    public void initParam(){
        initOnlineUserMap(); // 初始化用户列表
        initUnReadMessags(); // 初始化未读消息
        initLastMsgCache(); // 初始化消息缓存
    }

    // mOnlineUsers setter getter
    public void initOnlineUserMap() {
        mOnlineUsers = new LinkedHashMap<String, NearByPeople>();
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
    public void initLastMsgCache() {
        mLastMsgCache = new HashMap<String, String>();
    }

    public void addLastMsgCache(String paramIMEI, String paramMsg) {
        mLastMsgCache.put(paramIMEI, paramMsg);
    }

    public String getLastMsgCache(String paramIMEI) {
        return mLastMsgCache.get(paramIMEI);
    }

    public void removeLastMsgCache(String paramIMEI) {
        mLastMsgCache.remove(paramIMEI);
    }

    // mUnReadMessags setter getter
    public void initUnReadMessags() {
        mUnReadMessags = new ArrayList<Message>();
    }

    public void addUnReadMessags(Message msg) {
        Log.i("BaseActivity", "进入到 addUnReadMessages()");
        mUnReadMessags.add(msg);
    }

    public ArrayList<Message> getUnReadMessagsList() {
        return mUnReadMessags;
    }

    public void removeUnReadMessags(int position) {
        mUnReadMessags.remove(position);
    }
   
    /**
     * 获取用户头像bitmap
     * <p>
     * 优先从缓存中获取信息，若不存在，则从SD卡中重新读取并设置缓存
     * 
     * @param paramAvatarName
     *            头像文件名 "Avatar" + avatarID，例如 Avatar2
     * @return
     */
    public Bitmap getAvatar(String paramAvatarName) {
        if (mAvatarCache.containsKey(paramAvatarName)) {
            Reference<Bitmap> reference = mAvatarCache.get(paramAvatarName);
            if (reference.get() == null || reference.get().isRecycled()) {
                mAvatarCache.remove(paramAvatarName);
                return getAvatarFromRes(paramAvatarName);
            }
            else {
                return reference.get();
            }
        }
        else {
            return getAvatarFromRes(paramAvatarName);
        }
    }

    /**
     * 将drawable中的指定图片转为bitmap资源
     * 
     * @param paramAvatarName
     *            需要转换的图片文件名
     * @return
     */
    private Bitmap getAvatarFromRes(String paramAvatarName) {
        Bitmap returnBitmap = null;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = this.getResources().openRawResource(getIDfromDrawable(paramAvatarName));
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null) {
                throw new FileNotFoundException(paramAvatarName + "is not find");
            }
            mAvatarCache.put(paramAvatarName, new SoftReference<Bitmap>(bitmap));
            returnBitmap = bitmap;
        }
        catch (Exception e) {
            returnBitmap = mDefaultAvatar;
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
        return returnBitmap;
    }

    /**
     * 获取drawable中指定文件名图片的id
     * 
     * @param paramAvatarName
     *            图片名
     * @return
     */
    public int getIDfromDrawable(String paramAvatarName) {
        return this.getResources().getIdentifier(paramAvatarName, "drawable", getPackageName());
    }

}
