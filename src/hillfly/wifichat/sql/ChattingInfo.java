package hillfly.wifichat.sql;


/*该类存放是用户的聊天信息
 *所有用户聊天信息的属性是私有的
 *能够通过该类的公用方法获取里面的私有信息
 */
public class ChattingInfo {
    private int id; // ID序号
    private int sendID; // 发送者在用户表格所对应的ID
    private int receiverID; // 接收方在用户表格所对应的ID
    private String date; // 聊天信息的记录时间
    private String info; // 聊天信息的内容
    private int style;//聊天信息类型 TEXT(0), IMAGE(1), FILE(2), VOICE(3);

    // 以下是该类的构造函数
    public ChattingInfo() {

    }

    public ChattingInfo(int sendID, int receiverID, String date, String info) {
        this.sendID = sendID;
        this.receiverID = receiverID;
        this.date = date;
        this.info = info;
    }

    public ChattingInfo(int id, int sendID, int receiverID, String date, String info) {
        this(sendID, receiverID, date, info);
        this.id = id;
    }
    
    public ChattingInfo(int id, int sendID, int receiverID, String date, String info,int style) {
        this(sendID, receiverID, date, info);
        this.id = id;
        this.style=style;
    }

    /* 设置ID序列 */
    public void setID(int id) {
        this.id = id;
    }

    /* 获取序列ID */
    public int getId() {
        return id;
    }

    /* 设置发送方ID */
    public void setSendID(int sendID) {
        this.sendID = sendID;
    }

    /* 获取发送方ID */
    public int getSendID() {
        return sendID;
    }

    /* 设置接收方ID */
    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    /* 获取接收方ID */
    public int getReceiverID() {
        return receiverID;
    }

    /* 设置聊天时间 */
    public void setDate(String date) {
        this.date = date;
    }

    /* 获取聊天时间 */
    public String getDate() {
        return date;
    }

    /* 设置聊天信息 */
    public void setInfo(String info) {
        this.info = info;
    }

    /* 获取聊天信息 */
    public String getInfo() {
        return info;
    }
    
    /* 设置聊天信息类型 */
    public void setSytle(int style) {
        this.style = style;
    }

    /* 获取聊天信息类型 */
    public int getStyle() {
        return style;
    }
    /* 输出所有聊天信息 */
    public String toString() {
        return "ID:" + getId() + " sendID:" + getSendID() + " receiverID:" + getReceiverID()
                + " date:" + getDate() + " info:" + getInfo()+" style："+style;
    }
}
