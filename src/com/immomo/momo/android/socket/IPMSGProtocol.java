package com.immomo.momo.android.socket;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alibaba.fastjson.annotation.JSONField;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.util.JsonUtils;

/**
 * IPMSG协议抽象类
 * <p>
 * 数据包编号：一般是取毫秒数。用来唯一地区别每个数据包；
 * <p>
 * SenderIMEI：指的是发送者的设备IMEI
 * <p>
 * 命令：指的是飞鸽协议中定义的一系列命令，具体见下文；
 * <p>
 * 附加数据：额外发送的数据
 * 
 * @see IPMSGConst
 * 
 */
public class IPMSGProtocol {
    private static final String TAG = "SZU_IPMSGPProtocol";
    private static final String PACKETNO = "packetNo";
    private static final String COMMANDNO = "commandNo";
    private static final String ADDITIONAL = "additional";

    private String packetNo;// 数据包编号
    private String senderIMEI; // 发送者IMEI
    private int commandNo; // 命令
    private String addJSON; // 附加信息JSON
    private Object addObject; // 附加对象

    public IPMSGProtocol() {
        this.packetNo = getSeconds();
    }

    // 根据协议字符串初始化
    public IPMSGProtocol(String paramProtocolJSON) {
        JSONObject protocolJSON;
        try {
            protocolJSON = new JSONObject(paramProtocolJSON);
            packetNo = protocolJSON.getString(PACKETNO);
            commandNo = protocolJSON.getInt(COMMANDNO);
            senderIMEI = protocolJSON.getString(NearByPeople.IMEI);
            if (protocolJSON.has(ADDITIONAL)) {
                addJSON = protocolJSON.getString(ADDITIONAL);
                addObject = JsonUtils.getObject(addJSON, NearByPeople.class);
            }
        }
        catch (JSONException e) {
            Log.e(TAG, "非标准JSON文本");
        }
    }

    public IPMSGProtocol(String paramSenderIMEI, int paramCommandNo, NearByPeople paramPeople) {
        super();
        this.packetNo = getSeconds();
        this.senderIMEI = paramSenderIMEI;
        this.commandNo = paramCommandNo;
        this.addObject = paramPeople;
    }

    @JSONField(name = PACKETNO)
    public String getPacketNo() {
        return packetNo;
    }

    public void setPacketNo(String paramPacketNo) {
        this.packetNo = paramPacketNo;
    }

    @JSONField(name = NearByPeople.IMEI)
    public String getSenderIMEI() {
        return senderIMEI;
    }

    public void setSenderIMEI(String paramSenderIMEI) {
        this.senderIMEI = paramSenderIMEI;
    }

    @JSONField(name = COMMANDNO)
    public int getCommandNo() {
        return commandNo;
    }

    public void setCommandNo(int paramCommandNo) {
        this.commandNo = paramCommandNo;
    }

    @JSONField(name = ADDITIONAL)
    public Object getAddObject() {
        return addObject;
    }

    public void setAddObject(NearByPeople paramObject) {
        this.addObject = paramObject;
    }

    @JSONField(serialize = false)
    public String getAddJSON() {
        return this.addJSON;
    }

    public void setAddJSON(String paramJSONstr) {
        this.addJSON = paramJSONstr;
    }

    // 输出协议JSON串
    @JSONField(serialize = false)
    public String getProtocolJSON() {
        return JsonUtils.createJsonString(this);
    }

    // 得到数据包编号，毫秒数
    @JSONField(serialize = false)
    private String getSeconds() {
        Date nowDate = new Date();
        return Long.toString(nowDate.getTime());
    }

}
