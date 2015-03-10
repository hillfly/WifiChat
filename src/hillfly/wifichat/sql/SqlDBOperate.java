package hillfly.wifichat.sql;

import hillfly.wifichat.bean.Message;
import hillfly.wifichat.bean.Users;
import hillfly.wifichat.bean.Message.CONTENT_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONStringer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlDBOperate {
    private SQLHelper chatInfoSQLHelper; // 数据库类(t_chatting)
    private SQLiteDatabase chatInfoDataBase; // 数据库(t_chatting)的操作类
    private DBHelper userSQLHelper; // 数据库类(t_user)
    private SQLiteDatabase userDataBase;// 数据库(t_user)的操作类

    /*
     * 构造函数参数：context对象通过db的方法来操作数据库的增删改查
     */
    public SqlDBOperate(Context context) {
        chatInfoSQLHelper = new SQLHelper(context);
        chatInfoDataBase = chatInfoSQLHelper.getWritableDatabase();
        userSQLHelper = new DBHelper(context);
        userDataBase = userSQLHelper.getWritableDatabase();
    }

    /* 关闭数据库 */
    public void close() {
        userSQLHelper.close();
        userDataBase.close();
        chatInfoSQLHelper.close();
        chatInfoDataBase.close();
    }

    /*
     * 参数：userInfo类 作用：用来添加用户信息
     */
    public void addUserInfo(UserInfo user) {

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
        values.put("constellation", user.getConstellation());
        int id = getIDByIMEI(user.getIMEI());
        if (id != 0) {
            user.setId(id);
            updateUserInfo(user);
        }
        else
            userDataBase.insert(userSQLHelper.getTableName(), "id", values);
    }

    /* 获取在线信息尚未完善,默认在线状态(0) */
    public void addUserInfo(Users people) {
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
        values.put("constellation", people.getConstellation());
        int id = getIDByIMEI(people.getIMEI());
        if (id != 0) {
            userDataBase.update(userSQLHelper.getTableName(), values, "id = ?",
                    new String[] { String.valueOf(id) });
        }
        else
            userDataBase.insert(userSQLHelper.getTableName(), "id", values);
    }

    /*
     * 参数：userInfo类 作用：用来更新用户信息
     */
    public void updateUserInfo(UserInfo user) {
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
        values.put("constellation", user.getConstellation());
        userDataBase.update(userSQLHelper.getTableName(), values, "id = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    /*
     * 参数：用户的IMEI序列码结果返回IMEI码对应用户的ID
     */
    public int getIDByIMEI(String imei) {
        Cursor cursor = userDataBase.query(userSQLHelper.getTableName(), new String[] { "id" },
                "IMEI=?", new String[] { imei }, null, null, null);
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return id;
        }
        cursor.close();
        return 0;
    }

    /*
     * 参数：用户对应序号ID 作用:用来查找对应的用户 返回IMEI
     */
    public String getIMEIByUserID(int id) {
        Cursor cursor = userDataBase.query(userSQLHelper.getTableName(), new String[] { "IMEI" },
                "id=?", new String[] { String.valueOf(id) }, null, null, null);
        if (cursor.moveToNext()) {
            String IMEI = cursor.getString(cursor.getColumnIndex("IMEI"));
            cursor.close();
            return IMEI;
        }
        cursor.close();
        return null;
    }

    /*
     * 参数：用户对应序号ID 作用:用来查找对应的用户 返回userInfo类
     */
    public UserInfo getUserInfoByID(int id) {
        // db = helper.getWritableDatabase();
        // db.query(table, columns, selection, selectionArgs, groupBy, having,
        // orderBy)
        Cursor cursor = userDataBase.query(userSQLHelper.getTableName(), new String[] { "id",
                "name", "age", "IMEI", "sex", "ip", "status", "avater", "lastdate", "device",
                "constellation" }, "id=?", new String[] { String.valueOf(id) }, null, null, null);
        if (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor
                            .getColumnIndex("age")),
                    cursor.getString(cursor.getColumnIndex("sex")), cursor.getString(cursor
                            .getColumnIndex("IMEI")),
                    cursor.getString(cursor.getColumnIndex("ip")), cursor.getInt(cursor
                            .getColumnIndex("status")), cursor.getInt(cursor
                            .getColumnIndex("avater")), cursor.getString(cursor
                            .getColumnIndex("lastdate")), cursor.getString(cursor
                            .getColumnIndex("device")), cursor.getString(cursor
                            .getColumnIndex("constellation")));
            cursor.close();
            return userInfo;
        }
        cursor.close();
        return null;
    }

    /*
     * 参数：用户对应的IMEI码 作用:用来查找对应的用户 返回userInfo类
     */
    public UserInfo getUserInfoByIMEI(String imei) {
        // db = helper.getWritableDatabase();
        // db.query(table, columns, selection, selectionArgs, groupBy, having,
        // orderBy)
        Cursor cursor = userDataBase.query(userSQLHelper.getTableName(), new String[] { "id",
                "name", "age", "IMEI", "sex", "ip", "status", "avater", "lastdate", "device",
                "constellation" }, "IMEI=?", new String[] { imei }, null, null, null);
        if (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor
                            .getColumnIndex("age")),
                    cursor.getString(cursor.getColumnIndex("sex")), cursor.getString(cursor
                            .getColumnIndex("IMEI")),
                    cursor.getString(cursor.getColumnIndex("ip")), cursor.getInt(cursor
                            .getColumnIndex("status")), cursor.getInt(cursor
                            .getColumnIndex("avater")), cursor.getString(cursor
                            .getColumnIndex("lastdate")), cursor.getString(cursor
                            .getColumnIndex("device")), cursor.getString(cursor
                            .getColumnIndex("constellation")));
            cursor.close();
            return userInfo;
        }
        cursor.close();
        return null;
    }

    /*
     * 参数：用户信息的一系列序号如(1,2,3) 作用：用来删除用户信息
     */
    public void deteleUserInfo(Integer... ids) {
        if (ids.length > 0) {
            StringBuffer sb = new StringBuffer();
            String[] strPid = new String[ids.length];
            for (int i = 0; i < ids.length; i++) {
                sb.append('?').append(',');
                strPid[i] = String.valueOf(ids[i]);
            }
            sb.deleteCharAt(sb.length() - 1);
            // db = helper.getWritableDatabase();
            userDataBase.delete(userSQLHelper.getTableName(), "id in (" + sb + ")", strPid);
        }
    }

    /*
     * 用来获取近期的一系列用户信息 参数:start为步数，count为最大记录数，(倒序排列) 放回List<userInfo>
     */
    public List<UserInfo> getScrollDataOfUserInfo(int start, int count) {
        List<UserInfo> users = new ArrayList<UserInfo>();
        // db = helper.getWritableDatabase();
        Cursor cursor = userDataBase.query(userSQLHelper.getTableName(), new String[] { "id",
                "name", "age", "sex", "IMEI", "ip", "status", "avater", "lastdate", "device",
                "constellation" }, null, null, null, null, "id desc", start + "," + count);
        while (cursor.moveToNext()) {
            users.add(new UserInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor
                    .getString(cursor.getColumnIndex("name")), cursor.getInt(cursor
                    .getColumnIndex("age")), cursor.getString(cursor.getColumnIndex("sex")), cursor
                    .getString(cursor.getColumnIndex("IMEI")), cursor.getString(cursor
                    .getColumnIndex("ip")), cursor.getInt(cursor.getColumnIndex("status")), cursor
                    .getInt(cursor.getColumnIndex("avater")), cursor.getString(cursor
                    .getColumnIndex("lastdate")),
                    cursor.getString(cursor.getColumnIndex("device")), cursor.getString(cursor
                            .getColumnIndex("constellation"))));
        }
        cursor.close();
        return users;
    }

    /*
     * 作用: 用来获取表中用户总数量
     */
    public long getCountOfUserInfo() {
        // db = helper.getWritableDatabase();
        Cursor cursor = userDataBase.query(userSQLHelper.getTableName(),
                new String[] { "count(*)" }, null, null, null, null, null);
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
    public String sendUserInfoToJSON() {
        List<UserInfo> users;
        int count = (int) getCountOfUserInfo();
        users = getScrollDataOfUserInfo(0, count);
        JSONStringer jsonText = new JSONStringer();
        try {
            // 首先是{，对象开始。object和endObject必须配对使用
            jsonText.object();

            jsonText.key("user");

            // 键user的值是数组。array和endArray必须配对使用
            jsonText.array();
            for (UserInfo user : users) {
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

    /*
     * 参数：发送方ID sendID,接收方ID receiverID结果返回一系列的聊天记录的ID
     */
    public List<Integer> getIDOfChattingInfo(int senderID, int receiverID) {
        List<Integer> ids = new ArrayList<Integer>();
        Cursor cursor = chatInfoDataBase.query(chatInfoSQLHelper.getTableName(),
                new String[] { "id" }, "sendID=? and receiverID=?",
                new String[] { String.valueOf(senderID), String.valueOf(receiverID) }, null, null,
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
    public List<ChattingInfo> getAllMessageFromChattingInfo(int sendID, int receiverID) {
        List<ChattingInfo> infos = new ArrayList<ChattingInfo>();
        Cursor cursor = chatInfoDataBase.query(chatInfoSQLHelper.getTableName(), new String[] {
                "id", "sendID", "receiverID", "chatting", "date", "style" },
                "sendID=? and receiverID=?",
                new String[] { String.valueOf(sendID), String.valueOf(receiverID) }, null, null,
                null);
        while (cursor.moveToNext()) {
            infos.add(new ChattingInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor
                    .getInt(cursor.getColumnIndex("sendID")), cursor.getInt(cursor
                    .getColumnIndex("receiverID")),
                    cursor.getString(cursor.getColumnIndex("date")), cursor.getString(cursor
                            .getColumnIndex("chatting")), cursor.getInt(cursor
                            .getColumnIndex("style"))));
        }
        cursor.close();
        return infos;
    }

    /*
     * 参数：chattinginfo类 作用：用来添加聊天记录
     */
    public void addChattingInfo(ChattingInfo info) {

        ContentValues values = new ContentValues();
        values.put("sendID", info.getSendID());
        values.put("receiverID", info.getReceiverID());
        values.put("chatting", info.getInfo());
        values.put("date", info.getDate());
        values.put("style", info.getStyle());
        chatInfoDataBase.insert(chatInfoSQLHelper.getTableName(), "id", values);
    }

    /*
     * 参数：chattinginfo类 作用：用来添加聊天记录
     */
    public void addChattingInfo(int senderID, int recieverID, String time, String content,
            CONTENT_TYPE type) {

        ContentValues values = new ContentValues();
        values.put("sendID", senderID);
        values.put("receiverID", recieverID);
        values.put("chatting", content);
        values.put("date", time);
        values.put("style", getStyteByContentType(type));
        chatInfoDataBase.insert(chatInfoSQLHelper.getTableName(), "id", values);
    }

    private int getStyteByContentType(CONTENT_TYPE type) {
        if (type == CONTENT_TYPE.TEXT) {
            return 0;
        }
        else if (type == CONTENT_TYPE.IMAGE) {
            return 1;
        }
        else if (type == CONTENT_TYPE.FILE) {
            return 2;
        }
        else if (type == CONTENT_TYPE.VOICE) {
            return 3;
        }
        return -1;
    }

    /*
     * 参数：chattinginfo类 作用：用来添加聊天记录
     */
    public void addChattingInfo(String senderIMEI, String recieverIMEI, String time,
            String content, CONTENT_TYPE type) {

        ContentValues values = new ContentValues();
        values.put("sendID", getIDByIMEI(senderIMEI));
        values.put("receiverID", getIDByIMEI(recieverIMEI));
        values.put("chatting", content);
        values.put("date", time);
        values.put("style", getStyteByContentType(type));
        chatInfoDataBase.insert(chatInfoSQLHelper.getTableName(), "id", values);
    }

    /*
     * 参数：chattinginfo类 作用：用来更新聊天记录
     */
    public void updateChattingInfo(ChattingInfo info) {
        // db=helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sendID", info.getSendID());
        values.put("receiverID", info.getReceiverID());
        values.put("chatting", info.getInfo());
        values.put("date", info.getDate());
        values.put("style", info.getStyle());
        chatInfoDataBase.update(chatInfoSQLHelper.getTableName(), values, "id = ?",
                new String[] { String.valueOf(info.getId()) });
    }

    /*
     * 参数：聊天记录序号ID 作用:用来查找对应的一条聊天记录 返回chattinginfo类
     */
    public ChattingInfo getChattingInfoByID(int id) {
        // db = helper.getWritableDatabase();
        // db.query(table, columns, selection, selectionArgs, groupBy, having,
        // orderBy)
        Cursor cursor = chatInfoDataBase.query(chatInfoSQLHelper.getTableName(), new String[] {
                "id", "sendID", "receiverID", "chatting", "date", "style" }, "id=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor.moveToNext()) {
            ChattingInfo chattingInfo = new ChattingInfo(
                    cursor.getInt(cursor.getColumnIndex("id")), cursor.getInt(cursor
                            .getColumnIndex("sendID")), cursor.getInt(cursor
                            .getColumnIndex("receiverID")), cursor.getString(cursor
                            .getColumnIndex("date")), cursor.getString(cursor
                            .getColumnIndex("chatting")), cursor.getInt(cursor
                            .getColumnIndex("style")));
            cursor.close();
            return chattingInfo;
        }
        return null;
    }

    /*
     * 删除所有聊天记录
     */
    public void deteleAllChattingInfo() {
        chatInfoDataBase.delete(chatInfoSQLHelper.getTableName(), null, null);
    }

    /*
     * 参数：聊天记录的一系列序号如(1,2,3) 作用：用来删除聊天记录
     */
    public void deteleChattingInfo(Integer... ids) {
        if (ids.length > 0) {
            StringBuffer sb = new StringBuffer();
            String[] strPid = new String[ids.length];
            for (int i = 0; i < ids.length; i++) {
                sb.append('?').append(',');
                strPid[i] = String.valueOf(ids[i]);
            }
            sb.deleteCharAt(sb.length() - 1);
            // db = helper.getWritableDatabase();
            chatInfoDataBase.delete(chatInfoSQLHelper.getTableName(), "id in (" + sb + ")", strPid);
        }
    }

    /*
     * 用来获取近期的一系列聊天记录 参数:start为开始位置，count为最大记录数，(倒序排列) 放回List<chattingInfo>
     */
    public List<ChattingInfo> getScrollDataOfChattingInfo(int start, int count) {
        List<ChattingInfo> info = new ArrayList<ChattingInfo>();
        // db = helper.getWritableDatabase();
        Cursor cursor = chatInfoDataBase.query(chatInfoSQLHelper.getTableName(), new String[] {
                "id", "sendID", "receiverID", "chatting", "date", "style" }, null, null, null,
                null, "id desc", start + "," + count);
        while (cursor.moveToNext()) {
            info.add(new ChattingInfo(cursor.getInt(cursor.getColumnIndex("id")), cursor
                    .getInt(cursor.getColumnIndex("sendID")), cursor.getInt(cursor
                    .getColumnIndex("receiverID")),
                    cursor.getString(cursor.getColumnIndex("date")), cursor.getString(cursor
                            .getColumnIndex("chatting")), cursor.getInt(cursor
                            .getColumnIndex("style"))));
        }
        cursor.close();
        return info;
    }

    /*
     * 用来获取近期的一系列聊天记录 参数:start为开始位置，count为最大记录数，(倒序排列) 放回List<chattingInfo>
     */
    public List<Message> getScrollMessageOfChattingInfo(int start, int count, int senderID,
            int recieverID) {
        List<Message> messages = new ArrayList<Message>();
        Cursor cursor = chatInfoDataBase.query(
                chatInfoSQLHelper.getTableName(),
                new String[] { "id", "sendID", "receiverID", "chatting", "date", "style" },
                "(sendID=? and receiverID=?) or (receiverID=? and sendID=?)",
                new String[] { String.valueOf(senderID), String.valueOf(recieverID),
                        String.valueOf(senderID), String.valueOf(recieverID) }, null, null,
                "id desc", start + "," + count);
        while (cursor.moveToNext()) {
            Message message = chattingInfoToMessage(new ChattingInfo(cursor.getInt(cursor
                    .getColumnIndex("id")), cursor.getInt(cursor.getColumnIndex("sendID")),
                    cursor.getInt(cursor.getColumnIndex("receiverID")), cursor.getString(cursor
                            .getColumnIndex("date")), cursor.getString(cursor
                            .getColumnIndex("chatting")), cursor.getInt(cursor
                            .getColumnIndex("style"))));
            messages.add(message);
        }
        cursor.close();
        Collections.reverse(messages);
        return messages;
    }

    private Message chattingInfoToMessage(ChattingInfo chattingInfo) {
        Message message = new Message();
        message.setMsgContent(chattingInfo.getInfo()); // 设置聊天信息内容
        message.setSendTime(chattingInfo.getDate());// 设置聊天信息发送时间
        switch (chattingInfo.getStyle())// 设置聊天信息类型
        {
            case 0:
                message.setContentType(CONTENT_TYPE.TEXT);
                break;
            case 1:
                message.setContentType(CONTENT_TYPE.IMAGE);
                break;
            case 2:
                message.setContentType(CONTENT_TYPE.FILE);
                break;
            case 3:
                message.setContentType(CONTENT_TYPE.VOICE);
                break;
        }
        message.setSenderIMEI(getIMEIByUserID(chattingInfo.getSendID()));// 设置发送方ID
        return message;
    }

    /*
     * 作用: 用来获取表中聊天记录总数量
     */
    public long getCountOfChattingInfo() {
        // db = helper.getWritableDatabase();
        Cursor cursor = chatInfoDataBase.query(chatInfoSQLHelper.getTableName(),
                new String[] { "count(*)" }, null, null, null, null, null);
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
    public String sendChattingInfoToJSON(int sendID, int receiverID) {
        List<ChattingInfo> infos;

        infos = getAllMessageFromChattingInfo(sendID, receiverID);

        JSONStringer jsonText = new JSONStringer();
        try {
            // 首先是{，对象开始。object和endObject必须配对使用
            jsonText.object();

            jsonText.key("chatting");

            // 键user的值是数组。array和endArray必须配对使用
            jsonText.array();
            for (ChattingInfo info : infos) {
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
                jsonText.key("style");
                jsonText.value(info.getStyle());
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
