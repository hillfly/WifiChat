package com.immomo.momo.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.immomo.momo.android.R;

/**
 * @fileName NearByPeople.java
 * @description 附近个人实体类
 * @author _Hill3
 */
public class NearByPeople extends Entity implements Parcelable {

    /** 用户信息常量 **/
    public static final String NICKNAME = "Nickname";
    public static final String GENDER = "Gender";
    public static final String IMEI = "IMEI";
    public static final String DEVICE = "Device";
    public static final String AVATAR = "avatar";
    public static final String AGE = "Age";
    public static final String ONLINESTATEINT = "OnlineStateInt";
    public static final String ISCLIENT = "isClient";
    public static final String LOCALIPADDRESS = "localIPaddress";
    public static final String SERVERIPADDRESS = "serverIPaddress";
    public static final String LOGINTIME = "LoginTime";
    public static final String ENTITY_PEOPLE = "entity_people";  
    public static final String ENTITY_PROFILE = "entity_profile";

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
    private int msgCount; // 未接收消息数

    public NearByPeople() {
        msgCount = 0; // 初始化为0
    }

    public NearByPeople(String paramIMEI, int paramAvatar, String paramDevice, String paramNickname,
            String paramGender, int paramAge, String paramIP, String paramLogintime) {
        super();
        this.mIMEI = paramIMEI;
        this.mAvatar = paramAvatar;
        this.mDevice = paramDevice;
        this.mNickname = paramNickname;
        setGender(paramGender);
        this.mAge = paramAge;
        this.mIP = paramIP;
        this.mLogintime = paramLogintime;        
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
        if ("女".equals(paramGender)) {
            setGenderId(R.drawable.ic_user_famale);
            setGenderBgId(R.drawable.bg_gender_famal);
        } else {
            setGenderId(R.drawable.ic_user_male);
            setGenderBgId(R.drawable.bg_gender_male);
        }
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
    
    public static Parcelable.Creator<NearByPeople> getCreator() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIMEI);
        dest.writeInt(mAvatar);
        dest.writeString(mDevice);
        dest.writeString(mNickname);
        dest.writeString(mGender);
        dest.writeInt(mAge);
        dest.writeString(mIP);
        dest.writeString(mLogintime);
        dest.writeInt(msgCount);
    }

    public static final Parcelable.Creator<NearByPeople> CREATOR = new Parcelable.Creator<NearByPeople>() {

        @Override
        public NearByPeople createFromParcel(Parcel source) {
            NearByPeople people = new NearByPeople();      
            people.setIMEI(source.readString());
            people.setAvatar(source.readInt());
            people.setDevice(source.readString());
            people.setNickname(source.readString());
            people.setGender(source.readString());
            people.setAge(source.readInt());
            people.setIP(source.readString());
            people.setLogintime(source.readString());  
            people.setMsgCount(source.readInt());
            return people;
        }

        @Override
        public NearByPeople[] newArray(int size) {
            return new NearByPeople[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
