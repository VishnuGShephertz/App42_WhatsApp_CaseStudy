/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.db.DBHelper;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.service.App42MessangerService;
import com.shephertz.app42.message.util.Logger;
import com.shephertz.app42.message.util.Utility;
import com.shephertz.app42.paas.sdk.android.storage.Storage.JSONDocument;

/**
 * @author Vishnu
 * 
 */
public class ContactManager{
	private Context mContext;
	private HashMap<String, String> contactMap;
	
	
	public ContactManager(Context mContextl) {
		super();
		this.mContext = mContextl;
	}

	public  void refreshContacts() {
		new Thread() {
			@Override
			public void run() {
				try {
					 contactMap = getMyPhoneContacts(mContext);
					 ArrayList<JSONDocument> jsonDocList=App42MessangerService.loadApp42Contacts(Utility.getKeyList(contactMap));
			       	 updateMyContacts(jsonDocList);
				} catch (final Exception ex) {
					
				}
			}

		}.start();
	}

	private void updateMyContacts(ArrayList<JSONDocument> jsonDocList){
		final HashMap<String, AppUser> appUsersMap = new HashMap<String, AppUser>();
		final ArrayList<AppUser> myUserList = new ArrayList<AppUser>();
		for (JSONDocument userJsonDoc : jsonDocList) {
			try {
				JSONObject jsonData = new JSONObject(userJsonDoc.jsonDoc);
				Logger.i(jsonData);
				String userId = jsonData.getString(AppConstants.UserId);
				if (userId != null) {
					String name = contactMap.get(userId);
					if (name != null) {
						AppUser appUser = new AppUser(userId, name,
								jsonData.optString(AppConstants.PicUri, ""),
								jsonData.optString(AppConstants.Status, ""));
						appUsersMap.put(userId, appUser);
						myUserList.add(appUser);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DBHelper.instance(mContext).insertAndUpdateUsers(appUsersMap);
	}
	private  LinkedHashMap<String, String> getMyPhoneContacts(
			Context mContext) {
		LinkedHashMap<String, String> contactMap = new LinkedHashMap<String, String>();
		String[] mProjection = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };
		Cursor curPhoneContacts = mContext.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				mProjection, null, null, Phone.DISPLAY_NAME + " ASC");
		curPhoneContacts.moveToFirst();
		do {
			String	name = curPhoneContacts.getString(curPhoneContacts
					.getColumnIndex(Contacts.DISPLAY_NAME));
			String	mobNumber = curPhoneContacts
					.getString(curPhoneContacts
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			String formattedNo = Utility.getFormattedNo(mobNumber);
			contactMap.put(formattedNo, name);
			Logger.i("Name : " + name + "   MobileNo :" + mobNumber);
		} while (curPhoneContacts.moveToNext());
		curPhoneContacts.close();
		return contactMap;
	}

//	 public static void insertData(String phoneNo){
//	 try {
//	 JSONObject json=new JSONObject();
//	 json.put("userId", phoneNo);
//	 json.put("status", "Hey! I am using App42 Messanger");
//	 json.put("picUri", "");
//		new StorageService().insertJSONDocument(AppConstants.DBName,
//	 AppConstants.CollectionName, json,new App42CallBack() {
//	
//	 @Override
//	 public void onSuccess(Object arg0) {
//	 // TODO Auto-generated method stub
//	
//	 }
//	
//	 @Override
//	 public void onException(Exception arg0) {
//	 // TODO Auto-generated method stub
//	
//	 }
//	 });
//	 } catch (JSONException e) {
//	 // TODO Auto-generated catch block
//	 e.printStackTrace();
//	 }
//	
//	 }

}
