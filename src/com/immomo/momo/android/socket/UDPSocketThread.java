package com.immomo.momo.android.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.immomo.momo.android.BaseApplication;

public class UDPSocketThread implements Runnable {

    private static UDPSocketThread instance; // 唯一实例

    private static final String TAG = "SZU_UDPSocketThread";
    private static final int BUFFERLENGTH = 1024; // 缓冲大 小
    private String mGroup = "android"; // 个人主机名（这里用以区分是手机还是PC）
    private String mIMEI; // 个人IMEI
    private String mNickname;
    private String mGender;
    private String mLocalIPaddress;

    private byte[] receiveBuffer = new byte[BUFFERLENGTH];
    private byte[] sendBuffer = new byte[BUFFERLENGTH];

    private static BaseApplication mBaseApplication;
    private boolean isThreadRunning = false;
    private Thread mThread;

    private DatagramSocket UDPSocket;
    private DatagramPacket sendDatagramPacket;
    private DatagramPacket receiveDatagramPacket;

    /**
     * <p>
     * 获取UDPSocketThread实例
     * <p>
     * 单例模式，返回唯一实例
     * 
     * @param paramApplication
     * @return instance
     */
    public static UDPSocketThread getInstance(BaseApplication paramApplication) {
        mBaseApplication = paramApplication;
        if (instance == null) {
            instance = new UDPSocketThread();
        }
        return instance;
    }

    /**
     * <p>
     * 含参构造函数，获取用户登录信息
     * 
     * @param paramApplication
     */
    private UDPSocketThread() {
        mIMEI = mBaseApplication.getIMEI();
        mNickname = mBaseApplication.getNickname();
        mGender = mBaseApplication.getGender();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (isThreadRunning) {
            try {
                UDPSocket.receive(receiveDatagramPacket);
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                isThreadRunning = false;
                receiveDatagramPacket = null;

                if (UDPSocket != null) {
                    UDPSocket.close();
                    UDPSocket = null;
                }

                mThread = null;
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                e.printStackTrace();
                break;
            }

            if (receiveDatagramPacket.getLength() == 0) {
                Log.i(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String UDPListenResStr = ""; // 监听接收到的数据
            try {
                UDPListenResStr = new String(receiveBuffer, 0, receiveDatagramPacket.getLength(),
                        "gbk");
            }
            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "系统不支持GBK编码");
                e.printStackTrace();
            }
            Log.i(TAG, "接收到的UDP数据内容为:" + UDPListenResStr);

            IPMSGProtocol mIPMSGPro = new IPMSGProtocol(UDPListenResStr);
            int commandNo = mIPMSGPro.getCommandNo(); // 获取命令字

            switch (0x000000FF & (mIPMSGPro.getCommandNo())) {

            // 收到上线数据包，添加用户，并回送IPMSG_ANSENTRY应答。
                case IPMSGConst.IPMSG_BR_ENTRY: {
                    Log.i(TAG, "收到上线通知");

                    // addUser(ipmsgPro); // 添加用户
                    // MyFeiGeBaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_BR_ENTRY);

                    // 构造上线应答报文内容
                    IPMSGProtocol mIPMSGSend = new IPMSGProtocol();
                    mIPMSGSend.setVersion(String.valueOf(IPMSGConst.VERSION));
                    mIPMSGSend.setSenderIMEI(mIMEI);
                    mIPMSGSend.setSenderHost(mGroup);
                    mIPMSGSend.setCommandNo(IPMSGConst.IPMSG_ANSENTRY);
                    mIPMSGSend.setAdditionalSection(mNickname + "\0" + mGender);

                    sendUDPdata(mIPMSGSend.getProtocolString() + "\0",
                            receiveDatagramPacket.getAddress(), receiveDatagramPacket.getPort());
                    Log.i(TAG, "成功发送上线应答");
                }
                    break;

                // 收到上线应答，更新在线用户列表
                case IPMSGConst.IPMSG_ANSENTRY: {
                    Log.i(TAG, "收到上线应答");

                    // addUser(ipmsgPro);
                    // MyFeiGeBaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_ANSENTRY);
                }
                    break;

                // 收到下线广播
                case IPMSGConst.IPMSG_BR_EXIT: {
                    String userIp = receiveDatagramPacket.getAddress().getHostAddress();
                    // users.remove(userIp);
                    // MyFeiGeBaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_BR_EXIT);

                    Log.i(TAG, "根据下线报文成功删除ip为" + userIp + "的用户");
                }
                    break;

                // 拒绝接受文件
                case IPMSGConst.IPMSG_RELEASEFILES: {
                    // MyFeiGeBaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_RELEASEFILES);
                }
                    break;

            } // End of switch

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
        mThread = null;

    }

    public void connectUDPSocket() {
        try {
            // 绑定端口
            if (UDPSocket == null)
                UDPSocket = new DatagramSocket(IPMSGConst.PORT);
            Log.i(TAG, "connectUDPSocket() 绑定端口成功");

            // 创建数据接受包
            if (receiveDatagramPacket == null)
                receiveDatagramPacket = new DatagramPacket(receiveBuffer, BUFFERLENGTH);
            Log.i(TAG, "connectUDPSocket() 创建数据接收包成功");

            startUDPSocketThread();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void startUDPSocketThread() {
        if (mThread == null) {
            mThread = new Thread(this);
            mThread.start();
        }
        isThreadRunning = true;
        Log.i(TAG, "startUDPSocketThread() 线程启动成功");
    }

    public void stopUDPSocketThread() {
        if (mThread != null)
            mThread.interrupt();
        Log.i(TAG, "stopUDPSocketThread() 线程停止成功");
    }

    /** 用户上线通知 **/
    public void notifyOnline() {
        IPMSGProtocol mIpmsgProtocol = new IPMSGProtocol();
        mIpmsgProtocol.setVersion(String.valueOf(IPMSGConst.VERSION));
        mIpmsgProtocol.setSenderIMEI(mIMEI);
        mIpmsgProtocol.setSenderHost(mGroup);
        mIpmsgProtocol.setCommandNo(IPMSGConst.IPMSG_BR_ENTRY); // 上线命令

        InetAddress broadcastAddr;
        try {
            broadcastAddr = InetAddress.getByName("255.255.255.255"); // 广播地址
            sendUDPdata(mIpmsgProtocol.getProtocolString() + "\0", broadcastAddr, IPMSGConst.PORT);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "notifyOnline() 广播地址有误");
        }
    }

    /** 用户下线通知 **/
    public void notifyOffline() {
        IPMSGProtocol mIpmsgProtocol = new IPMSGProtocol();
        mIpmsgProtocol.setVersion(String.valueOf(IPMSGConst.VERSION));
        mIpmsgProtocol.setSenderIMEI(mIMEI);
        mIpmsgProtocol.setSenderHost(mGroup);
        mIpmsgProtocol.setCommandNo(IPMSGConst.IPMSG_BR_EXIT); // 下线命令
        mIpmsgProtocol.setAdditionalSection(mIMEI); // 附加信息加入用户IMEI

        InetAddress broadcastAddr;
        try {
            broadcastAddr = InetAddress.getByName("255.255.255.255"); // 广播地址
            sendUDPdata(mIpmsgProtocol.getProtocolString() + "\0", broadcastAddr, IPMSGConst.PORT);
        }
        catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "noticeOnline()....广播地址有误");
        }
    }

