/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shephertz.app42.message.model.App42Message;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.FriendMessage;
import com.shephertz.app42.message.model.Group;
import com.shephertz.app42.message.model.GroupMessage;

/**
 * @author Vishnu
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String Tag = "DBHelper";
	private static final String DBName = "App42Messanger.db";
	private static final int DBVersion = 1;
	private static DBHelper mInstance;
	private SQLiteDatabase dbInstance;

	/**
	 * @param context
	 */
	private DBHelper(Context context) {
		super(context, DBName, null, DBVersion);
		dbInstance = getWritableDatabase();
	}

	/**
	 * @param context
	 * @return
	 */
	public static DBHelper instance(Context context) {
		if (mInstance == null) {
			mInstance = new DBHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(QueryGenerator.getCreateUserTableQuery());
		db.execSQL(QueryGenerator.getCreateGroupTableQuery());
		// db.execSQL(QueryGenerator.getCreateContactTableQuery());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(Tag, "Upgrade DB from V: " + oldVersion + " to V:" + newVersion
				+ "; in App42 Case Study");
		db.execSQL(QueryGenerator.getDropGroupsTableQuery());
		db.execSQL(QueryGenerator.getDropUsersTableQuery());
		onCreate(db);
	}

	public void insertAndUpdateUsers(HashMap<String, AppUser> appUsersMap) {
		Cursor dbCursor = dbInstance.rawQuery(
				QueryGenerator.getAllUserIdQuery(), null);
		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				String userName = dbCursor.getString(0);
				noOfScorer++;
				AppUser appUser = appUsersMap.remove(userName);
				if (appUser != null) {
					updateUserInfo(appUser);
				} else
					insertUserInfo(appUser);
				dbCursor.moveToNext();
			}
		} else {
			insertInUsersInTable(appUsersMap);
		}
	}

	private void insertInUsersInTable(HashMap<String, AppUser> appUsersMap) {
		for (Entry<String, AppUser> entry : appUsersMap.entrySet()) {
			AppUser appUser = entry.getValue();
			insertUserInfo(appUser);
		}
	}

	public void updateUserInfo(AppUser appUser) {
		dbInstance.update(QueryGenerator.getUsersTableName(),
				QueryGenerator.getUpdateUserContent(appUser), DBProps.UserId
						+ "=?", new String[] { appUser.getUserId() });
	}

	private void insertUserInfo(AppUser appUser) {
		dbInstance.insert(QueryGenerator.getUsersTableName(), null,
				QueryGenerator.getInserUserContent(appUser));
	}

	public ArrayList<AppUser> fetchAppContacts() {
		ArrayList<AppUser> appContactsList = new ArrayList<AppUser>();
		Cursor dbCursor = dbInstance.rawQuery(
				QueryGenerator.getAllUsersInfoQuery(), null);
		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				System.out.println("user--" + dbCursor.toString());
				appContactsList.add(new AppUser(dbCursor.getString(0), dbCursor
						.getString(1), dbCursor.getString(3), dbCursor
						.getString(2)));
				dbCursor.moveToNext();
			}
		}
		return appContactsList;
	}
	
	public ArrayList<AppUser> fetchAllFriendMessages() {
		ArrayList<AppUser> appContactsList = new ArrayList<AppUser>();
		Cursor dbCursor = dbInstance.rawQuery(
				QueryGenerator.getAllFriendsMessagesQuery(), null);
		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				System.out.println("user--" + dbCursor.toString());
				if(!dbCursor.getString(2).equals(""))
				appContactsList.add(new AppUser(dbCursor.getString(0), dbCursor
						.getString(1), dbCursor.getString(2),dbCursor.getLong(3), dbCursor
						.getString(4)));
				dbCursor.moveToNext();
			}
		}
		return appContactsList;
	}
	
	public ArrayList<Group> fetchAllGroupsWithMessages() {
		ArrayList<Group> appGroupsList = new ArrayList<Group>();
		Cursor dbCursor = dbInstance.rawQuery(
				QueryGenerator.getAllGroupsMessagesQuery(), null);
		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				System.out.println("user--" + dbCursor.toString());
				//if(!dbCursor.getString(2).equals("")){
				appGroupsList.add(new Group(dbCursor.getString(0), dbCursor
						.getString(1), dbCursor.getString(2),dbCursor.getString(3), dbCursor
						.getLong(4), dbCursor
						.getString(5)));
				//}
				dbCursor.moveToNext();
			}
		}
		return appGroupsList;
	}
	
