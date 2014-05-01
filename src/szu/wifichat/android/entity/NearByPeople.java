package szu.wifichat.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import szu.wifichat.android.R;

/**
 * @fileName NearByPeople.java
 * @description 附近个人实体类
 * @author _Hill3
 */
public class NearByPeople extends Entity implements Parcelable {

    /** 用户信息常量 **/
    public static final String ID = "ID";
    public static final String NICKNAME = "Nickname";
    public static final String GENDER = "Gender";
    public static final String IMEI = "IMEI";
    public static final String DEVICE = "Device";
    public static final String AVATAR = "avatar";
    public static final String BIRTHDAY = "birthday";
    public static final String AGE = "Age";    
    public static final String CONSTELLATION = "Constellation";
    public static final String ONLINESTATEINT = "OnlineStateInt";
    public static final String ISCLIENT = "isClient";
    public static final String IPADDRESS = "Ipaddress";
    public static final String SERVERIPADDRESS = "serverIPaddress";
    public static final String LOGINTIME = "LoginTime";
    public static final String ENTITY_PEOPLE = "entity_people";

    private String mIMEI; // IMEI
    private int mAvatar; // 头像
    private String mDevice; // 设备 Android PC
    private String mNickname; // 昵称
    private String mConstellation; // 星座
    private String mGender; // 性别
    private int mGenderId; // 性别对应的图片资源ResId
    private int mGenderBgId; // 性别对应的背景资源ResId
    private int mAge; // 年龄
    private String mIpaddress; // IP地址
    private String mLogintime;// 登陆时间
    private int onlineStateInt; // 在线状态
    private int msgCount; // 未接收消息数

    public NearByPeople() {
        msgCount = 0; // 初始化为0
    }

    public NearByPeople(String paramIMEI, int paramAvatar, String paramDevice,
            String paramNickname, String paramGender, int paramAge, String paramConstellation,
            String paramIP, String paramLogintime) {
        super();
        this.mIMEI = paramIMEI;
        this.mAvatar = paramAvatar;
        this.mDevice = paramDevice;
        this.mNickname = paramNickname;
        setGender(paramGender);
        this.mAge = paramAge;
        this.mConstellation = paramConstellation;
        this.mIpaddress = paramIP;
        this.mLogintime = paramLogintime;
    }

    @JSONField(name = NearByPeople.IMEI)
    public String getIMEI() {
        return this.mIMEI;
    }

    public void setIMEI(String paramIMEI) {
        this.mIMEI = paramIMEI;
    }

    @JSONField(name = NearByPeople.AVATAR)
    public int getAvatar() {
        return this.mAvatar;
    }

    public void setAvatar(int paramAvatar) {
        this.mAvatar = paramAvatar;
    }

    @JSONField(name = NearByPeople.DEVICE)
    public String getDevice() {
        return this.mDevice;
    }

    public void setDevice(String paramDevice) {
        this.mDevice = paramDevice;
    }

    @JSONField(name = NearByPeople.NICKNAME)
    public String getNickname() {
        return this.mNickname;
    }

    public void setNickname(String paramNickname) {
        this.mNickname = paramNickname;
    }

    @JSONField(name = NearByPeople.GENDER)
    public String getGender() {
        return this.mGender;
    }

    public void setGender(String paramGender) {
        this.mGender = paramGender;
        if ("女".equals(paramGender)) {
            setGenderId(R.drawable.ic_user_famale);
            setGenderBgId(R.drawable.bg_gender_famal);
        }
        else {
            setGenderId(R.drawable.ic_user_male);
            setGenderBgId(R.drawable.bg_gender_male);
        }
    }

    @JSONField(serialize = false)
    public int getGenderId() {
        return this.mGenderId;
    }

    public void setGenderId(int paramGenderId) {
        this.mGenderId = paramGenderId;
    }

    @JSONField(serialize = false)
    public int getGenderBgId() {
        return this.mGenderBgId;
    }

    public void setGenderBgId(int paramGenderBgId) {
        this.mGenderBgId = paramGenderBgId;
    }

    @JSONField(name = NearByPeople.AGE)
    public int getAge() {
        return this.mAge;
    }

    public void setAge(int paramAge) {
        this.mAge = paramAge;
    }

    @JSONField(name = NearByPeople.CONSTELLATION)
    public String getConstellation() {
        return this.mConstellation;
    }

    public void setConstellation(String paramConstellation) {
        this.mConstellation = paramConstellation;
    }

    @JSONField(name = NearByPeople.IPADDRESS)
    public String getIpaddress() {
        return this.mIpaddress;
    }

    public void setIpaddress(String paramIpaddress) {
        this.mIpaddress = paramIpaddress;
    }

    @JSONField(name = NearByPeople.LOGINTIME)
    public String getLogintime() {
        return this.mLogintime;
    }

    public void setLogintime(String paramLogintime) {
        this.mLogintime = paramLogintime;
    }

    @JSONField(name = NearByPeople.ONLINESTATEINT)
    public int getOnlineStateInt() {
        return this.onlineStateInt;
    }

    public void setOnlineStateInt(int paramOnlineState) {
        this.onlineStateInt = paramOnlineState;
    }

    @JSONField(serialize = false)
    public int getMsgCount() {
        return this.msgCount;
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
        dest.writeString(mConstellation);
        dest.writeString(mIpaddress);
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
            people.setConstellation(source.readString());
            people.setIpaddress(source.readString());
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
