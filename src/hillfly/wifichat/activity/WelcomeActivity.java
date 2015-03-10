package hillfly.wifichat.activity;

import hillfly.wifichat.BaseActivity;
import hillfly.wifichat.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
        mActionBar = getActionBar();
        mActionBar.hide();
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

}
