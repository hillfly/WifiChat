package com.immomo.momo.android.socket;

import android.R.integer;



/**
 * 飞鸽协议常量
 */
public class IPMSGConst {
	public static final int VERSION = 0x001;		// 版本号
	public static final int PORT = 0x0979;			// 端口号，飞鸽协议默认端口2425
	
	// 命令
	public static final int IPMSG_NOOPERATION		 = 0x00000000;	//不进行任何操作
	public static final int IPMSG_BR_ENTRY			 = 0x00000001;	//用户上线
	public static final int IPMSG_BR_EXIT		 	 = 0x00000002;	//用户退出
	public static final int IPMSG_ANSENTRY			 = 0x00000003;	//通报在线
	public static final int IPMSG_BR_ABSENCE		 = 0x00000004;	//改为缺席模式
	
	public static final int IPMSG_BR_ISGETLIST		 = 0x00000010;	//寻找有效的可以发送用户列表的成员
	public static final int IPMSG_OKGETLIST			 = 0x00000011;	//通知用户列表已经获得
	public static final int IPMSG_GETLIST			 = 0x00000012;	//用户列表发送请求
	public static final int IPMSG_ANSLIST			 = 0x00000013;	//应答用户列表发送请求
	public static final int IPMSG_FILE_MTIME		 = 0x00000014;	//
	public static final int IPMSG_FILE_CREATETIME	 = 0x00000016;	//
	public static final int IPMSG_BR_ISGETLIST2		 = 0x00000018;	//
	
	public static final int IPMSG_SENDMSG 			 = 0x00000020;	//发送消息
	public static final int IPMSG_RECVMSG 			 = 0x00000021;	//通报收到消息
	public static final int IPMSG_READMSG 			 = 0x00000030;	//消息打开通知
	public static final int IPMSG_DELMSG 			 = 0x00000031;	//消息丢弃通知
	public static final int IPMSG_ANSREADMSG		 = 0x00000032;	//消息打开确认通知（version-8中添加）
	
	public static final int IPMSG_GETINFO			 = 0x00000040;	//获得IPMSG版本信息
	public static final int IPMSG_SENDINFO			 = 0x00000041;	//发送IPMSG版本信息
	
	public static final int IPMSG_GETABSENCEINFO	 = 0x00000050;	//获得缺席信息
	public static final int IPMSG_SENDABSENCEINFO	 = 0x00000051;	//发送缺席信息
	

	public static final int IPMSG_GETFILEDATA		 = 0x00000060;	//文件传输请求
	public static final int IPMSG_RELEASEFILES		 = 0x00000061;	//丢弃附加文件
	public static final int IPMSG_GETDIRFILES		 = 0x00000062;	//附着统计文件请求
	
	/*zhuangliebin新增*/
	public static final int IPMSG_GETIMAGEDATA       = 0x00000063;  //图片发送请求
	public static final int IPMSG_RECIEVEIMAGEDATA   = 0x00000064;  //图片接收确认
	public static final int IPMSG_GETIMAGESUCCESS    = 0x00000065;  //图片接收成功
	/*zhuangliebin新增*/
	
	public static final int IPMSG_GETPUBKEY			 = 0x00000072;	//获得RSA公钥
	public static final int IPMSG_ANSPUBKEY			 = 0x00000073;	//应答RSA公钥
	
	/* option for all command */
	public static final int IPMSG_ABSENCEOPT 		 = 0x00000100;	//缺席模式
	public static final int IPMSG_SERVEROPT 		 = 0x00000200;	//服务器（保留）
	public static final int IPMSG_DIALUPOPT 		 = 0x00010000;	//发送给个人
	public static final int IPMSG_FILEATTACHOPT 	 = 0x00200000;	//附加文件
	public static final int IPMSG_ENCRYPTOPT		 = 0x00400000;	//加密
	
