package com.immomo.momo.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.immomo.momo.android.R;

/**
 * @fileName NearByPeople.java
 * @package com.immomo.momo.android.entity
 * @description 附近个人实体类
 * @author 任东卫
 * @email 86930007@qq.com
 * @version 1.0
 */
public class NearByPeople extends Entity implements Parcelable {

	public static final String UID = "uid";
	public static final String AVATAR = "avatar";
	public static final String VIP = "vip";
	public static final String GROUP_ROLE = "group_role";
	public static final String INDUSTRY = "industry";
	public static final String WEIBO = "weibo";
	public static final String TX_WEIBO = "tx_weibo";
	public static final String RENREN = "renren";
	public static final String DEVICE = "device";
	public static final String RELATION = "relation";
	public static final String MULTIPIC = "multipic";
	public static final String NAME = "name";
	public static final String GENDER = "gender";
	public static final String AGE = "age";
	public static final String DISTANCE = "distance";
	public static final String TIME = "time";
	public static final String SIGN = "sign";

	private String uid;// ID
	private String avatar;// 头像
	private int isVip;// Vip 0-不是Vip,1-是Vip
	private int isGroupRole;// 群成员 0-未加入群,1-群主,2-群成员
	private String industry;// 行业
	private int isbindWeibo;// 绑定新浪微博 0-未绑定,1-VIP绑定，2-已绑定
	private int isbindTxWeibo;// 绑定腾讯微博 0-未绑定,1-VIP绑定，2-已绑定
	private int isbindRenRen;// 绑定人人 0-未绑定,1-已绑定
	private int device;// 设备 0-未绑定,1-Android,2-IPhone
	private int isRelation;// 好友关系 0-非好友,1-好友
	private int isMultipic;// 照片 0-无照片,1-有照片

	private String name;// 姓名
	private int gender;// 性别 0-女，1-男
	private int genderId;// 性别对应的图片资源ResId
	private int genderBgId;// 性别对应的背景资源ResId
	private int age;// 年龄
	private String distance;// 距离
	private String time;// 时间
	private String sign;// 签名

	public NearByPeople(String uid, String avatar, int isVip, int isGroupRole,
			String industry, int isbindWeibo, int isbindTxWeibo,
			int isbindRenRen, int device, int isRelation, int isMultipic,
			String name, int gender, int age, String distance, String time,
			String sign) {
		super();
		this.uid = uid;
		this.avatar = avatar;
		this.isVip = isVip;
		this.isGroupRole = isGroupRole;
		this.industry = industry;
		this.isbindWeibo = isbindWeibo;
		this.isbindTxWeibo = isbindTxWeibo;
		this.isbindRenRen = isbindRenRen;
		this.device = device;
		this.isRelation = isRelation;
		this.isMultipic = isMultipic;
		this.name = name;
		this.gender = gender;
		this.age = age;
		this.distance = distance;
		this.time = time;
		this.sign = sign;
		if (gender == 0) {
			setGenderId(R.drawable.ic_user_famale);
			setGenderBgId(R.drawable.bg_gender_famal);
		} else {
			setGenderId(R.drawable.ic_user_male);
			setGenderBgId(R.drawable.bg_gender_male);
		}
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getIsVip() {
		return isVip;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

	public int getIsGroupRole() {
		return isGroupRole;
	}

	public void setIsGroupRole(int isGroupRole) {
		this.isGroupRole = isGroupRole;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public int getIsbindWeibo() {
		return isbindWeibo;
	}

	public void setIsbindWeibo(int isbindWeibo) {
		this.isbindWeibo = isbindWeibo;
	}

	public int getIsbindTxWeibo() {
		return isbindTxWeibo;
	}

	public void setIsbindTxWeibo(int isbindTxWeibo) {
		this.isbindTxWeibo = isbindTxWeibo;
	}

	public int getIsbindRenRen() {
		return isbindRenRen;
	}

	public void setIsbindRenRen(int isbindRenRen) {
		this.isbindRenRen = isbindRenRen;
	}

	public int getDevice() {
		return device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public int getIsRelation() {
		return isRelation;
	}

	public void setIsRelation(int isRelation) {
		this.isRelation = isRelation;
	}

	public int getIsMultipic() {
		return isMultipic;
	}

	public void setIsMultipic(int isMultipic) {
		this.isMultipic = isMultipic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getGenderId() {
		return genderId;
	}

	public void setGenderId(int genderId) {
		this.genderId = genderId;
	}

	public int getGenderBgId() {
		return genderBgId;
	}

	public void setGenderBgId(int genderBgId) {
		this.genderBgId = genderBgId;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uid);
		dest.writeString(avatar);
		dest.writeInt(isVip);
		dest.writeInt(isGroupRole);
		dest.writeString(industry);
		dest.writeInt(isbindWeibo);
		dest.writeInt(isbindTxWeibo);
		dest.writeInt(isbindRenRen);
		dest.writeInt(device);
		dest.writeInt(isRelation);
		dest.writeInt(isMultipic);
		dest.writeString(name);
		dest.writeInt(gender);
		dest.writeInt(age);
		dest.writeString(distance);
		dest.writeString(time);
		dest.writeString(sign);
	}

	public static final Parcelable.Creator<NearByPeople> CREATOR = new Parcelable.Creator<NearByPeople>() {

		@Override
		public NearByPeople createFromParcel(Parcel source) {
			String uid = source.readString();
			String avatar = source.readString();
			int isVip = source.readInt();
			int isGroupRole = source.readInt();
			String industry = source.readString();
			int isbindWeibo = source.readInt();
			int isbindTxWeibo = source.readInt();
			int isbindRenRen = source.readInt();
			int device = source.readInt();
			int isRelation = source.readInt();
			int isMultipic = source.readInt();
			String name = source.readString();
			int gender = source.readInt();
			int age = source.readInt();
			String distance = source.readString();
			String time = source.readString();
			String sign = source.readString();
			NearByPeople people = new NearByPeople(uid, avatar, isVip,
					isGroupRole, industry, isbindWeibo, isbindTxWeibo,
					isbindRenRen, device, isRelation, isMultipic, name, gender,
					age, distance, time, sign);
			return people;
		}

		@Override
		public NearByPeople[] newArray(int size) {
			return new NearByPeople[size];
		}
	};
}
