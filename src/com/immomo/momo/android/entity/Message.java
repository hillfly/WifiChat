package com.immomo.momo.android.entity;

public class Message extends Entity {

	private String avatar;
	private long time;
	private String distance;
	private String content;

	private CONTENT_TYPE contentType;
	private MESSAGE_TYPE messageType;

	public Message(String avatar, long time, String distance, String content,
			CONTENT_TYPE contentType, MESSAGE_TYPE messageType) {
		super();
		this.avatar = avatar;
		this.time = time;
		this.distance = distance;
		this.content = content;
		this.contentType = contentType;
		this.messageType = messageType;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public CONTENT_TYPE getContentType() {
		return contentType;
	}

	public void setContentType(CONTENT_TYPE contentType) {
		this.contentType = contentType;
	}

	public MESSAGE_TYPE getMessageType() {
		return messageType;
	}

	public void setMessageType(MESSAGE_TYPE messageType) {
		this.messageType = messageType;
	}

	public enum CONTENT_TYPE {
		TEXT, IMAGE, MAP, VOICE;
	}

	public enum MESSAGE_TYPE {
		RECEIVER, SEND;
	}
}
