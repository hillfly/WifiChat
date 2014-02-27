package com.immomo.momo.sql;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONStringer;

import com.immomo.momo.android.entity.NearByPeople;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class userDAO {
    private DBHelper helper; // 数据库类(t_user)
    private SQLiteDatabase db;// 数据库(t_user)的操作类

    /*
     * 构造函数参数：context对象通过db的方法来操作数据库的增删改查
     */
    public userDAO(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /*
     * 参数：用户的IMEI序列码结果返回IMEI码对应用户的ID
     */
    public int getID(String imei) {
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id" }, "IMEI=?",
                new String[] { imei }, null, null, null);
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return id;
        }
        cursor.close();
        return 0;
    }

    /* 关闭数据库 */
    public void close() {
        db.close();
        helper.close();
    }

    /*
     * 参数：userInfo类 作用：用来添加用户信息
     */
    public void add(userInfo user) {

        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("sex", user.getSex());
        values.put("age", user.getAge());
        values.put("IMEI", user.getIMEI());
        values.put("ip", user.getIPAddr());
        values.put("status", user.getIsOnline());
        values.put("avater", user.getAvater());
        values.put("lastdate", user.getLastDate());
        values.put("device", user.getDevice());
        values.put("constellation",user.getConstellation());
        db.insert(helper.getTableName(), "id", values);
    }

    /*获取在线信息尚未完善,默认在线状态(0)*/
    public void add(NearByPeople people)
    {
    	 ContentValues values = new ContentValues();
         values.put("name", people.getNickname());
         values.put("sex", people.getGender());
         values.put("age", people.getAge());
         values.put("IMEI", people.getIMEI());
         values.put("ip", people.getIpaddress());
         values.put("status", people.getOnlineStateInt());
         values.put("avater", people.getAvatar());
         values.put("lastdate", people.getLogintime());
         values.put("device", people.getDevice());
         values.put("constellation",people.getConstellation());
         db.insert(helper.getTableName(), "id", values);
    }
    /*
     * 参数：userInfo类 作用：用来更用户信息
     */
    public void update(userInfo user) {
        // db=helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("sex", user.getSex());
        values.put("age", user.getAge());
        values.put("IMEI", user.getIMEI());
        values.put("ip", user.getIPAddr());
        values.put("status", user.getIsOnline());
        values.put("avater", user.getAvater());
        values.put("lastdate", user.getLastDate());
        values.put("device", user.getDevice());
        values.put("constellation",user.getConstellation());
        db.update(helper.getTableName(), values, "id = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    /*
     * 参数：用户对应序号ID 作用:用来查找对应的用户 返回userInfo类
     */
    public userInfo find(int id) {
        // db = helper.getWritableDatabase();
        // db.query(table, columns, selection, selectionArgs, groupBy, having,
        // orderBy)
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id", "name", "age", "IMEI",
                "sex", "ip", "status", "avater","lastdate","device","constellation"}, "id=?", new String[] { String.valueOf(id) },
                null, null, null);
        if (cursor.moveToNext()) {
            userInfo userInfo = new userInfo(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor
                            .getColumnIndex("age")),
                    cursor.getString(cursor.getColumnIndex("sex")), cursor.getString(cursor
                            .getColumnIndex("IMEI")),
                    cursor.getString(cursor.getColumnIndex("ip")), cursor.getInt(cursor
                            .getColumnIndex("status")), cursor.getInt(cursor
                            .getColumnIndex("avater")),cursor.getString(cursor.getColumnIndex("lastdate")),
                            cursor.getString(cursor.getColumnIndex("device")),cursor.getString(cursor.getColumnIndex("constellation")));
            cursor.close();
            return userInfo;
        }
        cursor.close();
        return null;
    }

    /*
     * 参数：用户对应的IMEI码 作用:用来查找对应的用户 返回userInfo类
     */
    public userInfo findUserInfo(String imei) {
        // db = helper.getWritableDatabase();
        // db.query(table, columns, selection, selectionArgs, groupBy, having,
        // orderBy)
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id", "name", "age", "IMEI",
                "sex", "ip", "status", "avater","lastdate","device","constellation"}, "IMEI=?", new String[] { imei }, null, null,
                null);
        if (cursor.moveToNext()) {
            userInfo userInfo = new userInfo(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor
                            .getColumnIndex("age")),
                    cursor.getString(cursor.getColumnIndex("sex")), cursor.getString(cursor
                            .getColumnIndex("IMEI")),
                    cursor.getString(cursor.getColumnIndex("ip")), cursor.getInt(cursor
                            .getColumnIndex("status")), cursor.getInt(cursor
                            .getColumnIndex("avater")),cursor.getString(cursor.getColumnIndex("lastdate")),
                            cursor.getString(cursor.getColumnIndex("device")),cursor.getString(cursor.getColumnIndex("constellation")));
            cursor.close();
            return userInfo;
        }
        cursor.close();
        return null;
    }

    /*
     * 参数：用户信息的一系列序号如(1,2,3) 作用：用来删除用户信息
     */
    public void detele(Integer... ids) {
        if (ids.length > 0) {
            StringBuffer sb = new StringBuffer();
            String[] strPid = new String[ids.length];
            for (int i = 0; i < ids.length; i++) {
                sb.append('?').append(',');
                strPid[i] = String.valueOf(ids[i]);
            }
            sb.deleteCharAt(sb.length() - 1);
            // db = helper.getWritableDatabase();
            db.delete(helper.getTableName(), "id in (" + sb + ")", strPid);
        }
    }

    /*
     * 用来获取近期的一系列用户信息 参数:start为步数，count为最大记录数，(倒序排列) 放回List<userInfo>
     */
    public List<userInfo> getScrollData(int start, int count) {
        List<userInfo> users = new ArrayList<userInfo>();
        // db = helper.getWritableDatabase();
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id", "name", "age", "sex",
                "IMEI", "ip", "status", "avater","lastdate","device","constellation"}, null, null, null, null, "id desc", start + ","
                + count);
        while (cursor.moveToNext()) {
            users.add(new userInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor
                    .getString(cursor.getColumnIndex("name")), cursor.getInt(cursor
                    .getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("sex")), cursor
                    .getString(cursor.getColumnIndex("IMEI")), cursor.getString(cursor
                    .getColumnIndex("ip")), cursor.getInt(cursor.getColumnIndex("status")), cursor
                    .getInt(cursor.getColumnIndex("avater")),cursor.getString(cursor.getColumnIndex("lastdate")),
                    cursor.getString(cursor.getColumnIndex("device")),cursor.getString(cursor.getColumnIndex("constellation"))));
        }
        cursor.close();
        return users;
    }

    /*
     * 作用: 用来获取表中用户总数量
     */
    public long getCount() {
        // db = helper.getWritableDatabase();
        Cursor cursor = db.query(helper.getTableName(), new String[] { "count(*)" }, null, null,
                null, null, null);
        if (cursor.moveToNext()) {
            long count = cursor.getLong(0);
            cursor.close();
            return count;
        }
        cursor.close();
        return 0;
    }

    /*
     * 该函数将所有数据库中用户表的信息用JSON形式的String来表示
     */
    public String sendToJSON() {
        List<userInfo> users;
        int count = (int) getCount();
        users = getScrollData(0, count);
        JSONStringer jsonText = new JSONStringer();
        try {
            // 首先是{，对象开始。object和endObject必须配对使用
            jsonText.object();

            jsonText.key("user");

            // 键user的值是数组。array和endArray必须配对使用
            jsonText.array();
            for (userInfo user : users) {
                jsonText.object();

                jsonText.key("id");
                jsonText.value(user.getId());
                jsonText.key("name");
                jsonText.value(user.getName());
                jsonText.key("sex");
                jsonText.value(user.getSex());
                jsonText.key("age");
                jsonText.value(user.getAge());
                jsonText.key("IMEI");
                jsonText.value(user.getIMEI());
                jsonText.key("ip");
                jsonText.value(user.getIPAddr());
                jsonText.key("status");
                jsonText.value(user.getIsOnline());
                jsonText.key("avater");
                jsonText.value(user.getAvater());
                jsonText.key("lastdate");
                jsonText.value(user.getLastDate());
                jsonText.key("device");
                jsonText.value(user.getDevice());
                jsonText.key("constellation");
                jsonText.value(user.getConstellation());
                jsonText.endObject();
            }
            jsonText.endArray();

            // }，对象结束
            jsonText.endObject();
        }
        catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
        return jsonText.toString();
    }

}