	/*  option for send command  */
	public static final int IPMSG_SENDCHECKOPT = 0x00000100;	//传送验证
	public static final int IPMSG_SECRETOPT = 0x00000200;		//密封的消息
	public static final int IPMSG_BROADCASTOPT = 0x00000400;	//广播
	public static final int IPMSG_MULTICASTOPT = 0x00000800;	//多播
	public static final int IPMSG_NOPOPUPOPT = 0x00001000;		//（不再有效）
	public static final int IPMSG_AUTORETOPT = 0x00002000;		//自动应答(Ping-pong protection)
	public static final int IPMSG_RETRYOPT = 0x00004000;		//重发标识（用于请求用户列表时）
	public static final int IPMSG_PASSWORDOPT = 0x00008000;		//密码
	public static final int IPMSG_NOLOGOPT = 0x00020000;		//没有日志文件
	public static final int IPMSG_NEWMUTIOPT = 0x00040000;		//新版本的多播（保留）
	public static final int IPMSG_NOADDLISTOPT = 0x00080000;	//不添加用户列表 Notice to the members outside of BR_ENTRY
	public static final int IPMSG_READCHECKOPT = 0x00100000;	//密封消息验证（version8中添加）
	public static final int IPMSG_SECRETEXOPT = (IPMSG_READCHECKOPT|IPMSG_SECRETOPT);
	
	/* encryption flags for encrypt command */
	public static final int IPMSG_RSA_512 			 = 0x00000001;
	public static final int IPMSG_RSA_1024 			 = 0x00000002;
	public static final int IPMSG_RSA_2048 			 = 0x00000004;
	public static final int IPMSG_RC2_40 			 = 0x00001000;
	public static final int IPMSG_RC2_128 			 = 0x00004000;
	public static final int IPMSG_RC2_256 			 = 0x00008000;
	public static final int IPMSG_BLOWFISH_128 			 = 0x00020000;
	public static final int IPMSG_BLOWFISH_256 			 = 0x00040000;
	public static final int IPMSG_SIGN_MD5 			 = 0x10000000;
	
	/* file types for fileattach command */
	public static final int IPMSG_FILE_REGULAR 			 = 0x00000001;
	public static final int IPMSG_FILE_DIR 			 = 0x00000002;
	public static final int IPMSG_FILE_RETPARENT 			 = 0x00000003;	// return parent directory
	public static final int IPMSG_FILE_SYMLINK 			 = 0x00000004;
	public static final int IPMSG_FILE_CDEV 			 = 0x00000005;	// for UNIX
	public static final int IPMSG_FILE_BDEV 			 = 0x00000006;	// for UNIX
	public static final int IPMSG_FILE_FIFO 			 = 0x00000007;	// for UNIX
	public static final int IPMSG_FILE_RESFORK 			 = 0x00000010;	// for Mac

	/* file attribute options for fileattach command */
	public static final int IPMSG_FILE_RONLYOPT 			 = 0x00000100;
	public static final int IPMSG_FILE_HIDDENOPT 			 = 0x00001000;
	public static final int IPMSG_FILE_EXHIDDENOPT 			 = 0x00002000;	// for MacOS X
	public static final int IPMSG_FILE_ARCHIVEOPT 			 = 0x00004000;
	public static final int IPMSG_FILE_SYSTEMOPT 			 = 0x00008000;

	/* extend attribute types for fileattach command */
	public static final int IPMSG_FILE_UID 				 = 0x00000001;
	public static final int IPMSG_FILE_USERNAME 		 = 0x00000002;	// uid by string
	public static final int IPMSG_FILE_GID 				 = 0x00000003;
	public static final int IPMSG_FILE_GROUPNAME 		 = 0x00000004;	// gid by string
	public static final int IPMSG_FILE_PERM 			 = 0x00000010;	// for UNIX
	public static final int IPMSG_FILE_MAJORNO 			 = 0x00000011;	// for UNIX devfile
	public static final int IPMSG_FILE_MINORNO 			 = 0x00000012;	// for UNIX devfile
	public static final int IPMSG_FILE_CTIME 			 = 0x00000013;	// for UNIX
	public static final int IPMSG_FILE_ATIME 			 = 0x00000015;
	public static final int IPMSG_FILE_CREATOR 			 = 0x00000020;	// for Mac
	public static final int IPMSG_FILE_FILETYPE 		 = 0x00000021;	// for Mac
	public static final int IPMSG_FILE_FINDERINFO 		 = 0x00000022;	// for Mac
	public static final int IPMSG_FILE_ACL 				 = 0x00000030;
	public static final int IPMSG_FILE_ALIASFNAME 		 = 0x00000040;	// alias fname
	public static final int IPMSG_FILE_UNICODEFNAME 	 = 0x00000041;	// UNICODE fname
	
	public static final int FILESENDSUCCESS  = 0xFF;    //文件发送成功
    public static final int FILERECEIVEINFO = 0xFE;     //接收文件，包含文件信息
	public static final int FILERECEIVESUCCESS = 0xFD;      //接收文件，包含文件信息
	

}
