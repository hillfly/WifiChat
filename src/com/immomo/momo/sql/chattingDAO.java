package com.immomo.momo.sql;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONStringer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class chattingDAO {
    private SQLHelper helper; // 数据库类(t_chatting)
    private SQLiteDatabase db; // 数据库(t_chatting)的操作类

    /*
     * 构造函数参数：context对象通过db的方法来操作数据库的增删改查
     */
    public chattingDAO(Context context) {
        helper = new SQLHelper(context);
        db = helper.getWritableDatabase();
    }

    /* 关闭数据库 */
    public void close() {
        db.close();
        helper.close();
    }

    /*
     * 参数：发送方ID sendID,接收方ID receiverID结果返回一系列的聊天记录的ID
     */
    public List<Integer> getID(int sendID, int receiverID) {
        List<Integer> ids = new ArrayList<Integer>();
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id" },
                "sendID=? and receiverID=?",
                new String[] { String.valueOf(sendID), String.valueOf(receiverID) }, null, null,
                null);
        while (cursor.moveToNext()) {
            ids.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
        }
        cursor.close();
        return ids;
    }

    /*
     * 参数：发送方ID sendID,接收方ID receiverID结果返回一系列的聊天记录
     */
    public List<chattingInfo> getAllMessage(int sendID, int receiverID) {
        List<chattingInfo> infos = new ArrayList<chattingInfo>();
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id", "sendID",
                "receiverID", "chatting", "date" }, "sendID=? and receiverID=?", new String[] {
                String.valueOf(sendID), String.valueOf(receiverID) }, null, null, null);
        while (cursor.moveToNext()) {
            infos.add(new chattingInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor
                    .getInt(cursor.getColumnIndex("sendID")), cursor.getInt(cursor
                    .getColumnIndex("receiverID")), cursor.getString(cursor
                    .getColumnIndex("chatting")), cursor.getString(cursor.getColumnIndex("date"))));
        }
        cursor.close();
        return infos;
    }

    /*
     * 参数：chattinginfo类 作用：用来添加聊天记录
     */
    public void add(chattingInfo info) {

        ContentValues values = new ContentValues();
        values.put("sendID", info.getSendID());
        values.put("receiverID", info.getReceiverID());
        values.put("chatting", info.getInfo());
        values.put("date", info.getDate());
        db.insert(helper.getTableName(), "id", values);
    }

    /*
     * 参数：chattinginfo类 作用：用来更新聊天记录
     */
    public void update(chattingInfo info) {
        // db=helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sendID", info.getSendID());
        values.put("receiverID", info.getReceiverID());
        values.put("chatting", info.getInfo());
        values.put("date", info.getDate());
        db.update(helper.getTableName(), values, "id = ?",
                new String[] { String.valueOf(info.getId()) });
    }

    /*
     * 参数：聊天记录序号ID 作用:用来查找对应的一条聊天记录 返回chattinginfo类
     */
    public chattingInfo find(int id) {
        // db = helper.getWritableDatabase();
        // db.query(table, columns, selection, selectionArgs, groupBy, having,
        // orderBy)
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id", "sendID",
                "receiverID", "chatting", "date" }, "id=?", new String[] { String.valueOf(id) },
                null, null, null);
        if (cursor.moveToNext()) {
            chattingInfo chattingInfo = new chattingInfo(
                    cursor.getInt(cursor.getColumnIndex("id")), cursor.getInt(cursor
                            .getColumnIndex("sendID")), cursor.getInt(cursor
                            .getColumnIndex("receiverID")), cursor.getString(cursor
                            .getColumnIndex("chatting")), cursor.getString(cursor
                            .getColumnIndex("date")));
            cursor.close();
            return chattingInfo;
        }
        return null;
    }

    /*
     * 参数：聊天记录的一系列序号如(1,2,3) 作用：用来删除聊天记录
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
     * 用来获取近期的一系列聊天记录 参数:start为步数，count为最大记录数，(倒序排列) 放回List<chattingInfo>
     */
    public List<chattingInfo> getScrollData(int start, int count) {
        List<chattingInfo> info = new ArrayList<chattingInfo>();
        // db = helper.getWritableDatabase();
        Cursor cursor = db.query(helper.getTableName(), new String[] { "id", "sendID",
                "receiverID", "chatting", "date" }, null, null, null, null, "id desc", start + ","
                + count);
        while (cursor.moveToNext()) {
            info.add(new chattingInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor
                    .getInt(cursor.getColumnIndex("sendID")), cursor.getInt(cursor
                    .getColumnIndex("receiverID")), cursor.getString(cursor
                    .getColumnIndex("chatting")), cursor.getString(cursor.getColumnIndex("date"))));
        }
        cursor.close();
        return info;
    }

    /*
     * 作用: 用来获取表中聊天记录总数量
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
        return 0;
    }

    /*
     * 该函数将所有数据库中聊天信息表的信息用JSON形式的String来表示
     */
    public String sendToJSON(int sendID, int receiverID) {
        List<chattingInfo> infos;

        infos = getAllMessage(sendID, receiverID);

        JSONStringer jsonText = new JSONStringer();
        try {
            // 首先是{，对象开始。object和endObject必须配对使用
            jsonText.object();

            jsonText.key("chatting");

            // 键user的值是数组。array和endArray必须配对使用
            jsonText.array();
            for (chattingInfo info : infos) {
                jsonText.object();

                jsonText.key("id");
                jsonText.value(info.getId());
                jsonText.key("sendID");
                jsonText.value(info.getSendID());
                jsonText.key("receiverID");
                jsonText.value(info.getReceiverID());
                jsonText.key("chatting");
                jsonText.value(info.getInfo());
                jsonText.key("date");
                jsonText.value(info.getDate());

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
