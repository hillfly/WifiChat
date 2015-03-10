package hillfly.wifichat.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 消息实体类
 * 
 * @author _Hill3
 */
public class Message extends Entity {

    private String senderIMEI;
    private String sendTime;
    private String MsgContent;
    private CONTENT_TYPE contentType;
    private int percent;

    public Message() {
    }

    public Message(String paramSenderIMEI, String paramSendTime, String paramMsgContent,
            CONTENT_TYPE paramContentType) {
        this.senderIMEI = paramSenderIMEI;
        this.sendTime = paramSendTime;
        this.MsgContent = paramMsgContent;
        this.contentType = paramContentType;
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
     * 克隆对象
     * 
     * @param
     */

    public Message clone() {
        return new Message(senderIMEI, sendTime, MsgContent, contentType);
    }

    @JSONField(serialize = false)
    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

}
