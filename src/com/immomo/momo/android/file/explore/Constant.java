package com.immomo.momo.android.file.explore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.immomo.momo.android.R;

public class Constant
{
	public static Map<String,Integer> exts = new HashMap<String,Integer>();
	static{
		  exts.put("doc", R.drawable.file_icon_office);exts.put("docx", R.drawable.file_icon_office);exts.put("xls", R.drawable.file_icon_office);exts.put("xlsx", R.drawable.file_icon_office);exts.put("ppt", R.drawable.file_icon_office);exts.put("pptx", R.drawable.file_icon_office);
		  exts.put("jpg", R.drawable.file_icon_picture);exts.put("jpeg", R.drawable.file_icon_picture);exts.put("gif", R.drawable.file_icon_picture);exts.put("png", R.drawable.file_icon_picture);exts.put("ico", R.drawable.file_icon_picture);
		  exts.put("apk", R.drawable.apk);exts.put("jar", R.drawable.file_icon_rar);exts.put("rar", R.drawable.file_icon_rar);exts.put("zip", R.drawable.file_icon_zip);
		  exts.put("mp3", R.drawable.file_icon_mp3);exts.put("wma", R.drawable.file_icon_wma);exts.put("aac", R.drawable.file_icon_mid);exts.put("ac3", R.drawable.file_icon_mid);exts.put("ogg", R.drawable.file_icon_mid);exts.put("flac", R.drawable.file_icon_mid);exts.put("midi", R.drawable.file_icon_mid);
		  exts.put("pcm", R.drawable.file_icon_mid);exts.put("wav", R.drawable.file_icon_wav);exts.put("amr", R.drawable.file_icon_mid);exts.put("m4a", R.drawable.file_icon_mid);exts.put("ape", R.drawable.file_icon_mid);exts.put("mid", R.drawable.file_icon_mid);exts.put("mka", R.drawable.file_icon_mid);
		  exts.put("svx", R.drawable.file_icon_mid);exts.put("snd", R.drawable.file_icon_mid);exts.put("vqf", R.drawable.file_icon_mid);exts.put("aif", R.drawable.file_icon_mid);exts.put("voc", R.drawable.file_icon_mid);exts.put("cda", R.drawable.file_icon_mid);exts.put("mpc", R.drawable.file_icon_mid);
		  exts.put("mpeg", R.drawable.file_icon_video);exts.put("mpg", R.drawable.file_icon_video);exts.put("dat", R.drawable.file_icon_video);exts.put("ra", R.drawable.file_icon_video);exts.put("rm", R.drawable.file_icon_video);exts.put("rmvb", R.drawable.file_icon_video);exts.put("mp4", R.drawable.file_icon_video);
		  exts.put("flv", R.drawable.file_icon_video);exts.put("mov", R.drawable.file_icon_video);exts.put("qt", R.drawable.file_icon_video);exts.put("asf", R.drawable.file_icon_video);exts.put("wmv", R.drawable.file_icon_video);exts.put("avi", R.drawable.file_icon_video);
		  exts.put("3gp", R.drawable.file_icon_video);exts.put("mkv", R.drawable.file_icon_video);exts.put("f4v", R.drawable.file_icon_video);exts.put("m4v", R.drawable.file_icon_video);exts.put("m4p", R.drawable.file_icon_video);exts.put("m2v", R.drawable.file_icon_video);exts.put("dat", R.drawable.file_icon_video);
		  exts.put("xvid", R.drawable.file_icon_video);exts.put("divx", R.drawable.file_icon_video);exts.put("vob", R.drawable.file_icon_video);exts.put("mpv", R.drawable.file_icon_video);exts.put("mpeg4", R.drawable.file_icon_video);exts.put("mpe", R.drawable.file_icon_video);exts.put("mlv", R.drawable.file_icon_video);
		  exts.put("ogm", R.drawable.file_icon_video);exts.put("m2ts", R.drawable.file_icon_video);exts.put("mts", R.drawable.file_icon_video);exts.put("ask", R.drawable.file_icon_video);exts.put("trp", R.drawable.file_icon_video);exts.put("tp", R.drawable.file_icon_video);exts.put("ts", R.drawable.file_icon_video);
	  }
	 
