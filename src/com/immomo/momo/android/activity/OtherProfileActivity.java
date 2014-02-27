package com.immomo.momo.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.message.ChatActivity;
import com.immomo.momo.android.entity.NearByPeople;
import com.immomo.momo.android.view.EmoticonsTextView;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;

public class OtherProfileActivity extends BaseActivity implements
        OnClickListener {

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

    private LinearLayout mLayoutJoinGroup;// 群组

    private NearByPeople mPeople;// 用户实体

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

        mLayoutJoinGroup = (LinearLayout) findViewById(R.id.otherprofile_joingroup_layout_container);
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
        Intent intent = new Intent(OtherProfileActivity.this,
                ChatActivity.class);
        intent.putExtra(NearByPeople.ENTITY_PEOPLE, mPeople);
        startActivity(intent);
    }

    private void getProfile() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在加载,请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Intent intent = getIntent();
                mPeople = intent.getParcelableExtra(NearByPeople.ENTITY_PEOPLE);
                if (mPeople == null) {
                    return false;
                } else {                    
                    mHeaderLayout.setDefaultTitle(mPeople.getNickname(), null);
                    mIvAvatar.setImageBitmap(mApplication.getAvatar(NearByPeople.AVATAR 
                            + mPeople.getAvatar()));
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (!result) {
                    showCustomToast("数据加载失败...");
                } else {
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
        initJoinGroup();
    }

    private void initJoinGroup() {
        View group = LayoutInflater.from(OtherProfileActivity.this).inflate(
                R.layout.otherprofile_joingroup_item, null);
        mLayoutJoinGroup.addView(group);
        ImageView mIvAvatar = (ImageView) group.findViewById(R.id.joingroup_item_avatar);
        EmoticonsTextView mEtvName = (EmoticonsTextView) group.findViewById(R.id.joingroup_item_name);
        HandyTextView mHtvOwner = (HandyTextView) group.findViewById(R.id.joingroup_item_owner);

        mIvAvatar.setImageBitmap(mApplication.getAvatar("nearby_group_1"));
        mEtvName.setText("℡一群二B的小青年");
        mHtvOwner.setVisibility(View.VISIBLE);
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub
        
    }
}
