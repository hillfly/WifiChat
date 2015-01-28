package szu.wifichat.android.socket.udp;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import szu.wifichat.android.BaseActivity;
import szu.wifichat.android.BaseApplication;
import szu.wifichat.android.entity.Entity;
import szu.wifichat.android.entity.Message;
import szu.wifichat.android.entity.NearByPeople;
import szu.wifichat.android.socket.tcp.TcpService;
import szu.wifichat.android.sql.SqlDBOperate;
import szu.wifichat.android.util.ImageUtils;
import szu.wifichat.android.util.LogUtils;
import szu.wifichat.android.util.SessionUtils;
import android.content.Context;

public class UDPSocketThread implements Runnable {

    private static UDPSocketThread instance; // 唯一实例

    private static final String TAG = "SZU_UDPSocketThread";
    private static final String BROADCASTIP = "255.255.255.255"; // 广播地址
    private static final int BUFFERLENGTH = 1024; // 缓冲大小

    private byte[] receiveBuffer = new byte[BUFFERLENGTH];
    private static byte[] sendBuffer = new byte[BUFFERLENGTH];

    private static BaseApplication mApplication;
    private static Context mContext;
    private boolean isThreadRunning;
    private Thread receiveUDPThread; // 接收UDP数据线程

    private static DatagramSocket UDPSocket;
    private static DatagramPacket sendDatagramPacket;
    private DatagramPacket receiveDatagramPacket;

    private static String mIMEI;
    private NearByPeople mNearByPeople;
    private SqlDBOperate mDBOperate;

    private UDPSocketThread() {
        mApplication.initParam();
        mDBOperate = new SqlDBOperate(mContext);
    }

    /**
     * <p>
     * 获取UDPSocketThread实例
     * <p>
     * 单例模式，返回唯一实例
     * 
     * @param paramApplication
     * @return instance
     */
    public static UDPSocketThread getInstance(BaseApplication application, Context context) {
        if (instance == null) {
            mApplication = application;
            mContext = context;
            instance = new UDPSocketThread();
        }
        return instance;
    }

