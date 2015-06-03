/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.db;

import android.content.ContentValues;

import com.shephertz.app42.message.model.App42Message;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.FriendMessage;
import com.shephertz.app42.message.model.GroupMessage;
import com.shephertz.app42.message.util.Utility;

/**
 * @author Vishnu
 * 
 */
public class QueryGenerator implements DBProps {

	private final static String DefaultMessage = "Hey I am using App42 Messanger";

	/**
	 * Query for Users table creation
	 * 
	 * @return queryString
	 */
	static String getCreateUserTableQuery() {
		return "CREATE TABLE " + UsersTableName + " (" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + UserId
				+ " VARCHAR(15), " + DisplayName + " VARCHAR(50), "
				+ LastMessage + " TEXT, " + Status + " VARCHAR(50), "
				+ DateTime + " INTEGER, " + PicUri + " TEXT );";
	}

	/**
	 * Query for Groups Table creation
	 * 
	 * @return queryString
	 */
	static String getCreateGroupTableQuery() {
		return "CREATE TABLE " + GroupsTableName + " (" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + GroupId
				+ " VARCHAR(15), " + DisplayName + " VARCHAR(50), " + Owner
				+ " VARCHAR(15), " + Sender + " VARCHAR(50), " + LastMessage
				+ " TEXT, " + DateTime + " INTEGER, " + PicUri + " TEXT );";
	}

	// /**
	// * Query for Groups Table creation
	// * @return queryString
	// */
	// static String getCreateContactTableQuery() {
	// return "CREATE TABLE AppContacts IF NOT EXISTS (" + ID +
	// " INTEGER PRIMARY KEY AUTOINCREMENT, "
	// + UserId + " VARCHAR(15), " + DisplayName + " VARCHAR(50), "
	// + Status + " VARCHAR(50), " + PicUri
	// + " TEXT );";
	// }

	static String getAllUserIdQuery() {
		return "SELECT  " + UserId + " FROM " + UsersTableName;
	}

	/**
	 * @return
	 */
	static String getDropUsersTableQuery() {
		return "DROP TABLE IF EXISTS Users";
	}

	/**
	 * @return
	 */
	static String getDropGroupsTableQuery() {
		return "DROP TABLE IF EXISTS Groups";
	}

	/**
	 * @return
	 */
	static String getDropGroupTableByIdQuery(String groupId) {
		return "DROP TABLE IF EXISTS "+getTableNameBelongsToGroup(groupId);
	}

	static String getUsersTableName() {
		return UsersTableName;
	}

	static String getGroupsTableName() {
		return GroupsTableName;
	}

	static ContentValues getInserUserContent(AppUser appUser) {
		ContentValues values = new ContentValues();
		values.put(DisplayName, appUser.getName());
		values.put(Status, appUser.getStatus());
		values.put(UserId, appUser.getUserId());
		values.put(PicUri, appUser.getPicUri());
		values.put(DateTime, Utility.getTime());
		values.put(LastMessage, appUser.getMessage());
		return values;
	}

	static ContentValues getUpdateUserContent(AppUser appUser) {
		ContentValues values = new ContentValues();
		values.put(DisplayName, appUser.getName());
		values.put(Status, appUser.getStatus());
		values.put(PicUri, appUser.getPicUri());
		//values.put(LastMessage, appUser.getMessage());
		values.put(DateTime, appUser.getLastCommTime());
		return values;
	}

	static ContentValues getInserUserWithMessageContent(
			App42Message app42Message) {
		ContentValues values = new ContentValues();
		values.put(DisplayName, app42Message.getSenderId());
		values.put(Status, DefaultMessage);
		values.put(UserId, app42Message.getSenderId());
		values.put(PicUri, "");
		values.put(DateTime, app42Message.getSendingTime());
		values.put(LastMessage, app42Message.getMessage());
		return values;
	}
	static ContentValues getInsertMyMessageContent(
			App42Message app42Message,String friendId) {
		ContentValues values = new ContentValues();
		values.put(DisplayName, friendId);
		values.put(Status, DefaultMessage);
		values.put(UserId, friendId);
		values.put(PicUri, "");
		values.put(DateTime, app42Message.getSendingTime());
		values.put(LastMessage, app42Message.getMessage());
		return values;
	}
	static ContentValues getInsertGroupWithMessageContent(
			GroupMessage app42Message) {
		ContentValues values = new ContentValues();
		values.put(DisplayName, app42Message.getGroupName());
		values.put(GroupId, app42Message.getGroupId());
		values.put(PicUri, app42Message.getGroupIconUri());
		values.put(Owner, app42Message.getSenderId());
		values.put(Sender, app42Message.getSenderName());
		values.put(DateTime, app42Message.getSendingTime());
		values.put(LastMessage, app42Message.getMessage());
		return values;
	}

	static ContentValues getUpdateLastMessageUsersContent(
			App42Message app42Message) {
		ContentValues values = new ContentValues();
		values.put(LastMessage, app42Message.getMessage());
		values.put(DateTime, app42Message.getSendingTime());
		return values;
	}

