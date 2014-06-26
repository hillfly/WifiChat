package szu.wifichat.android.activity;

import java.util.Calendar;
import java.util.Date;

import szu.wifichat.android.BaseActivity;
import szu.wifichat.android.R;
import szu.wifichat.android.entity.NearByPeople;
import szu.wifichat.android.socket.udp.UDPSocketThread;
import szu.wifichat.android.util.DateUtils;
import szu.wifichat.android.util.ImageUtils;
import szu.wifichat.android.util.SessionUtils;
import szu.wifichat.android.util.TextUtils;
import szu.wifichat.android.view.HandyTextView;
import szu.wifichat.android.view.HeaderLayout;
import szu.wifichat.android.view.HeaderLayout.HeaderStyle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingMyInfoActivity extends BaseActivity implements OnClickListener,
        OnDateChangedListener {
    private static final String TAG = "SZU_SettingMyInfoActivity";

    private static final int REQUEST_CODE = 1;
    // 登陆年龄限制
    private static final int MAX_AGE = 80;
    private static final int MIN_AGE = 12;

    private HeaderLayout mHeaderLayout;

    private EditText mEtNickname;

    private HandyTextView mHtvConstellation;
    private HandyTextView mHtvAge;
    private ImageView mIvAvater;
    private DatePicker mDpBirthday;
    private Calendar mCalendar;
    private Date mMinDate;
    private Date mMaxDate;
    private Date mSelectDate;

    private RadioGroup mRgGender;
    private RadioButton mRbGirl;
    private RadioButton mRbBoy;
    private Button mBtnBack;
    private Button mBtnNext;

    private int mAge;
    private int mAvatar;
    private String mGender;
    private String mBirthday;
    private String mConstellation; // 星座
    // private String mLastLogintime; // 上次登录时间
    private String mNickname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        initViews();
        initData();
        initEvents();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.my_information_page_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("编辑个人资料", null);

        mIvAvater = (ImageView) findViewById(R.id.setting_my_avater_img);
        mEtNickname = (EditText) findViewById(R.id.setting_my_nickname);
        mRgGender = (RadioGroup) findViewById(R.id.setting_baseinfo_rg_gender);
        mHtvConstellation = (HandyTextView) findViewById(R.id.setting_birthday_htv_constellation);
        mHtvAge = (HandyTextView) findViewById(R.id.setting_birthday_htv_age);
        mDpBirthday = (DatePicker) findViewById(R.id.setting_birthday_dp_birthday);

        mRbBoy = (RadioButton) findViewById(R.id.setting_baseinfo_rb_male);
        mRbGirl = (RadioButton) findViewById(R.id.setting_baseinfo_rb_female);

        mBtnBack = (Button) findViewById(R.id.setting_btn_back);
        mBtnNext = (Button) findViewById(R.id.setting_btn_next);

    }

    @Override
    protected void initEvents() {
        mBtnBack.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mIvAvater.setOnClickListener(this);
    }

    private void initData() {
        mAge = SessionUtils.getAge();
        mAvatar = SessionUtils.getAvatar();
        mGender = SessionUtils.getGender();
        mConstellation = SessionUtils.getConstellation(); // 星座
        // mLastLogintime = SessionUtils.getLoginTime(); // 上次登录时间
        mBirthday = SessionUtils.getBirthday();
        mSelectDate = DateUtils.getDate(mBirthday);

        if (mGender.equals("女")) {
            mRbGirl.setChecked(true);
        }
        else {
            mRbBoy.setChecked(true);
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
        mHtvAge.setText(mAge + "");
        mIvAvater.setImageBitmap(ImageUtils.getAvatar(mApplication, this,
                NearByPeople.AVATAR + mAvatar));
        mEtNickname.setText(SessionUtils.getNickname());
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.setting_btn_back:
                finish();
                break;

            case R.id.setting_btn_next:
                doNext();
                break;
            case R.id.setting_my_avater_img:
                Intent intent = new Intent(this, ChooseAvatarActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }

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
            showShortToast("请输入您的聊天昵称");
            mEtNickname.requestFocus();
            return false;
        }

        switch (mRgGender.getCheckedRadioButtonId()) {
            case R.id.setting_baseinfo_rb_female:
                mGender = "女";
                break;
            case R.id.setting_baseinfo_rb_male:
                mGender = "男";
                break;
            default:
                showShortToast("请选择性别");
                return false;
        }

        mNickname = mEtNickname.getText().toString().trim(); // 获取昵称
        mConstellation = mHtvConstellation.getText().toString().trim(); // 获取星座
        mAge = Integer.parseInt(mHtvAge.getText().toString().trim()); // 获取年龄
        return true;
    }

    private void doNext() {
        if ((!isValidated())) {
            return;
        }
        setAsyncTask();

    }

    private void setAsyncTask() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在存储个人信息...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    showLogInfo(TAG, "mNickname:" + mNickname + " mAge:" + mAge + " mGender:"
                            + mGender + " mOnlineState:" + " mAvatar:" + mAvatar + " mBirthday:"
                            + mBirthday);

                    // 设置用户Session信息
                    SessionUtils.setNickname(mNickname);
                    SessionUtils.setBirthday(mBirthday);
                    SessionUtils.setAge(mAge);
                    SessionUtils.setGender(mGender);
                    SessionUtils.setAvatar(mAvatar);
                    SessionUtils.setConstellation(mConstellation);

                    // 在SD卡中存储登陆信息
                    SharedPreferences.Editor mEditor = getSharedPreferences(GlobalSharedName,
                            Context.MODE_PRIVATE).edit();
                    mEditor.putString(NearByPeople.NICKNAME, mNickname)
                            .putString(NearByPeople.GENDER, mGender)
                            .putInt(NearByPeople.AVATAR, mAvatar).putInt(NearByPeople.AGE, mAge)
                            .putString(NearByPeople.BIRTHDAY, mBirthday)
                            .putString(NearByPeople.CONSTELLATION, mConstellation);
                    mEditor.commit();
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
                    UDPSocketThread.getInstance(mApplication, SettingMyInfoActivity.this)
                            .notifyOnline();
                    finish();
                }
                else {
                    showShortToast("操作失败,请尝试重启程序。");
                }
            }
        });
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mBirthday = String.valueOf(year) + String.format("%02d", monthOfYear)
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

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * 
     * requestCode 请求码，即调用startActivityForResult()传递过去的值 resultCode
     * 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int result = data.getExtras().getInt("result");// 得到新Activity

                // 更换使用缓存机制的头像。 by hill
                mAvatar = result + 1;
                mIvAvater.setImageBitmap(ImageUtils.getAvatar(mApplication, this,
                        NearByPeople.AVATAR + mAvatar));
            }
        }

    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
