package com.immomo.momo.android.tcp.socket;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.file.explore.Constant;
import com.immomo.momo.android.file.explore.FileState;
import com.immomo.momo.android.socket.IPMSGConst;

public class TcpService implements Runnable
{
	private static final String TAG = "TcpService";

	private ServerSocket serviceSocket;
	private boolean SCAN_FLAG = false; // 接收扫描标识
	private boolean REV_FLAG = false; // 接收标识
	private Thread mThread;
	ArrayList<FileState> receivedFileNames;
	ArrayList<SaveFileToDisk>saveFileToDisks;
	private String filePath=null;	//存放接收文件的路径
	
	private static Context mContext;
	private static TcpService instance; // 唯一实例
	
	private boolean IS_THREAD_STOP = false; // 是否线程开始标志

	public TcpService()
	{
		try
		{
			serviceSocket = new ServerSocket(Constant.TCP_SERVER_RECEIVE_PORT);
			saveFileToDisks=new ArrayList<TcpService.SaveFileToDisk>();
			Log.d(TAG, "建立监听服务器ServerSocket成功");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			Log.d(TAG, "建立监听服务器ServerSocket失败");
			e.printStackTrace();
		}
		mThread = new Thread(this);
	}
	
	 /**
     * <p>
     * 获取TcpService实例
     * <p>
     * 单例模式，返回唯一实例
     */
    public static TcpService getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new TcpService();
        }
        return instance;
    }
	
	public void setSavePath(String fileSavePath)
	{
		Log.d(TAG, "设置存储路径成功,路径为"+fileSavePath);
		this.filePath=fileSavePath;
		REV_FLAG=true;
	}
	
	public TcpService(Context context)
	{
		this();
		mContext = context;
	}

	private void scan_recv()
	{
		try
		{
			Socket socket = serviceSocket.accept(); // 接收UDP数据报
//			socket.setSoTimeout(5000); // 设置掉线时间
			Log.d(TAG, "客户端连接成功");
			
			SaveFileToDisk fileToDisk = new SaveFileToDisk(socket);
			saveFileToDisks.add(fileToDisk);
//			Intent intent = new Intent();
//			intent.setAction(Constant.receivedSendFileRequestAction);
//			mContext.sendBroadcast(intent);
			
//			fileToDisk.start();

		} catch (IOException e)
		{
			e.printStackTrace();
			Log.d(TAG, "客户端连接失败");
			SCAN_FLAG = false;
		}
	}
	
	
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		Log.d(TAG, "TCP_Service线程开启");
		while (!IS_THREAD_STOP)
		{
			if (SCAN_FLAG)
			{
				scan_recv();
				SCAN_FLAG=false;
			}
			if(REV_FLAG)
			{
				for(SaveFileToDisk fileToDisk:saveFileToDisks)
				{
					fileToDisk.start();
				}
				saveFileToDisks.clear();
				REV_FLAG=false;
				SCAN_FLAG=true;
			}
		}
	}

	public void release()
	{
		if(null !=serviceSocket && !serviceSocket.isClosed())
			try
			{
				serviceSocket.close();
				serviceSocket=null;
				Log.d(TAG, "关闭socket成功");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		while(SCAN_FLAG==true);//直到SCAN_FLAG为false的时候退出循环
			SCAN_FLAG = false;
		IS_THREAD_STOP = true;
	}

	public void startReceive()
	{
		SCAN_FLAG = true; // 使能扫描接收标识
		if (!mThread.isAlive())
			mThread.start(); // 开启线程
	}

	public void startReceive(ArrayList<FileState> receivedFileNames)
	{
		SCAN_FLAG = true; // 使能扫描接收标识
		if (!mThread.isAlive())
			mThread.start(); // 开启线程
		this.receivedFileNames=receivedFileNames;
	}
	
	public void stopReceive()
	{
		while(SCAN_FLAG==true);
		SCAN_FLAG = false; // 失能扫描接收标识
	}

	// 根据文件名从文件状态列表中获得该文件状态
	private FileState getFileStateByName(String fullPath,ArrayList<FileState> fileStates)
	{
		for (FileState fileState : fileStates)
		{
			if (fileState.fileName.equals(fullPath))
			{
				return fileState;
			}
		}
		return null;
	}
	public class SaveFileToDisk extends Thread
	{
		private boolean SCAN_RECIEVE=true;
		private InputStream input = null;
		private DataInputStream dataInput;
		private byte[] mBuffer = new byte[Constant.READ_BUFFER_SIZE];// 声明接收数组
//		private String filePath="/storage/sdcard0/";	//存放接收文件的路径
		public SaveFileToDisk(Socket socket)
		{
			try
			{
				input = socket.getInputStream();
				dataInput = new DataInputStream(input);
				Log.d(TAG, "获取网络输入流成功");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				Log.d(TAG, "获取网络输入流失败");
				SCAN_RECIEVE=false;
				e.printStackTrace();
			}
		}

		public void recieveFile()
		{
			int readSize = 0;
			FileOutputStream fileOutputStream=null;
			BufferedOutputStream bufferOutput=null;
			String strFiledata;
			String[] strData = null;
			try
			{
				strFiledata = dataInput.readUTF().toString();
				strData = strFiledata.split("!");
				long length = Long.parseLong(strData[1]);//文件大小
				Log.d(TAG,"读取文件信息成功:" + strData[0] + " "+ String.valueOf(length / 1048576) + "MB");
//				FileState fileState=new FileState(length,0,filePath+strData[0]);
//				receivedFileNames.add(fileState);
//				FileState fs=getFileStateByName(filePath+strData[0],receivedFileNames);
				fileOutputStream = new FileOutputStream(new File(filePath+strData[0]));//创建文件流
				bufferOutput = new BufferedOutputStream(fileOutputStream);//创建带缓冲区的文件流
				long lastLength=0;
				long currentLength=0;
				long lastTime=System.currentTimeMillis();
				long currentTime=0;
				int count=0;
				long startTime=System.currentTimeMillis();
				while (-1 != (readSize = dataInput.read(mBuffer)))
				{
					bufferOutput.write(mBuffer, 0, readSize);
					currentLength+=readSize;
					count++;
					if(count%1000==0)
					{
						currentTime=System.currentTimeMillis();
						long time=currentTime-lastTime;
						lastTime=currentTime;
						long Length=currentLength-lastLength;
						lastLength=currentLength;
						Log.d(TAG, "接收速度:"+String.valueOf((float)Length / (float)1048576 / ((float)time/(float)1000)) + "MB/S"
						+"接收大小:"+String.valueOf(currentLength / 1048576) + "MB/"+ String.valueOf(length / 1048576) + "MB");
//						fs.currentSize=currentLength;
//						fs.percent=(int)((float)currentLength/(float)length*100);
						
						Intent intent = new Intent();
						intent.setAction(Constant.fileReceiveStateUpdateAction);
						mContext.sendBroadcast(intent);
						
					}
					
//					Log.d(TAG, "接收:"+String.valueOf(currentLength / 1048576) + "MB"+ String.valueOf(length / 1048576) + "MB");
				}
				
				Log.d(TAG, "接收平均速度:"+String.valueOf(((float)length / (float)1048576) / (float)((System.currentTimeMillis()
						-startTime)/1000)) + "MB/S"+" 接收时间："+String.valueOf((System.currentTimeMillis()-startTime)/1000)+"s");
				//将byte数组的数据写进指定路径
				bufferOutput.flush();
				
			    BaseActivity.sendEmptyMessage(IPMSGConst.IPMSG_GETIMAGESUCCESS);
				input.close();
				Log.d(TAG, "关闭input成功");
				dataInput.close();
				Log.d(TAG, "关闭dataInput成功");
				bufferOutput.close();
				Log.d(TAG, "关闭bufferOutput成功");
				fileOutputStream.close();
				Log.d(TAG, "关闭fileOutputStream成功");
				
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				Log.d(TAG, "写入文件失败");
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			super.run();
			Log.d(TAG, "SaveFileToDisk线程开启");
			if(SCAN_RECIEVE)
				recieveFile();
		}
	}
}
