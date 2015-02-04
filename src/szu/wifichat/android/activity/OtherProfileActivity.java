package szu.wifichat.android.activity;

import szu.wifichat.android.BaseActivity;
import szu.wifichat.android.R;
import szu.wifichat.android.activity.message.ChatActivity;
import szu.wifichat.android.entity.Users;
import szu.wifichat.android.util.ImageUtils;
import szu.wifichat.android.view.HandyTextView;
import szu.wifichat.android.view.HeaderLayout;
import szu.wifichat.android.view.HeaderLayout.HeaderStyle;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class OtherProfileActivity extends BaseActivity implements OnClickListener {

    private HeaderLayout mHeaderLayout;// 标题栏
    private LinearLayout mLayoutChat;// 对话
    private ImageView mIvAvatar; // 头像

    private LinearLayout mLayoutGender;// 性别根布局
    private ImageView mIvGender;// 性别
    private HandyTextView mHtvAge;// 年龄
    private HandyTextView mHtvConstellation;// 星座
    private HandyTextView mHtvTime;// 登陆时间
    private HandyTextView mHtvIPaddress; // IP地址
    private HandyTextView mHtvDevice; // 设备品牌型号

    private Users mPeople;// 用户实体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherprofile);
        initViews();
        initEvents();
        init();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.otherprofile_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mLayoutChat = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_chat);
        mIvAvatar = (ImageView) findViewById(R.id.header_iv_logo);

        mLayoutGender = (LinearLayout) findViewById(R.id.otherprofile_layout_gender);
        mIvGender = (ImageView) findViewById(R.id.otherprofile_iv_gender);
        mHtvAge = (HandyTextView) findViewById(R.id.otherprofile_htv_age);
        mHtvConstellation = (HandyTextView) findViewById(R.id.otherprofile_htv_constellation);
        mHtvTime = (HandyTextView) findViewById(R.id.otherprofile_htv_time);
        mHtvIPaddress = (HandyTextView) findViewById(R.id.otherprofile_htv_ipaddress);
        mHtvDevice = (HandyTextView) findViewById(R.id.otherprofile_htv_device);

    }

    @Override
    protected void initEvents() {
        mLayoutChat.setOnClickListener(this);
    }

    private void init() {
        getProfile();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(OtherProfileActivity.this, ChatActivity.class);
        intent.putExtra(Users.ENTITY_PEOPLE, mPeople);
        startActivity(intent);
    }

    private void getProfile() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog(getString(R.string.dialog_loading));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Intent intent = getIntent();
                mPeople = intent.getParcelableExtra(Users.ENTITY_PEOPLE);
                if (mPeople == null) {
                    return false;
                }
                else {
                    mHeaderLayout.setDefaultTitle(mPeople.getNickname(), null);
                    mIvAvatar.setImageBitmap(ImageUtils.getAvatar(mApplication, mContext,
                            Users.AVATAR + mPeople.getAvatar()));
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (!result) {
                    showShortToast(R.string.dialog_loading_failue);
                }
                else {
                    initProfile();
                }
            }

        });
    }

    private void initProfile() {
        mLayoutGender.setBackgroundResource(mPeople.getGenderBgId());
        mIvGender.setImageResource(mPeople.getGenderId());
        mHtvAge.setText(mPeople.getAge() + "");
        mHtvConstellation.setText(mPeople.getConstellation());
        mHtvTime.setText(mPeople.getLogintime());
        mHtvIPaddress.setText(mPeople.getIpaddress());
        mHtvDevice.setText(mPeople.getDevice());
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
