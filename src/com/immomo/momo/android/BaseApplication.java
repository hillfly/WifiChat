package com.immomo.momo.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.immomo.momo.android.entity.CopyOfNearByPeople;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.socket.UDPSocketThread;

public class BaseApplication extends Application {
    private static final String AVATAR_DIR = "avatar/";
    private static final String PHOTO_ORIGINAL_DIR = "photo/original/";
    private static final String PHOTO_THUMBNAIL_DIR = "photo/thumbnail/";
    private static final String STATUS_PHOTO_DIR = "statusphoto/";

    /** 用户信息常量 **/
    public static final String ID = "ID";
    public static final String NICKNAME = "Nickname";
    public static final String GENDER = "Gender";
    public static final String IMEI = "IMEI";
    public static final String AVATAR = "Avatar";
    public static final String AGE = "Age";
    public static final String ONLINESTATEINT = "OnlineStateInt";
    public static final String ISCLIENT = "isClient";
    public static final String LOCALIPADDRESS = "localIPaddress";
    public static final String SERVERIPADDRESS = "serverIPaddress";
    public static final String LOGINTIME = "LoginTime";

    public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
    public static List<String> mEmoticons = new ArrayList<String>();
    public static List<String> mEmoticons_Zem = new ArrayList<String>();
    public static List<String> mEmoticons_Zemoji = new ArrayList<String>();

    public Map<String, SoftReference<Bitmap>> mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
    public Map<String, SoftReference<Bitmap>> mPhotoOriginalCache = new HashMap<String, SoftReference<Bitmap>>();
    public Map<String, SoftReference<Bitmap>> mPhotoThumbnailCache = new HashMap<String, SoftReference<Bitmap>>();
    public Map<String, SoftReference<Bitmap>> mStatusPhotoCache = new HashMap<String, SoftReference<Bitmap>>();

    public List<NearByPeople> mNearByPeoples = new ArrayList<NearByPeople>();
    public List<NearByGroup> mNearByGroups = new ArrayList<NearByGroup>();

    public double mLongitude;
    public double mLatitude;
    private Bitmap mDefaultAvatar;

