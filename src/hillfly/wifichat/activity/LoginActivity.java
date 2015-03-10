package hillfly.wifichat.activity;

import hillfly.wifichat.BaseActivity;
import hillfly.wifichat.R;
import hillfly.wifichat.adapter.SimpleListDialogAdapter;
import hillfly.wifichat.bean.Users;
import hillfly.wifichat.dialog.SimpleListDialog;
import hillfly.wifichat.dialog.SimpleListDialog.onSimpleListItemClickListener;
import hillfly.wifichat.util.DateUtils;
import hillfly.wifichat.util.ImageUtils;
import hillfly.wifichat.util.SessionUtils;
import hillfly.wifichat.util.SharePreferenceUtils;
import hillfly.wifichat.util.TextUtils;
import hillfly.wifichat.view.HandyTextView;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * @fileName LoginActivity.java
 * @description 用户登陆类
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
        onSimpleListItemClickListener, OnDateChangedListener {

    // 登陆年龄限制
    private static final int MAX_AGE = 80;
    private static final int MIN_AGE = 12;
    private static final String DEFAULT_DATA = "19920101";

    private LinearLayout mLlayoutMain; // 首次登陆主界面
    private HandyTextView mHtvSelectOnlineState;
    private EditText mEtNickname;

    private HandyTextView mHtvConstellation;
    private HandyTextView mHtvAge;
    private DatePicker mDpBirthday;
    private Calendar mCalendar;
    private Date mMinDate;
    private Date mMaxDate;
    private Date mSelectDate;

    private LinearLayout mLlayoutExMain; // 二次登陆页面
    private ImageView mImgExAvatar;
    private TextView mTvExNickmame;
    private LinearLayout mLayoutExGender; // 性别根布局
    private ImageView mIvExGender;
    private HandyTextView mHtvExAge;
    private TextView mTvExConstellation;// 星座
    private TextView mTvExLogintime; // 上次登录时间

    private Button mBtnBack;
    private Button mBtnNext;
    private Button mBtnChangeUser;
    private RadioGroup mRgGender;
    private TelephonyManager mTelephonyManager;
    private SimpleListDialog mSimpleListDialog;

    private int mAge;
    private int mAvatar;
    private String mBirthday;
    private String mGender;
    private String mIMEI;
    private String mConstellation; // 星座
    private String mLastLogintime; // 上次登录时间
    private String mNickname = "";
    private String mOnlineStateStr = "在线"; // 默认登录状态
    private int mOnlineStateInt = 0; // 默认登录状态编号
    private String[] mOnlineStateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        initViews();
        initData();
        initEvents();
    }

    @Override
    protected void initViews() {

        mEtNickname = (EditText) findViewById(R.id.login_et_nickname);
        mHtvSelectOnlineState = (HandyTextView) findViewById(R.id.login_htv_onlinestate);
        mRgGender = (RadioGroup) findViewById(R.id.login_baseinfo_rg_gender);
        mHtvConstellation = (HandyTextView) findViewById(R.id.login_birthday_htv_constellation);
        mHtvAge = (HandyTextView) findViewById(R.id.login_birthday_htv_age);
        mDpBirthday = (DatePicker) findViewById(R.id.login_birthday_dp_birthday);

        mBtnBack = (Button) findViewById(R.id.login_btn_back);
        mBtnNext = (Button) findViewById(R.id.login_btn_next);
        mBtnChangeUser = (Button) findViewById(R.id.login_btn_changeUser);

        SharePreferenceUtils sp = new SharePreferenceUtils();
        mNickname = sp.getNickname();

        // 若mNickname有内容，则读取本地存储的用户信息
        if (mNickname.length() != 0) {
            mTvExNickmame = (TextView) findViewById(R.id.login_tv_existName);
            mImgExAvatar = (ImageView) findViewById(R.id.login_img_existImg);
            mLayoutExGender = (LinearLayout) findViewById(R.id.login_layout_gender);
            mIvExGender = (ImageView) findViewById(R.id.login_iv_gender);
            mHtvExAge = (HandyTextView) findViewById(R.id.login_htv_age);
            mTvExConstellation = (TextView) findViewById(R.id.login_tv_constellation);
            mTvExLogintime = (TextView) findViewById(R.id.login_tv_lastlogintime);
            mLlayoutExMain = (LinearLayout) findViewById(R.id.login_linearlayout_existmain);
            mLlayoutMain = (LinearLayout) findViewById(R.id.login_linearlayout_main);
            mLlayoutMain.setVisibility(View.GONE);
            mLlayoutExMain.setVisibility(View.VISIBLE);

            mAvatar = sp.getAvatarId();
            mBirthday = sp.getBirthday();
            mOnlineStateInt = sp.getOnlineStateId();
            mGender = sp.getGender();
            mAge = sp.getAge();
            mConstellation = sp.getConstellation();
            mLastLogintime = sp.getLogintime();
            
            Picasso.with(mContext).load(ImageUtils.getImageID(Users.AVATAR + mAvatar))
                    .into(mImgExAvatar);
            mTvExNickmame.setText(mNickname);
            mTvExConstellation.setText(mConstellation);
            mHtvExAge.setText(mAge + "");
            mTvExLogintime.setText(DateUtils.getBetweentime(mLastLogintime));
            if ("女".equals(mGender)) {
                mIvExGender.setBackgroundResource(R.drawable.ic_user_famale);
                mLayoutExGender.setBackgroundResource(R.drawable.bg_gender_famal);
            }
            else {
                mIvExGender.setBackgroundResource(R.drawable.ic_user_male);
                mLayoutExGender.setBackgroundResource(R.drawable.bg_gender_male);
            }
        }
    }

    @Override
    protected void initEvents() {
        mHtvSelectOnlineState.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnChangeUser.setOnClickListener(this);
    }

    private void initData() {
        if (android.text.TextUtils.isEmpty(mBirthday)) {
            mSelectDate = DateUtils.getDate(DEFAULT_DATA);
            mBirthday = DEFAULT_DATA;
        }
        else {
            mSelectDate = DateUtils.getDate(mBirthday);
        }

        Calendar mMinCalendar = Calendar.getInstance();
        Calendar mMaxCalendar = Calendar.getInstance();

        mMinCalendar.set(Calendar.YEAR, mMinCalendar.get(Calendar.YEAR) - MIN_AGE);
        mMinDate = mMinCalendar.getTime();
        mMaxCalendar.set(Calendar.YEAR, mMaxCalendar.get(Calendar.YEAR) - MAX_AGE);
        mMaxDate = mMaxCalendar.getTime();

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(mSelectDate);
        flushBirthday(mCalendar);
        mDpBirthday.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH), this);
    }

    @Override
    public void onClick(View v) {
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
                mAge = -1;
                mGender = null;
                mIMEI = null;
                mOnlineStateStr = "在线"; // 默认登录状态
                mAvatar = 0;
                mConstellation = null;
                mOnlineStateInt = 0; // 默认登录状态编号
                SessionUtils.clearSession(); // 清空Session数据
                mLlayoutMain.setVisibility(View.VISIBLE);
                mLlayoutExMain.setVisibility(View.GONE);
                break;

            case R.id.login_btn_back:
                finish();
                break;

            case R.id.login_btn_next:
                doLoginNext();
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        mOnlineStateStr = mOnlineStateType[position];
        mOnlineStateInt = position; // 获取在线状态编号
        mHtvSelectOnlineState.requestFocus();
        mHtvSelectOnlineState.setText(mOnlineStateStr);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mBirthday = String.valueOf(year) + String.format("%02d", monthOfYear+1)
                + String.format("%02d", dayOfMonth);
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, monthOfYear, dayOfMonth);
        if (mCalendar.getTime().after(mMinDate) || mCalendar.getTime().before(mMaxDate)) {
            mCalendar.setTime(mSelectDate);
            mDpBirthday.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH), this);
        }
        else {
            flushBirthday(mCalendar);
        }
    }

    private void flushBirthday(Calendar calendar) {
        String constellation = TextUtils.getConstellation(calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        mSelectDate = calendar.getTime();
        mHtvConstellation.setText(constellation);
        int age = TextUtils.getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        mHtvAge.setText(age + "");
    }

    /**
     * 登录资料完整性验证，不完整则无法登陆，完整则记录输入的信息。
     * 
     * @return boolean 返回是否为完整， 完整(true),不完整(false)
     */
    private boolean isValidated() {
        mNickname = "";
        mGender = null;
        if (TextUtils.isNull(mEtNickname)) {
            showShortToast(R.string.login_toast_nickname);
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
                showShortToast(R.string.login_toast_sex);
                return false;
        }

        mNickname = mEtNickname.getText().toString().trim(); // 获取昵称
        mAvatar = (int) (Math.random() * 12 + 1); // 获取头像编号
        mConstellation = mHtvConstellation.getText().toString().trim(); // 获取星座
        mAge = Integer.parseInt(mHtvAge.getText().toString().trim()); // 获取年龄
        return true;
    }

    /**
     * 执行下一步跳转
     * <p>
     * 同时获取客户端的IMIE信息
     */
    private void doLoginNext() {
        if (mNickname.length() == 0) {
            if ((!isValidated())) {
                return;
            }
        }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog(getString(R.string.login_dialog_saveInfo));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mIMEI = mTelephonyManager.getDeviceId(); // 获取IMEI

                    // 设置用户Session信息
                    SessionUtils.setIMEI(mIMEI);
                    SessionUtils.setNickname(mNickname);
                    SessionUtils.setAge(mAge);
                    SessionUtils.setBirthday(mBirthday);
                    SessionUtils.setGender(mGender);
                    SessionUtils.setAvatar(mAvatar);
                    SessionUtils.setOnlineStateInt(mOnlineStateInt);
                    SessionUtils.setConstellation(mConstellation);
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
                    startActivity(WifiapActivity.class);
                    finish();
                }
                else {
                    showShortToast(R.string.login_toast_loginfailue);
                }
            }
        });
    }
}