	public static final int TCP_SERVER_RECEIVE_PORT = 4447;    //主机接收端口
	
	public static final String SEPARATOR="!";
	
	public static final byte REGISTER_MY_INFORMATION=0X01;			//广播自己的IP地址
	
	public static final byte BROADCAST_SEND_FILE=0x01;         //广播发送文件指令
	public static final byte BROADCAST_BUILD_SUCCESS=0x02;     //接收方回应
	
	public static final byte REQUEST_SEND_FILE_NAME=0x1;        //请求发送文件名指令
	public static final byte ACK_REQUEST_SEND_FILE_NAME=0X2;    //发送文件名确认指令
	public static final byte REQUEST_SEND_FILE_NAME_FINISH=0x03;//发送文件名完成指令
	public static final byte ACK_RESULT_SEND_SUCCUSS=0x04;      //接收文件名成功
	
	public static final byte RESPONSE_SEND_FILE_OK=0x05;        //对方接收文件名
	public static final byte RESPONSE_SEND_FILE_REFUSE=0x06;    //对法拒绝接收文件名
	public static final byte ACK_RESPONSE_SEND_FILE=0x07;       //发送确认接收方信息
	 
	public static final byte READY_TO_RECIEVE_FILE=0x08;		//接收方准备接收文件
	public static final byte RECIEVE_ONE_FILE_SUCCESS=0x09;     //接收一个文件成功
	public static final byte RECIEVE_ALL_FILE_SUCCESS=0x0A;     //接收所有文件完成
	
	public static final String BROADCAST_IP = "255.255.255.255";   //广播地址
	public static int MAX_LENGTH=256;                 			//UDP数据包接收数据数组最大长度
	public static final int DATAGRAM_SERVICE_RECEIVE_PORT = 4445;  //UDP服务器端接收端口
	public static final int DATAGRAM_CLIENT_SEND_PORT = 4446;      //UDP客户端发送端口
	public static int CMD_BUFFER_SIZE=256;//文件流缓冲大小
	public static int READ_BUFFER_SIZE=4161536;//文件流缓冲大小
	
	//其它定义
	public static final int FILE_RESULT_CODE = 1;
	public static final int SELECT_FILES = 1;//是否要在文件选择器中显示文件
	public static final int SELECT_FILE_PATH = 2;//文件选择器只显示文件夹
	//文件选择状态保存
	public static TreeMap<Integer,Boolean> fileSelectedState = new TreeMap<Integer,Boolean>();
	
	//转换文件大小  
	  public static String formatFileSize(long fileS) {
	      DecimalFormat df = new DecimalFormat("#.00");
	      String fileSizeString = "";
	      if (fileS < 1024) {
	    	  fileSizeString = fileS+"B";
	       //   fileSizeString = df.format((double) fileS) + "B";
	      } else if (fileS < 1048576) {
	          fileSizeString = df.format((double) fileS / 1024) + "K";
	      } else if (fileS < 1073741824) {
	          fileSizeString = df.format((double) fileS / 1048576) + "M";
	      } else {
	          fileSizeString = df.format((double) fileS / 1073741824) + "G";
	      }
	      return fileSizeString;
	  }
	  public static final String remoteUserRefuseReceiveFileAction = "com.android.szu.remoteUserRefuseReceiveFile";
	  public static final String fileSendStateUpdateAction = "com.android.szu.fileSendStateUpdate";
	  public static final String fileReceiveStateUpdateAction = "com.android.szu.fileReceiveStateUpdate";
	  public static final String receivedSendFileRequestAction = "com.android.szu.receivedSendFileRequest";
	  public static final String refuseReceiveFileAction = "com.android.szu.refuseReceiveFile";
}
