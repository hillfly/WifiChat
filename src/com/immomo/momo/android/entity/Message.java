package com.immomo.momo.android.entity;


/**
 * 消息实体类
 * 
 * @author _Hill3
 */
public class Message extends Entity {

    private String senderIMEI;
    private String senderName;
    private String sendTime;
    private String MsgContent;
    private CONTENT_TYPE contentType;
    private boolean isSelfMsg; // 是否为自己发的信息

    public Message() {
        isSelfMsg = false; // 消息默认为非本人所发
    }

    public Message(String paramSenderIMEI, String paramSenderName, String paramSendTime, 
            String paramMsgContent, CONTENT_TYPE paramContentType) {
        this.senderIMEI = paramSenderIMEI;
        this.senderName = paramSenderName;
        this.sendTime = paramSendTime;
        this.MsgContent = paramMsgContent;
        this.contentType = paramContentType;
        this.isSelfMsg = false; // 消息默认为非本人所发
    }

    /** 消息内容类型 **/
    public enum CONTENT_TYPE {
        TEXT, IMAGE, FILE, VOICE;
    }

    /**
     * 获取消息发送方IMEI
     * 
     * @return
     */
    public String getSenderIMEI() {
        return senderIMEI;
    }

    /**
     * 设置消息发送方IMEI
     * 
     * @param paramSenderIMEI
     *            
     */
    public void setSenderIMEI(String paramSenderIMEI) {
        this.senderIMEI = paramSenderIMEI;
    }

    /**
     * 获取消息发送者姓名
     * 
     * @return
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * 设置消息发送者姓名
     * 
     * @param paramSenderName
     *            姓名
     */
    public void setSenderName(String paramSenderName) {
        this.senderName = paramSenderName;
    }

    /**
     * 获取消息发送时间
     * 
     * @return
     */
    public String getSendTime() {
        return sendTime;
    }

    /**
     * 设置消息发送时间
     * 
     * @param paramSendTime
     *            发送时间,格式 xx年xx月xx日 xx:xx:xx
     */
    public void setSendTime(String paramSendTime) {
        this.sendTime = paramSendTime;
    }

    /**
     * 获取消息内容
     * 
     * @return
     */
    public String getMsgContent() {
        return MsgContent;
    }

    /**
     * 设置消息内容
     * 
     * @param paramMsgContent
     */
    public void setMsgContent(String paramMsgContent) {
        this.MsgContent = paramMsgContent;
    }

    /**
     * 获取消息内容类型
     * 
     * @return
     * @see CONTENT_TYPE
     */
    public CONTENT_TYPE getContentType() {
        return contentType;
    }

    /**
     * 设置消息内容类型
     * 
     * @param paramContentType
     * @see CONTENT_TYPE
     */
    public void setContentType(CONTENT_TYPE paramContentType) {
        this.contentType = paramContentType;
    }

    /**
     * 获取是否自己发送的消息
     * 
     * @return
     */
    public boolean getIsSelfMsg() {
        return isSelfMsg;
    }

    /**
     * 设置是否为自己发的消息
     * 
     * @param paramIsSelfMsg
     */
    public void setIsSelfMsg(Boolean paramIsSelfMsg) {
        this.isSelfMsg = paramIsSelfMsg;
    }
}
