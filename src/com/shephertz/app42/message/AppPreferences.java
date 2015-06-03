/**
 * -----------------------------------------------------------------------
 *     Copyright  2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message;

import android.content.Context;
import android.content.SharedPreferences;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.util.CorePreference;

/**
 * @author Vishnu
 *
 */
public class AppPreferences {
	private static AppPreferences mInstance = null;
	private SharedPreferences mAppPreferneces;
	private final String ProfileUri="profile_uri";
	private final String ProfileName="profileName";
	private final String ProfileStatus="profile_status";
	private final String DefaultMsg="Hey I am using App42Messanger";
	private final String UserId="userId";
	private final String NotAvailable="NA";

	private AppPreferences(Context context) {
			mAppPreferneces = App42API.appContext.getSharedPreferences(
					CorePreference.PrefName, android.content.Context.MODE_PRIVATE);
	}

	/**
	 * @return
	 */
	public static AppPreferences instance(Context context) {
		if (mInstance == null) {
			mInstance = new AppPreferences(context);
		}
		return mInstance;
	}

	/** 
	 * Update profile in local preferences
	 * @param localImagePath
	 * @param status
	 */
	public void updateProfile(String localImagePath,String status,String name){
		SharedPreferences.Editor localEditor = mAppPreferneces.edit();
		localEditor.putString(ProfileUri, localImagePath);
		localEditor.putString(ProfileStatus, status);
		localEditor.putString(ProfileName, name);
		localEditor.commit();
	}
	/**
	 * Save userId when user registered first Time
	 * @param localImagePath
	 * @param status
	 */
	public void saveUserId(String userId){
		SharedPreferences.Editor localEditor = mAppPreferneces.edit();
		localEditor.putString(UserId, userId);
		localEditor.commit();
	}
	/**
	 * @return user profile status
	 */
	public String getProfileStatus(){
		return mAppPreferneces.getString(ProfileStatus,DefaultMsg);
	}
	/**
	 * @return userId
	 */
	public String getUserId(){
		return mAppPreferneces.getString(UserId,null);
	}
	/**
	 * @return profileName
	 */
	public String getProfileName(){
		return mAppPreferneces.getString(ProfileName,NotAvailable);
	}
   
	/**
	 * @return pichUri
	 */
	public String getProfileUri(){
		return mAppPreferneces.getString(ProfileUri,DefaultMsg);
	}
}
