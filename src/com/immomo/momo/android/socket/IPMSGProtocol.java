package com.immomo.momo.android.socket;

import java.util.Date;

/**
 * IPMSG协议抽象类
 * <p>
 * IPMSG协议格式：版本号(默认为1):数据包编号:发送人IMEI:发送主机名:命令:附加数据
 * <p>
 * 数据包编号：一般是取毫秒数。用来唯一地区别每个数据包；
 * <p>
 * SenderIMEI：指的是发送者的设备IMEI
 * <p>
 * 发送主机名：指的是发送者的主机名，在此用来区分是移动设备还是PC，移动设备默认为android.
 * <p>
 * 命令：指的是飞鸽协议中定义的一系列命令，具体见下文；
 * <p>
 * 附加数据：额外发送的数据。当为上线应答报文时，附加信息内容是昵称、性别，中间用"\0"分隔
 * 
 * @see IPMSGConst
 * 
 */
public class IPMSGProtocol {
    private String version; // 版本号 目前都为1
    private String packetNo;// 数据包编号
    private String senderIMEI; // 发送者IMEI
    private String senderDevice; // 发送主机名
    private int commandNo; // 命令
    private String additionalSection; // 附加数据

    public IPMSGProtocol() {
        this.packetNo = getSeconds();   
    }

    // 根据协议字符串初始化
    public IPMSGProtocol(String paramProtocolString) {
        String[] args = paramProtocolString.split(":"); // 以:分割协议串
        version = args[0];
        packetNo = args[1];
        senderIMEI = args[2];
        senderDevice = args[3];
        commandNo = Integer.parseInt(args[4]);
        if (args.length >= 6) { // 是否有附加数据
            additionalSection = args[5];
            int mLength = args.length;
            for (int i = 6; i < mLength; i++) { // 处理附加数据中有:的情况
                additionalSection += (":" + args[i]);
            }
        }
        else {
            additionalSection = "";
        }
    }

    public IPMSGProtocol(String paramSenderIMEI, String paramSenderDevice, int paramCommandNo,
            String paramAdditionalSection) {
        super();
        this.version = "1";
        this.packetNo = getSeconds();
        this.senderIMEI = paramSenderIMEI;
        this.senderDevice = paramSenderDevice;
        this.commandNo = paramCommandNo;
        this.additionalSection = paramAdditionalSection;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String paramVersion) {
        this.version = paramVersion;
    }

    public String getPacketNo() {
        return packetNo;
    }

    public void setPacketNo(String paramPacketNo) {
        this.packetNo = paramPacketNo;
    }

    public String getSenderIMEI() {
        return senderIMEI;
    }

    public void setSenderIMEI(String paramSenderIMEI) {
        this.senderIMEI = paramSenderIMEI;
    }

    public String getSenderDevice() {
        return senderDevice;
    }

    public void setSenderDevice(String paramsenderDevice) {
        this.senderDevice = paramsenderDevice;
    }

    public int getCommandNo() {
        return commandNo;
    }

    public void setCommandNo(int paramCommandNo) {
        this.commandNo = paramCommandNo;
    }

    public String getAdditionalSection() {
        return additionalSection;
    }

    public void setAdditionalSection(String paramAdditionalSection) {
        this.additionalSection = paramAdditionalSection;
    }

    // 得到协议串
    public String getProtocolString() {
        StringBuffer sb = new StringBuffer();
        sb.append(version)
          .append(":" + packetNo)
          .append(":" + senderIMEI)
          .append(":" + senderDevice)
          .append(":" + commandNo + ":");
        if (additionalSection != null)
            sb.append(additionalSection);

        return sb.toString();
    }

    // 得到数据包编号，毫秒数
    private String getSeconds() {
        Date nowDate = new Date();
        return Long.toString(nowDate.getTime());
    }

}
