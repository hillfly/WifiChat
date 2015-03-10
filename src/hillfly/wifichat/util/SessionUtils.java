package hillfly.wifichat.util;

import hillfly.wifichat.bean.Users;

import java.util.HashMap;

public class SessionUtils {

    private static Users localUserInfo;
    private static HashMap<String, String> mlocalUserSession = new HashMap<String, String>(15);

    public static Users getLocalUserInfo() {
        if (localUserInfo == null) {
            localUserInfo = new Users(getAge(), getAvatar(), getOnlineStateInt(), getNickname(),
                    getGender(), getIMEI(), getDevice(), getBirthday(), getConstellation(),
                    getLocalIPaddress(), getLoginTime());

        }
        return localUserInfo;
    }

    public static void setLocalUserInfo(Users pUsers) {
        localUserInfo = pUsers;
        mlocalUserSession.put(Users.AGE, String.valueOf(pUsers.getAge()));
        mlocalUserSession.put(Users.AVATAR, String.valueOf(pUsers.getAvatar()));
        mlocalUserSession.put(Users.ONLINESTATEINT, String.valueOf(pUsers.getOnlineStateInt()));
        mlocalUserSession.put(Users.NICKNAME, pUsers.getNickname());
        mlocalUserSession.put(Users.GENDER, pUsers.getGender());
        mlocalUserSession.put(Users.IMEI, pUsers.getIMEI());
        mlocalUserSession.put(Users.DEVICE, pUsers.getDevice());
        mlocalUserSession.put(Users.BIRTHDAY, pUsers.getBirthday());
        mlocalUserSession.put(Users.CONSTELLATION, pUsers.getConstellation());
        mlocalUserSession.put(Users.IPADDRESS, pUsers.getIpaddress());
        mlocalUserSession.put(Users.LOGINTIME, pUsers.getLogintime());

    }

    public static void updateUserInfo() {
        localUserInfo = new Users(getAge(), getAvatar(), getOnlineStateInt(), getNickname(),
                getGender(), getIMEI(), getDevice(), getBirthday(), getConstellation(),
                getLocalIPaddress(), getLoginTime());
    }

    public static String getBirthday() {
        return mlocalUserSession.get(Users.BIRTHDAY);
    }

    /**
     * 获取用户数据库id
     * 
     * @return
     */
    public static int getLocalUserID() {
        return Integer.parseInt(mlocalUserSession.get(Users.ID));
    }

    /**
     * 获取本地IP
     * 
     * @return localIPaddress
     */
    public static String getLocalIPaddress() {
        return mlocalUserSession.get(Users.IPADDRESS);
    }

    /**
     * 获取热点IP
     * 
     * @return serverIPaddress
     */
    public static String getServerIPaddress() {
        return mlocalUserSession.get(Users.SERVERIPADDRESS);
    }

    /**
     * 获取昵称
     * 
     * @return Nickname
     */
    public static String getNickname() {
        return mlocalUserSession.get(Users.NICKNAME);
    }

    /**
     * 获取性别
     * 
     * @return Gender
     */
    public static String getGender() {
        return mlocalUserSession.get(Users.GENDER);
    }

    /**
     * 获取IMEI
     * 
     * @return IMEI
     */
    public static String getIMEI() {
        return mlocalUserSession.get(Users.IMEI);
    }

    /**
     * 获取设备品牌型号
     * 
     * @return device
     */
    public static String getDevice() {
        return mlocalUserSession.get(Users.DEVICE);
    }

    /**
     * 获取头像编号
     * 
     * @return AvatarNum
     */
    public static int getAvatar() {
        return Integer.parseInt(mlocalUserSession.get(Users.AVATAR));
    }

    /**
     * 获取星座
     * 
     * @return
     */
    public static String getConstellation() {
        return mlocalUserSession.get(Users.CONSTELLATION);
    }

    /**
     * 获取年龄
     * 
     * @return Age
     */
    public static int getAge() {
        return Integer.parseInt(mlocalUserSession.get(Users.AGE));
    }

