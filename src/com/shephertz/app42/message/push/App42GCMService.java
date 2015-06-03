/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.app42messanger.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shephertz.app42.message.AppConstants;
import com.shephertz.app42.message.AppPreferences;
import com.shephertz.app42.message.command.EventType;
import com.shephertz.app42.message.db.DBHelper;
import com.shephertz.app42.message.model.App42Message;
import com.shephertz.app42.message.model.FriendMessage;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.message.model.IMessageProps;
import com.shephertz.app42.message.ui.HomeActivity;
import com.shephertz.app42.paas.sdk.android.App42Log;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 * 
 * @author Vishnu Garg
 */

public class App42GCMService extends IntentService implements IMessageProps {
	private static final int NotificationId = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static final String ExtraMessage = "message";
	static int msgCount = 0;
	public static final String DisplayMessageAction = "com.shephertz.app42.message.push.DisplayMessage";

	public App42GCMService() {
		super("GcmIntentService");
	}

	public static final String TAG = "App42 Push Demo";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				App42Log.debug("Send error: " + extras.toString());
				App42GCMReceiver.completeWakefulIntent(intent);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				App42Log.debug("Deleted messages on server: "
						+ extras.toString());
				App42GCMReceiver.completeWakefulIntent(intent);
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				String message = intent.getExtras().getString("message");
				App42Log.debug("Received: " + extras.toString());
				App42Log.debug("Message: " + message);
				showNotification(message, intent);
			}
		}
	}

	private void showNotification(String message, Intent intent) {
		try {
			JSONObject messageJson = getMessageJson(message);
			App42Message appMessage = null;
			String title = "";
			String senderName = DBHelper.instance(this).getDisplayNameByUserId(
					messageJson.optString(SenderId));
			if (messageJson.optString(MessageEvent).equals(
					EventType.FriendMessage.toString())) {
				appMessage = new FriendMessage(messageJson);
				title = senderName;
				DBHelper.instance(this).insertFriendMessage(
						(FriendMessage) appMessage);
			} else if (messageJson.optString(MessageEvent).equals(
					EventType.GroupMessage.toString())) {
				GroupMessage grpMessage = new GroupMessage(messageJson);
				String groupName = grpMessage.getGroupName();
				grpMessage.setSenderName(senderName);
				title = groupName + "   " + senderName;
				String senderId = messageJson.optString(SenderId);
				String myUserId = AppPreferences.instance(this).getUserId();
				if (!senderId.equals(myUserId)) {
					appMessage = grpMessage;
					DBHelper.instance(this).insertGroupMessage(
							(GroupMessage) appMessage);
				}
			}
			if (appMessage != null) {
				broadCastMessage(message);
				sendNotification(appMessage, title);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		App42GCMReceiver.completeWakefulIntent(intent);
	}

	private JSONObject getMessageJson(String message) throws JSONException {

		return new JSONObject(message);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	/**
	 * @param msg
	 */
	private void sendNotification(App42Message msg, String title) {

		long when = System.currentTimeMillis();
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent;
		notificationIntent = new Intent(this, HomeActivity.class);
		notificationIntent.putExtra("message_delivered", true);
		if (msg.getEventType() == EventType.FriendMessage)
			notificationIntent.putExtra(AppConstants.IsGroup, false);
		else
			notificationIntent.putExtra(AppConstants.IsGroup, true);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(msg
								.getMessage()))
				.setContentText(msg.getMessage()).setWhen(when)
				.setNumber(++msgCount).setLights(Color.YELLOW, 1, 2)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
				.setDefaults(Notification.DEFAULT_VIBRATE);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NotificationId, mBuilder.build());
	}

	/**
     * 
     */
	public static void resetMsgCount() {
		msgCount = 0;
	}

	/**
	 * @param message
	 */
	public void broadCastMessage(String message) {
		Intent intent = new Intent(DisplayMessageAction);
		intent.putExtra(ExtraMessage, message);
		this.sendBroadcast(intent);
	}

	/**
	 * @return
	 */
	private String getActivityName() {
		ApplicationInfo ai;
		try {
			ai = this.getPackageManager().getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA);
			Bundle aBundle = ai.metaData;
			return aBundle.getString("onMessageOpen");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "MainActivity";
		}
	}

}
