package com.immomo.momo.android.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageMapUtils {
	public static ImageMapUtils mImageMapUtils;
	private Executor mExecutor;
	private OnMapDownloadStateListener mOnMapDownloadStateListener;
	private Bitmap mMapImage;

	private ImageMapUtils(Context context) {
		mExecutor = Executors.newFixedThreadPool(5);
	}

	public static ImageMapUtils create(Context context) {
		if (mImageMapUtils == null) {
			mImageMapUtils = new ImageMapUtils(context);
		}
		return mImageMapUtils;
	}

	public void display(final String uri) {
		mMapImage = null;
		if (mOnMapDownloadStateListener != null) {
			mOnMapDownloadStateListener.onStart();
		}
		mExecutor.execute(new Runnable() {

			@Override
			public void run() {
				InputStream is = null;
				try {
					URL url = new URL(uri);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					is = connection.getInputStream();
					mMapImage = BitmapFactory.decodeStream(is);
					String path = PhotoUtils.savePhotoToSDCard(mMapImage);
					if (mOnMapDownloadStateListener != null) {
						mOnMapDownloadStateListener.onFinish(path);
					}
				} catch (Exception e) {
					if (mOnMapDownloadStateListener != null) {
						mOnMapDownloadStateListener.onFinish(null);
					}
				}
			}
		});
	}

	public void display(double longitude, double latitude) {
		String uri = getMapUrl(longitude, latitude);
		display(uri);
	}

	public String getMapUrl(double longitude, double latitude) {
		return "http://maps.google.com/maps/api/staticmap?zoom=13&size=256x128&markers="
				+ latitude
				+ ","
				+ longitude
				+ "&maptype=roadmap&language=zh-CN&sensor=false";
	}

	public void setOnMapDownloadStateListener(
			OnMapDownloadStateListener listener) {
		mOnMapDownloadStateListener = listener;
	}

	public interface OnMapDownloadStateListener {
		void onStart();

		void onFinish(String path);
	}
}
