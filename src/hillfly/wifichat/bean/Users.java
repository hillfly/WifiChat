package hillfly.wifichat.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import hillfly.wifichat.R;

/**
 * @fileName NearByPeople.java
 * @description 附近个人实体类
 * @author _Hill3
 */
public class Users extends Entity implements Parcelable {

    /** 用户常量 **/

    // 共有
    public static final String AGE = "Age";
    public static final String AVATAR = "avatar";
    public static final String ONLINESTATEINT = "OnlineStateInt";
    public static final String NICKNAME = "Nickname";
    public static final String GENDER = "Gender";
    public static final String IMEI = "IMEI";
    public static final String DEVICE = "Device";
    public static final String BIRTHDAY = "birthday";
    public static final String CONSTELLATION = "Constellation";
    public static final String IPADDRESS = "Ipaddress";
    public static final String LOGINTIME = "LoginTime";

    // 个人
    public static final String ID = "ID";
    public static final String ISCLIENT = "isClient";
    public static final String SERVERIPADDRESS = "serverIPaddress";
    public static final String ENTITY_PEOPLE = "entity_people";

    private int mAge;
    private int mAvatar;
    private int mOnlineStateInt;
    private String mNickname;
    private String mGender;
    private String mIMEI;
    private String mDevice;
    private String mBirthday;
    private String mConstellation;
    private String mIpaddress;
    private String mLogintime;

    private int mGenderId;
    private int mGenderBgId;
    private int msgCount;

    public Users() {
        this.msgCount = 0;
    }

    public Users(int age, int avatar, int onlinestate, String nickname, String gender, String IMEI,
            String device, String birthday, String constellation, String ip, String logintime) {
        this.mAge = age;
        this.mAvatar = avatar;
        this.mOnlineStateInt = onlinestate;
        this.mNickname = nickname;
        this.setGender(gender);
        this.mIMEI = IMEI;
        this.mDevice = device;
        this.mBirthday = birthday;
        this.mConstellation = constellation;
        this.mIpaddress = ip;
        this.mLogintime = logintime;

    }

    /** 共用变量 get set **/

    @JSONField(name = Users.AGE)
    public int getAge() {
        return this.mAge;
    }

    @JSONField(name = Users.AVATAR)
    public int getAvatar() {
        return this.mAvatar;
    }

    @JSONField(name = Users.ONLINESTATEINT)
    public int getOnlineStateInt() {
        return this.mOnlineStateInt;
    }

    @JSONField(name = Users.NICKNAME)
    public String getNickname() {
        return this.mNickname;
    }

    @JSONField(name = Users.GENDER)
    public String getGender() {
        return this.mGender;
    }

    @JSONField(name = Users.IMEI)
    public String getIMEI() {
        return this.mIMEI;
    }

    @JSONField(name = Users.DEVICE)
    public String getDevice() {
        return this.mDevice;
    }

    @JSONField(name = Users.BIRTHDAY)
    public String getBirthday() {
        return this.mBirthday;
    }

    @JSONField(name = Users.CONSTELLATION)
    public String getConstellation() {
        return this.mConstellation;
    }

    @JSONField(name = Users.IPADDRESS)
    public String getIpaddress() {
        return this.mIpaddress;
    }

    @JSONField(name = Users.LOGINTIME)
    public String getLogintime() {
        return this.mLogintime;
    }

    public void setAge(int paramAge) {
        this.mAge = paramAge;
    }

    public void setAvatar(int paramAvatar) {
        this.mAvatar = paramAvatar;
    }

    public void setOnlineStateInt(int paramOnlineState) {
        this.mOnlineStateInt = paramOnlineState;
    }

    public void setNickname(String paramNickname) {
        this.mNickname = paramNickname;
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

    public void setIMEI(String paramIMEI) {
        this.mIMEI = paramIMEI;
    }

    public void setDevice(String paramDevice) {
        this.mDevice = paramDevice;
    }

    public void setBirthday(String paramBirthday) {
        this.mBirthday = paramBirthday;
    }

    public void setConstellation(String paramConstellation) {
        this.mConstellation = paramConstellation;
    }

    public void setIpaddress(String paramIpaddress) {
        this.mIpaddress = paramIpaddress;
    }

    public void setLogintime(String paramLogintime) {
        this.mLogintime = paramLogintime;
    }

    /** 个人变量 get set **/

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
        dest.writeInt(mAge);
        dest.writeInt(mAvatar);
        dest.writeInt(mOnlineStateInt);
        dest.writeString(mNickname);
        dest.writeString(mGender);
        dest.writeString(mIMEI);
        dest.writeString(mDevice);
        dest.writeString(mBirthday);
        dest.writeString(mConstellation);
        dest.writeString(mIpaddress);
        dest.writeString(mLogintime);
        dest.writeInt(msgCount);
    }

    public static final Parcelable.Creator<Users> CREATOR = new Parcelable.Creator<Users>() {

        @Override
        public Users createFromParcel(Parcel source) {
            Users user = new Users();
            user.setAge(source.readInt());
            user.setAvatar(source.readInt());
            user.setOnlineStateInt(source.readInt());
            user.setNickname(source.readString());
            user.setGender(source.readString());
            user.setIMEI(source.readString());
            user.setDevice(source.readString());
            user.setBirthday(source.readString());
            user.setConstellation(source.readString());
            user.setIpaddress(source.readString());
            user.setLogintime(source.readString());
            user.setMsgCount(source.readInt());
            return user;
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