    @Override
    public void run() {
        while (isThreadRunning) {

            try {
                UDPSocket.receive(receiveDatagramPacket);
            }
            catch (IOException e) {
                isThreadRunning = false;
                receiveDatagramPacket = null;
                if (UDPSocket != null) {
                    UDPSocket.close();
                    UDPSocket = null;
                }
                receiveUDPThread = null;
                LogUtils.e(TAG, "UDP数据包接收失败！线程停止");
                e.printStackTrace();
                break;
            }

            if (receiveDatagramPacket.getLength() == 0) {
                LogUtils.i(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String UDPListenResStr = ""; // 清空以前的监听数据
            try {
                UDPListenResStr = new String(receiveBuffer, 0, receiveDatagramPacket.getLength(),
                        "gbk");
            }
            catch (UnsupportedEncodingException e) {
                LogUtils.e(TAG, "系统不支持GBK编码");
            }
            // Log.i(TAG, "接收到的UDP数据内容为:" + UDPListenResStr);

            IPMSGProtocol ipmsgRes = new IPMSGProtocol(UDPListenResStr);
            int commandNo = ipmsgRes.getCommandNo(); // 获取命令字
            String senderIMEI = ipmsgRes.getSenderIMEI(); // 获取对方IMEI
            String senderIp = receiveDatagramPacket.getAddress().getHostAddress();

            if (BaseApplication.isDebugmode) {
                processCommand(commandNo, ipmsgRes, senderIMEI, senderIp);
            }
            else {
                if (!SessionUtils.isItself(senderIMEI)) {
                    processCommand(commandNo, ipmsgRes, senderIMEI, senderIp);
                }
            }

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receiveDatagramPacket != null) {
                receiveDatagramPacket.setLength(BUFFERLENGTH);
            }

        }

        receiveDatagramPacket = null;
        if (UDPSocket != null) {
            UDPSocket.close();
            UDPSocket = null;
        }
        receiveUDPThread = null;

    }

    public void processCommand(int commandNo, IPMSGProtocol ipmsgRes, String senderIMEI,
            String senderIp) {
        TcpService tcpService;
        switch (commandNo) {

        // 收到上线数据包，添加用户，并回送IPMSG_ANSENTRY应答。
            case IPMSGConst.IPMSG_BR_ENTRY: {
                LogUtils.i(TAG, "收到上线通知");
                addUser(ipmsgRes); // 增加用户至在线列表
                sendUDPdata(IPMSGConst.IPMSG_ANSENTRY, receiveDatagramPacket.getAddress(),
                        mNearByPeople);
                LogUtils.i(TAG, "成功发送上线应答");
            }
                break;

            // 收到上线应答，更新在线用户列表
            case IPMSGConst.IPMSG_ANSENTRY: {
                LogUtils.i(TAG, "收到上线应答");
                addUser(ipmsgRes);
            }
                break;

            // 收到下线广播
            case IPMSGConst.IPMSG_BR_EXIT: {
                mApplication.removeOnlineUser(senderIMEI, 1);
                LogUtils.i(TAG, "根据下线报文成功删除imei为" + senderIMEI + "的用户");
            }
                break;

            // 收到消息
            case IPMSGConst.IPMSG_SENDMSG: {
                LogUtils.i(TAG, "收到MSG消息");
                Message msg = (Message) ipmsgRes.getAddObject();

                switch (msg.getContentType()) {
                    case TEXT:
                        sendUDPdata(IPMSGConst.IPMSG_RECVMSG, senderIp, ipmsgRes.getPacketNo());
                        break;

                    case IMAGE:
                        LogUtils.d(TAG, "收到图片信息");
                        msg.setMsgContent(BaseApplication.IMAG_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        String THUMBNAIL_PATH = BaseApplication.THUMBNAIL_PATH + File.separator
                                + msg.getSenderIMEI();

                        LogUtils.d(TAG, "缩略图路径:" + THUMBNAIL_PATH);
                        LogUtils.d(TAG, "图片接收路径:" + msg.getMsgContent());

                        ImageUtils.createThumbnail(mContext, msg.getMsgContent(), THUMBNAIL_PATH
                                + File.separator);
                        break;

                    case VOICE:
                        LogUtils.d(TAG, "收到录音信息");
                        msg.setMsgContent(BaseApplication.VOICE_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        LogUtils.d(TAG, "接收路径:" + msg.getMsgContent());
                        break;

                    case FILE:
                        LogUtils.d(TAG, "收到文件 发送请求");
                        tcpService = TcpService.getInstance(mContext);
                        tcpService.setSavePath(BaseApplication.FILE_PATH);
                        tcpService.startReceive();
                        sendUDPdata(IPMSGConst.IPMSG_RECIEVE_FILE_DATA, senderIp);
                        msg.setMsgContent(BaseApplication.FILE_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        LogUtils.d(TAG, "接收路径:" + msg.getMsgContent());
                        break;
                }

                mDBOperate.addChattingInfo(senderIMEI, mIMEI, msg.getSendTime(),
                        msg.getMsgContent(), msg.getContentType()); // 将聊天记录加入数据库

                if (!isExistActiveActivity(msg)) { // 若没有对应的ChatActivity打开
                    mApplication.addUnReadPeople(mApplication.getOnlineUser(senderIMEI)); // 添加到未读用户列表

                }
                mApplication.addLastMsgCache(senderIMEI, msg); // 添加到消息缓存
                BaseActivity.sendEmptyMessage(IPMSGConst.IPMSG_SENDMSG);

            }
                break;

            case IPMSGConst.IPMSG_SEND_IMAGE_DATA:
                LogUtils.i(TAG, "收到IMAGE发送请求");

                tcpService = TcpService.getInstance(mContext);
                tcpService.setSavePath(BaseApplication.IMAG_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_RECEIVE_IMAGE_DATA, senderIp);
                break;

            case IPMSGConst.IPMSG_SEND_VOICE_DATA:
                LogUtils.i(TAG, "收到VOICE发送请求");

                tcpService = TcpService.getInstance(mContext);
                tcpService.setSavePath(BaseApplication.VOICE_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_RECIEVE_VOICE_DATA, senderIp);
                break;

            default:
                LogUtils.d(TAG, "收到命令：" + commandNo);
                BaseActivity.sendEmptyMessage(commandNo);
                break;

        } // End of switch
    }

    /** 建立Socket连接 **/
    public void connectUDPSocket() {
        try {
            // 绑定端口
            if (UDPSocket == null)
                UDPSocket = new DatagramSocket(IPMSGConst.PORT);
            LogUtils.i(TAG, "connectUDPSocket() 绑定端口成功");

            // 创建数据接受包
            if (receiveDatagramPacket == null)
                receiveDatagramPacket = new DatagramPacket(receiveBuffer, BUFFERLENGTH);

            startUDPSocketThread();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /** 开始监听线程 **/
    public void startUDPSocketThread() {
        if (receiveUDPThread == null) {
            receiveUDPThread = new Thread(this);
            receiveUDPThread.start();
        }
        isThreadRunning = true;
        LogUtils.i(TAG, "startUDPSocketThread() 线程启动成功");
    }

    /** 暂停监听线程 **/
    public void stopUDPSocketThread() {
        if (receiveUDPThread != null)
            receiveUDPThread.interrupt();
        isThreadRunning = false;
        receiveUDPThread = null;
        LogUtils.i(TAG, "stopUDPSocketThread() 线程停止成功");
    }

    /** 用户上线通知 **/
    public void notifyOnline() {
        // 获取本机用户数据
        mIMEI = SessionUtils.getIMEI();
        String device = SessionUtils.getDevice();
        String nickname = SessionUtils.getNickname();
        String gender = SessionUtils.getGender();
        String localIPaddress = SessionUtils.getLocalIPaddress();
        String constellation = SessionUtils.getConstellation();
        String logintime = SessionUtils.getLoginTime();
        int avatar = SessionUtils.getAvatar();
        int age = SessionUtils.getAge();
        mNearByPeople = new NearByPeople(mIMEI, avatar, device, nickname, gender, age,
                constellation, localIPaddress, logintime);
        sendUDPdata(IPMSGConst.IPMSG_BR_ENTRY, BROADCASTIP, mNearByPeople);
    }

    /** 用户下线通知 **/
    public void notifyOffline() {
        sendUDPdata(IPMSGConst.IPMSG_BR_EXIT, BROADCASTIP);
        LogUtils.e(TAG, "notifyOffline() 下线通知成功");
    }

    /**
     * 判断是否有已打开的聊天窗口来接收对应的数据。
     * 
     * @param paramMsg
     * @return
     */
    private boolean isExistActiveActivity(Message paramMsg) {
        if (!BaseActivity.isExistActiveChatActivity()) {
            return false;
        }
        else {
            OnActiveChatActivityListenner listenner = BaseActivity.getActiveChatActivityListenner();
            return listenner.isThisActivityMsg(paramMsg);
        }
    }

    /** 刷新用户列表 **/
    public void refreshUsers() {
        mApplication.removeOnlineUser(null, 0); // 清空在线用户列表
        notifyOnline(); // 发送上线通知
    }

    /**
     * 添加用户到在线列表中 (线程安全的)
     * 
     * @param paramIPMSGProtocol
     *            包含用户信息的IPMSGProtocol字符串
     */
    private synchronized void addUser(IPMSGProtocol paramIPMSGProtocol) {
        String receiveIMEI = paramIPMSGProtocol.getSenderIMEI();
        if (BaseApplication.isDebugmode) {
            NearByPeople newUser = (NearByPeople) paramIPMSGProtocol.getAddObject();
            mApplication.addOnlineUser(receiveIMEI, newUser);
            mDBOperate.addUserInfo(newUser);
        }
        else {
            if (!SessionUtils.isItself(receiveIMEI)) {
                NearByPeople newUser = (NearByPeople) paramIPMSGProtocol.getAddObject();
                mApplication.addOnlineUser(receiveIMEI, newUser);
                mDBOperate.addUserInfo(newUser);
            }
        }
        LogUtils.i(TAG, "成功添加imei为" + receiveIMEI + "的用户");
    }

    /**
     * 发送UDP数据包
     * 
     * @param commandNo
     *            消息命令
     * @param targetIP
     *            目标地址
     * @param addData
     *            附加数据
     * @see IPMSGConst
     */
    public static synchronized void sendUDPdata(int commandNo, String targetIP) {
        sendUDPdata(commandNo, targetIP, null);
    }

    public static synchronized void sendUDPdata(int commandNo, InetAddress targetIP) {
        sendUDPdata(commandNo, targetIP, null);
    }

    public static synchronized void
            sendUDPdata(int commandNo, InetAddress targetIP, Object addData) {
        sendUDPdata(commandNo, targetIP.getHostAddress(), addData);
    }

    public static synchronized void sendUDPdata(int commandNo, String targetIP, Object addData) {
        // 构造发送协议数据
        IPMSGProtocol ipmsgProtocol = null;
        if (addData == null) {
            ipmsgProtocol = new IPMSGProtocol(mIMEI, commandNo);
        }
        else if (addData instanceof Entity) {
            ipmsgProtocol = new IPMSGProtocol(mIMEI, commandNo, (Entity) addData);
        }
        else if (addData instanceof String) {
            ipmsgProtocol = new IPMSGProtocol(mIMEI, commandNo, (String) addData);
        }
        sendUDPdata(ipmsgProtocol, targetIP);
    }

    public static synchronized void sendUDPdata(IPMSGProtocol ipmsgProtocol, String targetIP) {
        // 构造发送报文
        InetAddress targetAddr;
        try {
            targetAddr = InetAddress.getByName(targetIP); // 目的地址
            sendBuffer = ipmsgProtocol.getProtocolJSON().getBytes("gbk");
            sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, targetAddr,
                    IPMSGConst.PORT);
            UDPSocket.send(sendDatagramPacket);
            LogUtils.i(TAG, "sendUDPdata() 数据发送成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "sendUDPdata() 发送UDP数据包失败");
        }

    }

}