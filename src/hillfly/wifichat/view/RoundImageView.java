package hillfly.wifichat.view;

import hillfly.wifichat.util.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {

	public RoundImageView(Context context) {
		super(context);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		bm = ImageUtils.toRoundCorner(bm, 3);
		super.setImageBitmap(bm);
	}

	public void setImageBitmap(Bitmap bm, int pixels) {
		bm = ImageUtils.toRoundCorner(bm, pixels);
		super.setImageBitmap(bm);
	}
}
