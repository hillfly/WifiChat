package com.immomo.momo.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
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
import com.immomo.momo.android.activity.wifiap.WifiApConst;
import com.immomo.momo.android.activity.wifiap.WifiapBroadcast;
import com.immomo.momo.android.adapter.WifiapAdapter;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.socket.UDPSocketThread;
import com.immomo.momo.android.sql.SqlDBOperate;
import com.immomo.momo.android.sql.UserInfo;
import com.immomo.momo.android.util.DateUtils;
import com.immomo.momo.android.util.SessionUtils;
import com.immomo.momo.android.util.TextUtils;
import com.immomo.momo.android.util.WifiUtils;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;
import com.immomo.momo.android.view.HeaderLayout.onRightImageButtonClickListener;
import com.immomo.momo.android.view.WifiapSearchAnimationFrameLayout;

/**
 * @fileName WifiapActivity.java
 * @description 热点连接与创建管理类
 * @author _Hill3
 */
public class WifiapActivity extends BaseActivity implements OnClickListener,
        onRightImageButtonClickListener, WifiapBroadcast.EventHandler,
        DialogInterface.OnClickListener {

    private static final String TAG = "SZU_WifiapActivity";

    private int wifiapOperateEnum = WifiApConst.NOTHING;
    private String localIPaddress; // 本地WifiIP
    private String serverIPaddres; // 热点IP
    private String mDevice = getPhoneModel(); // 手机品牌型号
    private boolean isClient = true; // 客户端标识,默认为true
    private ArrayList<String> mWifiApList; // 符合条件的热点列表

    private WifiapSearchAnimationFrameLayout m_FrameLWTSearchAnimation;
    private HeaderLayout mHeaderLayout;
    private BaseDialog mDialog; // 提示窗口
    private Button mBtnBack;
    private Button mBtnLogin;
    private Button mBtnCreateAp;
    private ImageView mIvWifiApIcon;
    private LinearLayout mLlCreateAP;
    private ListView mLvWifiList;
    private ProgressBar mPBCreatingAP;
    private TextView mTvWifiApInfo;
    private TextView mTvWifiApTips;

    private CreateAPProcess mCreateApProcess;
    private WTSearchProcess mSearchApProcess;
    private WifiapAdapter mWifiApAdapter;
    // private UserDAO mUserDAO; // 数据库操作实例，旧
    private UserInfo mUserInfo; // 用户信息类实例
    private SqlDBOperate mSqlDBOperate;// 数据库操作实例,新
    private WifiapBroadcast mWifiapBroadcast;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiap);
        initBroadcast(); // 注册广播
        mWifiUtils = WifiUtils.getInstance(this);
        initViews();
        initEvents();
        initAction();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                .detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .penaltyLog().penaltyDeath().build());

        mContext = this;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mWifiapBroadcast); // 撤销广播
        mWifiapBroadcast.removeehList(this);
        if (mSearchApProcess != null)
            mSearchApProcess.stop();

        if (mCreateApProcess != null)
            mCreateApProcess.stop();
        super.onDestroy();
    }

    /** 动态注册广播 */
    public void initBroadcast() {
        mWifiapBroadcast = new WifiapBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mWifiapBroadcast, filter);
    }

    /** 初始化视图 获取控件对象 **/
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.wifiap_header);
        mBtnBack = (Button) findViewById(R.id.wifiap_btn_back);
        mBtnLogin = (Button) findViewById(R.id.wifiap_btn_login);

        mLlCreateAP = ((LinearLayout) findViewById(R.id.create_ap_llayout_wt_main));// 创建热点的view
        mPBCreatingAP = ((ProgressBar) findViewById(R.id.creating_progressBar_wt_main));// 创建热点的进度条
        mTvWifiApInfo = ((TextView) findViewById(R.id.prompt_ap_text_wt_main));
        mBtnCreateAp = ((Button) findViewById(R.id.create_btn_wt_main)); // 创建热点的按钮
        m_FrameLWTSearchAnimation = ((WifiapSearchAnimationFrameLayout) findViewById(R.id.search_animation_wt_main));// 搜索时的动画
        mLvWifiList = ((ListView) findViewById(R.id.wt_list_wt_main));// 搜索到的热点
        mTvWifiApTips = (TextView) findViewById(R.id.wt_prompt_wt_main);
        mIvWifiApIcon = (ImageView) findViewById(R.id.radar_gif_wt_main);
    }

    /** 初始化全局设置 **/
    @Override
    protected void initEvents() {
        mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
        mHeaderLayout.setTitleRightImageButton("创建连接", null, R.drawable.search_wt, this);

        mWifiApList = new ArrayList<String>();
        mWifiApAdapter = new WifiapAdapter(this, mWifiApList);
        mLvWifiList.setAdapter(mWifiApAdapter);

        mWifiapBroadcast.addehList(this); // 监听广播

        mDialog = BaseDialog.getDialog(WifiapActivity.this, "提示", "", "确 定", this, "取 消", this);
        mDialog.setButton1Background(R.drawable.btn_default_popsubmit);

        mSearchApProcess = new WTSearchProcess();

        mBtnCreateAp.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    /** 初始化控件设置 **/
    protected void initAction() {
        if ((mSearchApProcess.running))
            return;

        if (!isWifiConnect() && !getWifiApState()) {
            mWifiUtils.OpenWifi();
            mSearchApProcess.start();
            mWifiUtils.startScan();
            m_FrameLWTSearchAnimation.startAnimation();
            mLlCreateAP.setVisibility(View.GONE);
            mTvWifiApTips.setText(R.string.wt_searching);
            mTvWifiApTips.setVisibility(View.VISIBLE);
            mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
        }
        if (isWifiConnect()) {
            this.mWifiUtils.startScan();
            this.mSearchApProcess.start();
            this.m_FrameLWTSearchAnimation.startAnimation();
            this.mLlCreateAP.setVisibility(View.GONE);
            this.mTvWifiApTips.setText(R.string.wt_searching);
            this.mTvWifiApTips.setVisibility(View.VISIBLE);
            this.mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
            this.mIvWifiApIcon.setVisibility(View.GONE);
        }

        if (getWifiApState()) {
            m_FrameLWTSearchAnimation.stopAnimation();
            if (mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER)) {
                mTvWifiApTips.setVisibility(View.GONE);
                mLlCreateAP.setVisibility(View.VISIBLE);
                mPBCreatingAP.setVisibility(View.GONE);
                mBtnCreateAp.setVisibility(View.VISIBLE);
                mIvWifiApIcon.setVisibility(View.VISIBLE);
                mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_close);
                mTvWifiApInfo.setText(getString(R.string.create_connect_ok)
                        + getString(R.string.ssid_connect_ok) + mWifiUtils.getApSSID());
                isClient = false;
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
        String str2 = TextUtils.getRandomNumStr(3);
        return str1 + "_" + str2;
    }

    public String getPhoneModel() {
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
        }
        catch (Exception localException) {
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
        if (!((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).isConnected())
            isConnect = false;
        return isConnect;
    }

    /**
     * 设置IP地址信息
     * 
     * @param isClient
     *            是否为客户端
     */
    public void setIPaddress(boolean isClient) {
        mWifiUtils.setNewWifiInfo(); // 更新connection信息
        if (!isClient) {
            // localIPaddress = m_wiFiAdmin.getServerIPAddress(); // 获取本地IP
            // serverIPaddres = localIPaddress; // 热点IP与本机IP相同
            serverIPaddres = localIPaddress = "192.168.43.1"; // android默认AP地址
        }
        else {
            localIPaddress = mWifiUtils.getLocalIPAddress();
            serverIPaddres = mWifiUtils.getServerIPAddress();
        }
        showLogInfo(TAG, "localIPaddress:" + localIPaddress + " serverIPaddres:" + serverIPaddres);
    }

    /**
     * 刷新热点列表UI
     * 
     * @param list
     */
    public void refreshAdapter(List<String> list) {
        Log.i(TAG, "refreshAdapter()");
        mWifiApAdapter.setData(list);
        mWifiApAdapter.notifyDataSetChanged();
    }

    /**
     * IP地址正确性验证
     * 
     * @return boolean 返回是否为正确， 正确(true),不正确(false)
     */
    private boolean isValidated() {
        setIPaddress(isClient); // 获取IP
        String NullIP = "192.168.43";
        if (localIPaddress == null || serverIPaddres == null || !localIPaddress.startsWith(NullIP)
                || !"192.168.43.1".equals(serverIPaddres)) {
            showCustomToast("请创建热点或者连接一个热点");
            return false;
        }
        return true;
    }

    /** 执行登陆 **/
    private void doLogin() {
        // TODO 为了方便测试，可以将次部分注释掉
        // if (!isValidated())
        // {
        // return;
        // }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在存储连接信息...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    // mUserDAO = new UserDAO(mContext); // 实例化数据库操作类，旧
                    mSqlDBOperate = new SqlDBOperate(mContext);// 实例化数据库操作类,新
                    String IMEI = SessionUtils.getIMEI();
                    String nickname = SessionUtils.getNickname();
                    String gender = SessionUtils.getGender();
                    String constellation = SessionUtils.getConstellation();
                    int age = SessionUtils.getAge();
                    int avatar = SessionUtils.getAvatar();
                    int onlineStateInt = SessionUtils.getOnlineStateInt();

                    String logintime = DateUtils.getNowtime();

                    // 录入数据库
                    // 若数据库中有IMEI对应的用户记录，则更新此记录; 无则创建新用户
                    // if ((mUserInfo = mUserDAO.findUserInfo(IMEI)) != null) {
                    if ((mUserInfo = mSqlDBOperate.getUserInfoByIMEI(IMEI)) != null) {
                        mUserInfo.setIPAddr(localIPaddress);
                        mUserInfo.setAvater(avatar);
                        mUserInfo.setIsOnline(onlineStateInt);
                        mUserInfo.setName(nickname);
                        mUserInfo.setSex(gender);
                        mUserInfo.setAge(age);
                        mUserInfo.setDevice(mDevice);
                        mUserInfo.setConstellation(constellation);
                        mUserInfo.setLastDate(logintime);
                        // mUserDAO.update(mUserInfo);
                        mSqlDBOperate.updateUserInfo(mUserInfo);
                    }
                    else {
                        mUserInfo = new UserInfo(nickname, age, gender, IMEI, localIPaddress,
                                onlineStateInt, avatar);
                        mUserInfo.setLastDate(logintime);
                        mUserInfo.setDevice(mDevice);
                        mUserInfo.setConstellation(constellation);
                        // mUserDAO.add(mUserInfo);
                        mSqlDBOperate.addUserInfo(mUserInfo);
                    }

                    /*
                     * BUG:程序第一次使用的时候获取不到自己的ID，即ID为0，是因为程序第一次使用的时候没有自己的用户表，
                     * 没有获取到ID，所以ID应当在这个位置获取，可以避免这个BUG
                     */

                    // int usserID = mUserDAO.getID(IMEI); // 获取用户id
                    int usserID = mSqlDBOperate.getIDByIMEI(IMEI); // 获取用户id
                    // 设置用户Session
                    SessionUtils.setLocalUserID(usserID);
                    SessionUtils.setDevice(mDevice);
                    SessionUtils.setIsClient(isClient);
                    SessionUtils.setLocalIPaddress(localIPaddress);
                    SessionUtils.setServerIPaddress(serverIPaddres);
                    SessionUtils.setLoginTime(logintime);

                    // 在SD卡中存储登陆信息
                    SharedPreferences.Editor mEditor = getSharedPreferences(GlobalSharedName,
                            Context.MODE_PRIVATE).edit();
                    mEditor.putString(NearByPeople.IMEI, IMEI)
                            .putString(NearByPeople.DEVICE, mDevice)
                            .putString(NearByPeople.NICKNAME, nickname)
                            .putString(NearByPeople.GENDER, gender)
                            .putInt(NearByPeople.AVATAR, avatar).putInt(NearByPeople.AGE, age)
                            .putInt(NearByPeople.ONLINESTATEINT, onlineStateInt)
                            .putString(NearByPeople.CONSTELLATION, constellation)
                            .putString(NearByPeople.LOGINTIME, logintime);
                    mEditor.commit();
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    // if (mUserDAO != null)
                    // mUserDAO.close(); // 关闭数据库连接
                    if (null != mSqlDBOperate) {
                        mSqlDBOperate.close();
                        mSqlDBOperate = null;
                    }
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) { // 初始化Thread
                    mUDPSocketThread = UDPSocketThread.getInstance(mApplication,
                            getApplicationContext());
                    mUDPSocketThread.connectUDPSocket(); // 新建Socket线程
                    mUDPSocketThread.notifyOnline(); // 发送上线广播
                    startActivity(MainTabActivity.class);
                    finish();
                }
                else {
                    showCustomToast("操作失败,请检查网络是否正常。");
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
                if ((mWifiUtils.getWifiApState() == 3) || (mWifiUtils.getWifiApState() == 13)
                        || (System.currentTimeMillis() - this.startTime >= 30000L)) {
                    Message msg = handler.obtainMessage(WifiApConst.ApCreateAPResult);
                    handler.sendMessage(msg);
                }
                try {
                    Thread.sleep(5L);
                }
                catch (Exception localException) {
                }
            }
        }

        public void start() {
            try {
                thread = new Thread(this);
                running = true;
                startTime = System.currentTimeMillis();
                thread.start();
            }
            finally {
            }
        }

        public void stop() {
            try {
                this.running = false;
                this.thread = null;
                this.startTime = 0L;
            }
            finally {
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
                    Message msg = handler.obtainMessage(WifiApConst.ApSearchTimeOut);
                    handler.sendMessage(msg);
                }
                try {
                    Thread.sleep(10L);
                }
                catch (Exception localException) {
                }
            }
        }

        public void start() {
            try {
                this.thread = new Thread(this);
                this.running = true;
                this.startTime = System.currentTimeMillis();
                this.thread.start();
            }
            finally {
            }
        }

        public void stop() {
            try {
                this.running = false;
                this.thread = null;
                this.startTime = 0L;
            }
            finally {
            }
        }
    }

    /** 监听 热点搜索按钮 **/
    @Override
    public void onClick() {
        if (!mSearchApProcess.running) {// 如果搜索线程没有启动
            if (mWifiUtils.getWifiApState() == 13 || mWifiUtils.getWifiApState() == 3) {
                wifiapOperateEnum = WifiApConst.SEARCH;
                mDialog.setMessage(getString(R.string.opened_ap_prompt));
                mDialog.show();
                return;
            }
            if (!mWifiUtils.mWifiManager.isWifiEnabled()) {// 如果wifi关闭着
                mWifiUtils.OpenWifi();
            }
            mWifiApList.clear();
            refreshAdapter(mWifiApList);
            mTvWifiApTips.setVisibility(View.VISIBLE);
            mTvWifiApTips.setText(R.string.wt_searching);
            mLlCreateAP.setVisibility(View.GONE);
            mIvWifiApIcon.setVisibility(View.GONE);
            mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
            mWifiUtils.startScan();
            mSearchApProcess.start();
            m_FrameLWTSearchAnimation.startAnimation();
        }
        else {
            // 重新启动一下
            mSearchApProcess.stop();
            mWifiApList.clear();
            refreshAdapter(mWifiApList);
            mWifiUtils.startScan();
            mSearchApProcess.start();
        }
    }

    /** 监听 主体界面按钮 **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        // 创建热点
            case R.id.create_btn_wt_main:

                // 如果不支持热点创建
                if (mWifiUtils.getWifiApState() == 4) {
                    Toast.makeText(getApplicationContext(), R.string.not_create_ap,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 如果wifi正打开着的，就提醒用户
                if (mWifiUtils.mWifiManager.isWifiEnabled()) {
                    wifiapOperateEnum = WifiApConst.CREATE;
                    mDialog.setMessage(getString(R.string.close_wifi_prompt));
                    mDialog.show();
                    return;
                }

                // 如果已经存在一个其他的共享热点
                if (((mWifiUtils.getWifiApState() == 3) || (mWifiUtils.getWifiApState() == 13))
                        && (!mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER))) {
                    wifiapOperateEnum = WifiApConst.CREATE;
                    mDialog.setMessage(getString(R.string.ap_used));
                    mDialog.show();
                    return;
                }

                // 如果存在一个同名的共享热点
                if (((mWifiUtils.getWifiApState() == 3) || (mWifiUtils.getWifiApState() == 13))
                        && (mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER))) {
                    wifiapOperateEnum = WifiApConst.CLOSE;
                    mDialog.setMessage(getString(R.string.close_ap_prompt));
                    mDialog.show();
                    return;
                }

                // 如果正在搜索状态

                if (mSearchApProcess.running) {
                    mSearchApProcess.stop();
                    m_FrameLWTSearchAnimation.stopAnimation();
                }
                mWifiUtils.closeWifi();
                wifiapOperateEnum = WifiApConst.CREATE;
                mDialog.setMessage(getString(R.string.close_wifi_prompt));
                mDialog.show();
                return;

                // 返回按钮
            case R.id.wifiap_btn_back:
                WifiapActivity.this.finish();
                break;

            // 下一步按钮
            case R.id.wifiap_btn_login:
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                doLogin();
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
                    case WifiApConst.CLOSE:
                        mWifiUtils.createWiFiAP(mWifiUtils.createWifiInfo(mWifiUtils.getApSSID(),
                                WifiApConst.WIFI_AP_PASSWORD, 3, "ap"), false);
                        mTvWifiApTips.setVisibility(View.VISIBLE);
                        mTvWifiApTips.setText("");
                        mLlCreateAP.setVisibility(View.GONE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
                        mIvWifiApIcon.setVisibility(View.GONE);

                        localIPaddress = null;
                        serverIPaddres = null;
                        isClient = true;

                        mWifiUtils.OpenWifi();
                        mSearchApProcess.start();
                        mWifiUtils.startScan();
                        m_FrameLWTSearchAnimation.startAnimation();

                        mTvWifiApTips.setVisibility(View.VISIBLE);
                        mTvWifiApTips.setText(R.string.wt_searching);
                        mLlCreateAP.setVisibility(View.GONE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
                        break;

                    // 执行创建热点事件
                    case WifiApConst.CREATE:
                        if (mSearchApProcess.running) {
                            mSearchApProcess.stop();
                            m_FrameLWTSearchAnimation.stopAnimation();
                        }
                        mWifiUtils.closeWifi();
                        mWifiUtils.createWiFiAP(mWifiUtils.createWifiInfo(
                                WifiApConst.WIFI_AP_HEADER + getLocalHostName(),
                                WifiApConst.WIFI_AP_PASSWORD, 3, "ap"), true);
                        if (mCreateApProcess == null) {
                            mCreateApProcess = new CreateAPProcess();
                        }
                        mCreateApProcess.start();
                        mWifiApList.clear();
                        refreshAdapter(mWifiApList);
                        mLlCreateAP.setVisibility(View.VISIBLE);
                        mPBCreatingAP.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setVisibility(View.GONE);
                        mTvWifiApTips.setVisibility(View.GONE);
                        mTvWifiApInfo.setText(getString(R.string.creating_ap));
                        break;

                    // 执行搜索wifi事件
                    case WifiApConst.SEARCH:
                        mTvWifiApTips.setVisibility(View.VISIBLE);
                        mTvWifiApTips.setText(R.string.wt_searching);
                        mLlCreateAP.setVisibility(View.GONE);
                        mBtnCreateAp.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
                        mIvWifiApIcon.setVisibility(View.GONE);
                        if (mCreateApProcess.running)
                            mCreateApProcess.stop();
                        mWifiUtils.createWiFiAP(mWifiUtils.createWifiInfo(mWifiUtils.getApSSID(),
                                WifiApConst.WIFI_AP_PASSWORD, 3, "ap"), false);
                        mWifiUtils.OpenWifi();
                        mSearchApProcess.start();
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

    /** handler 异步更新UI **/
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // 搜索超时
                case WifiApConst.ApSearchTimeOut:
                    mSearchApProcess.stop();
                    m_FrameLWTSearchAnimation.stopAnimation();
                    if (mWifiApList.isEmpty()) {
                        mTvWifiApTips.setVisibility(View.VISIBLE);
                        mTvWifiApTips.setText(R.string.wt_list_empty);
                    }
                    else {
                        mTvWifiApTips.setVisibility(View.GONE);
                    }
                    break;
                // 搜索结果
                case WifiApConst.ApScanResult:
                    int size = mWifiUtils.mWifiManager.getScanResults().size();
                    if (size > 0) {
                        for (int i = 0; i < size; ++i) {
                            String apSSID = mWifiUtils.mWifiManager.getScanResults().get(i).SSID;
                            if (apSSID.startsWith(WifiApConst.WIFI_AP_HEADER)
                                    && !mWifiApList.contains(apSSID)) {
                                mWifiApList.add(apSSID);
                                refreshAdapter(mWifiApList);
                            }
                        }
                        // if (m_listWifi.size() > 0) {
                        // m_wtSearchProcess.stop();
                        // m_FrameLWTSearchAnimation.stopAnimation();
                        // m_textVWTPrompt.setVisibility(View.GONE);
                        // m_wTAdapter.setData(m_listWifi);
                        // m_wTAdapter.notifyDataSetChanged();
                        // }
                    }
                    break;
                // 连接成功
                case WifiApConst.ApConnectResult:
                    mWifiUtils.setNewWifiInfo(); // 更新wifiInfo
                    if (mWifiUtils.getSSID().startsWith(WifiApConst.WIFI_AP_HEADER)) {
                        mSearchApProcess.stop();
                        m_FrameLWTSearchAnimation.stopAnimation();
                        mTvWifiApTips.setVisibility(View.GONE);
                        refreshAdapter(mWifiApList);
                        isClient = true; // 标识客户端
                    }

                    break;

                // 热点创建结果
                case WifiApConst.ApCreateAPResult:
                    mCreateApProcess.stop();
                    mPBCreatingAP.setVisibility(View.GONE);
                    if (((mWifiUtils.getWifiApState() == 3) || (mWifiUtils.getWifiApState() == 13))
                            && (mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER))) {
                        mTvWifiApTips.setVisibility(View.GONE);
                        mLlCreateAP.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setVisibility(View.VISIBLE);
                        mIvWifiApIcon.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_close);
                        // 去除 "+ getString(R.string.password_connect_ok)" 4/14
                        mTvWifiApInfo.setText(getString(R.string.create_connect_ok)
                                + getString(R.string.ssid_connect_ok) + mWifiUtils.getApSSID());
                        isClient = false; // 非客户端

                    }
                    else {
                        mBtnCreateAp.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
                        mTvWifiApInfo.setText(R.string.create_ap_fail);
                    }
                    break;
                case WifiApConst.ApConnectting:
                    mSearchApProcess.stop();
                    m_FrameLWTSearchAnimation.stopAnimation();
                    mTvWifiApTips.setVisibility(View.GONE);
                    break;
                case WifiApConst.ApConnected:
                    Log.i(TAG, "Apconnected");
                    refreshAdapter(mWifiApList);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void handleConnectChange() {
        Message msg = handler.obtainMessage(WifiApConst.ApConnectResult);
        handler.sendMessage(msg);
    }

    @Override
    public void scanResultsAvailable() {

        Message msg = handler.obtainMessage(WifiApConst.ApScanResult);
        handler.sendMessage(msg);
    }

    @Override
    public void wifiStatusNotification() {
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub
    }

}
