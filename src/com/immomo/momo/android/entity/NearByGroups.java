package com.immomo.momo.android.entity;

/**
 * @fileName NearByGroups.java
 * @package com.immomo.momo.android.entity
 * @description 附近群组内容实体类
 * @author 任东卫
 * @email 86930007@qq.com
 * @version 1.0
 */
public class NearByGroups extends Entity {

	public static final String AVATAR = "avatar";
	public static final String NAME = "name";
	public static final String IS_NEW = "isNew";
	public static final String IS_PARTY = "isParty";
	public static final String MEMBER_COUNT = "memberCount";
	public static final String MEMBER_TOTAL = "memberTotal";
	public static final String IS_VIP = "isVip";
	public static final String LEVEL = "level";
	public static final String SIGN = "sign";

	private String avatar;// 头像
	private String name;// 名字
	private int isNew;// 是否为新建
	private int isParty;// 是否举办聚会
	private int memberCount;// 当前成员数量
	private int memberTotal;// 当前可容纳成员数量
	private int isVip;// 是否VIP
	private int level;// 群等级
	private String sign;// 群签名

	public NearByGroups(String avatar, String name, int isNew, int isParty,
			int memberCount, int memberTotal, int isVip, int level, String sign) {
		super();
		this.avatar = avatar;
		this.name = name;
		this.isNew = isNew;
		this.isParty = isParty;
		this.memberCount = memberCount;
		this.memberTotal = memberTotal;
		this.isVip = isVip;
		this.level = level;
		this.sign = sign;
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

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public int getIsParty() {
		return isParty;
	}

	public void setIsParty(int isParty) {
		this.isParty = isParty;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public int getMemberTotal() {
		return memberTotal;
	}

	public void setMemberTotal(int memberTotal) {
		this.memberTotal = memberTotal;
	}

	public int getIsVip() {
		return isVip;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
