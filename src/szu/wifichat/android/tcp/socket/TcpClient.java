package szu.wifichat.android.tcp.socket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import szu.wifichat.android.BaseApplication;
import szu.wifichat.android.activity.message.ImageMessageItem;
import szu.wifichat.android.activity.message.VoiceMessageItem;
import szu.wifichat.android.entity.Message;
import szu.wifichat.android.entity.Message.CONTENT_TYPE;
import szu.wifichat.android.file.explore.Constant;
import szu.wifichat.android.file.explore.FileState;
import szu.wifichat.android.file.explore.FileStyle;
import szu.wifichat.android.util.SessionUtils;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class TcpClient implements Runnable {
    private static final String TAG = "SZU_TcpClient"; // Log标识符

    private Thread mThread; // 线程，对于一个网络连接，安卓系统要求必须新开一个线程
    private boolean IS_THREAD_STOP = false; // 是否线程开始标志
    private boolean SEND_FLAG = false; // 是否发送广播标志
    private static Context mContext = null; // 用来存储控件指针
    private static TcpClient instance; // 唯一实例
    private ArrayList<FileStyle> fileStyles;
    private ArrayList<FileState> fileStates;
    private ArrayList<SendFileThread> sendFileThreads = new ArrayList<TcpClient.SendFileThread>();
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

    public void sendFile(ArrayList<FileStyle> fileStyles, ArrayList<FileState> fileStates,
            String target_IP) {
        this.fileStyles = fileStyles;
        this.fileStates = fileStates;
        // sendFileThreads.clear();
        while (SEND_FLAG == true)
            ;

        for (FileStyle fileStyle : fileStyles) {
            Log.d(TAG, fileStyle.fullPath);
            SendFileThread sendFileThread = new SendFileThread(target_IP, fileStyle.fullPath);
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
        SendFileThread sendFileThread = new SendFileThread(target_IP, filePath);
        while (SEND_FLAG == true)
            ;
        sendFileThreads.add(sendFileThread);
        SEND_FLAG = true;
    }

    // 重写方法
    public void sendFile(String filePath, String target_IP, Message.CONTENT_TYPE type) {
        SendFileThread sendFileThread = new SendFileThread(target_IP, filePath, type);
        while (SEND_FLAG == true)
            ;
        sendFileThreads.add(sendFileThread);
        FileState sendFileState = new FileState(filePath);
        BaseApplication.sendFileStates.put(filePath, sendFileState);// 全局可访问的文件发送状态读取
        SEND_FLAG = true;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Log.d(TAG, "TCP_Client初始化");

        while (!IS_THREAD_STOP) {
            if (SEND_FLAG) {
                for (SendFileThread sendFileThread : sendFileThreads) {
                    sendFileThread.start();
                }
                sendFileThreads.clear();
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
        private static final String TAG = "SZU_SendFileThread";
        private boolean SEND_FLAG = true; // 是否发送广播标志
        private byte[] mBuffer = new byte[Constant.READ_BUFFER_SIZE]; // 数据报内容
        private OutputStream output = null;
        private DataOutputStream dataOutput;
        private FileInputStream fileInputStream;
        private Socket socket = null;
        private String target_IP;
        private String filePath;
        private Message.CONTENT_TYPE type;

        public SendFileThread(String target_IP, String filePath) {
            this.target_IP = target_IP;
            this.filePath = filePath;
        }

        public SendFileThread(String target_IP, String filePath, Message.CONTENT_TYPE type) {
            this(target_IP, filePath);
            this.type = type;
        }

        public void sendFile() {
            int readSize = 0;
            try {
                socket = new Socket(target_IP, Constant.TCP_SERVER_RECEIVE_PORT);
                fileInputStream = new FileInputStream(new File(filePath));
                output = socket.getOutputStream(); // 构造一个输出流
                dataOutput = new DataOutputStream(output);
                int fileSize = fileInputStream.available();
                dataOutput.writeUTF(filePath.substring(filePath.lastIndexOf(File.separator) + 1)
                        + "!" + fileSize + "!" + SessionUtils.getIMEI() + "!" + type);
                int count = 0;
                long length = 0;

                FileState fs = BaseApplication.sendFileStates.get(filePath);
                fs.fileSize = fileSize;
                fs.type = type;
                // FileState fs = getFileStateByName(filePath, fileStates);
                while (-1 != (readSize = fileInputStream.read(mBuffer))) {
                    length += readSize;
                    dataOutput.write(mBuffer, 0, readSize);
                    count++;
                    fs.percent = (int) (length * 100 / fileSize);

                    // if(count%10==0)
                    // {
                    // fs.currentSize=length;
                    // fs.percent=(int)((float)length/(float)fs.fileSize*100);
                    Intent intent = new Intent();

                    switch (type) {
                        case IMAGE:
                            intent.setAction(ImageMessageItem.IMAGE_UPDATE_ACTION);
                            Log.d(TAG, "更新图片，路径:" + fs.fileName + " 进度" + fs.percent);
                            intent.putExtra(fs.fileName, fs.percent);
                            break;

                        case VOICE:
                            intent.setAction(ImageMessageItem.IMAGE_UPDATE_ACTION);
                            Log.d(TAG, "更新图片，路径:" + fs.fileName + " 进度" + fs.percent);
                            intent.putExtra(fs.fileName, fs.percent);
                            break;

                        case FILE:
                            intent.setAction(Constant.fileSendStateUpdateAction);
                            break;

                        default:
                            break;
                    }

                    // if (type == CONTENT_TYPE.IMAGE) {
                    // intent.setAction(ImageMessageItem.IMAGE_UPDATE_ACTION);
                    // // intent.setAction(Constant.fileSendStateUpdateAction);
                    // Log.d(TAG, "更新图片，路径:" + fs.fileName + " 进度" +
                    // fs.percent);
                    // intent.putExtra(fs.fileName, fs.percent);
                    // }
                    // else if (type == CONTENT_TYPE.VOICE) {
                    // intent.setAction(VoiceMessageItem.VOICE_UPDATE_ACTION);
                    // // intent.setAction(Constant.fileSendStateUpdateAction);
                    // Log.d(TAG, "更新语音，路径:" + fs.fileName + " 进度" +
                    // fs.percent);
                    // intent.putExtra(fs.fileName, fs.percent);
                    // }
                    // else if (type == CONTENT_TYPE.FILE) {
                    // intent.setAction(Constant.fileSendStateUpdateAction);
                    // }

                    // intent.setAction(ImageMessageItem.IMAGE_FINISH_UPDATE_ATCTION);
                    mContext.sendBroadcast(intent);
                    // }
                    dataOutput.flush();
                }
                Log.d(TAG, fs.fileName + "发送完毕");
                // fs.currentSize=length;
                // fs.percent=100;

                output.close();
                dataOutput.close();
                socket.close();

                Intent intent = new Intent();

                if (type == CONTENT_TYPE.IMAGE) {
                    intent.setAction(ImageMessageItem.IMAGE_FINISH_UPDATE_ATCTION);
                    intent.putExtra(fs.fileName, 100);
                    // intent.setAction(Constant.fileSendStateUpdateAction);
                    Log.d(TAG, "图片发送完毕");
                }
                else if (type == CONTENT_TYPE.VOICE) {
                    intent.setAction(VoiceMessageItem.VOICE_FINISH_UPDATE_ATCTION);
                    intent.putExtra(fs.fileName, 100);
                    // intent.setAction(Constant.fileSendStateUpdateAction);
                    Log.d(TAG, "语音发送完毕");
                }
                else if (type == CONTENT_TYPE.FILE) {
                    intent.setAction(Constant.fileSendStateUpdateAction);

                }
                mContext.sendBroadcast(intent);
                BaseApplication.sendFileStates.remove(fs.fileName);
            }
            catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "建立客户端socket失败");
                SEND_FLAG = false;
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "建立客户端socket失败");
                SEND_FLAG = false;
                e.printStackTrace();
            }
            finally {
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
