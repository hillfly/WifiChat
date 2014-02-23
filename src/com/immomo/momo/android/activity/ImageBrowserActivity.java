package com.immomo.momo.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.immomo.momo.android.BaseActivity;
import com.immomo.momo.android.R;
import com.immomo.momo.android.adapter.ImageBrowserAdapter;
import com.immomo.momo.android.view.PhotoTextView;
import com.immomo.momo.android.view.ScrollViewPager;

public class ImageBrowserActivity extends BaseActivity implements
        OnPageChangeListener, OnClickListener {

    private ScrollViewPager mSvpPager;
    private PhotoTextView mPtvPage;
    private ImageBrowserAdapter mAdapter;
    private int mPosition;
    private int mTotal;

    public static final String PATH = "path";
    public static final String POSITION = "position";
    public static final String IMAGE_TYPE = "image_type";
    public static final String TYPE_ALBUM = "image_album";
    public static final String TYPE_PHOTO = "image_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagebrowser);
        initViews();
        initEvents();
        init();
    }

    @Override
    protected void initViews() {
        mSvpPager = (ScrollViewPager) findViewById(R.id.imagebrowser_svp_pager);
        mPtvPage = (PhotoTextView) findViewById(R.id.imagebrowser_ptv_page);
    }

    @Override
    protected void initEvents() {
        mSvpPager.setOnPageChangeListener(this);
    }

    private void init() {
        String mType = getIntent().getStringExtra(IMAGE_TYPE);
        String path = getIntent().getStringExtra(PATH);
        List<String> photos = new ArrayList<String>();
        photos.add(path);
        mPtvPage.setText("1/1");
        mAdapter = new ImageBrowserAdapter(mApplication, photos, mType);
        mSvpPager.setAdapter(mAdapter);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        mPosition = arg0;
        mPtvPage.setText((mPosition % mTotal) + 1 + "/" + mTotal);
    }

    @Override
    public void onClick(View arg0) {
        showCustomToast("图片已保存到本地");
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.zoom_exit);
    }

}
