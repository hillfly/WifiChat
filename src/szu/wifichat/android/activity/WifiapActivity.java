package szu.wifichat.android.activity;

import java.util.ArrayList;
import java.util.List;

import szu.wifichat.android.BaseActivity;
import szu.wifichat.android.BaseDialog;
import szu.wifichat.android.R;
import szu.wifichat.android.activity.maintabs.MainTabActivity;
import szu.wifichat.android.activity.wifiap.WifiApConst;
import szu.wifichat.android.activity.wifiap.WifiapBroadcast;
import szu.wifichat.android.adapter.WifiapAdapter;
import szu.wifichat.android.entity.NearByPeople;
import szu.wifichat.android.socket.udp.UDPSocketThread;
import szu.wifichat.android.sql.SqlDBOperate;
import szu.wifichat.android.sql.UserInfo;
import szu.wifichat.android.util.DateUtils;
import szu.wifichat.android.util.LogUtils;
import szu.wifichat.android.util.SessionUtils;
import szu.wifichat.android.util.TextUtils;
import szu.wifichat.android.util.WifiUtils;
import szu.wifichat.android.view.HeaderLayout;
import szu.wifichat.android.view.HeaderLayout.HeaderStyle;
import szu.wifichat.android.view.HeaderLayout.onRightImageButtonClickListener;
import szu.wifichat.android.view.WifiSearchView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

    private WifiSearchView mWifisearchAnimation;
    private HeaderLayout mHeaderLayout;
    private BaseDialog mDialog; // 提示窗口
    private Button mBtnBack;
    private Button mBtnLogin;
    private Button mBtnCreateAp;
    private ImageView mIvWifiApIcon;
    private LinearLayout mLlCreateAP;
    private ListView mLvWifiList;
    private TextView mTvWifiApInfo;
    private TextView mTvWifiApTips;

    private WTSearchProcess mSearchApProcess;
    private WifiapAdapter mWifiApAdapter;
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
        mTvWifiApInfo = ((TextView) findViewById(R.id.prompt_ap_text_wt_main));
        mBtnCreateAp = ((Button) findViewById(R.id.create_btn_wt_main)); // 创建热点的按钮
        mWifisearchAnimation = ((WifiSearchView) findViewById(R.id.search_animation_wt_main));// 搜索时的动画
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

        mDialog = BaseDialog.getDialog(WifiapActivity.this, R.string.dialog_tips, "", "确 定", this,
                "取 消", this);
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

        if (!mWifiUtils.isWifiConnect() && !mWifiUtils.isWifiApEnabled()) { // 无开启热点无连接WIFI
            mWifiUtils.OpenWifi();
            mSearchApProcess.start();
            mWifiUtils.startScan();
            mWifisearchAnimation.startAnimation();
            mLlCreateAP.setVisibility(View.GONE);
            mTvWifiApTips.setText(R.string.wifiap_text_searchap_searching);
            mTvWifiApTips.setVisibility(View.VISIBLE);
            mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
        }
        if (mWifiUtils.isWifiConnect()) { // 已连接WIFI
            this.mWifiUtils.startScan();
            this.mSearchApProcess.start();
            this.mWifisearchAnimation.startAnimation();
            this.mLlCreateAP.setVisibility(View.GONE);
            this.mTvWifiApTips.setText(R.string.wifiap_text_searchap_searching);
            this.mTvWifiApTips.setVisibility(View.VISIBLE);
            this.mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
            this.mIvWifiApIcon.setVisibility(View.GONE);
        }

        if (mWifiUtils.isWifiApEnabled()) { // 已开启热点
            mWifisearchAnimation.stopAnimation();
            if (mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER)) {
                mTvWifiApTips.setVisibility(View.GONE);
                mLlCreateAP.setVisibility(View.VISIBLE);
                mBtnCreateAp.setVisibility(View.VISIBLE);
                mIvWifiApIcon.setVisibility(View.VISIBLE);
                mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_close);
                mTvWifiApInfo.setText(getString(R.string.wifiap_text_connectap_succeed)
                        + getString(R.string.wifiap_text_connectap_ssid) + mWifiUtils.getApSSID());
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
        str2 = str1 + "_" + str2;
        return str2;
    }

    /**
     * 设置IP地址信息
     * 
     * @param isClient
     *            是否为客户端
     */
    public void setIPaddress(boolean isClient) {
        mWifiUtils.setNewWifiManagerInfo();
        LogUtils.d(TAG,
                "isClient:" + isClient + "|" + "isWifiConnect:" + mWifiUtils.isWifiConnect());
        if (!isClient && !mWifiUtils.isWifiConnect()) {
            // localIPaddress = mWifiUtils.getServerIPAddress(); // 获取本地IP
            // serverIPaddres = localIPaddress; // 热点IP与本机IP相同
            serverIPaddres = localIPaddress = "192.168.43.1";
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
        String nullIP = "0.0.0.0";

        if (nullIP.equals(localIPaddress) || nullIP.equals(serverIPaddres)
                || localIPaddress == null || serverIPaddres == null) {
            showShortToast("请创建热点或者连接一个热点");
            return false;
        }

        return true;
    }

    /** 执行登陆 **/
    private void doLogin() {
        // TODO 为了方便测试，可以将次部分注释掉
        if (!isValidated()) {
            return;
        }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在存储连接信息...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
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
                        mSqlDBOperate.updateUserInfo(mUserInfo);
                    }
                    else {
                        mUserInfo = new UserInfo(nickname, age, gender, IMEI, localIPaddress,
                                onlineStateInt, avatar);
                        mUserInfo.setLastDate(logintime);
                        mUserInfo.setDevice(mDevice);
                        mUserInfo.setConstellation(constellation);
                        mSqlDBOperate.addUserInfo(mUserInfo);
                    }

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
                            .putString(NearByPeople.BIRTHDAY, SessionUtils.getBirthday())
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
                    showShortToast("操作失败,请检查网络是否正常。");
                }
            }
        });
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
            if (mWifiUtils.isWifiApEnabled()) {
                wifiapOperateEnum = WifiApConst.SEARCH;
                mDialog.setMessage(getString(R.string.wifiap_dialog_searchap_closeap_confirm));
                mDialog.show();
                return;
            }
            if (!mWifiUtils.mWifiManager.isWifiEnabled()) {// 如果wifi关闭着
                mWifiUtils.OpenWifi();
            }
            mWifiApList.clear();
            refreshAdapter(mWifiApList);
            mTvWifiApTips.setVisibility(View.VISIBLE);
            mTvWifiApTips.setText(R.string.wifiap_text_searchap_searching);
            mLlCreateAP.setVisibility(View.GONE);
            mIvWifiApIcon.setVisibility(View.GONE);
            mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
            mWifiUtils.startScan();
            mSearchApProcess.start();
            mWifisearchAnimation.startAnimation();
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
                if (mWifiUtils.getWifiApStateInt() == 4) {
                    showShortToast(R.string.wifiap_dialog_createap_nonsupport);
                    return;
                }

                // 如果wifi正打开着的，就提醒用户
                if (mWifiUtils.mWifiManager.isWifiEnabled()) {
                    wifiapOperateEnum = WifiApConst.CREATE;
                    mDialog.setMessage(getString(R.string.wifiap_dialog_createap_closewifi_confirm));
                    mDialog.show();
                    return;
                }

                // 如果已经存在一个其他的共享热点
                if (((mWifiUtils.getWifiApStateInt() == 3) || (mWifiUtils.getWifiApStateInt() == 13))
                        && (!mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER))) {
                    wifiapOperateEnum = WifiApConst.CREATE;
                    mDialog.setMessage(getString(R.string.wifiap_dialog_createap_used));
                    mDialog.show();
                    return;
                }

                // 如果存在一个同名的共享热点
                if (((mWifiUtils.getWifiApStateInt() == 3) || (mWifiUtils.getWifiApStateInt() == 13))
                        && (mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER))) {
                    wifiapOperateEnum = WifiApConst.CLOSE;
                    mDialog.setMessage(getString(R.string.wifiap_dialog_closeap_confirm));
                    mDialog.show();
                    return;
                }

                // 如果正在搜索状态

                if (mSearchApProcess.running) {
                    mSearchApProcess.stop();
                    mWifisearchAnimation.stopAnimation();
                }
                mWifiUtils.closeWifi();
                wifiapOperateEnum = WifiApConst.CREATE;
                mDialog.setMessage(getString(R.string.wifiap_dialog_createap_closewifi_confirm));
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
                        mWifiUtils.closeWifiAp();
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
                        mWifisearchAnimation.startAnimation();

                        mTvWifiApTips.setVisibility(View.VISIBLE);
                        mTvWifiApTips.setText(R.string.wifiap_text_searchap_searching);
                        mLlCreateAP.setVisibility(View.GONE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
                        break;

                    // 创建热点
                    case WifiApConst.CREATE:
                        if (mSearchApProcess.running) {
                            mSearchApProcess.stop();
                            mWifisearchAnimation.stopAnimation();
                        }
                        mWifiUtils.startWifiAp(WifiApConst.WIFI_AP_HEADER + getLocalHostName(),
                                WifiApConst.WIFI_AP_PASSWORD, handler);
                        mWifiApList.clear();
                        refreshAdapter(mWifiApList);
                        mLlCreateAP.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setVisibility(View.GONE);
                        mTvWifiApTips.setVisibility(View.GONE);
                        mTvWifiApInfo.setText(getString(R.string.wifiap_text_createap_creating));
                        break;

                    // 搜索wifi
                    case WifiApConst.SEARCH:
                        mTvWifiApTips.setVisibility(View.VISIBLE);
                        mTvWifiApTips.setText(R.string.wifiap_text_searchap_searching);
                        mLlCreateAP.setVisibility(View.GONE);
                        mBtnCreateAp.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
                        mIvWifiApIcon.setVisibility(View.GONE);

                        mWifiUtils.closeWifiAp();
                        mWifiUtils.OpenWifi();
                        mSearchApProcess.start();
                        mWifisearchAnimation.startAnimation();
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
                    mWifisearchAnimation.stopAnimation();
                    if (mWifiApList.isEmpty()) {
                        mTvWifiApTips.setVisibility(View.VISIBLE);
                        mTvWifiApTips.setText(R.string.wifiap_text_searchap_empty);
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
                    }
                    break;
                // 连接成功
                case WifiApConst.ApConnectResult:
                    mWifiUtils.setNewWifiManagerInfo();

                    if (mWifiUtils.getSSID().startsWith(WifiApConst.WIFI_AP_HEADER)) {
                        mSearchApProcess.stop();
                        mWifisearchAnimation.stopAnimation();
                        mTvWifiApTips.setVisibility(View.GONE);
                        refreshAdapter(mWifiApList);
                        isClient = true; // 标识客户端
                    }

                    break;

                // 热点创建结果
                case WifiApConst.ApCreateAPResult:
                    if (mWifiUtils.isWifiApEnabled()
                            && mWifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER)) {
                        mTvWifiApTips.setVisibility(View.GONE);
                        mLlCreateAP.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setVisibility(View.VISIBLE);
                        mIvWifiApIcon.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_close);
                        mTvWifiApInfo.setText(getString(R.string.wifiap_text_connectap_succeed)
                                + getString(R.string.wifiap_text_connectap_ssid)
                                + mWifiUtils.getApSSID());
                        isClient = false; // 非客户端
                    }
                    else {
                        mBtnCreateAp.setVisibility(View.VISIBLE);
                        mBtnCreateAp.setBackgroundResource(R.drawable.wifiap_create);
                        mTvWifiApInfo.setText(R.string.wifiap_text_createap_fail);
                    }
                    break;
                case WifiApConst.ApConnectting:
                    mSearchApProcess.stop();
                    mWifisearchAnimation.stopAnimation();
                    mTvWifiApTips.setVisibility(View.GONE);
                    break;
                case WifiApConst.ApConnected:
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
