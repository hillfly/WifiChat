package com.immomo.momo.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.socket.UDPSocketThread;
import com.immomo.momo.android.socket.UDPSocketThread.OnReceiveMsgListener;

public class BaseApplication extends Application {

    public static String FORMATTIMESTR = "yyyy年MM月dd日 HH:mm:ss"; // 时间格式化格式

    /** mEmoticons 表情 **/
    public static Map<String, Integer> mEmoticonsId = new HashMap<String, Integer>();
    public static List<String> mEmoticons = new ArrayList<String>();
    public static List<String> mEmoticons_Zem = new ArrayList<String>();
    public static List<String> mEmoticons_Zemoji = new ArrayList<String>();

    /** 缓存 **/
    public Map<String, SoftReference<Bitmap>> mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
    public Map<String, NearByPeople> OnlineUsers; // 当前所有用户的集合，以IMEI为KEY
    public Map<String, String> LastMsgCache; // 最后一条消息，以IMEI为KEY
    public ConcurrentLinkedQueue<Message> ReceiveMessages; // 未读消息队列
    public Vector<OnReceiveMsgListener> mReceiveMsgListener; // ReceiveMsgListener容器

    // public List<NearByPeople> mNearByPeoples = new ArrayList<NearByPeople>();
    public List<NearByGroup> mNearByGroups = new ArrayList<NearByGroup>();

    public double mLongitude;
    public double mLatitude;
    private Bitmap mDefaultAvatar;

