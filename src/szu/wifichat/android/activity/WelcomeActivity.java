package szu.wifichat.android.activity;

import szu.wifichat.android.BaseActivity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import szu.wifichat.android.R;

public class WelcomeActivity extends BaseActivity implements OnClickListener {

    private Button mBtnLogin;

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
    }

    @Override
    protected void initEvents() {
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        case R.id.welcome_btn_login:
            startActivity(LoginActivity.class);
            break;

        }
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
