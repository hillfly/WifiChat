package szu.wifichat.android.activity;

import szu.wifichat.android.BaseActivity;
import szu.wifichat.android.view.HeaderLayout;
import szu.wifichat.android.view.HeaderLayout.HeaderStyle;
import android.os.Bundle;
import android.os.Message;

import szu.wifichat.android.R;

public class AboutActivity extends BaseActivity {
    
    private HeaderLayout mHeaderLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initViews();
		initEvents();
	}

	@Override
	protected void initViews() {
	    mHeaderLayout = (HeaderLayout) findViewById(R.id.aboutus_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("关于我们", null);
	}

	@Override
	protected void initEvents() {
	}

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub
        
    }

}
