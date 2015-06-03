/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.model;

import com.shephertz.app42.message.command.EventType;
import com.shephertz.app42.message.command.MessageType;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 *
 */
public class App42Message {
	protected EventType eventType;
	protected MessageType messageType;
	protected long sendingTime;
	protected String senderId;
	protected String message;
	public void setMessage(String message) {
		this.message = message;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	public MessageType getMessageType() {
		return messageType;
	}
	public String getMessage() {
		return message;
	}
	public long getSendingTime() {
		return sendingTime;
	}
	public String getSenderId() {
		return senderId;
	}
	/**
	 * @param eventType
	 * @param senderId
	 * @param message
	 * @param msgType
	 * @param time
	 */
	public App42Message(String eventType,String senderId,
			String message, String msgType, long time) {
		this.senderId=senderId;
		this.message=message;
		this.messageType=MessageType.valueOf(msgType);
		this.sendingTime=time;
		this.eventType=EventType.valueOf(eventType);
	}
	public App42Message(String senderId){
		this.senderId=senderId;
		this.message="";
		this.sendingTime=Utility.getTime();
	}
	public App42Message(String senderId,String message,String msgType,long time){
		this.senderId=senderId;
		this.messageType=MessageType.valueOf(msgType);
		this.message=message;
		this.sendingTime=time;
	}
}
