package hillfly.wifichat.adapter;

import hillfly.wifichat.BaseApplication;
import hillfly.wifichat.BaseArrayListAdapter;

import java.util.List;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import hillfly.wifichat.R;

public class EmoteAdapter extends BaseArrayListAdapter {

    public EmoteAdapter(Context context, List<String> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_emote, null);
            holder = new ViewHolder();
            holder.mIvImage = (ImageView) convertView.findViewById(R.id.emote_item_iv_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = (String) getItem(position);
        int id = BaseApplication.mEmoticonsId.get(name);
        holder.mIvImage.setImageResource(id);
        return convertView;
    }

    class ViewHolder {
        ImageView mIvImage;
    }
}
