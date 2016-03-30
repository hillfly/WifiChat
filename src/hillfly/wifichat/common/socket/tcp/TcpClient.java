package hillfly.wifichat.common.socket.tcp;

import hillfly.wifichat.common.BaseApplication;
import hillfly.wifichat.common.socket.udp.UDPMessageListener;
import hillfly.wifichat.consts.Constant;
import hillfly.wifichat.consts.IPMSGConst;
import hillfly.wifichat.model.FileState;
import hillfly.wifichat.model.FileStyle;
import hillfly.wifichat.model.Message;
import hillfly.wifichat.util.DateUtils;
import hillfly.wifichat.util.FileUtils;
import hillfly.wifichat.util.Logger;
import hillfly.wifichat.util.SessionUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;

public class TcpClient implements Runnable {
    private static final Logger logger = Logger.getLogger(TcpClient.class);

    private Thread mThread;
    private boolean IS_THREAD_STOP = false; // 是否线程开始标志
    private boolean SEND_FLAG = false; // 是否发送广播标志
    private static TcpClient instance;
    // private ArrayList<FileStyle> fileStyles;
    // private ArrayList<FileState> fileStates;
    private ArrayList<SendFileThread> sendFileThreads;
    private SendFileThread sendFileThread;
    private static Handler mHandler = null;

    private TcpClient() {
        sendFileThreads = new ArrayList<TcpClient.SendFileThread>();
        mThread = new Thread(this);
        logger.d("建立线程成功");

    }

    public static void setHandler(Handler paramHandler) {
        mHandler = paramHandler;
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
        if (instance == null) {
            instance = new TcpClient();
        }
        return instance;
    }

    public void sendFile(ArrayList<FileStyle> fileStyles, ArrayList<FileState> fileStates,
            String target_IP) {
        while (SEND_FLAG == true)
            ;

        for (FileStyle fileStyle : fileStyles) {
            SendFileThread sendFileThread = new SendFileThread(target_IP, fileStyle.fullPath);
            sendFileThreads.add(sendFileThread);
        }
        SEND_FLAG = true;
    }

    private TcpClient(Context context) {
        this();
        logger.d("TCP_Client初始化完毕");
    }

    public void startSend() {
        logger.d("发送线程开启");
        IS_THREAD_STOP = false; // 使能发送标识
        if (!mThread.isAlive())
            mThread.start();
    }

    public void sendFile(String filePath, String target_IP) {
        SendFileThread sendFileThread = new SendFileThread(target_IP, filePath);
        while (SEND_FLAG == true)
            ;
        sendFileThreads.add(sendFileThread);
        SEND_FLAG = true;
    }

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
        logger.d("TCP_Client初始化");

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

    public class SendFileThread extends Thread {
        
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
                output = socket.getOutputStream();
                dataOutput = new DataOutputStream(output);
                int fileSize = fileInputStream.available();
                dataOutput.writeUTF(filePath.substring(filePath.lastIndexOf(File.separator) + 1)
                        + "!" + fileSize + "!" + SessionUtils.getIMEI() + "!" + type);
                long length = 0;

                FileState fs = BaseApplication.sendFileStates.get(filePath);
                fs.fileSize = fileSize;
                fs.type = type;
                while (-1 != (readSize = fileInputStream.read(mBuffer))) {
                    length += readSize;
                    dataOutput.write(mBuffer, 0, readSize);
                    fs.percent = (int) (length * 100 / fileSize);

                    switch (type) {
                        case IMAGE:
                            break;

                        case VOICE:
                            break;

                        case FILE:
                            android.os.Message msg = mHandler.obtainMessage();
                            msg.obj = fs;
                            msg.sendToTarget();

                            break;

                        default:
                            break;
                    }
                    dataOutput.flush();
                }
                logger.d(fs.fileName + "发送完毕");

                output.close();
                dataOutput.close();
                socket.close();

                switch (type) {
                    case IMAGE:
                        Message imageMsg = new Message(SessionUtils.getIMEI(),
                                DateUtils.getNowtime(), fs.fileName, type);
                        imageMsg.setMsgContent(FileUtils.getNameByPath(imageMsg.getMsgContent()));
                        UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, target_IP, imageMsg);
                        logger.d("图片发送完毕");
                        break;

                    case VOICE:
                        Message voiceMsg = new Message(SessionUtils.getIMEI(),
                                DateUtils.getNowtime(), fs.fileName, type);
                        voiceMsg.setMsgContent(FileUtils.getNameByPath(voiceMsg.getMsgContent()));
                        UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, target_IP, voiceMsg);
                        logger.d("语音发送完毕");
                        break;

                    case FILE:
                        android.os.Message msg = mHandler.obtainMessage();
                        fs.percent = 100;
                        msg.obj = fs;
                        msg.sendToTarget();
                        break;

                    default:
                        break;
                }

                BaseApplication.sendFileStates.remove(fs.fileName);
            }
            catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                logger.d("建立客户端socket失败");
                SEND_FLAG = false;
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                logger.d("建立客户端socket失败");
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
            logger.d("SendFileThread初始化");
            if (SEND_FLAG) {
                sendFile();
            }
        }
    }
}
