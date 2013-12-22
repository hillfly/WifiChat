package com.immomo.momo.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.immomo.momo.android.BaseApplication;
import com.immomo.momo.android.activity.ImageBrowserActivity;
import com.immomo.momo.android.view.photoview.PhotoView;

public class ImageBrowserAdapter extends PagerAdapter {

	private BaseApplication mApplication;
	private List<String> mPhotos = new ArrayList<String>();
	private String mType;

	public ImageBrowserAdapter(BaseApplication application,
			List<String> photos, String type) {
		mApplication = application;
		if (photos != null) {
			mPhotos = photos;
		}
		mType = type;
	}

	@Override
	public int getCount() {
		if (mPhotos.size() > 1) {
			return Integer.MAX_VALUE;
		}
		return mPhotos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		PhotoView photoView = new PhotoView(container.getContext());
		Bitmap bitmap = null;
		if (ImageBrowserActivity.TYPE_ALBUM.equals(mType)) {
			bitmap = mApplication.getPhotoOriginal(mPhotos.get(position
					% mPhotos.size()));
		} else if (ImageBrowserActivity.TYPE_PHOTO.equals(mType)) {
			bitmap = BitmapFactory.decodeFile(mPhotos.get(position));
		}
		photoView.setImageBitmap(bitmap);
		container.addView(photoView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
