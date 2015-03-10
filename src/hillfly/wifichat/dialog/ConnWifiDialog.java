package hillfly.wifichat.dialog;

import hillfly.wifichat.BaseDialog;
import hillfly.wifichat.activity.wifiap.WifiApConst;
import hillfly.wifichat.util.WifiUtils;
import hillfly.wifichat.util.WifiUtils.WifiCipherType;
import hillfly.wifichat.R;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class ConnWifiDialog extends BaseDialog {

    private EditText mEtConnectPwd;
    private CheckBox mCkShowPwd;
    private ScanResult mScanResult;
    private Handler mHandler;

    public ConnWifiDialog(Context context, Handler handler) {
        super(context);
        setDialogContentView(R.layout.include_dialog_connectwifi);
        mHandler = handler;
        initViews();
        initEvents();

    }

    private void initViews() {
        mEtConnectPwd = (EditText) findViewById(R.id.dialog_et_connectWifi);
        mCkShowPwd = (CheckBox) findViewById(R.id.dialog_cb_showpwd);
    }

    private void initEvents() {

        setButton1(mContext.getString(R.string.btn_yes), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String pwd = getWifiPwd();
                if (TextUtils.isEmpty(pwd)) {
                    return;
                }
                else {

                    ConnWifiDialog.this.setButton1Text(mContext
                            .getString(R.string.wifiap_btn_connecting));
                    ConnWifiDialog.this.setButton1Clickable(false);
                    ConnWifiDialog.this.setButton2Clickable(false);

                    WifiCipherType type = null;
                    String capString = mScanResult.capabilities;
                    if (capString.toUpperCase().contains("WPA")) {
                        type = WifiCipherType.WIFICIPHER_WPA;
                    }
                    else if (capString.toUpperCase().contains("WEP")) {
                        type = WifiCipherType.WIFICIPHER_WEP;
                    }
                    else {
                        type = WifiCipherType.WIFICIPHER_NOPASS;
                    }

                    // 连接网络
                    boolean connFlag = WifiUtils.connectWifi(mScanResult.SSID, pwd, type);
                    ConnWifiDialog.this.setButton1Text(mContext.getString(R.string.btn_yes));
                    ConnWifiDialog.this.setButton1Clickable(true);
                    ConnWifiDialog.this.setButton2Clickable(true);
                    if (connFlag) {
                        clearInput();
                        ConnWifiDialog.this.cancel();
                    }
                    else {
                        mHandler.sendEmptyMessage(WifiApConst.WiFiConnectError);
                    }
                }

            }
        });

        setButton2(mContext.getString(R.string.btn_cancel), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearInput();
                ConnWifiDialog.this.cancel();

            }
        });

        setButton3(null, null);
        mEtConnectPwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mCkShowPwd.setEnabled(false);
                    ConnWifiDialog.this.setButton1Clickable(false);
                }
                else {
                    mCkShowPwd.setEnabled(true);
                    ConnWifiDialog.this.setButton1Clickable(true);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCkShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 文本正常显示
                    mEtConnectPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = mEtConnectPwd.getText();
                    Selection.setSelection(etable, etable.length());

                }
                else {
                    // 文本以密码形式显示
                    mEtConnectPwd.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Editable etable = mEtConnectPwd.getText();
                    Selection.setSelection(etable, etable.length());

                }
            }
        });

    }

    public String getWifiPwd() {
        return mEtConnectPwd.getText().toString().trim();
    }

    public void setBtn1ClickListener(DialogInterface.OnClickListener listener) {
        ConnWifiDialog.this.setButton1(mContext.getString(R.string.wifiap_btn_connectwifi),
                listener);
    }

    public void setScanResult(ScanResult scanResult) {
        this.mScanResult = scanResult;
    }

    private void clearInput() {
        this.mEtConnectPwd.setText("");
        this.mCkShowPwd.setChecked(false);
    }
}
