/**
 * 
 */
package com.shephertz.app42.message.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Vishnu
 * 
 */
public class Group implements IGroup, Parcelable {

	public Group(String groupId, String groupName, String message,
			String senderName, long lastCommTime, String picUri) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.picUri = picUri;
		this.senderName = senderName;
		this.message = message;
		this.lastCommTime = lastCommTime;
	}

	private String groupId;
	private String groupName;
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	private String picUri;
	public void setPicUri(String picUri) {
		this.picUri = picUri;
	}

	private String senderName;
	private String message;
	private long lastCommTime;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IGroup#getGroupId()
	 */
	@Override
	public String getGroupId() {
		// TODO Auto-generated method stub
		return this.groupId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IGroup#getName()
	 */
	@Override
	public String getGroupName() {
		// TODO Auto-generated method stub
		return this.groupName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IGroup#getPicUri()
	 */
	@Override
	public String getPicUri() {
		// TODO Auto-generated method stub
		return this.picUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IGroup#getSenderName()
	 */
	@Override
	public String getSenderName() {
		// TODO Auto-generated method stub
		return this.senderName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IGroup#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		if (this.message == null)
			return "";
		return this.message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IGroup#getLastCommTime()
	 */
	@Override
	public long getLastCommTime() {
		// TODO Auto-generated method stub
		return this.lastCommTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(groupId);
		dest.writeString(groupName);
		dest.writeString(picUri);
		dest.writeString(senderName);
		dest.writeString(message);
		dest.writeLong(lastCommTime);
	}

	/**
	 * @param source
	 */
	public Group(Parcel source) {
		groupId = source.readString();
		groupName = source.readString();
		picUri = source.readString();
		senderName = source.readString();
		message = source.readString();
		lastCommTime = source.readLong();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Group createFromParcel(Parcel in) {
			return new Group(in);
		}

		public Group[] newArray(int size) {
			return new Group[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

}
