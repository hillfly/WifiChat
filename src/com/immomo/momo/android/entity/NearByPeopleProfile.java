package com.immomo.momo.android.entity;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class NearByPeopleProfile extends Entity implements Parcelable {

	public static final String UID = "uid";
	public static final String AVATAR = "avatar";
	public static final String NAME = "name";
	public static final String GENDER = "gender";
	public static final String AGE = "age";
	public static final String CONSTELLATION = "constellation";
	public static final String DISTANCE = "distance";
	public static final String TIME = "time";
	public static final String SIGNATURE = "signature";
	public static final String SIGN = "sign";
	public static final String SIGN_PIC = "sign_pic";
	public static final String SIGN_DIS = "sign_dis";
	public static final String PHOTOS = "photos";

	private String uid;// ID
	private String avatar;// 头像
	private String name;// 姓名
	private int gender;// 性别 0-女，1-男
	private int genderId;// 性别对应的图片资源ResId
	private int genderBgId;// 性别对应的背景资源ResId
	private int age;// 年龄
	private String constellation;// 星座
	private String distance;// 距离
	private String time;// 时间
	private boolean isHasSign;// 是否有签名
	private String sign;// 签名
	private String signPicture;// 签名图片
	private String signDistance;// 签名距离
	private List<String> photos;// 照片

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

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
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

	public boolean isHasSign() {
		return isHasSign;
	}

	public void setHasSign(boolean isHasSign) {
		this.isHasSign = isHasSign;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSignPicture() {
		return signPicture;
	}

	public void setSignPicture(String signPicture) {
		this.signPicture = signPicture;
	}

	public String getSignDistance() {
		return signDistance;
	}

	public void setSignDistance(String signDistance) {
		this.signDistance = signDistance;
	}

	public static Parcelable.Creator<NearByPeopleProfile> getCreator() {
		return CREATOR;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uid);
		dest.writeString(avatar);
		dest.writeString(name);
		dest.writeInt(gender);
		dest.writeInt(genderId);
		dest.writeInt(genderBgId);
		dest.writeInt(age);
		dest.writeString(constellation);
		dest.writeString(distance);
		dest.writeString(time);
		dest.writeInt(isHasSign ? 1 : 0);
		dest.writeString(sign);
		dest.writeString(signPicture);
		dest.writeString(signDistance);
		dest.writeList(photos);
	}

	public static final Parcelable.Creator<NearByPeopleProfile> CREATOR = new Parcelable.Creator<NearByPeopleProfile>() {

		@SuppressWarnings("unchecked")
		@Override
		public NearByPeopleProfile createFromParcel(Parcel source) {
			NearByPeopleProfile profile = new NearByPeopleProfile();
			profile.setUid(source.readString());
			profile.setAvatar(source.readString());
			profile.setName(source.readString());
			profile.setGender(source.readInt());
			profile.setGenderId(source.readInt());
			profile.setGenderBgId(source.readInt());
			profile.setAge(source.readInt());
			profile.setConstellation(source.readString());
			profile.setDistance(source.readString());
			profile.setTime(source.readString());
			profile.setHasSign(source.readInt() == 1 ? true : false);
			profile.setSign(source.readString());
			profile.setSignPicture(source.readString());
			profile.setSignDistance(source.readString());
			profile.setPhotos(source.readArrayList(NearByPeopleProfile.class
					.getClassLoader()));
			return profile;
		}

		@Override
		public NearByPeopleProfile[] newArray(int size) {
			return new NearByPeopleProfile[size];
		}
	};

}
