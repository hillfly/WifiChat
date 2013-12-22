package com.immomo.momo.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.SimpleListDialogAdapter;
import com.immomo.momo.android.dialog.SimpleListDialog;
import com.immomo.momo.android.dialog.SimpleListDialog.onSimpleListItemClickListener;
import com.immomo.momo.android.view.HandyTextView;
import com.immomo.momo.android.view.HeaderLayout;
import com.immomo.momo.android.view.HeaderLayout.HeaderStyle;

public class LoginActivity extends BaseActivity implements OnClickListener,
        onSimpleListItemClickListener {

    private HeaderLayout mHeaderLayout;
    private EditText mEtNickname;
    private HandyTextView mHtvSelectOnlineState;
    private Button mBtnBack;
    private Button mBtnNext;
    private String mNickname;
    private String mGender;
    private RadioGroup mRgGender;
    private String mOnlineState;

    private SimpleListDialog mSimpleListDialog;
    private String[] mOnlineStateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.login_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("登录", null);
        mEtNickname = (EditText) findViewById(R.id.login_et_nickname);
        mHtvSelectOnlineState = (HandyTextView) findViewById(R.id.login_htv_onlinestate);
        mRgGender = (RadioGroup) findViewById(R.id.login_baseinfo_rg_gender);
        mBtnBack = (Button) findViewById(R.id.login_btn_back);
        mBtnNext = (Button) findViewById(R.id.login_btn_next);
    }

    @Override
    protected void initEvents() {
        mHtvSelectOnlineState.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login_htv_onlinestate:
            mOnlineStateType = getResources().getStringArray(
                    R.array.onlinestate_type);
            mSimpleListDialog = new SimpleListDialog(LoginActivity.this);
            mSimpleListDialog.setTitle("选择在线状态");
            mSimpleListDialog.setTitleLineVisibility(View.GONE);
            mSimpleListDialog.setAdapter(new SimpleListDialogAdapter(
                    LoginActivity.this, mOnlineStateType));
            mSimpleListDialog
                    .setOnSimpleListItemClickListener(LoginActivity.this);
            mSimpleListDialog.show();
            break;

        case R.id.login_btn_back:
            finish();
            break;

        case R.id.login_btn_next:
            login_next();
            break;
        }
    }

    @Override
    public void onItemClick(int position) {
        mOnlineState = mOnlineStateType[position];
        mHtvSelectOnlineState.requestFocus();
        mHtvSelectOnlineState.setText(mOnlineState);

    }

    /**
     * 判断文本框的内容是否为空
     * 
     * @param editText
     *            需要判断是否为空的EditText对象
     * @return boolean 返回是否为空,空(true),非空(false)
     */
    private boolean isNull(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 登录资料（昵称与性别）完整性验证，不完整则无法登陆，完整则记录输入的信息。
     * 
     * @return boolean 返回是否为完整， 完整(true),不完整(false)
     */
    private boolean validate() {
        mNickname = null;
        mGender = null;
        if (isNull(mEtNickname)) {
            showCustomToast("请输入您的聊天昵称");
            mEtNickname.requestFocus();
            return false;
        }
        switch (mRgGender.getCheckedRadioButtonId()) {
        case R.id.login_baseinfo_rb_female:
            mGender = "女";
            break;
        case R.id.login_baseinfo_rb_male:
            mGender = "男";
            break;
        default:
            showCustomToast("请选择性别");
            return false;
        }
        mNickname = mEtNickname.getText().toString().trim();
        return true;
    }

    /**
     * 执行下一步跳转
     * <p>
     * 同时获取客户端的IMIE信息，异步创建db，并存储IMIE、登陆资料
     * </p>
     * <p>
     * 若无法获取IMIE、无法创建db、无法存储信息，则返回false，不执行跳转
     * </p>
     * 
     * @return boolean 返回是否执行跳转， 是(true),否(false)
     */
    private void login_next() {
        if ((!validate())) {
            return;
        }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog("正在获取相关信息,请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);

                    // 此处进行相关操作：个人信息的获取与存储;

                    return true;

                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) {
                    Intent intent = new Intent(LoginActivity.this,
                            WifiapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showCustomToast("操作失败,请检查软件是否安装正确");
                }
            }
        });
    }
}
