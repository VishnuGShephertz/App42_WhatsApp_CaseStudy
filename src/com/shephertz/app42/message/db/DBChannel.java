package com.shephertz.app42.message.db;

import java.util.ArrayList;

import android.content.Context;

import com.shephertz.app42.message.listener.DBListener;
import com.shephertz.app42.message.listener.DBMessageListener;
import com.shephertz.app42.message.model.App42Message;
import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.Group;

/**
 * @author Vishnu
 * 
 */
public class DBChannel {
	public static void fetchAppContacts(final Context context,
			final DBListener callBack) {
		new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<AppUser> appContactList = DBHelper.instance(
							context).fetchAppContacts();
					callBack.onAppContactsFetched(appContactList);
				} catch (Exception ex) {
					callBack.onException(ex);
				}
			}
		}.start();
	}

	public static void fetchFriendsListWithMessage(final Context context,
			final DBListener callBack) {
		new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<AppUser> appContactList = DBHelper.instance(
							context).fetchAllFriendMessages();
					callBack.onAppContactsFetched(appContactList);
				} catch (Exception ex) {
					callBack.onException(ex);
				}
			}
		}.start();
	}

	public static void fetchGroupListWithMessage(final Context context,
			final DBListener callBack) {
		new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<Group> appGroupList = DBHelper.instance(context)
							.fetchAllGroupsWithMessages();
					callBack.onAppGroupsFetched(appGroupList);
				} catch (Exception ex) {
					callBack.onException(ex);
				}
			}
		}.start();
	}

	
	public static void fetchGroupMessages(final String groupId,final DBMessageListener callBack,final Context context) {
		new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<App42Message> messageList =DBHelper.instance(context).fetchGroupMessages(groupId);
					callBack.onMessagesFetched(messageList);
				} catch (Exception ex) {
					callBack.onException(ex);
				}
			}
		}.start();
	}

	public static void fetchFriendMessages(final String friendId,final DBMessageListener callBack,final Context context) {
		new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<App42Message> messageList =DBHelper.instance(context).fetchFriendMessages(friendId);
					callBack.onMessagesFetched(messageList);
				} catch (Exception ex) {
					callBack.onException(ex);
				}
			}
		}.start();
	}
}
