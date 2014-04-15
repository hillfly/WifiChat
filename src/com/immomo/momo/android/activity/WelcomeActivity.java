package com.immomo.momo.android.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;

public class WelcomeActivity extends BaseActivity implements OnClickListener {

    private Button mBtnLogin;
    private ImageButton mIbtnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mBtnLogin = (Button) findViewById(R.id.welcome_btn_login);
        mIbtnAbout = (ImageButton) findViewById(R.id.welcome_ibtn_about);
    }

    @Override
    protected void initEvents() {
        mBtnLogin.setOnClickListener(this);
        mIbtnAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.welcome_btn_login:
                startActivity(LoginActivity.class);
                break;

            case R.id.welcome_ibtn_about:
                startActivity(AboutTabsActivity.class);
                break;
        }
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
