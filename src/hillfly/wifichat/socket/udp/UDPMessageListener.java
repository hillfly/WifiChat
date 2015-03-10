package hillfly.wifichat.socket.udp;

import hillfly.wifichat.ActivitiesManager;
import hillfly.wifichat.BaseApplication;
import hillfly.wifichat.activity.message.ChatActivity;
import hillfly.wifichat.bean.Entity;
import hillfly.wifichat.bean.Message;
import hillfly.wifichat.bean.Users;
import hillfly.wifichat.socket.tcp.TcpService;
import hillfly.wifichat.sql.SqlDBOperate;
import hillfly.wifichat.util.ImageUtils;
import hillfly.wifichat.util.LogUtils;
import hillfly.wifichat.util.SessionUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

public class UDPMessageListener implements Runnable {

    private static final String TAG = "SZU_UDPMessageListener";

    private static final int POOL_SIZE = 5; // 单个CPU线程池大小
    private static final int BUFFERLENGTH = 1024; // 缓冲大小

    private static byte[] sendBuffer = new byte[BUFFERLENGTH];
    private static byte[] receiveBuffer = new byte[BUFFERLENGTH];

    private HashMap<String, String> mLastMsgCache; // 最后一条消息缓存，以IMEI为KEY
    private ArrayList<Users> mUnReadPeopleList; // 未读消息的用户队列
    private HashMap<String, Users> mOnlineUsers; // 在线用户集合，以IMEI为KEY

    private String BROADCASTIP;
    private Thread receiveUDPThread;
    private boolean isThreadRunning;
    private List<OnNewMsgListener> mListenerList;

    private Users mLocalUser; // 本机用户对象
    private SqlDBOperate mDBOperate;

    private static ExecutorService executor;
    private static DatagramSocket UDPSocket;
    private static DatagramPacket sendDatagramPacket;
    private DatagramPacket receiveDatagramPacket;

    private static Context mContext;
    private static UDPMessageListener instance;

