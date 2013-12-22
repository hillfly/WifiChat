package com.immomo.momo.android.entity;

import java.util.List;

/**
 * @fileName NearByGroup.java
 * @package com.immomo.momo.android.entity
 * @description 附近群组实体类
 * @author 任东卫
 * @email 86930007@qq.com
 * @version 1.0
 */
public class NearByGroup extends Entity {

	public static final String ADDRESS = "address";
	public static final String DISTANCE = "distance";
	public static final String GROUP_COUNT = "group_count";
	public static final String GROUPS = "groups";

	private String address;// 地址
	private String distance;// 距离
	private int groupCount;// 群数量
	private List<NearByGroups> mGroups;// 群内容

	public NearByGroup(String address, String distance, int groupCount,
			List<NearByGroups> mGroups) {
		super();
		this.address = address;
		this.distance = distance;
		this.groupCount = groupCount;
		this.mGroups = mGroups;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public List<NearByGroups> getmGroups() {
		return mGroups;
	}

	public void setmGroups(List<NearByGroups> mGroups) {
		this.mGroups = mGroups;
	}
}