    /**
     * 获取登录状态编码
     * 
     * @return OnlineStateInt
     */
    public static int getOnlineStateInt() {
        return Integer.parseInt(mlocalUserSession.get(Users.ONLINESTATEINT));
    }

    /**
     * 获取是否为客户端
     * 
     * @return isClient
     */
    public static boolean getIsClient() {
        return Boolean.parseBoolean(mlocalUserSession.get(Users.ISCLIENT));
    }

    /**
     * 获取登录时间
     * 
     * @return Data 登录时间 年月日
     */
    public static String getLoginTime() {
        return mlocalUserSession.get(Users.LOGINTIME);
    }

    public static void setBirthday(String birthday) {
        mlocalUserSession.put(Users.BIRTHDAY, birthday);
    }

    /**
     * 设置用户数据库id
     * 
     * @param paramID
     */
    public static void setLocalUserID(int paramID) {
        mlocalUserSession.put(Users.ID, String.valueOf(paramID));
    }

    /**
     * 设置登录时间
     * 
     * @param paramLoginTime
     */
    public static void setLoginTime(String paramLoginTime) {
        mlocalUserSession.put(Users.LOGINTIME, paramLoginTime);
    }

    /**
     * 设置本地IP
     * 
     * @param paramLocalIPaddress
     *            本地IP地址值
     */
    public static void setLocalIPaddress(String paramLocalIPaddress) {
        mlocalUserSession.put(Users.IPADDRESS, paramLocalIPaddress);
    }

    /**
     * 设置热点IP
     * 
     * @param paramServerIPaddress
     *            热点IP地址值
     */
    public static void setServerIPaddress(String paramServerIPaddress) {
        mlocalUserSession.put(Users.SERVERIPADDRESS, paramServerIPaddress);
    }

    /**
     * 设置昵称
     * 
     * @param paramNickname
     * 
     */
    public static void setNickname(String paramNickname) {
        mlocalUserSession.put(Users.NICKNAME, paramNickname);
    }

    /**
     * 设置星座
     * 
     * @param paramConstellation
     */
    public static void setConstellation(String paramConstellation) {
        mlocalUserSession.put(Users.CONSTELLATION, paramConstellation);
    }

    /**
     * 设置性别
     * 
     * @param paramGender
     * 
     */
    public static void setGender(String paramGender) {
        mlocalUserSession.put(Users.GENDER, paramGender);
    }

    /**
     * 设置IMEI
     * 
     * @param paramIMEI
     *            本机的IMEI值
     */
    public static void setIMEI(String paramIMEI) {
        mlocalUserSession.put(Users.IMEI, paramIMEI);
    }

    /**
     * 设置设备品牌型号
     * 
     * @param paramDevice
     */
    public static void setDevice(String paramDevice) {
        mlocalUserSession.put(Users.DEVICE, paramDevice);
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
    public static void setOnlineStateInt(int paramOnlineStateInt) {
        mlocalUserSession.put(Users.ONLINESTATEINT, String.valueOf(paramOnlineStateInt));
    }

    /**
     * 设置头像编号
     * 
     * @param paramAvatar
     *            选择的头像编号
     */
    public static void setAvatar(int paramAvatar) {
        mlocalUserSession.put(Users.AVATAR, String.valueOf(paramAvatar));
    }

    /**
     * 设置年龄
     * 
     * @param paramAge
     */
    public static void setAge(int paramAge) {
        mlocalUserSession.put(Users.AGE, String.valueOf(paramAge));
    }

    /**
     * 设置是否为客户端
     * 
     * @param paramIsClient
     */
    public static void setIsClient(boolean paramIsClient) {
        mlocalUserSession.put(Users.ISCLIENT, String.valueOf(paramIsClient));
    }

    public static boolean isLocalUser(String paramIMEI) {
        if (paramIMEI == null) {
            return false;
        }
        else if (getIMEI().equals(paramIMEI)) {
            return true;
        }
        return false;
    }

    /** 清空全局登陆Session信息 **/
    public static void clearSession() {
        mlocalUserSession.clear();
    }

}
