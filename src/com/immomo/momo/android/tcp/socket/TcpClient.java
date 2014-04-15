package com.immomo.momo.android.tcp.socket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.immomo.momo.android.file.explore.Constant;
import com.immomo.momo.android.file.explore.FileState;
import com.immomo.momo.android.file.explore.FileStyle;

public class TcpClient implements Runnable {
    private static final String TAG = "TcpClient"; // Log标识符

    private Thread mThread; // 线程，对于一个网络连接，安卓系统要求必须新开一个线程
    private boolean IS_THREAD_STOP = false; // 是否线程开始标志
    private boolean SEND_FLAG = false; // 是否发送广播标志
    private static Context mContext = null; // 用来存储控件指针
    private static TcpClient instance; // 唯一实例
    private ArrayList<FileStyle> fileStyles;
    private ArrayList<FileState> fileStates;
    private ArrayList<SendFileThread> sendFileThreads;
    // private static final String TARGET_IP = "192.16.137.2"; //广播地址
    // private String filePath;
    private SendFileThread sendFileThread;

    public TcpClient() {
        mThread = new Thread(this); // 新建一个线程
        Log.d(TAG, "建立线程成功");

    }

    public Thread getThread() {
        return mThread;
    }

    /**
     * <p>
     * 获取TcpService实例
     * <p>
     * 单例模式，返回唯一实例
     */
    public static TcpClient getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new TcpClient();
        }
        return instance;
    }

    public void sendFile(ArrayList<FileStyle> fileStyles, ArrayList<FileState> fileStates, String target_IP) {
        this.fileStyles = fileStyles;
        this.fileStates = fileStates;
        // sendFileThreads.clear();
        while (SEND_FLAG == true)
            ;

        sendFileThreads = new ArrayList<TcpClient.SendFileThread>();
        for (FileStyle fileStyle : fileStyles) {
            Log.d(TAG, fileStyle.fullPath);
            SendFileThread sendFileThread = new SendFileThread(target_IP,
                    fileStyle.fullPath);
            sendFileThreads.add(sendFileThread);
        }
        SEND_FLAG = true;
    }

    public TcpClient(Context context) {
        this();
        mContext = context;
        Log.d(TAG, "TCP_Client初始化完毕");
    }

    public void startSend() {
        Log.d(TAG, "发送线程开启");
        IS_THREAD_STOP = false; // 使能发送标识
        if (!mThread.isAlive())
            mThread.start(); // 启动线程
    }

    public void sendFile(String filePath, String target_IP) {
        while (SEND_FLAG == true)
            ;
        // sendFileThread=new SendFileThread(target_IP, filePath);
        // SEND_FLAG=true;
        sendFileThreads = new ArrayList<TcpClient.SendFileThread>();
        SendFileThread sendFileThread = new SendFileThread(target_IP, filePath);
        sendFileThreads.add(sendFileThread);
        SEND_FLAG = true;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Log.d(TAG, "TCP_Client初始化");

        while (!IS_THREAD_STOP) {
            if (SEND_FLAG) {
                // sendFileThread.start();
                for (SendFileThread sendFileThread : sendFileThreads) {
                    sendFileThread.start();
                }
                SEND_FLAG = false;
            }

        }
    }

    public void release() {
        while (SEND_FLAG == true)
            ;
        while (sendFileThread.isAlive())
            ;
        IS_THREAD_STOP = false;
    }

    // 根据文件名从文件状态列表中获得该文件状态
    private FileState getFileStateByName(String fullPath, ArrayList<FileState> fileStates) {
        for (FileState fileState : fileStates) {
            if (fileState.fileName.equals(fullPath)) {
                return fileState;
            }
        }
        return null;
    }

    public class SendFileThread extends Thread {
        private static final String TAG = "SendFileThread";
        private boolean SEND_FLAG = true; // 是否发送广播标志
        private byte[] mBuffer = new byte[Constant.READ_BUFFER_SIZE]; // 数据报内容
        private OutputStream output = null;
        private DataOutputStream dataOutput;
        private FileInputStream fileInputStream;
        private Socket socket = null;
        private String target_IP;
        private String filePath;

        public SendFileThread(String target_IP, String filePath) {
            this.target_IP = target_IP;
            this.filePath = filePath;
        }

        public void sendFile() {
            int readSize = 0;
            try {
                socket = new Socket(target_IP, Constant.TCP_SERVER_RECEIVE_PORT);
                fileInputStream = new FileInputStream(new File(filePath));
                output = socket.getOutputStream(); // 构造一个输出流
                dataOutput = new DataOutputStream(output);
                dataOutput.writeUTF(filePath.substring(filePath.lastIndexOf(File.separator) + 1)
                        + "!" + fileInputStream.available());
                int count = 0;
                long length = 0;
                // FileState fs = getFileStateByName(filePath, fileStates);
                while (-1 != (readSize = fileInputStream.read(mBuffer))) {
                    length += readSize;
                    dataOutput.write(mBuffer, 0, readSize);
                    count++;
                    // if(count%10==0)
                    // {
                    // fs.currentSize=length;
                    // fs.percent=(int)((float)length/(float)fs.fileSize*100);
                    Intent intent = new Intent();
                    intent.setAction(Constant.fileSendStateUpdateAction);
                    mContext.sendBroadcast(intent);
                    // }
                    dataOutput.flush();
                }

                // fs.currentSize=length;
                // fs.percent=100;
                Intent intent = new Intent();
                intent.setAction(Constant.fileSendStateUpdateAction);
                mContext.sendBroadcast(intent);

                output.close();
                dataOutput.close();
                socket.close();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "建立客户端socket失败");
                SEND_FLAG = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "建立客户端socket失败");
                SEND_FLAG = false;
                e.printStackTrace();
            } finally {
                // IS_THREAD_STOP=true;
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.d(TAG, "SendFileThread初始化");
            if (SEND_FLAG) {
                sendFile();
            }
        }
    }
}
