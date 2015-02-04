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
public class Users extends Entity implements Parcelable {

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

    private String mIMEI;
    private int mAvatar;
    private String mDevice;
    private String mNickname;
    private String mConstellation;
    private String mGender;
    private int mGenderId;
    private int mGenderBgId;
    private int mAge;
    private String mIpaddress;
    private String mLogintime;
    private int onlineStateInt;
    private int msgCount;

    public Users() {
        msgCount = 0;
    }

    public Users(String paramIMEI, int paramAvatar, String paramDevice,
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

    @JSONField(name = Users.IMEI)
    public String getIMEI() {
        return this.mIMEI;
    }

    public void setIMEI(String paramIMEI) {
        this.mIMEI = paramIMEI;
    }

    @JSONField(name = Users.AVATAR)
    public int getAvatar() {
        return this.mAvatar;
    }

    public void setAvatar(int paramAvatar) {
        this.mAvatar = paramAvatar;
    }

    @JSONField(name = Users.DEVICE)
    public String getDevice() {
        return this.mDevice;
    }

    public void setDevice(String paramDevice) {
        this.mDevice = paramDevice;
    }

    @JSONField(name = Users.NICKNAME)
    public String getNickname() {
        return this.mNickname;
    }

    public void setNickname(String paramNickname) {
        this.mNickname = paramNickname;
    }

    @JSONField(name = Users.GENDER)
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

    @JSONField(name = Users.AGE)
    public int getAge() {
        return this.mAge;
    }

    public void setAge(int paramAge) {
        this.mAge = paramAge;
    }

    @JSONField(name = Users.CONSTELLATION)
    public String getConstellation() {
        return this.mConstellation;
    }

    public void setConstellation(String paramConstellation) {
        this.mConstellation = paramConstellation;
    }

    @JSONField(name = Users.IPADDRESS)
    public String getIpaddress() {
        return this.mIpaddress;
    }

    public void setIpaddress(String paramIpaddress) {
        this.mIpaddress = paramIpaddress;
    }

    @JSONField(name = Users.LOGINTIME)
    public String getLogintime() {
        return this.mLogintime;
    }

    public void setLogintime(String paramLogintime) {
        this.mLogintime = paramLogintime;
    }

    @JSONField(name = Users.ONLINESTATEINT)
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

    public static Parcelable.Creator<Users> getCreator() {
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

    public static final Parcelable.Creator<Users> CREATOR = new Parcelable.Creator<Users>() {

        @Override
        public Users createFromParcel(Parcel source) {
            Users people = new Users();
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
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