    public UDPSocketThread mUDPSocketThread; // UDP Socket线程类
    private Map<String, String> GlobalSession; // 全局Session
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
            } else {
                return reference.get();
            }
        } else {
            return getAvatarFromRes(paramAvatarName);
        }
    }

    public Bitmap getAvatarFromRes(String paramAvatarName) {
        Bitmap returnBitmap = null;
        int avatarID = 0;
        Resources res = null;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            res = this.getResources();
            avatarID = res.getIdentifier(paramAvatarName, "drawable",
                    getPackageName());
            is = res.openRawResource(avatarID);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null) {
                throw new FileNotFoundException(paramAvatarName + "is not find");
            }
            mAvatarCache.put(paramAvatarName, new SoftReference<Bitmap>(bitmap));
            returnBitmap = bitmap;
        } catch (Exception e) {
            returnBitmap = mDefaultAvatar;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {

            }
        }
        return returnBitmap;

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
        return GlobalSession.get(NearByPeople.LOCALIPADDRESS);
    }

    /**
     * 获取热点IP
     * 
     * @return serverIPaddress
     */
    public String getServerIPaddress() {
        return GlobalSession.get(NearByPeople.SERVERIPADDRESS);
    }

    /**
     * 获取昵称
     * 
     * @return Nickname
     */
    public String getNickname() {
        return GlobalSession.get(NearByPeople.NICKNAME);
    }

    /**
     * 获取性别
     * 
     * @return Gender
     */
    public String getGender() {
        return GlobalSession.get(NearByPeople.GENDER);
    }

    /**
     * 获取IMEI
     * 
     * @return IMEI
     */
    public String getIMEI() {
        return GlobalSession.get(NearByPeople.IMEI);
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
     * 获取设备品牌型号
     * 
     * @return device
     */
    public String getDevice() {
        return GlobalSession.get(NearByPeople.DEVICE);
    }

    /**
     * 获取头像编号
     * 
     * @return AvatarNum
     */
    public int getAvatar() {
        return Integer.parseInt(GlobalSession.get(NearByPeople.AVATAR));
    }

    /**
     * 获取年龄
     * 
     * @return Age
     */
    public int getAge() {
        return Integer.parseInt(GlobalSession.get(NearByPeople.AGE));
    }

    /**
     * 获取登录状态编码
     * 
     * @return OnlineStateInt
     */
    public int getOnlineStateInt() {
        return Integer.parseInt(GlobalSession.get(NearByPeople.ONLINESTATEINT));
    }

    /**
     * 获取是否为客户端
     * 
     * @return isClient
     */
    public boolean getIsClient() {
        return Boolean.parseBoolean(GlobalSession.get(NearByPeople.ISCLIENT));
    }

    /**
     * 获取登录时间
     * 
     * @return Data 登录时间 年月日
     */
    public String getLoginTime() {
        return GlobalSession.get(NearByPeople.LOGINTIME);
    }

    /**
     * 设置登录时间
     * 
     * @param paramLoginTime
     */
    public void setLoginTime(String paramLoginTime) {
        GlobalSession.put(NearByPeople.LOGINTIME, paramLoginTime);
    }

    /**
     * 设置本地IP
     * 
     * @param paramLocalIPaddress
     *            本地IP地址值
     */
    public void setLocalIPaddress(String paramLocalIPaddress) {
        GlobalSession.put(NearByPeople.LOCALIPADDRESS, paramLocalIPaddress);
    }

    /**
     * 设置热点IP
     * 
     * @param paramServerIPaddress
     *            热点IP地址值
     */
    public void setServerIPaddress(String paramServerIPaddress) {
        GlobalSession.put(NearByPeople.SERVERIPADDRESS, paramServerIPaddress);
    }

    /**
     * 设置昵称
     * 
     * @param paramNickname
     *            设置的昵称
     */
    public void setNickname(String paramNickname) {
        GlobalSession.put(NearByPeople.NICKNAME, paramNickname);
    }

    /**
     * 设置性别
     * 
     * @param paramGender
     *            设置的性别
     */
    public void setGender(String paramGender) {
        GlobalSession.put(NearByPeople.GENDER, paramGender);
    }

    /**
     * 设置IMEI
     * 
     * @param paramIMEI
     *            本机的IMEI值
     */
    public void setIMEI(String paramIMEI) {
        GlobalSession.put(NearByPeople.IMEI, paramIMEI);
    }

    /**
     * 设置设备品牌型号
     * 
     * @param paramDevice
     */
    public void setDevice(String paramDevice) {
        GlobalSession.put(NearByPeople.DEVICE, paramDevice);
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
        GlobalSession.put(NearByPeople.ONLINESTATEINT,
                String.valueOf(paramOnlineStateInt));
    }

    /**
     * 设置头像编号
     * 
     * @param paramAvatar
     *            选择的头像编号
     */
    public void setAvatar(int paramAvatar) {
        GlobalSession.put(NearByPeople.AVATAR, String.valueOf(paramAvatar));
    }

    /**
     * 设置年龄
     * 
     * @param paramAge
     */
    public void setAge(int paramAge) {
        GlobalSession.put(NearByPeople.AGE, String.valueOf(paramAge));
    }

    /**
     * 设置是否为客户端
     * 
     * @param paramIsClient
     */
    public void setIsClient(boolean paramIsClient) {
        GlobalSession.put(NearByPeople.ISCLIENT, String.valueOf(paramIsClient));
    }

    /** 清空全局登陆Session信息 **/
    public void clearSession() {
        GlobalSession.clear();
    }

    /**
     * 返回此时时间
     * 
     * @return String: XXX年XX月XX日 XX:XX:XX
     */
    public String getNowtime() {
        return new SimpleDateFormat(FORMATTIMESTR).format(new Date());
    }

    /**
     * 格式化输出指定时间点与现在的差
     * 
     * @param paramTime
     *            指定的时间点
     * @return 格式化后的时间差，类似 X秒前、X小时前、X年前
     */
    public String getBetweentime(String paramTime) {
        String returnStr = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATTIMESTR);
        try {
            Date nowData = new Date();
            Date mDate = dateFormat.parse(paramTime);
            long betweenForSec = Math.abs(mDate.getTime() - nowData.getTime()) / 1000; // 秒
            if (betweenForSec < 60) {
                returnStr = betweenForSec + "秒前";
            } else if (betweenForSec < (60 * 60)) {
                returnStr = betweenForSec / 60 + "分钟前";
            } else if (betweenForSec < (60 * 60 * 24)) {
                returnStr = betweenForSec / (60 * 60) + "小时前";
            } else if (betweenForSec < (60 * 60 * 24 * 30)) {
                returnStr = betweenForSec / (60 * 60 * 24) + "天前";
            } else if (betweenForSec < (60 * 60 * 24 * 30 * 12)) {
                returnStr = betweenForSec / (60 * 60 * 24 * 30) + "个月前";
            } else
                returnStr = betweenForSec / (60 * 60 * 24 * 30 * 12) + "年前";
        } catch (ParseException e) {
            returnStr = "TimeError"; // 错误提示
        }
        return returnStr;
    }

}
