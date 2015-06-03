/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.push;

/**
 * @author Vishnu Garg
 */
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.util.Logger;

/**
 * @author Vishnu Garg
 * 
 */
public class App42GCMManager {
	private static final int PlayServiceResolutionRequest = 9000;
	public static final String KeyRegId = "registration_id";
	private static final String KeyAppVersion = "appVersion";
	private static final String PrefKey = "App42PushSample";
	private static final String KeyRegisteredOnApp42 = "app42_register";
	private static final String KeyUserId="app42UserId";

	/**
	 * This function checks for GooglePlay Service availability
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isPlayServiceAvailable(Activity activity) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PlayServiceResolutionRequest).show();
			} else {
				Logger.i("This device is not supported.");
			}
			return false;
		}
		return true;
	}

	/**
	 * This function used to get GCM Registration Id from New API
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(KeyRegId, "");
		if (registrationId.isEmpty()) {
			Logger.i("Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(KeyAppVersion, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Logger.i("App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @param context
	 * @return
	 */
	private static SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences(PrefKey, Context.MODE_PRIVATE);
	}

	/**
	 * Get AppVersion
	 * 
	 * @param context
	 * @return
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Store registartion Id from GCM in preferences
	 * 
	 * @param context
	 * @param regId
	 */
	public static void storeRegistrationId(Context context, String regId) {
		
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Logger.i("Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(KeyRegId, regId);
		editor.putInt(KeyAppVersion, appVersion);
		editor.commit();
	}

	/**
	 * Validate if registered
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isApp42Registerd(Context context) {
		//return false;
		return getGCMPreferences(context).getBoolean(KeyRegisteredOnApp42,
				false);
	}
	
	/**
	 * Validate if registered
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getUserID(Context context) {
		return getGCMPreferences(context).getBoolean(KeyRegisteredOnApp42,
				false);
	}

	/**
	 * @param context
	 * @param regId
	 */
	public static void storeApp42Success(String userId) {
		Context context=App42ChatApplication.getContext();
		if(context==null)
			return;
		final SharedPreferences prefs = getGCMPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KeyRegisteredOnApp42, true);
		editor.putString(KeyUserId, userId);
		editor.commit();
	}

	/**
	 * @throws InterruptedException
	 */
	@SuppressLint("NewApi")
	public static void registerOnGCM() {
		String registrationId = getRegistrationId(App42ChatApplication
				.getContext());
		if (registrationId.isEmpty()) {
			new Thread() {
				@Override
				public void run() {
					int retry = 0;
					while (retry < 3) {
						try {
							GoogleCloudMessaging gcm = GoogleCloudMessaging
									.getInstance(App42ChatApplication
											.getContext());
							String gcmId = gcm.register(AppConstants.ProjectNo);
							storeRegistrationId(
									App42ChatApplication.getContext(), gcmId);
							break;
						} catch (IOException ignore) {
							try {
								sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							retry++;
							Logger.w("failed to register GCM. retry " + retry
									+ " times  reason=" + ignore.getMessage());
						}
					}
				}
			}.start();
		}
	}

	/**
	 * Used to register device on GCM
	 * 
	 * @param context
	 * @param googleProjectNo
	 * @param gcm
	 * @param callback
	 */
	public static void registeronGCM(final Context context,
			final String googleProjectNo, final GoogleCloudMessaging gcm) {

	}
}
