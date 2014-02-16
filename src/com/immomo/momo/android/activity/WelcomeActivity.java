package com.immomo.momo.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.activity.register.RegisterActivity;
import com.immomo.momo.android.view.HandyTextView;

public class WelcomeActivity extends BaseActivity implements OnClickListener {

    private LinearLayout mLinearCtrlbar;
    private LinearLayout mLinearAvatars;
    private Button mBtnRegister;
    private Button mBtnLogin;
    private ImageButton mIbtnAbout;

    private View[] mMemberBlocks;
    private String[] mAvatars = new String[] { "welcome_0", "welcome_1",
            "welcome_2", "welcome_3", "welcome_4", "welcome_5" };
    private String[] mDistances = new String[] { "test1", "test2", "test3",
            "test4", "test5", "test6" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();
        initEvents();
        initAvatarsItem();
        showWelcomeAnimation();
    }

    @Override
    protected void initViews() {
        mLinearCtrlbar = (LinearLayout) findViewById(R.id.welcome_linear_ctrlbar);
        mLinearAvatars = (LinearLayout) findViewById(R.id.welcome_linear_avatars);
        mBtnRegister = (Button) findViewById(R.id.welcome_btn_register);
        mBtnLogin = (Button) findViewById(R.id.welcome_btn_login);
        mIbtnAbout = (ImageButton) findViewById(R.id.welcome_ibtn_about);
    }

    @Override
    protected void initEvents() {
        mBtnRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mIbtnAbout.setOnClickListener(this);
    }

    private void initAvatarsItem() {
        initMemberBlocks();
        int mLength = mMemberBlocks.length;
        for (int i = 0; i < mLength; i++) {
            ((ImageView) mMemberBlocks[i]
                    .findViewById(R.id.welcome_item_iv_avatar))
                    .setImageBitmap(mApplication.getAvatar(mAvatars[i]));
            ((HandyTextView) mMemberBlocks[i]
                    .findViewById(R.id.welcome_item_htv_distance))
                    .setText(mDistances[i]);
        }
    }

    private void initMemberBlocks() {
        mMemberBlocks = new View[6];
        mMemberBlocks[0] = findViewById(R.id.welcome_include_member_avatar_block0);
        mMemberBlocks[1] = findViewById(R.id.welcome_include_member_avatar_block1);
        mMemberBlocks[2] = findViewById(R.id.welcome_include_member_avatar_block2);
        mMemberBlocks[3] = findViewById(R.id.welcome_include_member_avatar_block3);
        mMemberBlocks[4] = findViewById(R.id.welcome_include_member_avatar_block4);
        mMemberBlocks[5] = findViewById(R.id.welcome_include_member_avatar_block5);

        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
        int widthAndHeight = (mScreenWidth - margin * 12) / 6;
        int mLength = mMemberBlocks.length;
        for (int i = 0; i < mLength; i++) {
            ViewGroup.LayoutParams params = mMemberBlocks[i].findViewById(
                    R.id.welcome_item_iv_avatar).getLayoutParams();
            params.width = widthAndHeight;
            params.height = widthAndHeight;
            mMemberBlocks[i].findViewById(R.id.welcome_item_iv_avatar)
                    .setLayoutParams(params);
        }
        mLinearAvatars.invalidate();
    }

    private void showWelcomeAnimation() {
        Animation animation = AnimationUtils.loadAnimation(
                WelcomeActivity.this, R.anim.welcome_ctrlbar_slideup);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mLinearAvatars.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mLinearAvatars.setVisibility(View.VISIBLE);
                    }
                }, 800);
            }
        });
        mLinearCtrlbar.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.welcome_btn_register:
                startActivity(RegisterActivity.class);
                break;

            case R.id.welcome_btn_login:
                startActivity(LoginActivity.class);
                break;

            case R.id.welcome_ibtn_about:
                startActivity(AboutTabsActivity.class);
                break;
        }
    }
}
