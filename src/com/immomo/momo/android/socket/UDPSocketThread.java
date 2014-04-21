package com.immomo.momo.android.socket;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.content.Context;
import android.util.Log;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.entity.Entity;
import com.immomo.momo.android.entity.Message;
import com.immomo.momo.android.entity.Message.CONTENT_TYPE;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.sql.SqlDBOperate;
import com.immomo.momo.android.tcp.socket.TcpService;
import com.immomo.momo.android.util.SessionUtils;

public class UDPSocketThread implements Runnable
{

	private static UDPSocketThread instance; // 唯一实例

	private static final String TAG = "SZU_UDPSocketThread";
	private static final String BROADCASTIP = "255.255.255.255"; // 广播地址
	private static final int BUFFERLENGTH = 1024; // 缓冲大小

	private byte[] receiveBuffer = new byte[BUFFERLENGTH];
	private byte[] sendBuffer = new byte[BUFFERLENGTH];

	private static BaseApplication mApplication;
	private static Context mContext;
	private boolean isThreadRunning;
	private Thread receiveUDPThread; // 接收UDP数据线程

	private DatagramSocket UDPSocket;
	private DatagramPacket sendDatagramPacket;
	private DatagramPacket receiveDatagramPacket;

	private String mIMEI;
	private NearByPeople mNearByPeople; // 本机用户类
	// private UserDAO mUserDAO;
	// private ChattingDAO mChattingDAO;
	private SqlDBOperate mDBOperate;// 新增数据库类可以操作用户数据库和聊天信息数据库

