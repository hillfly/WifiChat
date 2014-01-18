package com.immomo.momo.android.entity;

import com.immomo.momo.android.R;

/**
 * @fileName NearByPeople.java
 * @description 附近个人实体类
 * @author _Hill3
 */
public class CopyOfNearByPeople extends Entity {

    public static final String IMEI = "imei";
    public static final String AVATAR = "avatar";
    public static final String DEVICE = "device";
    public static final String NICKNAME = "nickname";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String IP = "ip";
    public static final String LOGINTIME = "logintime";
    // public static final String SIGN = "sign";

    private String mIMEI; // IMEI
    private int mAvatar; // 头像
    private String mDevice; // 设备 Android PC
    private String mNickname; // 昵称
    private String mGender; // 性别
    private int mGenderId; // 性别对应的图片资源ResId
    private int mGenderBgId; // 性别对应的背景资源ResId
    private int mAge; // 年龄
    private String mIP; // IP地址
    private String mLogintime;// 登陆时间
    // private String mSign; // 个人签名
    private int msgCount; // 未接收消息数

    public CopyOfNearByPeople() {
        msgCount = 0; // 初始化为0
    }

    public CopyOfNearByPeople(String paramIMEI, int paramAvatar, String paramDevice,
            String paramNickname, String paramGender, int paramAge, String paramIP,
            String paramLogintime) {
        super();
        this.mIMEI = paramIMEI;
        this.mAvatar = paramAvatar;
        this.mDevice = paramDevice;
        this.mNickname = paramNickname;
        this.mGender = paramGender;
        this.mAge = paramAge;
        this.mIP = paramIP;
        this.mLogintime = paramLogintime;
        // this.mSign = paramSign;
        if (mGender.equals("女")) {
            setGenderId(R.drawable.ic_user_famale);
            setGenderBgId(R.drawable.bg_gender_famal);
        }
        else {
            setGenderId(R.drawable.ic_user_male);
            setGenderBgId(R.drawable.bg_gender_male);
        }
    }

    public String getIMEI() {
        return mIMEI;
    }

    public void setIMEI(String paramIMEI) {
        this.mIMEI = paramIMEI;
    }

    public int getAvatar() {
        return mAvatar;
    }

    public void setAvatar(int paramAvatar) {
        this.mAvatar = paramAvatar;
    }

    public String getDevice() {
        return mDevice;
    }

    public void setDevice(String paramDevice) {
        this.mDevice = paramDevice;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String paramNickname) {
        this.mNickname = paramNickname;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String paramGender) {
        this.mGender = paramGender;
    }

    public int getGenderId() {
        return mGenderId;
    }

    public void setGenderId(int paramGenderId) {
        this.mGenderId = paramGenderId;
    }

    public int getGenderBgId() {
        return mGenderBgId;
    }

    public void setGenderBgId(int paramGenderBgId) {
        this.mGenderBgId = paramGenderBgId;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int paramAge) {
        this.mAge = paramAge;
    }

    public String getIP() {
        return mIP;
    }

    public void setIP(String paramIP) {
        this.mIP = paramIP;
    }

    public String getLogintime() {
        return mLogintime;
    }

    public void setLogintime(String paramLogintime) {
        this.mLogintime = paramLogintime;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int paramMsgCount) {
        this.msgCount = paramMsgCount;
    }

    // public String getSign() {
    // return mSign;
    // }

    // public void setSign(String paramSign) {
    // this.mSign = paramSign;
    // }

}
