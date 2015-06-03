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
public class FriendMessage extends App42Message implements IMessageProps {

	/**
	 * @param messageJson
	 * @return
	 * @throws JSONException
	 */
	public FriendMessage(JSONObject messageJson) throws JSONException {
		super(messageJson.getString(MessageEvent), messageJson
				.getString(SenderId), messageJson.getString(Message), messageJson
				.getString(MsgType), messageJson.getLong(MsgTime));
	}
	
	/**
	 * @param messageJson
	 * @return
	 * @throws JSONException
	 */
	public FriendMessage(String senderId){
		super(EventType.FriendMessage.toString(),senderId, "Hello ru there", MessageType.Text.toString(),Utility.getTime());
	}
	public FriendMessage(String senderId,String message,String msgType,long time){
		super(senderId,message, msgType, time);
	}
	/**
	 * @param messageJson
	 * @return
	 * @throws JSONException
	 */
	public FriendMessage(String senderId,String message,MessageType msgType){
		super(EventType.FriendMessage.toString(),senderId, message, msgType.toString(),Utility.getTime());
	}
	/**
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getMessageJson() throws JSONException {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put(MessageEvent, this.eventType);
		jsonMessage.put(SenderId, this.senderId);
		jsonMessage.put(MsgType, this.messageType.toString());
		jsonMessage.put(Message, this.message);
		jsonMessage.put(MsgTime, this.sendingTime);
		return jsonMessage;
	}
}
