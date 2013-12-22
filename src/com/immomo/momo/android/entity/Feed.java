package com.immomo.momo.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Feed extends Entity implements Parcelable {
	public static final String TIME = "time";
	public static final String CONTENT = "content";
	public static final String CONTENT_IMAGE = "content_image";
	public static final String SITE = "site";
	public static final String COMMENT_COUNT = "comment_count";
	private String time;
	private String content;
	private String contentImage;
	private String site;
	private int commentCount;

	public Feed() {
		super();
	}

	public Feed(String time, String content, String contentImage, String site,
			int commentCount) {
		super();
		this.time = time;
		this.content = content;
		this.contentImage = contentImage;
		this.site = site;
		this.commentCount = commentCount;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentImage() {
		return contentImage;
	}

	public void setContentImage(String contentImage) {
		this.contentImage = contentImage;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(time);
		dest.writeString(content);
		dest.writeString(contentImage);
		dest.writeString(site);
		dest.writeInt(commentCount);
	}

	public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {

		@Override
		public Feed createFromParcel(Parcel source) {
			Feed feed = new Feed();
			feed.setTime(source.readString());
			feed.setContent(source.readString());
			feed.setContentImage(source.readString());
			feed.setSite(source.readString());
			feed.setCommentCount(source.readInt());
			return feed;
		}

		@Override
		public Feed[] newArray(int size) {
			return new Feed[size];
		}
	};
}
