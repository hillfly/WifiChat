package szu.wifichat.android.activity.maintabs;

import android.os.Message;
import szu.wifichat.android.BaseActivity;

public class TabItemActivity extends BaseActivity {

    protected Long exitTime = (long) 0;

    protected void init() {
    }

    @Override
    protected void initViews() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initEvents() {
        // TODO Auto-generated method stub

    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() { // 返回桌面
        if (MainTabActivity.getIsTabActive()) {
            System.out.println(System.currentTimeMillis() - exitTime);
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showShortToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            }
            else {
                finish();
            }
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        UDPSocketThread.getInstance(BaseApplication.getInstance(), this).stopUDPSocketThread();
    }

}
