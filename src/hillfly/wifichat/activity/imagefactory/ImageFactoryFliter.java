package hillfly.wifichat.activity.imagefactory;

import hillfly.wifichat.util.ImageUtils;
import hillfly.wifichat.view.RotateImageView;
import hillfly.wifichat.view.RotateImageView.RotateType;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import hillfly.wifichat.R;

public class ImageFactoryFliter extends ImageFactory {

	private RotateImageView mRivImage;

	private String mPath;
	private Bitmap mBitmap;
	private List<FilterItem> mFilterItems;
	private View[] mFliterBlocks;
	private int mSelectBlock = 0;
	private Bitmap mSelectBitmap;

	public ImageFactoryFliter(ImageFactoryActivity activity,
			View contentRootView) {
		super(activity, contentRootView);
	}

	@Override
	public void initViews() {
		mRivImage = (RotateImageView) findViewById(R.id.imagefactory_fliter_riv_image);
	}

	@Override
	public void initEvents() {

	}

	public void Rotate() {
		mSelectBitmap = mRivImage.rotate(RotateType.RIGHT, 90.0f);
	}

	public Bitmap getBitmap() {
		return mSelectBitmap;
	}

	public void init(String path) {
		mPath = path;
		mBitmap = ImageUtils.getBitmapFromPath(mPath);
		if (mBitmap != null) {
			mSelectBitmap = mBitmap;
			mRivImage.setImageBitmap(mBitmap);
			initFilterList();
			initFilterBlocks();
			refreshBlockBg();
		}
	}

	private void initFilterList() {
		mFilterItems = new ArrayList<ImageFactoryFliter.FilterItem>();
		FilterItem filterItem_1 = new FilterItem(FilterType.默认, "默认");
		FilterItem filterItem_2 = new FilterItem(FilterType.LOMO, "LOMO");		
		mFilterItems.add(filterItem_1);
		mFilterItems.add(filterItem_2);		
	}

	private void initFilterBlocks() {
		mFliterBlocks = new View[2];
		mFliterBlocks[0] = findViewById(R.id.imagefactory_fliter_item_1);
		mFliterBlocks[1] = findViewById(R.id.imagefactory_fliter_item_2);	
		int mLength = mFilterItems.size();
		for (int i = 0; i < mLength; i++) {
			View cover = mFliterBlocks[i].findViewById(R.id.filter_item_cover);
			cover.setTag(i);
			cover.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mSelectBlock = (Integer) v.getTag();
					refreshBlockBg();
					changeImage();
				}
			});
			ImageView image = (ImageView) mFliterBlocks[i]
					.findViewById(R.id.filter_item_image);
			TextView text = (TextView) mFliterBlocks[i]
					.findViewById(R.id.filter_item_text);
			image.setImageBitmap(ImageUtils.getFilter(
					mFilterItems.get(i).mFilterType, mBitmap));
			text.setText(mFilterItems.get(i).mFilterName);

		}
	}

	private void refreshBlockBg() {
	    int mLength = mFilterItems.size();
		for (int i = 0; i < mLength; i++) {
			View cover = mFliterBlocks[i].findViewById(R.id.filter_item_cover);
			if (mSelectBlock == i) {
				cover.setSelected(true);
			} else {
				cover.setSelected(false);
			}
		}
	}

	private void changeImage() {
		mSelectBitmap = ImageUtils.getFilter(
				mFilterItems.get(mSelectBlock).mFilterType, mBitmap);
		mRivImage.setImageBitmap(mSelectBitmap);
	}

	public class FilterItem {

		public FilterItem(FilterType mFilterType, String mFilterName) {
			super();
			this.mFilterType = mFilterType;
			this.mFilterName = mFilterName;
		}

		public FilterType mFilterType;
		public String mFilterName;
	}

	public enum FilterType {
		默认, LOMO;
	}
}