    public synchronized void sendUDPdata(String sendStr, InetAddress targetIP, int sendPort) {
        try {
            sendBuffer = sendStr.getBytes("gbk");
            sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, targetIP,
                    sendPort);
            Log.i(TAG, "sendDatagramPacket 创建成功");
            UDPSocket.send(sendDatagramPacket);
            Log.i(TAG, "sendUDPdata() 数据发送成功");
        }
        catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "sendUDPdata() 发送UDP数据包失败");
            e.printStackTrace();
        }
        finally {
            sendDatagramPacket = null;
        }
    }

    // 添加用户到Users的Map中
    private synchronized void addUser(IPMSGProtocol paramIPMSGProtocol) {
        mLocalIPaddress = mBaseApplication.getLocalIPaddress();
        String receiveIP = receiveDatagramPacket.getAddress().getHostAddress();
        User user = new User();
        user.setAlias(paramIPMSGProtocol.getSenderIMEI()); // 别名暂定发送者名称

        String extraInfo = paramIPMSGProtocol.getAdditionalSection();
        String[] userInfo = extraInfo.split("\0"); // 对附加信息进行分割,得到用户名和分组名
        if (userInfo.length < 1) {
            user.setUserName(paramIPMSGProtocol.getSenderIMEI());
            if (receiveIP.equals(mLocalIPaddress))
                user.setGroupName("自己");
            else
                user.setGroupName("对方未分组好友");
        }
        else if (userInfo.length == 1) {
            user.setUserName(userInfo[0]);
            if (receiveIP.equals(mLocalIPaddress))
                user.setGroupName("自己");
            else
                user.setGroupName("对方未分组好友");
        }
        else {
            user.setUserName(userInfo[0]);
            if (receiveIP.equals(mLocalIPaddress))
                user.setGroupName("自己");
            else
                user.setGroupName(userInfo[1]);
        }

        user.setIp(receiveIP);
        user.setHostName(paramIPMSGProtocol.getSenderHost());
        user.setMac(""); // 暂时没用这个字段
        users.put(receiveIP, user);
        Log.i(TAG, "成功添加ip为" + receiveIP + "的用户");
    }
}