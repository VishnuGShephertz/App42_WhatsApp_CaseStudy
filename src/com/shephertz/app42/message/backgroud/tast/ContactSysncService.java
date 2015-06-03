/**
 * -----------------------------------------------------------------------
 *     Copyright © 2012 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.backgroud.tast;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.tools.ContactManager;
import com.shephertz.app42.paas.sdk.android.App42API;

/**
 * @author Vishnu
 * 
 */
public class ContactSysncService extends WakefulIntentService  {
	public final int TypeWifi = 1;
	public final int TypeMobile = 2;
	public final int TypeNotConnected = 0;
	public ContactSysncService() {
		super("ContactSysncService");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.example.keepalivetask.WakefulIntentService#doWakefulWork(android.
	 * content.Intent)
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		int status = getConnectivityStatus(this);
		if (status == TypeNotConnected)
			return;
		App42API.initialize(this, AppConstants.ApiKey, AppConstants.SecretKey);
		new ContactManager(ContactSysncService.this).refreshContacts();
		
	}

	/**
	 * This function checks connectivityStatus If connectivity Changes
	 * 
	 * @param context
	 * @return
	 */
	public int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TypeWifi;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TypeMobile;
		}
		return TypeNotConnected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.ContactListener#onContactFetched
	 * (java.util.HashMap)
	 */
//	@Override
//	public void onContactFetched(final HashMap<String, String> phoneContactMap) {
//		Logger.i(phoneContactMap.toString());
////		contactMap = phoneContactMap;
////		App42MessangerService.loadApp42Contacts(Utility.getKeyList(contactMap),
////				this);
//	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.ContactListener#onGetApp42Contacts
	 * (java.util.ArrayList)
	 */
//	@Override
//	public void onGetApp42Contacts(ArrayList<JSONDocument> jsonDocList) {
//		// TODO Auto-generated method stub
//		final HashMap<String, AppUser> appUsersMap = new HashMap<String, AppUser>();
//		final ArrayList<AppUser> myUserList = new ArrayList<AppUser>();
//		for (JSONDocument userJsonDoc : jsonDocList) {
//			try {
//				JSONObject jsonData = new JSONObject(userJsonDoc.jsonDoc);
//				Logger.i(jsonData);
//				String userId = jsonData.getString(AppConstants.UserId);
//				if (userId != null) {
//					String name = contactMap.get(userId);
//					if (name != null) {
//						AppUser appUser = new AppUser(userId, name,
//								jsonData.optString(AppConstants.PicUri, ""),
//								jsonData.optString(AppConstants.Status, ""));
//						appUsersMap.put(userId, appUser);
//						myUserList.add(appUser);
//					}
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		DBHelper.instance(ContactSysncService.this).insertAndUpdateUsers(appUsersMap);
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.shephertz.app42.message.listener.ContactListener#onErrorInFetching
	 * (com.shephertz.app42.paas.sdk.android.App42Exception)
	 */
//	@Override
//	public void onErrorInFetching(final Exception ex) {
//		Logger.e(ex.getMessage());
//	}

}
