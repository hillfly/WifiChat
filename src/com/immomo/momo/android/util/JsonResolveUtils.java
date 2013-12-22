package com.immomo.momo.android.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.R;
import com.immomo.momo.android.entity.Feed;
import com.immomo.momo.android.entity.FeedComment;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByGroups;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.entity.NearByPeopleProfile;

/**
 * @fileName JsonResolveUtils.java
 * @package com.immomo.momo.android.util
 * @description Json解析工具类
 * @author 任东卫
 * @email 86930007@qq.com
 * @version 1.0
 */
public class JsonResolveUtils {
	// 附近个人的json文件名称
	private static final String NEARBY_PEOPLE = "nearby_people.json";
	// 附近个人的json文件名称
	private static final String NEARBY_GROUP = "nearby_group.json";
	// 用户资料文件夹
	private static final String PROFILE = "profile/";
	// 用户状态文件夹
	private static final String STATUS = "status/";
	// 后缀名
	private static final String SUFFIX = ".json";
	// 状态评论
	private static final String FEEDCOMMENT = "feedcomment.json";

	/**
	 * 解析附近个人Json数据
	 * 
	 * @param application
	 * @return
	 */
	public static boolean resolveNearbyPeople(BaseApplication application) {
		if (application.mNearByPeoples != null
				&& application.mNearByPeoples.isEmpty()) {
			String json = TextUtils.getJson(
					application.getApplicationContext(), NEARBY_PEOPLE);
			if (json != null) {
				try {
					JSONArray array = new JSONArray(json);
					NearByPeople people = null;
					JSONObject object = null;
					for (int i = 0; i < array.length(); i++) {
						object = array.getJSONObject(i);
						String uid = object.getString(NearByPeople.UID);
						String avatar = object.getString(NearByPeople.AVATAR);
						int isVip = object.getInt(NearByPeople.VIP);
						int isGroupRole = object
								.getInt(NearByPeople.GROUP_ROLE);
						String industry = object
								.getString(NearByPeople.INDUSTRY);
						int isbindWeibo = object.getInt(NearByPeople.WEIBO);
						int isbindTxWeibo = object
								.getInt(NearByPeople.TX_WEIBO);
						int isbindRenRen = object.getInt(NearByPeople.RENREN);
						int device = object.getInt(NearByPeople.DEVICE);
						int isRelation = object.getInt(NearByPeople.RELATION);
						int isMultipic = object.getInt(NearByPeople.MULTIPIC);
						String name = object.getString(NearByPeople.NAME);
						int gender = object.getInt(NearByPeople.GENDER);
						int age = object.getInt(NearByPeople.AGE);
						String distance = object
								.getString(NearByPeople.DISTANCE);
						String time = object.getString(NearByPeople.TIME);
						String sign = object.getString(NearByPeople.SIGN);

						people = new NearByPeople(uid, avatar, isVip,
								isGroupRole, industry, isbindWeibo,
								isbindTxWeibo, isbindRenRen, device,
								isRelation, isMultipic, name, gender, age,
								distance, time, sign);
						application.mNearByPeoples.add(people);
					}
				} catch (JSONException e) {
					application.mNearByPeoples.clear();
				}
			}
		}
		if (application.mNearByPeoples.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 解析附近群组Json数据
	 * 
	 * @param application
	 * @return
	 */
	public static boolean resolveNearbyGroup(BaseApplication application) {
		if (application.mNearByGroups != null
				&& application.mNearByGroups.isEmpty()) {
			String json = TextUtils.getJson(
					application.getApplicationContext(), NEARBY_GROUP);
			if (json != null) {
				try {
					JSONArray array = new JSONArray(json);

					NearByGroup group = null;
					JSONObject object = null;
					JSONArray groupsArray = null;
					List<NearByGroups> groupsList = null;
					NearByGroups groups = null;

					for (int i = 0; i < array.length(); i++) {
						object = array.getJSONObject(i);
						String address = object.getString(NearByGroup.ADDRESS);
						String distance = object
								.getString(NearByGroup.DISTANCE);
						int groupCount = object.getInt(NearByGroup.GROUP_COUNT);

						groupsArray = object.getJSONArray(NearByGroup.GROUPS);
						groupsList = new ArrayList<NearByGroups>();
						for (int j = 0; j < groupsArray.length(); j++) {
							JSONObject o = groupsArray.getJSONObject(j);
							String avatar = o.getString(NearByGroups.AVATAR);
							String name = o.getString(NearByGroups.NAME);
							int isNew = o.getInt(NearByGroups.IS_NEW);
							int isParty = o.getInt(NearByGroups.IS_PARTY);
							int memberCount = o
									.getInt(NearByGroups.MEMBER_COUNT);
							int memberTotal = o
									.getInt(NearByGroups.MEMBER_TOTAL);
							int isVip = o.getInt(NearByGroups.IS_VIP);
							int level = o.getInt(NearByGroups.LEVEL);
							String sign = o.getString(NearByGroups.SIGN);
							groups = new NearByGroups(avatar, name, isNew,
									isParty, memberCount, memberTotal, isVip,
									level, sign);
							groupsList.add(groups);
						}
						group = new NearByGroup(address, distance, groupCount,
								groupsList);
						application.mNearByGroups.add(group);
					}
				} catch (JSONException e) {
					application.mNearByGroups.clear();
				}
			}
		}
		if (application.mNearByGroups.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 解析附近个人资料
	 * 
	 * @param context
	 * @param profile
	 * @param uid
	 * @return
	 */
	public static boolean resolveNearbyProfile(Context context,
			NearByPeopleProfile profile, String uid) {
		if (!android.text.TextUtils.isEmpty(uid)) {
			String json = TextUtils.getJson(context, PROFILE + uid + SUFFIX);
			if (json != null) {
				try {
					JSONObject object = new JSONObject(json);
					String userId = object.getString(NearByPeopleProfile.UID);
					String avatar = object
							.getString(NearByPeopleProfile.AVATAR);
					String name = object.getString(NearByPeopleProfile.NAME);
					int gender = object.getInt(NearByPeopleProfile.GENDER);
					int genderId = -1;
					int genderBgId = -1;
					if (gender == 0) {
						genderId = R.drawable.ic_user_famale;
						genderBgId = R.drawable.bg_gender_famal;
					} else {
						genderId = R.drawable.ic_user_male;
						genderBgId = R.drawable.bg_gender_male;
					}
					int age = object.getInt(NearByPeopleProfile.AGE);
					String constellation = object
							.getString(NearByPeopleProfile.CONSTELLATION);
					String distance = object
							.getString(NearByPeopleProfile.DISTANCE);
					String time = object.getString(NearByPeopleProfile.TIME);

					boolean isHasSign = false;
					String sign = null;
					String signPic = null;
					String signDis = null;

					if (object.has(NearByPeopleProfile.SIGNATURE)) {
						isHasSign = true;
						JSONObject signObject = object
								.getJSONObject(NearByPeopleProfile.SIGNATURE);
						sign = signObject.getString(NearByPeopleProfile.SIGN);
						if (signObject.has(NearByPeopleProfile.SIGN_PIC)) {
							signPic = signObject
									.getString(NearByPeopleProfile.SIGN_PIC);
						}
						signDis = signObject
								.getString(NearByPeopleProfile.SIGN_DIS);
					}

					JSONArray photosArray = object
							.getJSONArray(NearByPeopleProfile.PHOTOS);
					List<String> photos = new ArrayList<String>();
					for (int i = 0; i < photosArray.length(); i++) {
						photos.add(photosArray.getString(i));
					}
					profile.setUid(userId);
					profile.setAvatar(avatar);
					profile.setName(name);
					profile.setGender(gender);
					profile.setGenderId(genderId);
					profile.setGenderBgId(genderBgId);
					profile.setAge(age);
					profile.setConstellation(constellation);
					profile.setDistance(distance);
					profile.setTime(time);
					profile.setHasSign(isHasSign);
					profile.setSign(sign);
					profile.setSignPicture(signPic);
					profile.setSignDistance(signDis);
					profile.setPhotos(photos);
				} catch (JSONException e) {
					e.printStackTrace();
					profile = null;
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析附近个人状态
	 * 
	 * @param context
	 * @param feeds
	 * @param uid
	 * @return
	 */
	public static boolean resolveNearbyStatus(Context context,
			List<Feed> feeds, String uid) {
		if (!android.text.TextUtils.isEmpty(uid)) {
			String json = TextUtils.getJson(context, STATUS + uid + SUFFIX);
			if (json != null) {
				try {
					JSONArray array = new JSONArray(json);
					Feed feed = null;
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						String time = object.getString(Feed.TIME);
						String content = object.getString(Feed.CONTENT);
						String contentImage = null;
						if (object.has(Feed.CONTENT_IMAGE)) {
							contentImage = object.getString(Feed.CONTENT_IMAGE);
						}
						String site = object.getString(Feed.SITE);
						int commentCount = object.getInt(Feed.COMMENT_COUNT);
						feed = new Feed(time, content, contentImage, site,
								commentCount);
						feeds.add(feed);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					feeds = null;
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析状态评论
	 * 
	 * @param context
	 * @param comments
	 * @return
	 */
	public static boolean resoleFeedComment(Context context,
			List<FeedComment> comments) {
		String json = TextUtils.getJson(context, FEEDCOMMENT);
		if (json != null) {
			try {
				JSONArray array = new JSONArray(json);
				FeedComment comment = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String name = object.getString(FeedComment.NAME);
					String avatar = object.getString(FeedComment.AVATAR);
					String content = object.getString(FeedComment.CONTENT);
					String time = object.getString(FeedComment.TIME);
					comment = new FeedComment(name, avatar, content, time);
					comments.add(comment);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				comments = null;
				return false;
			}
			return true;
		}
		return false;
	}
}
