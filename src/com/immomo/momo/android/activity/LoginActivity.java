package com.immomo.momo.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.SimpleListDialogAdapter;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;

public class LoginActivity extends BaseActivity implements OnClickListener,
        onSimpleListItemClickListener {

    private HeaderLayout mHeaderLayout;
    private LinearLayout mLlayoutMain; // 首次登陆主界面
    private LinearLayout mLlayoutExistMain; // 缓存的登陆页面
    private HandyTextView mHtvSelectOnlineState;
    private EditText mEtNickname;
    private TextView mTvExistNickmame;
    private ImageView mImgExistAvatar;
    private Button mBtnBack;
    private Button mBtnNext;
    private Button mBtnChangeUser;
    private RadioGroup mRgGender;
    private TelephonyManager mTelephonyManager;
    private SimpleListDialog mSimpleListDialog;

    private String mNickname = "";
    private String mGender;
    private String mIMEI = null;
    private String mOnlineStateStr = "在线"; // 默认登录状态
    private int mAvatar;
    private int mOnlineStateInt = 0; // 默认登录状态编号
    private String[] mOnlineStateType;
    private static final String TAG = "SZU_loginActivity";

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);      
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.login_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("登录", null);
        mEtNickname = (EditText) findViewById(R.id.login_et_nickname);
        mHtvSelectOnlineState = (HandyTextView) findViewById(R.id.login_htv_onlinestate);
        mRgGender = (RadioGroup) findViewById(R.id.login_baseinfo_rg_gender);
        mBtnBack = (Button) findViewById(R.id.login_btn_back);
        mBtnNext = (Button) findViewById(R.id.login_btn_next);
        mBtnChangeUser = (Button) findViewById(R.id.login_btn_changeUser);

        SharedPreferences mSharedPreferences = getSharedPreferences(GlobalSharedName,
                Context.MODE_PRIVATE);
        mNickname = mSharedPreferences.getString("Nickname", "");
        
        // 若mNickname有内容，则读取本地存储的用户信息
        if (mNickname.length() != 0) {
            mTvExistNickmame = (TextView) findViewById(R.id.login_tv_existName);
            mImgExistAvatar = (ImageView) findViewById(R.id.login_img_existImg);
            mLlayoutExistMain = (LinearLayout) findViewById(R.id.login_linearlayout_existmain);
            mLlayoutMain = (LinearLayout) findViewById(R.id.login_linearlayout_main);
            mLlayoutMain.setVisibility(View.GONE);
            mLlayoutExistMain.setVisibility(View.VISIBLE);

            mAvatar = mSharedPreferences.getInt("Avatar", 0);
            mOnlineStateInt = mSharedPreferences.getInt("OnlineStateInt", 0);
            mGender = mSharedPreferences.getString("Gender", null);

            mImgExistAvatar.setImageResource(getResources().getIdentifier("avatar" + mAvatar,
                    "drawable", getPackageName())); // 通过getIdentifier获取图片id
            mTvExistNickmame.setText(mNickname);
        }
    }

    @Override
    protected void initEvents() {
        mHtvSelectOnlineState.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnChangeUser.setOnClickListener(this);
    }

    @Override
    public void onClick(
            View v) {
        switch (v.getId()) {
            case R.id.login_htv_onlinestate:
                mOnlineStateType = getResources().getStringArray(R.array.onlinestate_type);
                mSimpleListDialog = new SimpleListDialog(LoginActivity.this);
                mSimpleListDialog.setTitle("选择在线状态");
                mSimpleListDialog.setTitleLineVisibility(View.GONE);
                mSimpleListDialog.setAdapter(new SimpleListDialogAdapter(LoginActivity.this,
                        mOnlineStateType));
                mSimpleListDialog.setOnSimpleListItemClickListener(LoginActivity.this);
                mSimpleListDialog.show();
                break;

            // 更换用户,清空数据
            case R.id.login_btn_changeUser:                
                mNickname = "";
                mGender = null;
                mIMEI = null;
                mOnlineStateStr = "在线"; // 默认登录状态
                mAvatar = 0;
                mOnlineStateInt = 0; // 默认登录状态编号
                mApplication.clearSession(); // 清空Session数据                
                mLlayoutMain.setVisibility(View.VISIBLE);
                mLlayoutExistMain.setVisibility(View.GONE);
                break;

            case R.id.login_btn_back:
                finish();
                break;

            case R.id.login_btn_next:
                login_next();
                break;
        }
    }

    @Override
    public void onItemClick(
            int position) {
        mOnlineStateStr = mOnlineStateType[position];
        mOnlineStateInt = position; // 获取在线状态编号
        mHtvSelectOnlineState.requestFocus();
        mHtvSelectOnlineState.setText(mOnlineStateStr);

    }

    /**
     * 判断文本框的内容是否为空
     * 
     * @param editText
     *            需要判断是否为空的EditText对象
     * @return boolean 返回是否为空,空(true),非空(false)
     */
    private boolean isNull(
            EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 登录资料（昵称与性别）完整性验证，不完整则无法登陆，完整则记录输入的信息。
     * 
     * @return boolean 返回是否为完整， 完整(true),不完整(false)
     */
    private boolean validate() {
        mNickname = null;
        mGender = null;
        if (isNull(mEtNickname)) {
            showCustomToast("请输入您的聊天昵称");
            mEtNickname.requestFocus();
            return false;
        }
        switch (mRgGender.getCheckedRadioButtonId()) {
            case R.id.login_baseinfo_rb_female:
                mGender = "女";
                break;
            case R.id.login_baseinfo_rb_male:
                mGender = "男";
                break;
            default:
                showCustomToast("请选择性别");
                return false;
        }
        mNickname = mEtNickname.getText().toString().trim(); // 获取昵称
        mAvatar = (int) (Math.random() * 12 + 1); // 获取头像编号
        return true;
    }

    /**
     * 执行下一步跳转
     * <p>
     * 同时获取客户端的IMIE信息，异步创建db，并存储IMIE、登陆资料
     * </p>
     * <p>
     * 若无法获取IMIE、无法创建db、无法存储信息，则返回false，不执行跳转
     * </p>
     * 
     * @return boolean 返回是否执行跳转， 是(true),否(false)
     */
    private void login_next() {
        if (mNickname.length() == 0)
            if ((!validate())) {
                return;
            }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在获取相关信息,请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mIMEI = mTelephonyManager.getDeviceId(); // 获取IMEI
                    Log.i(TAG, "mNickname:" + mNickname + " mGender:" + mGender + " mOnlineState:"
                            + mOnlineStateStr + "|" + mOnlineStateInt + " mAvatar:" + mAvatar
                            + " IMEI:" + mIMEI);
                    
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();

                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) {                    
                    mApplication.setIMEI(mIMEI);
                    mApplication.setNickname(mNickname);
                    mApplication.setGender(mGender);
                    mApplication.setAvatar(mAvatar);
                    mApplication.setOnlineStateInt(mOnlineStateInt);
                    
                    Intent intent = new Intent(LoginActivity.this, WifiapActivity.class);         
                    startActivity(intent);
                    finish();
                }
                else {
                    showCustomToast("操作失败,请检查软件是否安装正确");
                }
            }
        });
    }
}
