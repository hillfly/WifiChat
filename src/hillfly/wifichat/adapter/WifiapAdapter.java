package hillfly.wifichat.adapter;

import hillfly.wifichat.activity.wifiap.WifiApConst;
import hillfly.wifichat.util.WifiUtils;

import java.util.List;

import com.squareup.picasso.Picasso;

import hillfly.wifichat.R;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiapAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ScanResult> mDatas;
    private Context mContext;
    private boolean isWifiConnected;

    public WifiapAdapter(Context context, List<ScanResult> list) {
        super();
        this.mDatas = list;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.isWifiConnected = false;
    }

    // 新加的一个函数，用来更新数据
    public void setData(List<ScanResult> list) {
        this.mDatas = list;
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ScanResult ap = mDatas.get(position);
        ViewHolder viewHolder = null;
        isWifiConnected = false;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_wifiap, null);
            viewHolder.rssi = ((ImageView) convertView.findViewById(R.id.wifiap_item_iv_rssi));
            viewHolder.ssid = ((TextView) convertView.findViewById(R.id.wifiap_item_tv_ssid));
            viewHolder.desc = ((TextView) convertView.findViewById(R.id.wifiap_item_tv_desc));
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (WifiUtils.isWifiConnect() && ap.BSSID.equals(WifiUtils.getBSSID())) {
            isWifiConnected = true;
        }

        viewHolder.ssid.setText(ap.SSID);
        viewHolder.desc.setText(getDesc(ap));
        Picasso.with(mContext).load(getRssiImgId(ap)).into(viewHolder.rssi);
        return convertView;
    }

    private String getDesc(ScanResult ap) {
        String desc = "";
        if (ap.SSID.startsWith(WifiApConst.WIFI_AP_HEADER)) {
            desc = "专用网络，可以直接连接";
        }
        else {
            String descOri = ap.capabilities;
            if (descOri.toUpperCase().contains("WPA-PSK")
                    || descOri.toUpperCase().contains("WPA2-PSK")) {
                desc = "受到密码保护";
            }
            else {
                desc = "未受保护的网络";
            }
        }

        // 是否连接此热点
        if (isWifiConnected) {
            desc = "已连接";
        }
        return desc;
    }

    private int getRssiImgId(ScanResult ap) {
        int imgId;
        if (isWifiConnected) {
            imgId = R.drawable.ic_connected;
        }
        else {
            int rssi = Math.abs(ap.level);
            if (rssi > 100) {
                imgId = R.drawable.ic_small_wifi_rssi_0;
            }
            else if (rssi > 80) {
                imgId = R.drawable.ic_small_wifi_rssi_1;
            }
            else if (rssi > 70) {
                imgId = R.drawable.ic_small_wifi_rssi_2;
            }
            else if (rssi > 60) {
                imgId = R.drawable.ic_small_wifi_rssi_3;
            }
            else {
                imgId = R.drawable.ic_small_wifi_rssi_4;
            }
        }
        return imgId;
    }

    public static class ViewHolder {
        public ImageView rssi;
        public TextView ssid;
        public TextView desc;
    }
}
