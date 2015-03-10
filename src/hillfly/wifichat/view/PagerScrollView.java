package hillfly.wifichat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class PagerScrollView extends ScrollView {

	private float mDistanceX;
	private float mDistanceY;
	private float mLastX;
	private float mLastY;

	public PagerScrollView(Context context) {
		super(context);
	}

	public PagerScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PagerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDistanceX = 0;
			mDistanceY = 0;
			mLastX = ev.getX();
			mLastY = ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			mDistanceX += Math.abs(curX - mLastX);
			mDistanceY += Math.abs(curY - mLastY);
			mLastX = curX;
			mLastY = curY;
			if (mDistanceX > mDistanceY) {
				return false;
			}

		}
		return super.onInterceptTouchEvent(ev);
	}
}