    private UDPMessageListener() {
        BROADCASTIP = "255.255.255.255";
        // BROADCASTIP = WifiUtils.getBroadcastAddress();

        mDBOperate = new SqlDBOperate(mContext);
        mListenerList = new ArrayList<UDPMessageListener.OnNewMsgListener>();
        mOnlineUsers = new LinkedHashMap<String, Users>();
        mLastMsgCache = new HashMap<String, String>();
        mUnReadPeopleList = new ArrayList<Users>();

        int cpuNums = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNums * POOL_SIZE); // 根据CPU数目初始化线程池
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
    public static UDPMessageListener getInstance(Context context) {
        if (instance == null) {
            mContext = context;
            instance = new UDPMessageListener();
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
                LogUtils.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String UDPListenResStr = "";
            try {
                UDPListenResStr = new String(receiveBuffer, 0, receiveDatagramPacket.getLength(),
                        "gbk");
            }
            catch (UnsupportedEncodingException e) {
                LogUtils.e(TAG, "系统不支持GBK编码");
            }

            IPMSGProtocol ipmsgRes = new IPMSGProtocol(UDPListenResStr);
            int commandNo = ipmsgRes.getCommandNo(); // 获取命令字
            String senderIMEI = ipmsgRes.getSenderIMEI();
            String senderIp = receiveDatagramPacket.getAddress().getHostAddress();

            if (BaseApplication.isDebugmode) {
                processMessage(commandNo, ipmsgRes, senderIMEI, senderIp);
            }
            else {
                if (!SessionUtils.isLocalUser(senderIMEI)) {
                    processMessage(commandNo, ipmsgRes, senderIMEI, senderIp);
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

    public void processMessage(int commandNo, IPMSGProtocol ipmsgRes, String senderIMEI,
            String senderIp) {
        TcpService tcpService;
        switch (commandNo) {

        // 收到上线数据包，添加用户，并回送IPMSG_ANSENTRY应答。
            case IPMSGConst.IPMSG_BR_ENTRY: {
                LogUtils.i(TAG, "收到上线通知");
                addUser(ipmsgRes);
                sendUDPdata(IPMSGConst.IPMSG_ANSENTRY, receiveDatagramPacket.getAddress(),
                        mLocalUser);
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
                removeOnlineUser(senderIMEI, 1);
                LogUtils.i(TAG, "成功删除imei为" + senderIMEI + "的用户");
            }
                break;

            case IPMSGConst.IPMSG_REQUEST_IMAGE_DATA:
                LogUtils.i(TAG, "收到IMAGE发送请求");

                tcpService = TcpService.getInstance(mContext);
                tcpService.setSavePath(BaseApplication.IMAG_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_CONFIRM_IMAGE_DATA, senderIp);
                break;

            case IPMSGConst.IPMSG_REQUEST_VOICE_DATA:
                LogUtils.i(TAG, "收到VOICE发送请求");

                tcpService = TcpService.getInstance(mContext);
                tcpService.setSavePath(BaseApplication.VOICE_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_CONFIRM_VOICE_DATA, senderIp);
                break;

            case IPMSGConst.IPMSG_SENDMSG: {
                LogUtils.i(TAG, "收到MSG消息");
                Message msg = (Message) ipmsgRes.getAddObject();

                switch (msg.getContentType()) {
                    case TEXT:
                        sendUDPdata(IPMSGConst.IPMSG_RECVMSG, senderIp, ipmsgRes.getPacketNo());
                        break;

                    case IMAGE:
                        LogUtils.i(TAG, "收到图片信息");
                        msg.setMsgContent(BaseApplication.IMAG_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        String THUMBNAIL_PATH = BaseApplication.THUMBNAIL_PATH + File.separator
                                + msg.getSenderIMEI();

                        LogUtils.d(TAG, "缩略图文件夹路径:" + THUMBNAIL_PATH);
                        LogUtils.d(TAG, "图片文件路径:" + msg.getMsgContent());

                        ImageUtils.createThumbnail(mContext, msg.getMsgContent(), THUMBNAIL_PATH
                                + File.separator);
                        break;

                    case VOICE:
                        LogUtils.i(TAG, "收到录音信息");
                        msg.setMsgContent(BaseApplication.VOICE_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        LogUtils.d(TAG, "文件路径:" + msg.getMsgContent());
                        break;

                    case FILE:
                        LogUtils.i(TAG, "收到文件 发送请求");
                        tcpService = TcpService.getInstance(mContext);
                        tcpService.setSavePath(BaseApplication.FILE_PATH);
                        tcpService.startReceive();
                        sendUDPdata(IPMSGConst.IPMSG_CONFIRM_FILE_DATA, senderIp);
                        msg.setMsgContent(BaseApplication.FILE_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        LogUtils.d(TAG, "文件路径:" + msg.getMsgContent());
                        break;
                }

                // 加入数据库
                mDBOperate.addChattingInfo(senderIMEI, SessionUtils.getIMEI(), msg.getSendTime(),
                        msg.getMsgContent(), msg.getContentType());

                // 加入未读消息列表
                android.os.Message pMessage = new android.os.Message();
                pMessage.what = commandNo;
                pMessage.obj = msg;

                ChatActivity v = ActivitiesManager.getChatActivity();
                if (v == null) {
                    addUnReadPeople(getOnlineUser(senderIMEI)); // 添加到未读用户列表
                    for (int i = 0; i < mListenerList.size(); i++) {
                        android.os.Message pMsg = new android.os.Message();
                        pMsg.what = IPMSGConst.IPMSG_RECVMSG;
                        mListenerList.get(i).processMessage(pMsg);
                    }
                }
                else {
                    v.processMessage(pMessage);
                }

                addLastMsgCache(senderIMEI, msg); // 添加到消息缓存
                BaseApplication.playNotification();

            }
                break;

            default:
                LogUtils.i(TAG, "收到命令：" + commandNo);

                android.os.Message pMessage = new android.os.Message();
                pMessage.what = commandNo;

                ChatActivity v = ActivitiesManager.getChatActivity();
                if (v != null) {
                    v.processMessage(pMessage);
                }

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
        isThreadRunning = false;
        if (receiveUDPThread != null)
            receiveUDPThread.interrupt();
        receiveUDPThread = null;
        instance = null; // 置空, 消除静态变量引用
        LogUtils.i(TAG, "stopUDPSocketThread() 线程停止成功");
    }

    public void addMsgListener(OnNewMsgListener listener) {
        this.mListenerList.add(listener);
    }

    public void removeMsgListener(OnNewMsgListener listener) {
        this.mListenerList.remove(listener);
    }

    /** 用户上线通知 **/
    public void notifyOnline() {
        // 获取本机用户数据
        mLocalUser = SessionUtils.getLocalUserInfo();
        sendUDPdata(IPMSGConst.IPMSG_BR_ENTRY, BROADCASTIP, mLocalUser);
        LogUtils.i(TAG, "notifyOnline() 上线通知成功");
    }

    /** 用户下线通知 **/
    public void notifyOffline() {
        sendUDPdata(IPMSGConst.IPMSG_BR_EXIT, BROADCASTIP);
        LogUtils.i(TAG, "notifyOffline() 下线通知成功");
    }

    /** 刷新用户列表 **/
    public void refreshUsers() {
        removeOnlineUser(null, 0); // 清空在线用户列表
        notifyOnline();
    }

    /**
     * 添加用户到在线列表中 (线程安全的)
     * 
     * @param paramIPMSGProtocol
     *            包含用户信息的IPMSGProtocol字符串
     */
    private void addUser(IPMSGProtocol paramIPMSGProtocol) {
        String receiveIMEI = paramIPMSGProtocol.getSenderIMEI();
        if (BaseApplication.isDebugmode) {
            Users newUser = (Users) paramIPMSGProtocol.getAddObject();
            addOnlineUser(receiveIMEI, newUser);
            mDBOperate.addUserInfo(newUser);
        }
        else {
            if (!SessionUtils.isLocalUser(receiveIMEI)) {
                Users newUser = (Users) paramIPMSGProtocol.getAddObject();
                addOnlineUser(receiveIMEI, newUser);
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
    public static void sendUDPdata(int commandNo, String targetIP) {
        sendUDPdata(commandNo, targetIP, null);
    }

    public static void sendUDPdata(int commandNo, InetAddress targetIP) {
        sendUDPdata(commandNo, targetIP, null);
    }

    public static void sendUDPdata(int commandNo, InetAddress targetIP, Object addData) {
        sendUDPdata(commandNo, targetIP.getHostAddress(), addData);
    }

    public static void sendUDPdata(int commandNo, String targetIP, Object addData) {
        IPMSGProtocol ipmsgProtocol = null;
        String imei = SessionUtils.getIMEI();

        if (addData == null) {
            ipmsgProtocol = new IPMSGProtocol(imei, commandNo);
        }
        else if (addData instanceof Entity) {
            ipmsgProtocol = new IPMSGProtocol(imei, commandNo, (Entity) addData);
        }
        else if (addData instanceof String) {
            ipmsgProtocol = new IPMSGProtocol(imei, commandNo, (String) addData);
        }
        sendUDPdata(ipmsgProtocol, targetIP);
    }

    public static void sendUDPdata(final IPMSGProtocol ipmsgProtocol, final String targetIP) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    InetAddress targetAddr = InetAddress.getByName(targetIP); // 目的地址
                    sendBuffer = ipmsgProtocol.getProtocolJSON().getBytes("gbk");
                    sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                            targetAddr, IPMSGConst.PORT);
                    UDPSocket.send(sendDatagramPacket);
                    LogUtils.i(TAG, "sendUDPdata() 数据发送成功");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "sendUDPdata() 发送UDP数据包失败");
                }

            }
        });

    }

    public synchronized void addOnlineUser(String paramIMEI, Users paramObject) {
        mOnlineUsers.put(paramIMEI, paramObject);

        for (int i = 0; i < mListenerList.size(); i++) {
            android.os.Message pMsg = new android.os.Message();
            pMsg.what = IPMSGConst.IPMSG_BR_ENTRY;
            mListenerList.get(i).processMessage(pMsg);
        }

        LogUtils.d(TAG, "addUser | OnlineUsersNum：" + mOnlineUsers.size());
    }

    public Users getOnlineUser(String paramIMEI) {
        return mOnlineUsers.get(paramIMEI);
    }

    /**
     * 移除在线用户
     * 
     * @param paramIMEI
     *            需要移除的用户IMEI
     * @param paramtype
     *            操作类型，0:清空在线列表，1:移除指定用户
     */
    public void removeOnlineUser(String paramIMEI, int paramtype) {
        if (paramtype == 1) {
            mOnlineUsers.remove(paramIMEI);
            for (int i = 0; i < mListenerList.size(); i++) {
                android.os.Message pMsg = new android.os.Message();
                pMsg.what = IPMSGConst.IPMSG_BR_EXIT;
                mListenerList.get(i).processMessage(pMsg);
            }

        }
        else if (paramtype == 0) {
            mOnlineUsers.clear();
        }

        LogUtils.d(TAG, "removeUser | OnlineUsersNum：" + mOnlineUsers.size());
    }

    public HashMap<String, Users> getOnlineUserMap() {
        return mOnlineUsers;
    }

    /**
     * 新增用户缓存
     * 
     * @param paramIMEI
     *            新增记录的对应用户IMEI
     * @param paramMsg
     *            需要缓存的消息对象
     */
    public void addLastMsgCache(String paramIMEI, Message msg) {
        StringBuffer content = new StringBuffer();
        switch (msg.getContentType()) {
            case FILE:
                content.append("<FILE>: ").append(msg.getMsgContent());
                break;
            case IMAGE:
                content.append("<IMAGE>: ").append(msg.getMsgContent());
                break;
            case VOICE:
                content.append("<VOICE>: ").append(msg.getMsgContent());
                break;
            default:
                content.append(msg.getMsgContent());
                break;
        }
        if (msg.getMsgContent().isEmpty()) {
            content.append(" ");
        }
        mLastMsgCache.put(paramIMEI, content.toString());
    }

    /**
     * 获取消息缓存
     * 
     * @param paramIMEI
     *            需要获取消息缓存记录的用户IMEI
     * @return
     */
    public String getLastMsgCache(String paramIMEI) {
        return mLastMsgCache.get(paramIMEI);
    }

    /**
     * 移除消息缓存
     * 
     * @param paramIMEI
     *            需要清除缓存的用户IMEI
     */
    public void removeLastMsgCache(String paramIMEI) {
        mLastMsgCache.remove(paramIMEI);
    }

    public void clearMsgCache() {
        mLastMsgCache.clear();
    }

    public void clearUnReadMessages() {
        mUnReadPeopleList.clear();
    }

    /**
     * 新增未读消息用户
     * 
     * @param people
     */
    public void addUnReadPeople(Users people) {
        if (!mUnReadPeopleList.contains(people))
            mUnReadPeopleList.add(people);
    }

    /**
     * 获取未读消息队列
     * 
     * @return
     */
    public ArrayList<Users> getUnReadPeopleList() {
        return mUnReadPeopleList;
    }

    /**
     * 获取未读用户数
     * 
     * @return
     */
    public int getUnReadPeopleSize() {
        return mUnReadPeopleList.size();
    }

    /**
     * 移除指定未读用户
     * 
     * @param people
     */
    public void removeUnReadPeople(Users people) {
        if (mUnReadPeopleList.contains(people))
            mUnReadPeopleList.remove(people);
    }

    /**
     * 新消息处理接口
     */
    public interface OnNewMsgListener {
        public void processMessage(android.os.Message pMsg);
    }

}