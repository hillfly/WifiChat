package com.immomo.momo.android.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.BaseDialog;
import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.maintabs.MainTabActivity;
import com.immomo.momo.android.activity.wifiap.WifiapBroadcast;
import com.immomo.momo.android.activity.wifiap.WifiapOperateEnum;
import com.immomo.momo.android.adapter.WifiapAdapter;
import com.immomo.momo.android.util.WifiUtils;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;
import com.immomo.momo.android.view.HeaderLayout.onRightImageButtonClickListener;
import com.immomo.momo.android.view.WifiapSearchAnimationFrameLayout;
import com.immomo.momo.sql.userDAO;
import com.immomo.momo.sql.userInfo;

public class WifiapActivity extends BaseActivity implements OnClickListener,
        onRightImageButtonClickListener, WifiapBroadcast.EventHandler,
        DialogInterface.OnClickListener {
    public static final int m_nApSearchTimeOut = 0;// 搜索超时
    public static final int m_nApScanResult = 1;// 搜索到wifi返回结果
    public static final int m_nApConnectResult = 2;// 连接上wifi热点
    public static final int m_nApCreateAPResult = 3;// 创建热点结果
    public static final int m_nApUserResult = 4;// 用户上线人数更新命令(待定)
    public static final int m_nApConnected = 5;// 点击连接后断开wifi，3.5秒后刷新adapter
    public static final String PACKAGE_NAME = "com.immomo.momo.android.activity";
    public static final String FIRST_OPEN_KEY = "version";
    public static final String WIFI_AP_HEADER = "max"; // way_
    public static final String WIFI_AP_PASSWORD = "wifichat123";
    private final String TAG = "SZU_WifiapActivity";

    private int wifiapOperateEnum = WifiapOperateEnum.NOTHING;
    private String localIPaddress = "0.0.0.0"; // 本地WifiIP
    private String serverIPaddres = "0.0.0.0"; // 热点IP
    private String mNickname;
    private String mGender;
    private String mIMEI;
    private int mID;
    private int mAvatar;
    private int mOnlineStateInt;
    private boolean isClient; // 客户端标识

    private WifiapSearchAnimationFrameLayout m_FrameLWTSearchAnimation;
    private HeaderLayout m_HeaderLayout;
    private BaseDialog m_Dialog; // 提示窗口
    private Button m_BtnBack;
    private Button m_BtnLogin;
    private Button m_btnCreateWT;
    private ImageView m_imgRadar;
    private LinearLayout m_linearLCreateAP;
    private ListView m_listVWT;
    private ProgressBar m_progBarCreatingAP;
    private TextView m_textVPromptAP;
    private TextView m_textVWTPrompt;
    private CreateAPProcess m_createAPProcess;
    private WTSearchProcess m_wtSearchProcess;
    private WifiapAdapter m_wTAdapter;
    private WifiUtils m_wiFiAdmin;
    private userDAO mUserDAO; // 数据库操作实例
    private userInfo mUserInfo; // 用户信息类实例
    ArrayList<ScanResult> m_listWifi = new ArrayList<ScanResult>();

    /** handler 异步更新UI **/
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // 搜索超时
            case m_nApSearchTimeOut:
                m_wtSearchProcess.stop();
                m_FrameLWTSearchAnimation.stopAnimation();
                m_listWifi.clear();
                m_textVWTPrompt.setVisibility(View.VISIBLE);
                m_textVWTPrompt.setText(R.string.wt_list_empty);
                break;
            // 搜索结果
            case m_nApScanResult:
                m_listWifi.clear();
                int size = m_wiFiAdmin.mWifiManager.getScanResults().size();
                if (size > 0) {
                    for (int i = 0; i < size; ++i) {
                        ScanResult scanResult = m_wiFiAdmin.mWifiManager
                                .getScanResults().get(i);
                        if (scanResult.SSID.startsWith(WIFI_AP_HEADER)) {
                            m_listWifi.add(scanResult);
                        }
                    }
                    if (m_listWifi.size() > 0) {
                        m_wtSearchProcess.stop();
                        m_FrameLWTSearchAnimation.stopAnimation();
                        m_textVWTPrompt.setVisibility(View.GONE);
                        m_wTAdapter.setData(m_listWifi);
                        m_wTAdapter.notifyDataSetChanged();
                    }
                }
                break;
            // 连接结果
            case m_nApConnectResult:
                m_wTAdapter.notifyDataSetChanged();
                isClient = true; // 标识客户端
                localIPaddress = m_wiFiAdmin.getLocalIPAddress();
                serverIPaddres = m_wiFiAdmin.getServerIPAddress();

                Log.i(TAG, "localIPaddress:" + localIPaddress
                        + " serverIPaddres:" + serverIPaddres);

                break;
            // 热点创建结果
            case m_nApCreateAPResult:
                m_createAPProcess.stop();
                m_progBarCreatingAP.setVisibility(View.GONE);
                if (((m_wiFiAdmin.getWifiApState() == 3) || (m_wiFiAdmin
                        .getWifiApState() == 13))
                        && (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {
                    m_textVWTPrompt.setVisibility(View.GONE);
                    m_linearLCreateAP.setVisibility(View.VISIBLE);
                    m_btnCreateWT.setVisibility(View.VISIBLE);
                    m_imgRadar.setVisibility(View.VISIBLE);
                    m_btnCreateWT
                            .setBackgroundResource(R.drawable.wifiap_close);
                    m_textVPromptAP
                            .setText(getString(R.string.create_connect_ok)
                                    + getString(R.string.ssid_connect_ok)
                                    + m_wiFiAdmin.getApSSID()
                                    + getString(R.string.password_connect_ok));
                    isClient = false; // 非客户端
                    localIPaddress = m_wiFiAdmin.getServerIPAddress(); // 获取本地IP
                    serverIPaddres = localIPaddress; // 热点IP与本机IP相同

                    Log.i(TAG, "localIPaddress:" + localIPaddress
                            + " serverIPaddres:" + serverIPaddres);
                } else {
                    m_btnCreateWT.setVisibility(View.VISIBLE);
                    m_btnCreateWT
                            .setBackgroundResource(R.drawable.wifiap_create);
                    m_textVPromptAP.setText(R.string.create_ap_fail);
                }
                break;
            case m_nApUserResult:
                // 更新用户上线人数，待定
                break;
            case m_nApConnected:
                m_wTAdapter.notifyDataSetChanged();
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiap);
        m_wtSearchProcess = new WTSearchProcess();
        m_createAPProcess = new CreateAPProcess();
        m_wiFiAdmin = WifiUtils.getInstance(this);
        mUserDAO = new userDAO(this); // 实例化数据库操作类
        initViews();
        initEvents();
        initAction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiapBroadcast.ehList.remove(this);
        m_wtSearchProcess.stop();
        m_createAPProcess.stop();
    }

    /** 初始化视图 获取控件对象 **/
    protected void initViews() {
        m_HeaderLayout = (HeaderLayout) findViewById(R.id.wifiap_header);
        m_BtnBack = (Button) findViewById(R.id.wifiap_btn_back);
        m_BtnLogin = (Button) findViewById(R.id.wifiap_btn_login);

        m_linearLCreateAP = ((LinearLayout) findViewById(R.id.create_ap_llayout_wt_main));// 创建热点的view
        m_progBarCreatingAP = ((ProgressBar) findViewById(R.id.creating_progressBar_wt_main));// 创建热点的进度条
        m_textVPromptAP = ((TextView) findViewById(R.id.prompt_ap_text_wt_main));
        m_btnCreateWT = ((Button) findViewById(R.id.create_btn_wt_main)); // 创建热点的按钮
        m_FrameLWTSearchAnimation = ((WifiapSearchAnimationFrameLayout) findViewById(R.id.search_animation_wt_main));// 搜索时的动画
        m_listVWT = ((ListView) findViewById(R.id.wt_list_wt_main));// 搜索到的热点
        m_textVWTPrompt = (TextView) findViewById(R.id.wt_prompt_wt_main);
        m_imgRadar = (ImageView) findViewById(R.id.radar_gif_wt_main);
    }

    /** 初始化全局设置 **/
    @Override
    protected void initEvents() {
        m_HeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
        m_HeaderLayout.setTitleRightImageButton("创建连接", null,
                R.drawable.search_wt, this);

        m_wTAdapter = new WifiapAdapter(this, m_listWifi);
        m_listVWT.setAdapter(m_wTAdapter);

        WifiapBroadcast.ehList.add(this); // 监听广播

        m_Dialog = BaseDialog.getDialog(WifiapActivity.this, "提示", "", "确定",
                this, "取消", this);
        m_Dialog.setButton1Background(R.drawable.btn_default_popsubmit);

        m_btnCreateWT.setOnClickListener(this);
        m_BtnBack.setOnClickListener(this);
        m_BtnLogin.setOnClickListener(this);
    }

    /** 初始化控件设置 **/
    protected void initAction() {
        if ((this.m_wtSearchProcess.running)
                || (this.m_createAPProcess.running))
            return;

        if (!isWifiConnect() && !getWifiApState()) {
            m_wiFiAdmin.OpenWifi();
            m_wtSearchProcess.start();
            m_wiFiAdmin.startScan();
            m_FrameLWTSearchAnimation.startAnimation();
            m_textVWTPrompt.setVisibility(View.VISIBLE);
            m_textVWTPrompt.setText(R.string.wt_searching);
            m_linearLCreateAP.setVisibility(View.GONE);
            m_btnCreateWT.setBackgroundResource(R.drawable.wifiap_create);
        }
        if (isWifiConnect()) {
            this.m_wiFiAdmin.startScan();
            this.m_wtSearchProcess.start();
            this.m_FrameLWTSearchAnimation.startAnimation();
            this.m_textVWTPrompt.setVisibility(View.VISIBLE);
            this.m_textVWTPrompt.setText(R.string.wt_searching);
            this.m_linearLCreateAP.setVisibility(View.GONE);
            this.m_btnCreateWT.setBackgroundResource(R.drawable.wifiap_create);
            this.m_imgRadar.setVisibility(View.GONE);
            m_listWifi.clear();
            if (m_wiFiAdmin.mWifiManager.getScanResults() != null) {
                int result = m_wiFiAdmin.mWifiManager.getScanResults().size();
                int i = 0;
                for (i = 0; i < result; ++i) {
                    if (m_wiFiAdmin.mWifiManager.getScanResults().get(i).SSID
                            .startsWith(WIFI_AP_HEADER))
                        m_listWifi.add(m_wiFiAdmin.mWifiManager
                                .getScanResults().get(i));
                }
                Log.i(TAG, "wifi size:"
                        + m_wiFiAdmin.mWifiManager.getScanResults().size());
            }
            m_wTAdapter.setData(m_listWifi);
            m_wTAdapter.notifyDataSetChanged();
        }

        if (getWifiApState()) {
            if (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER)) {
                m_textVWTPrompt.setVisibility(View.GONE);
                m_linearLCreateAP.setVisibility(View.VISIBLE);
                m_progBarCreatingAP.setVisibility(View.GONE);
                m_btnCreateWT.setVisibility(View.VISIBLE);
                m_imgRadar.setVisibility(View.VISIBLE);
                m_btnCreateWT.setBackgroundResource(R.drawable.wifiap_close);
                m_textVPromptAP.setText(getString(R.string.create_connect_ok)
                        + getString(R.string.ssid_connect_ok)
                        + m_wiFiAdmin.getApSSID()
                        + getString(R.string.password_connect_ok));
            }
        }

    }

    /**
     * 获取Wifi热点名
     * 
     * <p>
     * BuildBRAND 系统定制商 ； BuildMODEL 版本
     * </p>
     * 
     * @return 返回 定制商+版本 (String类型),用于创建热点。
     */
    public String getLocalHostName() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        if (-1 == str2.toUpperCase().indexOf(str1.toUpperCase()))
            str2 = str1 + "_" + str2;
        return str2;
    }

    /**
     * 获取热点状态
     * 
     * @return boolean值，对应热点的开启(true)和关闭(false)
     */
    public boolean getWifiApState() {
        try {
            WifiManager localWifiManager = (WifiManager) getSystemService("wifi");
            int i = ((Integer) localWifiManager.getClass()
                    .getMethod("getWifiApState", new Class[0])
                    .invoke(localWifiManager, new Object[0])).intValue();
            return (3 == i) || (13 == i);
        } catch (Exception localException) {
        }
        return false;
    }

    /**
     * 判断是否连接上wifi
     * 
     * @return boolean值(isConnect),对应已连接(true)和未连接(false)
     */
    public boolean isWifiConnect() {
        boolean isConnect = true;
        if (!((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected())
            isConnect = false;
        return isConnect;
    }

    /**
     * IP地址正确性验证
     * 
     * @return boolean 返回是否为正确， 正确(true),不正确(false)
     */
    private boolean validate() {
        String NullIP = "0.0.0.0";
        if (localIPaddress.equals(NullIP) || serverIPaddres.equals(NullIP)) {
            showCustomToast("请创建热点或者连接一个热点");
            return false;
        }
        return true;
    }

    /** 执行登陆 **/
    private void login() {
        if ((!validate())) {
            return;
        }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在配置连接信息...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mIMEI = mApplication.getIMEI();
                    mNickname = mApplication.getNickname();
                    mGender = mApplication.getGender();
                    mAvatar = mApplication.getAvatar();
                    mOnlineStateInt = mApplication.getOnlineStateInt();
                 
                    // 录入数据库
                    // 若数据库中有IMEI对应的用户记录，则更新此记录; 无则创建新用户
                    if ((mUserInfo = mUserDAO.findUserInfo(mIMEI)) != null) {
                        mID = mUserInfo.getId(); // 返回该用户id
                        mUserInfo.setIPAddr(localIPaddress);
                        mUserInfo.setAvater(mAvatar);
                        mUserInfo.setIsOnline(mOnlineStateInt);
                        mUserInfo.setName(mNickname);
                        mUserInfo.setSex(mGender);
                        mUserDAO.update(mUserInfo);
                        mUserDAO.close();
                    } else {
                        mUserInfo = new userInfo(mNickname, 0, mGender, mIMEI,
                                localIPaddress, mOnlineStateInt, mAvatar);
                        mUserDAO.add(mUserInfo);
                        mID = mUserDAO.getID(mIMEI); // 返回该用户id
                        mUserDAO.close();
                    }
                    
                    // 设置全局Session
                    mApplication.setID(mID);
                    mApplication.setIsClient(isClient);
                    mApplication.setLocalIPaddress(localIPaddress);
                    mApplication.setServerIPaddress(serverIPaddres);                 
                    
                    // 在SD卡中存储登陆信息
                    SharedPreferences.Editor mEditor = getSharedPreferences(
                            GlobalSharedName, Context.MODE_PRIVATE).edit();
                    mEditor.putString("IMEI", mIMEI)
                            .putString("Nickname", mNickname)
                            .putString("Gender", mGender)
                            .putInt("Avatar", mAvatar)
                            .putInt("OnlineStateInt", mOnlineStateInt);
                    mEditor.commit();
                    
                    Log.i(TAG, "mNickname:" + mNickname + " mGender:" + mGender
                            + " mOnlineStateInt:" + mOnlineStateInt
                            + " mAvatar:" + mAvatar + " IMEI:" + mIMEI);
                    Log.i(TAG, "ID:" + mID + " isClient:" + isClient + " serverIPaddress:"
                            + serverIPaddres + " localIPaddress:"
                            + localIPaddress);

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) {
                    Intent intent = new Intent(WifiapActivity.this,
                            MainTabActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showCustomToast("操作失败,请检查软件是否安装正确");
                }
            }
        });
    }

    /**
     * 创建热点线程类
     * 
     * <p>
     * 线程启动后，热点创建的结果将通过handler更新
     * </p>
     */
    class CreateAPProcess implements Runnable {
        public boolean running = false;
        private long startTime = 0L;
        private Thread thread = null;

        CreateAPProcess() {
        }

        public void run() {
            while (true) {
                if (!this.running)
                    return;
                if ((m_wiFiAdmin.getWifiApState() == 3)
                        || (m_wiFiAdmin.getWifiApState() == 13)
                        || (System.currentTimeMillis() - this.startTime >= 30000L)) {
                    Message msg = handler.obtainMessage(m_nApCreateAPResult);
                    handler.sendMessage(msg);
                }
                try {
                    Thread.sleep(5L);
                } catch (Exception localException) {
                }
            }
        }

        public void start() {
            try {
                thread = new Thread(this);
                running = true;
                startTime = System.currentTimeMillis();
                thread.start();
            } finally {
            }
        }

        public void stop() {
            try {
                this.running = false;
                this.thread = null;
                this.startTime = 0L;
            } finally {
            }
        }
    }

    /**
     * 热点搜索线程类
     * 
     * <p>
     * 线程启动后，热点搜索的结果将通过handler更新
     * </p>
     */
    class WTSearchProcess implements Runnable {
        public boolean running = false;
        private long startTime = 0L;
        private Thread thread = null;

        WTSearchProcess() {
        }

        public void run() {
            while (true) {
                if (!this.running)
                    return;
                if (System.currentTimeMillis() - this.startTime >= 30000L) {
                    Message msg = handler.obtainMessage(m_nApSearchTimeOut);
                    handler.sendMessage(msg);
                }
                try {
                    Thread.sleep(10L);
                } catch (Exception localException) {
                }
            }
        }

        public void start() {
            try {
                this.thread = new Thread(this);
                this.running = true;
                this.startTime = System.currentTimeMillis();
                this.thread.start();
            } finally {
            }
        }

        public void stop() {
            try {
                this.running = false;
                this.thread = null;
                this.startTime = 0L;
            } finally {
            }
        }
    }

    @Override
    public void handleConnectChange() {
        Message msg = handler.obtainMessage(m_nApConnectResult);
        handler.sendMessage(msg);
    }

    @Override
    public void scanResultsAvailable() {
        Message msg = handler.obtainMessage(m_nApScanResult);
        handler.sendMessage(msg);
    }

    @Override
    public void wifiStatusNotification() {
        m_wiFiAdmin.mWifiManager.getWifiState();
    }

    /** 监听 热点搜索按钮 **/
    @Override
    public void onClick() {
        if (!m_wtSearchProcess.running) {// 如果搜索线程没有启动
            if (m_wiFiAdmin.getWifiApState() == 13
                    || m_wiFiAdmin.getWifiApState() == 3) {
                wifiapOperateEnum = WifiapOperateEnum.SEARCH;
                m_Dialog.setMessage(getString(R.string.opened_ap_prompt));
                m_Dialog.show();
                return;
            }
            if (!m_wiFiAdmin.mWifiManager.isWifiEnabled()) {// 如果wifi打开着的
                m_wiFiAdmin.OpenWifi();
            }
            m_textVWTPrompt.setVisibility(View.VISIBLE);
            m_textVWTPrompt.setText(R.string.wt_searching);
            m_linearLCreateAP.setVisibility(View.GONE);
            m_imgRadar.setVisibility(View.GONE);
            m_btnCreateWT.setBackgroundResource(R.drawable.wifiap_create);
            m_wiFiAdmin.startScan();
            m_wtSearchProcess.start();
            m_FrameLWTSearchAnimation.startAnimation();
        } else {
            // 重新启动一下
            m_wtSearchProcess.stop();
            m_wiFiAdmin.startScan();
            m_wtSearchProcess.start();
        }

    }

    /** 监听 主体界面按钮 **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        // 创建热点
        case R.id.create_btn_wt_main:

            // 如果不支持热点创建
            if (m_wiFiAdmin.getWifiApState() == 4) {
                Toast.makeText(getApplicationContext(), R.string.not_create_ap,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 如果wifi正打开着的，就提醒用户
            if (m_wiFiAdmin.mWifiManager.isWifiEnabled()) {
                wifiapOperateEnum = WifiapOperateEnum.CREATE;
                m_Dialog.setMessage(getString(R.string.close_wifi_prompt));
                m_Dialog.show();
                return;
            }

            // 如果已经存在一个其他的共享热点
            if (((m_wiFiAdmin.getWifiApState() == 3) || (m_wiFiAdmin
                    .getWifiApState() == 13))
                    && (!m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {
                wifiapOperateEnum = WifiapOperateEnum.CREATE;
                m_Dialog.setMessage(getString(R.string.ap_used));
                m_Dialog.show();
                return;
            }

            // 如果存在一个同名的共享热点
            if (((m_wiFiAdmin.getWifiApState() == 3) || (m_wiFiAdmin
                    .getWifiApState() == 13))
                    && (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))) {
                wifiapOperateEnum = WifiapOperateEnum.CLOSE;
                m_Dialog.setMessage(getString(R.string.close_ap_prompt));
                m_Dialog.show();
                return;
            }

            // 如果正在搜索状态
            if (m_wtSearchProcess.running) {
                m_wtSearchProcess.stop();
                m_FrameLWTSearchAnimation.stopAnimation();
            }
            m_wiFiAdmin.closeWifi();
            m_wiFiAdmin.createWiFiAP(
                    m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER
                            + getLocalHostName(), WIFI_AP_PASSWORD, 3, "ap"),
                    true);
            m_createAPProcess.start();
            m_listWifi.clear();
            m_wTAdapter.setData(m_listWifi);
            m_wTAdapter.notifyDataSetChanged();
            m_linearLCreateAP.setVisibility(View.VISIBLE);
            m_progBarCreatingAP.setVisibility(View.VISIBLE);
            m_btnCreateWT.setVisibility(View.GONE);
            m_textVWTPrompt.setVisibility(View.GONE);
            m_textVPromptAP.setText(getString(R.string.creating_ap));
            break;

        // 返回按钮
        case R.id.wifiap_btn_back:
            WifiapActivity.this.finish();
            break;

        // 下一步按钮
        case R.id.wifiap_btn_login:
            m_Dialog.dismiss();
            login();
            break;

        }
    }

    /** 监听 提示窗口按钮 **/
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {

        // 确定
        case 0:
            dialog.dismiss();
            switch (wifiapOperateEnum) {

            // 执行关闭热点事件
            case WifiapOperateEnum.CLOSE:
                m_textVWTPrompt.setVisibility(View.VISIBLE);
                m_textVWTPrompt.setText("");
                m_linearLCreateAP.setVisibility(View.GONE);
                m_btnCreateWT.setBackgroundResource(R.drawable.wifiap_create);
                m_imgRadar.setVisibility(View.GONE);
                m_wiFiAdmin.createWiFiAP(m_wiFiAdmin.createWifiInfo(
                        m_wiFiAdmin.getApSSID(), WIFI_AP_PASSWORD, 3, "ap"),
                        false);

                m_wiFiAdmin.OpenWifi();
                m_wtSearchProcess.start();
                m_wiFiAdmin.startScan();
                m_FrameLWTSearchAnimation.startAnimation();
                m_textVWTPrompt.setVisibility(View.VISIBLE);
                m_textVWTPrompt.setText(R.string.wt_searching);
                m_linearLCreateAP.setVisibility(View.GONE);
                m_btnCreateWT.setBackgroundResource(R.drawable.wifiap_create);
                break;

            // 执行创建热点事件
            case WifiapOperateEnum.CREATE:
                if (m_wtSearchProcess.running) {
                    m_wtSearchProcess.stop();
                    m_FrameLWTSearchAnimation.stopAnimation();
                }
                m_wiFiAdmin.closeWifi();
                m_wiFiAdmin.createWiFiAP(m_wiFiAdmin.createWifiInfo(
                        WIFI_AP_HEADER + getLocalHostName(), WIFI_AP_PASSWORD,
                        3, "ap"), true);
                m_createAPProcess.start();
                m_listWifi.clear();
                m_wTAdapter.setData(m_listWifi);
                m_wTAdapter.notifyDataSetChanged();
                m_linearLCreateAP.setVisibility(View.VISIBLE);
                m_progBarCreatingAP.setVisibility(View.VISIBLE);
                m_btnCreateWT.setVisibility(View.GONE);
                m_textVWTPrompt.setVisibility(View.GONE);
                m_textVPromptAP.setText(getString(R.string.creating_ap));
                break;

            // 执行搜索wifi事件
            case WifiapOperateEnum.SEARCH:
                m_textVWTPrompt.setVisibility(View.VISIBLE);
                m_textVWTPrompt.setText(R.string.wt_searching);
                m_linearLCreateAP.setVisibility(View.GONE);
                m_btnCreateWT.setVisibility(View.VISIBLE);
                m_btnCreateWT.setBackgroundResource(R.drawable.wifiap_create);
                m_imgRadar.setVisibility(View.GONE);
                if (m_createAPProcess.running)
                    m_createAPProcess.stop();
                m_wiFiAdmin.createWiFiAP(m_wiFiAdmin.createWifiInfo(
                        m_wiFiAdmin.getApSSID(), WIFI_AP_PASSWORD, 3, "ap"),
                        false);
                m_wiFiAdmin.OpenWifi();
                m_wtSearchProcess.start();
                m_FrameLWTSearchAnimation.startAnimation();
                break;
            }
            break;

        // 取消
        case 1:
            dialog.cancel();
            break;
        }
    }

}
