package szu.wifichat.android.activity;

import szu.wifichat.android.BaseActivity;
import szu.wifichat.android.R;
import szu.wifichat.android.adapter.AvatarAdapter;
import szu.wifichat.android.view.HeaderLayout;
import szu.wifichat.android.view.HeaderLayout.HeaderStyle;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ChooseAvatarActivity extends BaseActivity implements
        OnItemClickListener {
    private GridView gridView;
    private HeaderLayout mHeaderLayout;
    AvatarAdapter adapter;
    // 图片的文字标题
    private static String[] titles = new String[] { "pic1", "pic2", "pic3",
            "pic4", "pic5", "pic6", "pic7", "pic8", "pic9", "pic10", "pic11",
            "pic12" };
    // 图片ID数组
    private static int[] images = new int[] { R.drawable.avatar1,
            R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
            R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7,
            R.drawable.avatar8, R.drawable.avatar9, R.drawable.avatar10,
            R.drawable.avatar11, R.drawable.avatar12, };

    public static int getImage(int position) {
        return images[position];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setavater);
        initViews();
        initData();
        initEvents();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Toast.makeText(ChooseAvatarActivity.this, "pic" + (position + 1),
                Toast.LENGTH_SHORT).show();
        // 数据是使用Intent返回
        Intent intent = new Intent();
        // 把返回数据存入Intent
        intent.putExtra("result", position);
        // 设置返回数据
        ChooseAvatarActivity.this.setResult(RESULT_OK, intent);
        // 关闭Activity
        ChooseAvatarActivity.this.finish();
    }

    @Override
    protected void initViews() {
        // TODO Auto-generated method stub
        gridView = (GridView) findViewById(R.id.gridview);
        mHeaderLayout = (HeaderLayout) findViewById(R.id.myavater_header);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("请选择你的头像", null);
    }

    private void initData() {
        adapter = new AvatarAdapter(titles, images, this);
        gridView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        // TODO Auto-generated method stub
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
