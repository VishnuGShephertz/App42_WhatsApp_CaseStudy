/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 * 
 */
public class AppUser implements IUser, Parcelable {

	private String userId;
	private String name;
	private String picUri;
	private String status;
	private String message;
	private long lastCommTime;

	/**
	 * @param lastCommTime
	 */
	public void setLastCommTime(long lastCommTime) {
		this.lastCommTime = lastCommTime;
	}

	/**
	 * @param userId
	 * @param name
	 * @param picUri
	 * @param status
	 */
	public AppUser(String userId, String name, String picUri, String status) {
		super();
		this.userId = userId;
		this.name = name;
		this.picUri = picUri;
		this.status = status;
		System.out.println(userId + "--" + name + "---" + picUri + "--" + "--"
				+ status);
	}

	/**
	 * @param userId
	 * @param name
	 * @param picUri
	 * @param status
	 */
	public AppUser(String userId, String name, String message,
			long lastCommTime, String picUri) {
		super();
		this.userId = userId;
		this.name = name;
		this.picUri = picUri;
		this.message = message;
		this.lastCommTime = lastCommTime;
		System.out.println(userId + "--" + name + "---" + picUri + "--" + "--"
				+ message + "--" + lastCommTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.chat.model.IUser#getUserId()
	 */
	@Override
	public String getUserId() {
		// TODO Auto-generated method stub
		return this.userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.chat.model.IUser#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.chat.model.IUser#getPicUri()
	 */
	@Override
	public String getPicUri() {
		// TODO Auto-generated method stub
		return this.picUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IUser#getStatus()
	 */
	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IUser#getLastCommTime()
	 */
	@Override
	public long getLastCommTime() {
		// TODO Auto-generated method stub
		if (lastCommTime == 0)
			return Utility.getTime();
		return this.lastCommTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shephertz.app42.message.model.IUser#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		if (this.message == null)
			return "";
		return this.message;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(userId);
		dest.writeString(name);
		dest.writeString(picUri);
		dest.writeString(status);
		dest.writeString(message);
		dest.writeLong(lastCommTime);
	}

	/**
	 * @param source
	 */
	public AppUser(Parcel source) {
		userId = source.readString();
		name = source.readString();
		picUri = source.readString();
		status = source.readString();
		message = source.readString();
		lastCommTime = source.readLong();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public AppUser createFromParcel(Parcel in) {
			return new AppUser(in);
		}
		public AppUser[] newArray(int size) {
			return new AppUser[size];
		}
	};

}
