package com.immomo.momo.android.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.R;

public class MoMoRefreshListView extends HandyListView {
    private final static int RELEASE_TO_REFRESH = 0;
    private final static int PULL_TO_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;
    private final static int RATIO = 3;

    private View mHeader;

    private HandyTextView mHtvTitle;
    private HandyTextView mHtvTime;
    private ImageView mIvArrow;
    private ImageView mIvLoading;
    private ImageView mIvCancel;

    private android.view.animation.RotateAnimation mPullAnimation;
    private android.view.animation.RotateAnimation mReverseAnimation;
    private Animation mLoadingAnimation;

    private boolean mIsRecored;

    private int mHeaderHeight;

    private int mStartY;

    private int mState; // 刷新状态

    private boolean mIsBack;
    private OnRefreshListener mOnRefreshListener;
    private OnCancelListener mOnCancelListener;
    private boolean mIsRefreshable;
    private boolean mIsCancelable;

    public MoMoRefreshListView(Context context) {
        super(context);
        init();
    }

    public MoMoRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MoMoRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHeader = mInflater.inflate(R.layout.include_pull_to_refreshing_header,
                null);
        mHtvTitle = (HandyTextView) mHeader
                .findViewById(R.id.refreshing_header_htv_title);
        mHtvTime = (HandyTextView) mHeader
                .findViewById(R.id.refreshing_header_htv_time);
        mIvArrow = (ImageView) mHeader
                .findViewById(R.id.refreshing_header_iv_arrow);
        mIvLoading = (ImageView) mHeader
                .findViewById(R.id.refreshing_header_iv_loading);
        mIvCancel = (ImageView) mHeader
                .findViewById(R.id.refreshing_header_iv_cancel);

        mIvCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mOnCancelListener != null && mIsCancelable) {
                    mOnCancelListener.onCancel();
                }
            }
        });

        measureView(mHeader);
        addHeaderView(mHeader);

        mHeaderHeight = mHeader.getMeasuredHeight();
        mHeader.setPadding(0, -1 * mHeaderHeight, 0, 0);
        mHeader.invalidate();

        mHtvTitle.setText("下拉刷新");
        SimpleDateFormat format = new SimpleDateFormat(BaseApplication.FORMATTIMESTR);
        String date = format.format(new Date());
        mHtvTime.setText("最后刷新: " + date);

        mPullAnimation = new android.view.animation.RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mPullAnimation.setInterpolator(new LinearInterpolator());
        mPullAnimation.setDuration(250);
        mPullAnimation.setFillAfter(true);

        mReverseAnimation = new android.view.animation.RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseAnimation.setInterpolator(new LinearInterpolator());
        mReverseAnimation.setDuration(200);
        mReverseAnimation.setFillAfter(true);

        mLoadingAnimation = AnimationUtils.loadAnimation(mContext,
                R.anim.loading);

        mState = DONE;
        mIsRefreshable = false;
    }

    @Override
    public void onDown(MotionEvent ev) {
        if (mIsRefreshable) {
            if (mFirstVisibleItem == 0 && !mIsRecored) {
                mIsRecored = true;
                mStartY = mDownPoint.y;
            }
        }
    }

    @Override
    public void onMove(MotionEvent ev) {
        if (mIsRefreshable) {
            if (!mIsRecored && mFirstVisibleItem == 0) {
                mIsRecored = true;
                mStartY = mMovePoint.y;
            }
            if (mState != REFRESHING && mIsRecored && mState != LOADING) {
                if (mState == RELEASE_TO_REFRESH) {
                    setSelection(0);
                    if (((mMovePoint.y - mStartY) / RATIO < mHeaderHeight)
                            && (mMovePoint.y - mStartY) > 0) {
                        mState = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                    } else if (mMovePoint.y - mStartY <= 0) {
                        mState = DONE;
                        changeHeaderViewByState();
                    }
                }
                if (mState == PULL_TO_REFRESH) {
                    setSelection(0);
                    if ((mMovePoint.y - mStartY) / RATIO >= mHeaderHeight) {
                        mState = RELEASE_TO_REFRESH;
                        mIsBack = true;
                        changeHeaderViewByState();
                    } else if (mMovePoint.y - mStartY <= 0) {
                        mState = DONE;
                        changeHeaderViewByState();
                    }
                }
                if (mState == DONE) {
                    if (mMovePoint.y - mStartY > 0) {
                        mState = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                    }
                }
                if (mState == PULL_TO_REFRESH) {
                    mHeader.setPadding(0, -1 * mHeaderHeight
                            + (mMovePoint.y - mStartY) / RATIO, 0, 0);
                }
                if (mState == RELEASE_TO_REFRESH) {
                    mHeader.setPadding(0, (mMovePoint.y - mStartY) / RATIO
                            - mHeaderHeight, 0, 0);
                }

            }

        }
    }

    @Override
    public void onUp(MotionEvent ev) {
        if (mState != REFRESHING && mState != LOADING) {
            if (mState == PULL_TO_REFRESH) {
                mState = DONE;
                changeHeaderViewByState();
            }
            if (mState == RELEASE_TO_REFRESH) {
                mState = REFRESHING;
                changeHeaderViewByState();
                onRefresh();

            }
        }
        mIsRecored = false;
        mIsBack = false;
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private void changeHeaderViewByState() {
        switch (mState) {
            case RELEASE_TO_REFRESH:
                mIvArrow.setVisibility(View.VISIBLE);
                mIvLoading.setVisibility(View.GONE);
                mHtvTitle.setVisibility(View.VISIBLE);
                mHtvTime.setVisibility(View.VISIBLE);
                mIvCancel.setVisibility(View.GONE);
                mIvArrow.clearAnimation();
                mIvArrow.startAnimation(mPullAnimation);
                mIvLoading.clearAnimation();
                mHtvTitle.setText("松开刷新");
                break;
            case PULL_TO_REFRESH:
                mIvArrow.setVisibility(View.VISIBLE);
                mIvLoading.setVisibility(View.GONE);
                mHtvTitle.setVisibility(View.VISIBLE);
                mHtvTime.setVisibility(View.VISIBLE);
                mIvCancel.setVisibility(View.GONE);
                mIvLoading.clearAnimation();
                mIvArrow.clearAnimation();
                if (mIsBack) {
                    mIsBack = false;
                    mIvArrow.clearAnimation();
                    mIvArrow.startAnimation(mReverseAnimation);
                    mHtvTitle.setText("下拉刷新");
                } else {
                    mHtvTitle.setText("下拉刷新");
                }
                break;

            case REFRESHING:
                mHeader.setPadding(0, 0, 0, 0);
                mIvLoading.setVisibility(View.VISIBLE);
                mIvArrow.setVisibility(View.GONE);
                mIvLoading.clearAnimation();
                mIvLoading.startAnimation(mLoadingAnimation);
                mIvArrow.clearAnimation();
                mHtvTitle.setText("正在刷新...");
                mHtvTime.setVisibility(View.VISIBLE);
                if (mIsCancelable) {
                    mIvCancel.setVisibility(View.VISIBLE);
                } else {
                    mIvCancel.setVisibility(View.GONE);
                }

                break;
            case DONE:
                mHeader.setPadding(0, -1 * mHeaderHeight, 0, 0);

                mIvLoading.setVisibility(View.GONE);
                mIvArrow.clearAnimation();
                mIvLoading.clearAnimation();
                mIvArrow.setImageResource(R.drawable.ic_common_droparrow);
                mHtvTitle.setText("下拉刷新");
                mHtvTime.setVisibility(View.VISIBLE);
                mIvCancel.setVisibility(View.GONE);
                break;
        }
    }

    /** 下拉刷新 **/
    public void onRefreshComplete() {
        mState = DONE;
        SimpleDateFormat format = new SimpleDateFormat(BaseApplication.FORMATTIMESTR);
        String date = format.format(new Date());
        mHtvTime.setText("最后刷新: " + date);
        changeHeaderViewByState();
    }

    private void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    public void onManualRefresh() {
        if (mIsRefreshable) {
            mState = REFRESHING; 
            changeHeaderViewByState(); // 下拉刷新 - 正在刷新
            onRefresh();
        }
    }

    public void setOnRefreshListener(OnRefreshListener l) {
        mOnRefreshListener = l;
        mIsRefreshable = true;
    }

    public void setOnCancelListener(OnCancelListener l) {
        mOnCancelListener = l;
        mIsCancelable = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public interface OnCancelListener {
        public void onCancel();
    }
}
