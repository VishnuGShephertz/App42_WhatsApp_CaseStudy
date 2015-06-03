/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.message.command.EventType;
import com.shephertz.app42.message.command.MessageType;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 * 
 */

public class GroupMessage extends App42Message implements IMessageProps {
	private String groupId;
    private String groupName;
    private String senderName;
    private String groupIconUri;
	public void setGroupIconUri(String groupIconUri) {
		this.groupIconUri = groupIconUri;
	}
	public String getGroupIconUri() {
		return groupIconUri;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getGroupName() {
		return groupName;
	}
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param notifyData
	 * @return
	 * @throws JSONException
	 */
	public  GroupMessage(JSONObject notifyData)
			throws JSONException {
		super(notifyData.getString(MessageEvent),
				notifyData.getString(SenderId), notifyData.getString(Message),
				notifyData.getString(MsgType), notifyData.getLong(MsgTime));
		this.groupId=notifyData.optString(GroupId);
		this.groupName=notifyData.optString(GroupName);
	}
	
	/**
	 * @param senderId
	 * @param senderName
	 * @param groupId
	 * @param groupName
	 * @param groupIconUri
	 */
	public  GroupMessage(String senderId,String senderName,String groupId,String groupName,String groupIconUri)
		{
		super(senderId);
		this.groupId=groupId;
		this.groupName=groupName;
		this.senderName=senderName;
		this.groupIconUri=groupIconUri;
	} 
	/**
	 * @param senderId
	 * @param senderName
	 * @param groupId
	 * @param groupName
	 * @param groupIconUri
	 */
	public  GroupMessage(String groupId,String groupName,String senderId,String message,MessageType msgType,String senderName)
		{
		super(EventType.GroupMessage.toString(),senderId, message, msgType.toString(),Utility.getTime());
		this.groupId=groupId;
		this.groupName=groupName;
		this.senderName=senderName;
	} 
	/**
	 * @param senderId
	 * @param senderName
	 * @param groupId
	 * @param groupName
	 * @param groupIconUri
	 */
	public  GroupMessage(String groupId,String groupName,String senderId,String message,MessageType msgType,String senderName,String groupIconUri)
		{
		super(EventType.GroupMessage.toString(),senderId, message, msgType.toString(),Utility.getTime());
		this.groupId=groupId;
		this.groupName=groupName;
		this.senderName=senderName;
		this.groupIconUri=groupIconUri;
	} 
	/**
	 * @param message
	 * @param msgType
	 * @param time
	 * @param senderName
	 */
	public  GroupMessage(String message,String msgType,long time,String senderName)
		{
		super("",message, msgType, time);
		this.senderName=senderName;
	}
	/**
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getMessageJson() throws JSONException {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put(MessageEvent, this.eventType.toString());
		jsonMessage.put(SenderId, this.senderId);
		jsonMessage.put(MsgType, this.messageType.toString());
		jsonMessage.put(Message, this.message);
		jsonMessage.put(MsgTime, this.sendingTime);
		jsonMessage.put(GroupId, this.groupId);
		jsonMessage.put(GroupName, this.groupName);
		return jsonMessage;
	}
}
