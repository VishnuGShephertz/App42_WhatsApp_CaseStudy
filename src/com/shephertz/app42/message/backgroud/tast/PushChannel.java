/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.backgroud.tast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import com.shephertz.app42.message.command.PushCommand;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;

/**
 * @author Vishnu
 *
 */
public class PushChannel {
   private final static int PoolSize=5; 
	
	public static void subcribeDevicesToGroup(final ArrayList<String> userList,final String myUserId,final String groupName){
		new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ExecutorService executor = Executors.newFixedThreadPool(PoolSize);
				//executor.execute(new PushWorkerThread(AppCommand.CreateChannel, myUserId, groupName));
				executor.execute(new PushWorkerThread(PushCommand.SubscribeChannel, myUserId, groupName));
				for(String user:userList){
					executor.execute(new PushWorkerThread(PushCommand.SubscribeChannel, user, groupName));
				}
			}
		};	
		//executor.execute(new PushWorkerThread("", groupName, AppCommand.PushToChannel));
	}
	
	public static void sendMessageToGroup(String message,String groupId){
		new PushNotificationService().sendPushMessageToChannel(groupId, message, new App42CallBack() {
			
			@Override
			public void onSuccess(Object arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static void sendMessageToFriend(String message,String friendId){
		new PushNotificationService().sendPushMessageToUser(friendId, message, new App42CallBack() {
			
			@Override
			public void onSuccess(Object arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
