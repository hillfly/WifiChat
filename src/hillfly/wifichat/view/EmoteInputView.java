package hillfly.wifichat.view;

import hillfly.wifichat.BaseApplication;
import hillfly.wifichat.adapter.EmoteAdapter;
import hillfly.wifichat.R;
import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

public class EmoteInputView extends LinearLayout implements OnItemClickListener {

    private GridView mGvDisplay;
    private EmoteAdapter mDefaultAdapter;
    private EmoticonsEditText mEEtView;

    public EmoteInputView(Context context) {
        super(context);
        init();
    }

    public EmoteInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EmoteInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.common_emotionbar, this);
        mGvDisplay = (GridView) findViewById(R.id.emotionbar_gv_display);
        mGvDisplay.setOnItemClickListener(this);

        mDefaultAdapter = new EmoteAdapter(getContext(), BaseApplication.mEmoticons_Zem);
        mGvDisplay.setAdapter(mDefaultAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String text = null;
        text = BaseApplication.mEmoticons_Zem.get(arg2);
        if (mEEtView != null && !TextUtils.isEmpty(text)) {
            int start = mEEtView.getSelectionStart();
            CharSequence content = mEEtView.getText().insert(start, text);
            mEEtView.setText(content);
            // 定位光标位置
            CharSequence info = mEEtView.getText();
            if (info instanceof Spannable) {
                Spannable spanText = (Spannable) info;
                Selection.setSelection(spanText, start + text.length());
            }

        }
    }

    public void setEditText(EmoticonsEditText editText) {
        mEEtView = editText;
    }
}
