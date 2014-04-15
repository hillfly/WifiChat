package com.immomo.momo.android.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.entity.NearByGroup;
import com.immomo.momo.android.entity.NearByGroups;

/**
 * @fileName JsonUtils.java
 * @package com.immomo.momo.android.util
 * @description Json解析工具类
 */
public class JsonUtils {
    // 附近个人的json文件名称
    private static final String NEARBY_GROUP = "nearby_group.json";

    /**
     * 解析附近群组Json数据
     * 
     * @param application
     * @return
     */
    public static boolean resolveNearbyGroup(BaseApplication application) {
        if (application.mNearByGroups != null && application.mNearByGroups.isEmpty()) {
            String json = TextUtils.getJson(application.getApplicationContext(), NEARBY_GROUP);
            if (json != null) {
                try {
                    JSONArray array = new JSONArray(json);

                    NearByGroup group = null;
                    JSONObject object = null;
                    JSONArray groupsArray = null;
                    List<NearByGroups> groupsList = null;
                    NearByGroups groups = null;
                    int mLength = array.length();
                    for (int i = 0; i < mLength; i++) {
                        object = array.getJSONObject(i);
                        String address = object.getString(NearByGroup.ADDRESS);
                        String distance = object.getString(NearByGroup.DISTANCE);
                        int groupCount = object.getInt(NearByGroup.GROUP_COUNT);

                        groupsArray = object.getJSONArray(NearByGroup.GROUPS);
                        groupsList = new ArrayList<NearByGroups>();
                        int mGroupLength = groupsArray.length();
                        for (int j = 0; j < mGroupLength; j++) {
                            JSONObject o = groupsArray.getJSONObject(j);
                            String avatar = o.getString(NearByGroups.AVATAR);
                            String name = o.getString(NearByGroups.NAME);
                            int isNew = o.getInt(NearByGroups.IS_NEW);
                            int isParty = o.getInt(NearByGroups.IS_PARTY);
                            int memberCount = o.getInt(NearByGroups.MEMBER_COUNT);
                            int memberTotal = o.getInt(NearByGroups.MEMBER_TOTAL);
                            int isVip = o.getInt(NearByGroups.IS_VIP);
                            int level = o.getInt(NearByGroups.LEVEL);
                            String sign = o.getString(NearByGroups.SIGN);
                            groups = new NearByGroups(avatar, name, isNew, isParty, memberCount,
                                    memberTotal, isVip, level, sign);
                            groupsList.add(groups);
                        }
                        group = new NearByGroup(address, distance, groupCount, groupsList);
                        application.mNearByGroups.add(group);
                    }
                }
                catch (JSONException e) {
                    application.mNearByGroups.clear();
                }
            }
        }
        if (application.mNearByGroups.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 将javaBean转换成json对象
     * 
     * @param paramObject
     *            需要解析的对象
     */
    public static String createJsonString(Object paramObject) {
        String str = JSON.toJSONString(paramObject);
        return str;
    }

    /**
     * 对单个javaBean进行解析
     * 
     * @param <T>
     * @param paramJson 需要解析的json字符串
     * @param paramCls 需要转换成的类
     * @return
     */
    public static <T> T getObject(String paramJson, Class<T> paramCls) {
        T t = null;
        try {
            t = JSON.parseObject(paramJson, paramCls);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

}