    private static Map<String, String> GlobalSession; // 全局Session
    public Map<String, CopyOfNearByPeople> OnlineUsers; // 当前所有用户的集合，以IP为KEY
    public UDPSocketThread mUDPSocketThread; // UDP Socket线程类

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalSession = new HashMap<String, String>(); // 存储用户登陆信息
        mDefaultAvatar = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_common_def_header);
        for (int i = 1; i < 64; i++) {
            String emoticonsName = "[zem" + i + "]";
            int emoticonsId = getResources().getIdentifier("zem" + i,
                    "drawable", getPackageName());
            mEmoticons.add(emoticonsName);
            mEmoticons_Zem.add(emoticonsName);
            mEmoticonsId.put(emoticonsName, emoticonsId);
        }
        for (int i = 1; i < 59; i++) {
            String emoticonsName = "[zemoji" + i + "]";
            int emoticonsId = getResources().getIdentifier("zemoji_e" + i,
                    "drawable", getPackageName());
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

    public Bitmap getAvatar(String imageName) {
        if (mAvatarCache.containsKey(imageName)) {
            Reference<Bitmap> reference = mAvatarCache.get(imageName);
            if (reference.get() == null || reference.get().isRecycled()) {
                mAvatarCache.remove(imageName);
            } else {
                return reference.get();
            }
        }
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getAssets().open(AVATAR_DIR + imageName);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null) {
                throw new FileNotFoundException(imageName + "is not find");
            }
            mAvatarCache.put(imageName, new SoftReference<Bitmap>(bitmap));
            return bitmap;
        } catch (Exception e) {
            return mDefaultAvatar;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {

            }
        }
    }

    public Bitmap getPhotoOriginal(String imageName) {
        if (mPhotoOriginalCache.containsKey(imageName)) {
            Reference<Bitmap> reference = mPhotoOriginalCache.get(imageName);
            if (reference.get() == null || reference.get().isRecycled()) {
                mPhotoOriginalCache.remove(imageName);
            } else {
                return reference.get();
            }
        }
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getAssets().open(PHOTO_ORIGINAL_DIR + imageName);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null) {
                throw new FileNotFoundException(imageName + "is not find");
            }
            mPhotoOriginalCache.put(imageName,
                    new SoftReference<Bitmap>(bitmap));
            return bitmap;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {

            }
        }
    }

    public Bitmap getPhotoThumbnail(String imageName) {
        if (mPhotoThumbnailCache.containsKey(imageName)) {
            Reference<Bitmap> reference = mPhotoThumbnailCache.get(imageName);
            if (reference.get() == null || reference.get().isRecycled()) {
                mPhotoThumbnailCache.remove(imageName);
            } else {
                return reference.get();
            }
        }
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getAssets().open(PHOTO_THUMBNAIL_DIR + imageName);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null) {
                throw new FileNotFoundException(imageName + "is not find");
            }
            mPhotoThumbnailCache.put(imageName, new SoftReference<Bitmap>(
                    bitmap));
            return bitmap;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {

            }
        }
    }

    public Bitmap getStatusPhoto(String imageName) {
        if (mStatusPhotoCache.containsKey(imageName)) {
            Reference<Bitmap> reference = mStatusPhotoCache.get(imageName);
            if (reference.get() == null || reference.get().isRecycled()) {
                mStatusPhotoCache.remove(imageName);
            } else {
                return reference.get();
            }
        }
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getAssets().open(STATUS_PHOTO_DIR + imageName);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null) {
                throw new FileNotFoundException(imageName + "is not find");
            }
            mStatusPhotoCache.put(imageName, new SoftReference<Bitmap>(bitmap));
            return bitmap;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {

            }
        }
    }

    /****************************
     * 全局Session设置 _Hill3
     ****************************/

    /**
     * 获取本地IP
     * 
     * @return localIPaddress
     */
    public String getLocalIPaddress() {
        return GlobalSession.get(LOCALIPADDRESS);
    }

    /**
     * 获取热点IP
     * 
     * @return serverIPaddress
     */
    public String getServerIPaddress() {
        return GlobalSession.get(SERVERIPADDRESS);
    }

    /**
     * 获取昵称
     * 
     * @return Nickname
     */
    public String getNickname() {
        return GlobalSession.get(NICKNAME);
    }

    /**
     * 获取性别
     * 
     * @return Gender
     */
    public String getGender() {
        return GlobalSession.get(GENDER);
    }

    /**
     * 获取IMEI
     * 
     * @return IMEI
     */
    public String getIMEI() {
        return GlobalSession.get(IMEI);
    }

    /**
     * 获取数据库用户编号
     * 
     * @return id
     */
    // public int getID() {
    // return Integer.parseInt(GlobalSession.get(ID));
    // }

    /**
     * 获取头像编号
     * 
     * @return AvatarNum
     */
    public int getAvatar() {
        return Integer.parseInt(GlobalSession.get(AVATAR));
    }

    /**
     * 获取年龄
     * 
     * @return Age
     */
    public int getAge() {
        return Integer.parseInt(GlobalSession.get(AGE));
    }

    /**
     * 获取登录状态编码
     * 
     * @return OnlineStateInt
     */
    public int getOnlineStateInt() {
        return Integer.parseInt(GlobalSession.get(ONLINESTATEINT));
    }

    /**
     * 获取是否为客户端
     * 
     * @return isClient
     */
    public boolean getIsClient() {
        return Boolean.parseBoolean(GlobalSession.get(ISCLIENT));
    }

    /**
     * 获取登录时间
     * 
     * @return Data 登录时间 年月日
     */
    public String getLoginTime() {
        return GlobalSession.get(LOGINTIME);
    }

    /**
     * 设置登录时间
     * 
     * @param paramLoginTime
     */
    public void setLoginTime(String paramLoginTime) {
        GlobalSession.put(LOGINTIME, paramLoginTime);
    }

    /**
     * 设置本地IP
     * 
     * @param paramLocalIPaddress
     *            本地IP地址值
     */
    public void setLocalIPaddress(String paramLocalIPaddress) {
        GlobalSession.put(LOCALIPADDRESS, paramLocalIPaddress);
    }

    /**
     * 设置热点IP
     * 
     * @param paramServerIPaddress
     *            热点IP地址值
     */
    public void setServerIPaddress(String paramServerIPaddress) {
        GlobalSession.put(SERVERIPADDRESS, paramServerIPaddress);
    }

    /**
     * 设置昵称
     * 
     * @param paramNickname
     *            设置的昵称
     */
    public void setNickname(String paramNickname) {
        GlobalSession.put(NICKNAME, paramNickname);
    }

    /**
     * 设置性别
     * 
     * @param paramGender
     *            设置的性别
     */
    public void setGender(String paramGender) {
        GlobalSession.put(GENDER, paramGender);
    }

    /**
     * 设置IMEI
     * 
     * @param paramIMEI
     *            本机的IMEI值
     */
    public void setIMEI(String paramIMEI) {
        GlobalSession.put(IMEI, paramIMEI);
    }

    /**
     * 设置用户ID
     * 
     * @param paramID
     *            该用户IMEI在数据库中对应的id编号
     */
    // public void setID(int paramID) {
    // GlobalSession.put(ID, String.valueOf(paramID));
    // }

    /**
     * 设置登陆状态编码
     * 
     * <p>
     * 登陆编码：0 - 在线 , 1 - 忙碌 , 2 - 隐身 , 3 - 离开
     * </p>
     * 
     * @param paramOnlineStateInt
     *            登陆状态的具体编码
     */
    public void setOnlineStateInt(int paramOnlineStateInt) {
        GlobalSession.put(ONLINESTATEINT, String.valueOf(paramOnlineStateInt));
    }

    /**
     * 设置头像编号
     * 
     * @param paramAvatar
     *            选择的头像编号
     */
    public void setAvatar(int paramAvatar) {
        GlobalSession.put(AVATAR, String.valueOf(paramAvatar));
    }

    /**
     * 设置年龄
     * 
     * @param paramAge
     */
    public void setAge(int paramAge) {
        GlobalSession.put(AGE, String.valueOf(paramAge));
    }

    /**
     * 设置是否为客户端
     * 
     * @param paramIsClient
     */
    public void setIsClient(boolean paramIsClient) {
        GlobalSession.put(ISCLIENT, String.valueOf(paramIsClient));
    }

    /** 清空全局登陆Session信息 **/
    public void clearSession() {
        GlobalSession.put(ID, "0");
        GlobalSession.put(NICKNAME, null);
        GlobalSession.put(GENDER, null);
        GlobalSession.put(IMEI, null);
        GlobalSession.put(AVATAR, "0");
        GlobalSession.put(AGE, "0");
        GlobalSession.put(ONLINESTATEINT, "0");
        GlobalSession.put(ISCLIENT, "false");
        GlobalSession.put(LOCALIPADDRESS, "0.0.0.0");
        GlobalSession.put(SERVERIPADDRESS, "0.0.0.0");
        GlobalSession.put(LOGINTIME, null);
    }

}