	private UDPSocketThread()
	{
		mApplication.initParam(); // 初始化相关参数
		// mUserDAO = new UserDAO(mContext);
		// mChattingDAO = new ChattingDAO(mContext);
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
	public static UDPSocketThread getInstance(BaseApplication application,
			Context context)
	{
		mApplication = application;
		mContext = context;
		if (instance == null)
		{
			instance = new UDPSocketThread();
		}
		return instance;
	}

	@Override
	public void run()
	{
		while (isThreadRunning)
		{

			try
			{
				UDPSocket.receive(receiveDatagramPacket);
			} catch (IOException e)
			{
				isThreadRunning = false;
				receiveDatagramPacket = null;
				if (UDPSocket != null)
				{
					UDPSocket.close();
					UDPSocket = null;
				}
				receiveUDPThread = null;
				Log.e(TAG, "UDP数据包接收失败！线程停止");
				e.printStackTrace();
				break;
			}

			if (receiveDatagramPacket.getLength() == 0)
			{
				Log.i(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
				continue;
			}

			String UDPListenResStr = ""; // 清空以前的监听数据
			try
			{
				UDPListenResStr = new String(receiveBuffer, 0,
						receiveDatagramPacket.getLength(), "gbk");
			} catch (UnsupportedEncodingException e)
			{
				Log.e(TAG, "系统不支持GBK编码");
			}
			Log.i(TAG, "接收到的UDP数据内容为:" + UDPListenResStr);

			IPMSGProtocol ipmsgRes = new IPMSGProtocol(UDPListenResStr);
			int commandNo = ipmsgRes.getCommandNo(); // 获取命令字
			String senderIMEI = ipmsgRes.getSenderIMEI(); // 获取对方IMEI
			
			if (SessionUtils.isItself(senderIMEI))
//			if (!SessionUtils.isItself(senderIMEI))
			{ // 过滤自己发送的广播
				switch (commandNo)
				{

				// 收到上线数据包，添加用户，并回送IPMSG_ANSENTRY应答。
				case IPMSGConst.IPMSG_BR_ENTRY:
				{
					Log.i(TAG, "收到上线通知");
					addUser(ipmsgRes); // 增加用户至在线列表
					// BaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_BR_ENTRY);

					sendUDPdata(IPMSGConst.IPMSG_ANSENTRY,
							receiveDatagramPacket.getAddress(), mNearByPeople);
					Log.i(TAG, "成功发送上线应答");
				}
					break;

				// 收到上线应答，更新在线用户列表
				case IPMSGConst.IPMSG_ANSENTRY:
				{
					Log.i(TAG, "收到上线应答");
					addUser(ipmsgRes); // 增加用户至在线列表
				}
					break;

				// 收到下线广播
				case IPMSGConst.IPMSG_BR_EXIT:
				{
					mApplication.removeOnlineUser(senderIMEI, 1); // 移除用户
					// BaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_BR_EXIT);

					Log.i(TAG, "根据下线报文成功删除imei为" + senderIMEI + "的用户");
				}
					break;

				// 拒绝接受文件
				case IPMSGConst.IPMSG_RELEASEFILES:
				{
					// BaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_RELEASEFILES);
				}
					break;

				// 收到消息
				case IPMSGConst.IPMSG_SENDMSG:
				{
					Log.i(TAG, "收到MSG消息");
					String senderIp = receiveDatagramPacket.getAddress()
							.getHostAddress();
					Message msg = (Message) ipmsgRes.getAddObject();
					Log.d(TAG, msg.getContentType().toString());
					if (msg.getContentType() == CONTENT_TYPE.IMAGE)
					{

						Log.d(TAG, "收到图片发送请求");
						TcpService tcpService = TcpService
								.getInstance(mContext);
						// String filePathString = "/storage/sdcard0/";
						tcpService.setSavePath(BaseApplication.IMAG_PATH);
						tcpService.startReceive();
						sendUDPdata(IPMSGConst.IPMSG_RECIEVEIMAGEDATA, senderIp);

						msg.setMsgContent(BaseApplication.IMAG_PATH
								+ File.separator + msg.getSenderIMEI()
								+ File.separator + msg.getMsgContent());
						Log.d(TAG, "接收路径:" + msg.getMsgContent());
						mDBOperate.addChattingInfo(senderIMEI, mIMEI,
								msg.getSendTime(), msg.getMsgContent(),
								msg.getContentType());// 将聊天记录加入数据库

					} else if (msg.getContentType() == CONTENT_TYPE.TEXT)
					{
						sendUDPdata(IPMSGConst.IPMSG_RECVMSG, senderIp,
								ipmsgRes.getPacketNo());

					}
					// 消息接受确认

					Log.d(TAG, msg.getMsgContent());

					if (!isExistActiveActivity(msg))
					{ // 若没有对应的ChatActivity打开
						mApplication.addUnReadPeople(mApplication
								.getOnlineUser(senderIMEI)); // 添加到未读用户列表
						if (msg.getContentType() == CONTENT_TYPE.TEXT)
							BaseActivity
									.sendEmptyMessage(IPMSGConst.IPMSG_SENDMSG);
						// mChattingDAO.add(new
						// ChattingInfo(mUserDAO.getID(senderIMEI), mUserDAO
						// .getID(mIMEI), msg.getSendTime(),
						// msg.getMsgContent())); //将聊天记录加入数据库，旧
						mDBOperate.addChattingInfo(senderIMEI, mIMEI,
								msg.getSendTime(), msg.getMsgContent(),
								msg.getContentType());// 将聊天记录加入数据库

					}
					mApplication.addLastMsgCache(senderIMEI, msg); // 添加到消息缓存

				}
					break;

				case IPMSGConst.IPMSG_RECIEVEIMAGEDATA:
				{
					Log.d(TAG, "收到图片发送请求确认");
					BaseActivity
							.sendEmptyMessage(IPMSGConst.IPMSG_RECIEVEIMAGEDATA);
				}
					break;
				case IPMSGConst.IPMSG_GETIMAGESUCCESS:
				{

				}
					break;
				} // End of switch

				// 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
				if (receiveDatagramPacket != null)
				{
					receiveDatagramPacket.setLength(BUFFERLENGTH);
				}
			}
		}

		receiveDatagramPacket = null;
		if (UDPSocket != null)
		{
			UDPSocket.close();
			UDPSocket = null;
		}
		receiveUDPThread = null;

	}

	/** 建立Socket连接 **/
	public void connectUDPSocket()
	{
		try
		{
			// 绑定端口
			if (UDPSocket == null)
				UDPSocket = new DatagramSocket(IPMSGConst.PORT);
			Log.i(TAG, "connectUDPSocket() 绑定端口成功");

			// 创建数据接受包
			if (receiveDatagramPacket == null)
				receiveDatagramPacket = new DatagramPacket(receiveBuffer,
						BUFFERLENGTH);
			Log.i(TAG, "connectUDPSocket() 创建数据接收包成功");

			startUDPSocketThread();
		} catch (SocketException e)
		{
			e.printStackTrace();
		}
	}

	/** 开始监听线程 **/
	public void startUDPSocketThread()
	{
		if (receiveUDPThread == null)
		{
			receiveUDPThread = new Thread(this);
			receiveUDPThread.start();
		}
		isThreadRunning = true;
		Log.i(TAG, "startUDPSocketThread() 线程启动成功");
	}

	/** 暂停监听线程 **/
	public void stopUDPSocketThread()
	{
		if (receiveUDPThread != null)
			receiveUDPThread.interrupt();
		isThreadRunning = false;
		Log.i(TAG, "stopUDPSocketThread() 线程停止成功");
	}

	/** 用户上线通知 **/
	public void notifyOnline()
	{
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
		mNearByPeople = new NearByPeople(mIMEI, avatar, device, nickname,
				gender, age, constellation, localIPaddress, logintime);
		sendUDPdata(IPMSGConst.IPMSG_BR_ENTRY, BROADCASTIP, mNearByPeople);
	}

	/** 用户下线通知 **/
	public void notifyOffline()
	{
		sendUDPdata(IPMSGConst.IPMSG_BR_EXIT, BROADCASTIP);
		Log.e(TAG, "notifyOffline() 下线通知成功");
	}

	/**
	 * 判断是否有已打开的聊天窗口来接收对应的数据。
	 * 
	 * @param paramMsg
	 * @return
	 */
	private boolean isExistActiveActivity(Message paramMsg)
	{
		if (!BaseActivity.isExistActiveChatActivity())
		{
			return false;
		} else
		{
			OnActiveChatActivityListenner listenner = BaseActivity
					.getActiveChatActivityListenner();
			return listenner.isThisActivityMsg(paramMsg);
		}
	}

	/** 刷新用户列表 **/
	public void refreshUsers()
	{
		mApplication.removeOnlineUser(null, 0); // 清空在线用户列表
		notifyOnline(); // 发送上线通知
		// MyFeiGeBaseActivity.sendEmptyMessage(IpMessageConst.IPMSG_BR_ENTRY);
	}

	/**
	 * 添加用户到在线列表中 (线程安全的)
	 * 
	 * @param paramIPMSGProtocol
	 *            包含用户信息的IPMSGProtocol字符串
	 */
	private synchronized void addUser(IPMSGProtocol paramIPMSGProtocol)
	{
		String receiveIMEI = paramIPMSGProtocol.getSenderIMEI();
		if (SessionUtils.isItself(receiveIMEI))
//		if (!SessionUtils.isItself(receiveIMEI))
		{
			NearByPeople newUser = (NearByPeople) paramIPMSGProtocol
					.getAddObject();
			mApplication.addOnlineUser(receiveIMEI, newUser);
			// TODO 添加用户进数据库
			// mUserDAO.add(newUser);//旧
			mDBOperate.addUserInfo(newUser);// 新
			Log.i(TAG, "成功添加imei为" + receiveIMEI + "的用户");
		}
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
	public synchronized void sendUDPdata(int commandNo, String targetIP)
	{
		sendUDPdata(commandNo, targetIP, null);
	}

	public synchronized void sendUDPdata(int commandNo, InetAddress targetIP)
	{
		sendUDPdata(commandNo, targetIP, null);
	}

	public synchronized void sendUDPdata(int commandNo, InetAddress targetIP,
			Object addData)
	{
		sendUDPdata(commandNo, targetIP.getHostAddress(), addData);
	}

	public synchronized void sendUDPdata(int commandNo, String targetIP,
			Object addData)
	{
		// 构造发送协议数据
		IPMSGProtocol ipmsgProtocol = null;
		if (addData == null)
		{
			ipmsgProtocol = new IPMSGProtocol(mIMEI, commandNo);
		} else if (addData instanceof Entity)
		{
			ipmsgProtocol = new IPMSGProtocol(mIMEI, commandNo,
					(Entity) addData);
		} else if (addData instanceof String)
		{
			ipmsgProtocol = new IPMSGProtocol(mIMEI, commandNo,
					(String) addData);
		}
		sendUDPdata(ipmsgProtocol, targetIP);
	}

	public synchronized void sendUDPdata(IPMSGProtocol ipmsgProtocol,
			String targetIP)
	{
		// 构造发送报文
		InetAddress targetAddr;
		try
		{
			targetAddr = InetAddress.getByName(targetIP); // 目的地址
			sendBuffer = ipmsgProtocol.getProtocolJSON().getBytes("gbk");
			sendDatagramPacket = new DatagramPacket(sendBuffer,
					sendBuffer.length, targetAddr, IPMSGConst.PORT);
			Log.i(TAG, "sendDatagramPacket 创建成功");
			UDPSocket.send(sendDatagramPacket);
			Log.i(TAG, "sendUDPdata() 数据发送成功");
		} catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "sendUDPdata() 发送UDP数据包失败");
		} finally
		{
			sendDatagramPacket = null;
		}
	}

}