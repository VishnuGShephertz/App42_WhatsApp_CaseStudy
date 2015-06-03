/**
 * -----------------------------------------------------------------------
 *     Copyright  2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Log;

import android.app.Application;
import android.content.Context;

/**
 * @author Vishnu
 * 
 */
public class App42ChatApplication extends Application {
	private static Context context;
	private static String userId;
    
	/**
	 * @return userId of user
	 */
	public static String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 */
	public static void setUserId(String userId) {
		App42ChatApplication.userId = userId;
	}

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		App42API.initialize(this, AppConstants.ApiKey, AppConstants.SecretKey);
		//For debugging logs
		App42Log.setDebug(true);
	}

	/**
	 * @return Context of application
	 */
	public static Context getContext() {
		return context;
	}
	/**
	 * @param msgId
	 * @return message in String format 
	 */
	public static String getMessage(int msgId) {
		return context.getResources().getString(msgId);
	}
}