	static ContentValues getUpdateLastMessageInGroupContent(
			GroupMessage groupMessage) {
		ContentValues values = new ContentValues();
		values.put(LastMessage, groupMessage.getMessage());
		values.put(DateTime, groupMessage.getSendingTime());
		values.put(Sender, groupMessage.getSenderName());
		if(groupMessage.getMessageType()==com.shephertz.app42.message.command.MessageType.GroupIconChange){
			values.put(PicUri, groupMessage.getMessage());
		}
		else if(groupMessage.getMessageType()==com.shephertz.app42.message.command.MessageType.GroupNameChange){
			values.put(DisplayName, groupMessage.getMessage());
		}
		return values;
	}

	static String getAllUsersInfoQuery() {
		return "SELECT  " + UserId + "," + DisplayName + "," + Status + ","
				+ PicUri + " FROM " + UsersTableName + " ORDER BY "
				+ DisplayName + " DESC";
	}

	static String getAllFriendsMessagesQuery() {
		return "SELECT  " + UserId + "," + DisplayName + "," + LastMessage
				+ "," + DateTime + "," + PicUri + " FROM " + UsersTableName
				+ " ORDER BY " + DateTime + " DESC";
	}

	static String getAllGroupsMessagesQuery() {
		return "SELECT  " + GroupId + "," + DisplayName + "," + LastMessage
				+ "," + Sender + "," + DateTime + "," + PicUri + " FROM "
				+ GroupsTableName + " ORDER BY " + DateTime + " DESC";
	}

	static String getFriendsMessagesQuery(String friendId) {
		return "SELECT  " + UserId + ","+ Message + "," + MessageType + "," + DateTime
				+ " FROM " + getTableNameBelongsToUser(friendId) + " ORDER BY "
				+ DateTime + " ASC";
	}

	static String getGroupMessagesQuery(String groupId) {
		return "SELECT  " + Message + "," + MessageType + "," + DateTime + ","
				+ Sender + " FROM " + getTableNameBelongsToGroup(groupId)
				+ " ORDER BY " + DateTime + " ASC";
	}

	static String getCreateTableQueryByUserId(String userId) {
		String query = "CREATE TABLE IF NOT EXISTS "
				+ getTableNameBelongsToUser(userId) + " (" + MessageType
				+ " VARCHAR(10), "+UserId + " VARCHAR(50), " + DateTime + " INTEGER, " + Message
				+ " TEXT);";
		return query;
	}

	static String getCreateTableQueryByGroupId(String groupId) {
		String query = "CREATE TABLE IF NOT EXISTS "
				+ getTableNameBelongsToGroup(groupId) + " (" + MessageType
				+ " VARCHAR(10), " + Sender + " VARCHAR(50), " + DateTime
				+ " INTEGER, " + Message + " TEXT);";
		return query;
	}

	static String getTableNameBelongsToUser(String userId) {
		return "user_" + userId;
	}

	static String getTableNameBelongsToGroup(String groupId) {
		return "group_" + groupId;
	}

	static ContentValues getUserMessageContent(FriendMessage friendMessage) {
		ContentValues values = new ContentValues();
		values.put(DateTime, friendMessage.getSendingTime());
		values.put(UserId, friendMessage.getSenderId());
		values.put(MessageType, friendMessage.getMessageType().toString());
		values.put(Message, friendMessage.getMessage());
		return values;
	}

	static ContentValues getGroupMessageContent(GroupMessage groupMessage) {
		ContentValues values = new ContentValues();
		values.put(DateTime, groupMessage.getSendingTime());
		values.put(MessageType, groupMessage.getMessageType().toString());
		values.put(Message, groupMessage.getMessage());
		values.put(Sender, groupMessage.getSenderName());
		return values;
	}

	// static String getAInfoQuery(){
	// return "SELECT  " + UserId + "," + DisplayName + "," + Status + ","
	// + PicUri + ","+ DateTime + " FROM " + UsersTableName + " ORDER BY " +
	// DateTime + " DESC";
	// }

	static String getUserExistsQuery(String userName) {
		return "SELECT  " + UserId + " FROM " + UsersTableName + " WHERE "
				+ UserId + "=" + userName;
	}

	static String getUserDisplayNameQuery(String userName) {
		return "SELECT  " + DisplayName + " FROM " + UsersTableName + " WHERE "
				+ UserId + "=" + userName;
	}
	static String getGroupOwnerNameQuery(String groupId) {
		return "SELECT  " + Owner + " FROM " + GroupsTableName + " WHERE "
				+ GroupId + "=" + groupId;
	}
	static String getGroupNameUrlQuery(String groupId) {
		return "SELECT  " + DisplayName +","+PicUri+ " FROM " + GroupsTableName + " WHERE "
				+ GroupId + "=" + groupId;
	}
	static String getUserById(String userName) {
		return "SELECT  " + DisplayName+","+PicUri+","+Status + " FROM " + UsersTableName + " WHERE "
				+ UserId + "=" + userName;
	}

	static String getGroupExistsQuery(String groupId) {
		return "SELECT  " + GroupId + " FROM " + GroupsTableName + " WHERE "
				+ GroupId + "=" + groupId;
	}
}
