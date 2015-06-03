/**
 * 
 */
package com.shephertz.app42.message.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.message.App42ChatApplication;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.listener.App42CallBackListener;
import com.shephertz.app42.message.model.FriendMessage;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.push.PushNotification;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.Storage.JSONDocument;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.android.upload.UploadService;

/**
 * @author Vishnu
 * 
 */
public class App42MessangerService {

//	public static ArrayList<JSONDocument> loadApp42Contacts(
//			ArrayList<String> UserContacts) {
//		Query compound = null;
//		for (int i = 1; i < UserContacts.size(); i++) {
//			if (i == 1)
//				compound = QueryBuilder.build("userId", UserContacts.get(0),
//						Operator.EQUALS);
//			Query query = QueryBuilder.build("userId", UserContacts.get(i),
//					Operator.EQUALS);
//			compound = QueryBuilder.compoundOperator(compound, Operator.OR,
//					query);
//		}
//		Storage storage = new StorageService().findDocumentsByQuery(
//				AppConstants.DBName, AppConstants.CollectionName, compound);
//		return storage.getJsonDocList();
//	}

	public static ArrayList<JSONDocument> loadApp42Contacts(
			JSONArray UserContacts) {
		Query q1 = QueryBuilder.build("userId", UserContacts, Operator.INLIST);
		Storage storageObj1 = new StorageService().findDocumentsByQuery(
				AppConstants.DBName, AppConstants.CollectionName, q1);
		System.out.println(storageObj1);
		return storageObj1.getJsonDocList();
	}

	public static void updateUserProfile(final String profileIconUri,
			final String userStatus, final String userId) {
		try {
			JSONObject jsonData = new JSONObject();
			jsonData.put(AppConstants.Status, userStatus);
			jsonData.put(AppConstants.PicUri, profileIconUri);
			jsonData.put(AppConstants.UserId, userId);
			new StorageService().updateDocumentByKeyValue(AppConstants.DBName,
					AppConstants.CollectionName, AppConstants.UserId, userId,
					jsonData, new App42CallBack() {
						@Override
						public void onSuccess(Object arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onException(Exception arg0) {
							// TODO Auto-generated method stub
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createUserProfile(final String profileIconUri,
			final String userStatus, final String userId) {
		try {
			JSONObject jsonData = new JSONObject();
			jsonData.put(AppConstants.Status, userStatus);
			jsonData.put(AppConstants.PicUri, profileIconUri);
			jsonData.put(AppConstants.UserId, userId);
			new StorageService().insertJSONDocument(AppConstants.DBName,
					AppConstants.CollectionName, jsonData, new App42CallBack() {
						@Override
						public void onSuccess(Object arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onException(Exception arg0) {
							// TODO Auto-generated method stub
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendImageMessageToFriend(final String friendId,
			final String localImagePath, final FriendMessage friednMessage) {
		new Thread() {
			@Override
			public void run() {
				try {
					Upload uploadResult = new UploadService().uploadFile(
							"message" + new Date().getTime()
									+ new Random().nextInt(), localImagePath,
							UploadFileType.IMAGE, "Imagesx");
					String webImageUrl = uploadResult.getFileList().get(0)
							.getUrl();
					friednMessage.setMessage(webImageUrl);
					new PushNotificationService()
							.sendPushMessageToUser(friendId, friednMessage
									.getMessageJson().toString());
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void sendImageMessageToGroup(final String groupId,
			final String webImageUrl, final GroupMessage groupMessage) {
		new Thread() {
			@Override
			public void run() {
				try {
					new PushNotificationService().sendPushMessageToChannel(
							groupId, groupMessage.getMessageJson().toString());
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void exitFromGroup(String userName, String channelName,
			final App42CallBackListener callback) {
		new PushNotificationService().unsubscribeFromChannel(channelName,
				userName, new App42CallBack() {

					@Override
					public void onSuccess(Object arg0) {
						// TODO Auto-generated method stub
						callback.onUnsubscribedFromChannel();
					}

					@Override
					public void onException(Exception arg0) {
						// TODO Auto-generated method stub
						callback.onCallBackException(arg0.getMessage());
					}
				});
	}

	public static void uploadImage(String localImagePath,
			final App42CallBackListener callback) {
		new UploadService().uploadFile("icon" + new Date().getTime()
				+ new Random().nextInt(), localImagePath, UploadFileType.IMAGE,
				"Imagesx", new App42CallBack() {
					@Override
					public void onSuccess(Object arg0) {
						// TODO Auto-generated method stub
						String webImageUrl = ((Upload) arg0).getFileList()
								.get(0).getUrl();
						callback.onImageUploadSuccess(webImageUrl);
					}

					@Override
					public void onException(Exception arg0) {
						// TODO Auto-generated method stub
						callback.onCallBackException(arg0.getMessage());
					}
				});
	}

	public static void getGroupUsers(String groupId,
			final App42CallBackListener callback) {
		new PushNotificationService().getChannelUsers(groupId, 20, 0,
				new App42CallBack() {

					@Override
					public void onSuccess(Object response) {
						// TODO Auto-generated method stub
						callback.onGroupUsersFetched((ArrayList<PushNotification>) response);
					}

					@Override
					public void onException(Exception arg0) {
						// TODO Auto-generated method stub
						callback.onCallBackException(arg0.getMessage());
					}
				});
	}

	public static void updateFriendProfile(final String localImagePath,
			final String userStatus, final String userId) {
		new Thread() {
			@Override
			public void run() {
				try {
					JSONObject jsonData = new JSONObject();
					jsonData.put(AppConstants.Status, userStatus);
					jsonData.put(AppConstants.PicUri, localImagePath);
					jsonData.put(AppConstants.UserId, userId);
					new StorageService().updateDocumentByKeyValue(
							AppConstants.DBName, AppConstants.CollectionName,
							AppConstants.UserId, userId, jsonData);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

}
