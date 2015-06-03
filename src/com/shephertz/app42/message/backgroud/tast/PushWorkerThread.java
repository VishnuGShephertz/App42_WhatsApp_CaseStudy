/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.backgroud.tast;

import com.shephertz.app42.message.command.PushCommand;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;

/**
 * @author Vishnu
 * 
 */
public class PushWorkerThread implements Runnable {
	private PushCommand command;
	private String userId;
	private String groupName;
	private String message;

	public PushWorkerThread(PushCommand command) {
		this.command = command;
	}

	public PushWorkerThread(PushCommand command, String userId, String groupName) {
		this.command = command;
		this.groupName = groupName;
		this.userId = userId;
	}

	public PushWorkerThread(String message, String groupName, PushCommand command) {
		this.command = command;
		this.groupName = groupName;
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (command == PushCommand.SubscribeChannel)
			subscribeDevice();
		else if (command == PushCommand.PushToChannel)
			sendMessageToChannel();
	}

	private void subscribeDevice() {
		new PushNotificationService().subscribeToChannel(
				this.groupName, this.userId);
	}
	

	private void sendMessageToChannel() {
		new PushNotificationService().sendPushMessageToChannel(
				this.groupName, message);
	}
}