//	public ArrayList<AppUser> fetchAllGroupsWithMessage() {
//		ArrayList<AppUser> appContactsList = new ArrayList<AppUser>();
//		Cursor dbCursor = dbInstance.rawQuery(
//				QueryGenerator.getAllFriendsMessagesQuery(), null);
//		if (dbCursor.getCount() > 0) {
//			int noOfScorer = 0;
//			dbCursor.moveToFirst();
//			while ((!dbCursor.isAfterLast())
//					&& noOfScorer < dbCursor.getCount()) {
//				System.out.println("user--" + dbCursor.toString());
//				if(!dbCursor.getString(2).equals(""))
//				appContactsList.add(new AppUser(dbCursor.getString(0), dbCursor
//						.getString(1), dbCursor.getString(2),dbCursor.getLong(3), dbCursor
//						.getString(4)));
//				dbCursor.moveToNext();
//			}
//		}
//		return appContactsList;
//	}

	/*
	 * Called in Case of acceptance or InVitation event
	 */
	public void insertFriendMessage(FriendMessage friendMessage) {
		try {
			updateMasterUsertable(friendMessage);
			dbInstance.execSQL(QueryGenerator
					.getCreateTableQueryByUserId(friendMessage.getSenderId()));
			dbInstance.insert(QueryGenerator
					.getTableNameBelongsToUser(friendMessage.getSenderId()),
					null, QueryGenerator.getUserMessageContent(friendMessage));
		} catch (SQLiteException e) {
			Log.e(Tag, "insert()", e);
		}
	}
	/*
	 * Called in Case of acceptance or InVitation event
	 */
	public void insertMyMessage(FriendMessage myMessage,String friendId) {
		try {
			Cursor cursor = dbInstance.rawQuery(
					QueryGenerator.getUserExistsQuery(friendId),
					null);
			if (cursor.getCount() > 0) {
				dbInstance.update(QueryGenerator.getUsersTableName(),
						QueryGenerator.getUpdateLastMessageUsersContent(myMessage),
						DBProps.UserId + "=?",
						new String[] { friendId });
			} else {
				dbInstance
						.insert(QueryGenerator.getUsersTableName(),
								null,
								QueryGenerator
										.getInsertMyMessageContent(myMessage,friendId));
			}
			dbInstance.execSQL(QueryGenerator
					.getCreateTableQueryByUserId(friendId));
			dbInstance.insert(QueryGenerator
					.getTableNameBelongsToUser(friendId),
					null, QueryGenerator.getUserMessageContent(myMessage));
		} catch (SQLiteException e) {
			Log.e(Tag, "insert()", e);
		}
	}
	
	/*
	 * Called in Case of acceptance or InVitation event
	 */
	public void insertGroupMessage(GroupMessage groupMessage) {
		try {
			updateMasterGrouptable(groupMessage);
			dbInstance.execSQL(QueryGenerator
					.getCreateTableQueryByGroupId(groupMessage.getGroupId()));
			dbInstance.insert(QueryGenerator
					.getTableNameBelongsToGroup(groupMessage.getGroupId()),
					null, QueryGenerator.getGroupMessageContent(groupMessage));
		} catch (SQLiteException e) {
			Log.e(Tag, "insert()", e);
		}
	}
	
	public void createNewGroup(GroupMessage groupMessage){
		dbInstance
		.insert(QueryGenerator.getGroupsTableName(),
				null,
				QueryGenerator
						.getInsertGroupWithMessageContent(groupMessage));
	}
	private void updateMasterGrouptable(GroupMessage groupMessage) {
		Cursor cursor = dbInstance.rawQuery(
				QueryGenerator.getGroupExistsQuery(groupMessage.getGroupId()),
				null);
		if (cursor.getCount() > 0) {
			dbInstance.update(QueryGenerator.getGroupsTableName(),
					QueryGenerator.getUpdateLastMessageInGroupContent(groupMessage),
					DBProps.GroupId + "=?",
					new String[] { groupMessage.getGroupId()});
		} else {
			dbInstance
					.insert(QueryGenerator.getGroupsTableName(),
							null,
							QueryGenerator
									.getInsertGroupWithMessageContent(groupMessage));
		}
	}


	private void updateMasterUsertable(FriendMessage friendMessage) {
		Cursor cursor = dbInstance.rawQuery(
				QueryGenerator.getUserExistsQuery(friendMessage.getSenderId()),
				null);
		if (cursor.getCount() > 0) {
			dbInstance.update(QueryGenerator.getUsersTableName(),
					QueryGenerator.getUpdateLastMessageUsersContent(friendMessage),
					DBProps.UserId + "=?",
					new String[] { friendMessage.getSenderId() });
		} else {
			dbInstance
					.insert(QueryGenerator.getUsersTableName(),
							null,
							QueryGenerator
									.getInserUserWithMessageContent(friendMessage));
		}
	}

	public String getDisplayNameByUserId(String userId){
		Cursor cursor = dbInstance.rawQuery(
				QueryGenerator.getUserDisplayNameQuery(userId),
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
		else{
			return userId;
		}
	}
	
	public String getGroupOwnerName(String groupId){
		Cursor cursor = dbInstance.rawQuery(
				QueryGenerator.getGroupOwnerNameQuery(groupId),
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
		else{
			return "NA";
		}
	}
	public String[] getGroupNameAndPicUrl(String groupId){
		String [] str=new String[2];
		Cursor cursor = dbInstance.rawQuery(
				QueryGenerator.getGroupNameUrlQuery(groupId),
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			str[0]= cursor.getString(0);
			str[1]= cursor.getString(1);
			return str;
		}
		else{
			return null;
		}
	}
	
	/**
	 * @param userId
	 * @return
	 */
	public AppUser getUserById(String userId){
		Cursor cursor = dbInstance.rawQuery(
				QueryGenerator.getUserById(userId),
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return new AppUser(userId, cursor.getString(0), cursor.getString(1), cursor.getString(2));
		}
		else{
			return new AppUser(userId, userId,"", "Hey I am using App42Messanger");
		}
	}
	
	/**
	 * @param friendId
	 * @return
	 */
	public ArrayList<App42Message> fetchFriendMessages(String friendId) {
		ArrayList<App42Message> messagesList = new ArrayList<App42Message>();
		Cursor dbCursor = dbInstance.rawQuery(
				QueryGenerator.getFriendsMessagesQuery(friendId), null);
		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				System.out.println("user--" + dbCursor.toString());
				if(dbCursor.getString(0)!=null&&!dbCursor.getString(1).equals(""))
				messagesList.add(new FriendMessage(dbCursor.getString(0),dbCursor.getString(1), dbCursor
						.getString(2), dbCursor.getLong(3)));
				dbCursor.moveToNext();
			}
		}
		return messagesList;
	}
	
	public ArrayList<App42Message> fetchGroupMessages(String groupId) {
		ArrayList<App42Message> messagesList = new ArrayList<App42Message>();
		Cursor dbCursor = dbInstance.rawQuery(
				QueryGenerator.getGroupMessagesQuery(groupId), null);
		if (dbCursor.getCount() > 0) {
			int noOfScorer = 0;
			dbCursor.moveToFirst();
			while ((!dbCursor.isAfterLast())
					&& noOfScorer < dbCursor.getCount()) {
				System.out.println("user--" + dbCursor.toString());
				if(!dbCursor.getString(0).equals(""))
				messagesList.add(new GroupMessage(dbCursor.getString(0), dbCursor
						.getString(1), dbCursor.getLong(2),dbCursor
						.getString(3)));
				dbCursor.moveToNext();
			}
		}
		return messagesList;
	}
	
	public void deleteGroup(String groupId){
		dbInstance.execSQL(QueryGenerator.getDropGroupTableByIdQuery(groupId));
		dbInstance.delete(QueryGenerator.getGroupsTableName(),  "groupId=?", new String[] { groupId });
	}
}
