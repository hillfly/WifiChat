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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByPeople;

public class BaseApplication extends Application {
    private Bitmap mDefaultAvatar;
    private static final String AVATAR_DIR = "avatar/";
    private static final String PHOTO_ORIGINAL_DIR = "photo/original/";
    private static final String PHOTO_THUMBNAIL_DIR = "photo/thumbnail/";
    private static final String STATUS_PHOTO_DIR = "statusphoto/";
    public Map<String, SoftReference<Bitmap>> mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
    public Map<String, SoftReference<Bitmap>> mPhotoOriginalCache = new HashMap<String, SoftReference<Bitmap>>();
    public Map<String, SoftReference<Bitmap>> mPhotoThumbnailCache = new HashMap<String, SoftReference<Bitmap>>();
    public Map<String, SoftReference<Bitmap>> mStatusPhotoCache = new HashMap<String, SoftReference<Bitmap>>();

    public List<NearByPeople> mNearByPeoples = new ArrayList<NearByPeople>();
    public List<NearByGroup> mNearByGroups = new ArrayList<NearByGroup>();

    public static List<String> mEmoticons = new ArrayList<String>();
    public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
    public static List<String> mEmoticons_Zem = new ArrayList<String>();
    public static List<String> mEmoticons_Zemoji = new ArrayList<String>();

    public LocationClient mLocationClient;
    public double mLongitude;
    public double mLatitude;
    private Map<String, String> GlobalSession; // 全局Session

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

        // 获取当前用户位置
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.setAK("60b43d1a9513d904b6aa2948b27b4a20");
        mLocationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceivePoi(BDLocation arg0) {

            }

            @Override
            public void onReceiveLocation(BDLocation arg0) {
                mLongitude = arg0.getLongitude();
                mLatitude = arg0.getLatitude();
                Log.i("地理位置", "经度:" + mLongitude + ",纬度:" + mLatitude);
                mLocationClient.stop();
            }
        });
        mLocationClient.start();
        mLocationClient.requestOfflineLocation();
        System.out.println("开始获取");
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
        return GlobalSession.get("localIPaddress");
    }

    /**
     * 获取热点IP
     * 
     * @return serverIPaddress
     */
    public String getServerIPaddress() {
        return GlobalSession.get("serverIPaddress");
    }

    /**
     * 获取昵称
     * 
     * @return Nickname
     */
    public String getNickname() {
        return GlobalSession.get("Nickname");
    }

    /**
     * 获取性别
     * 
     * @return Gender
     */
    public String getGender() {
        return GlobalSession.get("Gender");
    }

    /**
     * 获取IMEI
     * 
     * @return IMEI
     */
    public String getIMEI() {
        return GlobalSession.get("IMEI");
    }

    /**
     * 获取数据库用户编号
     * 
     * @return id
     */
    public int getID() {
        return Integer.parseInt(GlobalSession.get("ID"));
    }

    /**
     * 获取头像编号
     * 
     * @return AvatarNum
     */
    public int getAvatar() {
        return Integer.parseInt(GlobalSession.get("Avatar"));
    }

    /**
     * 获取登录状态编码
     * 
     * @return OnlineStateInt
     */
    public int getOnlineStateInt() {
        return Integer.parseInt(GlobalSession.get("OnlineStateInt"));
    }

    /**
     * 获取是否为客户端
     * 
     * @return isClient
     */
    public boolean getIsClient() {
        return Boolean.parseBoolean(GlobalSession.get("isClient"));
    }

    /**
     * 设置本地IP
     * 
     * @param paramLocalIPaddress
     *            本地IP地址值
     */
    public void setLocalIPaddress(String paramLocalIPaddress) {
        GlobalSession.put("localIPaddress", paramLocalIPaddress);
    }

    /**
     * 设置热点IP
     * 
     * @param paramServerIPaddress
     *            热点IP地址值
     */
    public void setServerIPaddress(String paramServerIPaddress) {
        GlobalSession.put("serverIPaddress", paramServerIPaddress);
    }

    /**
     * 设置昵称
     * 
     * @param paramNickname
     *            设置的昵称
     */
    public void setNickname(String paramNickname) {
        GlobalSession.put("Nickname", paramNickname);
    }

    /**
     * 设置性别
     * 
     * @param paramGender
     *            设置的性别
     */
    public void setGender(String paramGender) {
        GlobalSession.put("Gender", paramGender);
    }

    /**
     * 设置IMEI
     * 
     * @param paramIMEI
     *            本机的IMEI值
     */
    public void setIMEI(String paramIMEI) {
        GlobalSession.put("IMEI", paramIMEI);
    }

    /**
     * 设置用户ID
     * 
     * @param paramID
     *            该用户IMEI在数据库中对应的id编号
     */
    public void setID(int paramID) {
        GlobalSession.put("ID", String.valueOf(paramID));
    }

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
        GlobalSession
                .put("OnlineStateInt", String.valueOf(paramOnlineStateInt));
    }

    /**
     * 设置头像编号
     * 
     * @param paramAvatar
     *            选择的头像编号
     */
    public void setAvatar(int paramAvatar) {
        GlobalSession.put("Avatar", String.valueOf(paramAvatar));
    }

    /**
     * 设置是否为客户端
     * 
     * @param paramIsClient
     */
    public void setIsClient(boolean paramIsClient) {
        GlobalSession.put("isClient", String.valueOf(paramIsClient));
    }

    /** 清空全局登陆Session信息 **/
    public void clearSession() {
        GlobalSession.put("ID", "0");
        GlobalSession.put("Nickname", null);
        GlobalSession.put("Gender", null);
        GlobalSession.put("IMEI", null);
        GlobalSession.put("Avatar", "0");
        GlobalSession.put("OnlineStateInt", "0");
        GlobalSession.put("isClient", "false");
        GlobalSession.put("localIPaddress", "0.0.0.0");
        GlobalSession.put("serverIPaddress", "0.0.0.0");
        Log.i("SZU_BaseApplication", "clearSession");
    }
    
}

