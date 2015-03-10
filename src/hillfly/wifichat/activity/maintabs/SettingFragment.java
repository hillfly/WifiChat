package hillfly.wifichat.activity.maintabs;

import hillfly.wifichat.ActivitiesManager;
import hillfly.wifichat.BaseApplication;
import hillfly.wifichat.BaseDialog;
import hillfly.wifichat.BaseFragment;
import hillfly.wifichat.R;
import hillfly.wifichat.activity.AboutActivity;
import hillfly.wifichat.activity.MainTabActivity;
import hillfly.wifichat.activity.SettingInfoActivity;
import hillfly.wifichat.sql.SqlDBOperate;
import hillfly.wifichat.util.FileUtils;
import hillfly.wifichat.view.SettingSwitchButton;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SettingFragment extends BaseFragment implements OnClickListener,
        OnCheckedChangeListener, DialogInterface.OnClickListener {

    private Button mAboutUsButton;
    private Button mDeleteAllChattingInfoButton;
    private Button mExitApplicationButton;

    private ImageView mSettingInfoButton;
    private SettingSwitchButton mSoundSwitchButton;
    private SettingSwitchButton mVibrateSwitchButton;
    private RelativeLayout mSettingInfoLayoutButton;

    private BaseDialog mDeleteCacheDialog; // 提示窗口
    private BaseDialog mExitDialog;
    private SqlDBOperate mSqlDBOperate;

    private int mDialogFlag;

    public SettingFragment() {

    }

    public SettingFragment(Context context) {
        super(context);
    }

    @Override
    public View
            onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_settting, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews() {
        mSettingInfoButton = (ImageView) findViewById(R.id.btn_setting_my_information);
        mSettingInfoLayoutButton = (RelativeLayout) findViewById(R.id.setting_my_info_layout);
        mSoundSwitchButton = (SettingSwitchButton) findViewById(R.id.checkbox_sound);
        mVibrateSwitchButton = (SettingSwitchButton) findViewById(R.id.checkbox_vibration);
        mDeleteAllChattingInfoButton = (Button) findViewById(R.id.btn_delete_all_chattinginfo);
        mAboutUsButton = (Button) findViewById(R.id.btn_about_us);
        mExitApplicationButton = (Button) findViewById(R.id.btn_exit_application);
    }

    @Override
    protected void initEvents() {
        mSettingInfoButton.setOnClickListener(this);
        mSettingInfoLayoutButton.setOnClickListener(this);
        mSoundSwitchButton.setOnCheckedChangeListener(this);
        mVibrateSwitchButton.setOnCheckedChangeListener(this);
        mDeleteAllChattingInfoButton.setOnClickListener(this);
        mAboutUsButton.setOnClickListener(this);
        mExitApplicationButton.setOnClickListener(this);

    }

    @Override
    protected void init() {
        mDeleteCacheDialog = BaseDialog.getDialog(getActivity(), R.string.dialog_tips,
                getString(R.string.setting_dialog_chatlog_delete_confirm),
                getString(R.string.setting_dialog_chatlog_delete_ok), this,
                getString(R.string.setting_dialog_chatlog_delete_cancel), this);

        mExitDialog = BaseDialog.getDialog(getActivity(), R.string.dialog_tips,
                getString(R.string.setting_dialog_logout_confirm),
                getString(R.string.setting_dialog_logout_ok), this,
                getString(R.string.setting_dialog_logout_cancel), this);

        mSoundSwitchButton.setChecked(BaseApplication.getSoundFlag());
        mVibrateSwitchButton.setChecked(BaseApplication.getVibrateFlag());
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

        // case R.id.btn_setting_my_information:
            case R.id.setting_my_info_layout:
                startActivity(getActivity(), SettingInfoActivity.class);
                break;

            case R.id.btn_delete_all_chattinginfo:
                mDialogFlag = 1;
                mDeleteCacheDialog.show();
                break;

            case R.id.btn_about_us:
                startActivity(getActivity(), AboutActivity.class);
                break;

            case R.id.btn_exit_application:
                mDialogFlag = 2;
                mExitDialog.show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        switch (buttonView.getId()) {
            case R.id.checkbox_sound:
                buttonView.setChecked(isChecked);
                BaseApplication.setSoundFlag(!isChecked);
                break;

            case R.id.checkbox_vibration:
                buttonView.setChecked(isChecked);
                BaseApplication.setVibrateFlag(isChecked);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub

        switch (mDialogFlag) {
            case 1:
                if (which == 0) {
                    setAsyncTask(1);
                }
                else if (which == 1) {
                    mDeleteCacheDialog.dismiss();
                }
                break;
            case 2:
                if (which == 0) {
                    setAsyncTask(2);
                }
                else if (which == 1) {
                    mExitDialog.dismiss();
                }
                break;
        }
    }

    private void setAsyncTask(final int flag) {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                switch (flag) {
                    case 1:
                        mDeleteCacheDialog.dismiss();
                        showLoadingDialog(getString(R.string.setting_dialog_chatlog_deleting));
                        break;
                    case 2:
                        mExitDialog.dismiss();
                        showLoadingDialog(getString(R.string.setting_dialog_logout_confirm));
                        break;
                    default:
                        break;
                }

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    switch (flag) {
                        case 1:
                            mSqlDBOperate = new SqlDBOperate(getActivity());
                            mSqlDBOperate.deteleAllChattingInfo();
                            mSqlDBOperate.close();
                            mUDPListener.clearMsgCache();
                            mUDPListener.clearUnReadMessages();
                            FileUtils.delAllFile(BaseApplication.SAVE_PATH);
                            break;

                        case 2:
                            break;

                        default:
                            break;
                    }
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
                    dismissLoadingDialog();
                    switch (flag) {
                        case 1:
                            ((MainTabActivity) getActivity()).handler.sendEmptyMessage(0);
                            showShortToast(R.string.setting_dialog_toast_delect_success);
                            break;

                        case 2:
                            ActivitiesManager.finishAllActivities();
                            break;

                        default:
                            break;
                    }

                }
                else {
                    showShortToast(R.string.setting_dialog_toast_delect_failue);
                }
            }
        });
    }

}
