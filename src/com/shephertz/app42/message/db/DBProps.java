/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.db;

/**
 * @author Vishnu
 *
 */
public interface DBProps {
	 static final String UserId = "userId";
	 static final String DisplayName = "displayName";
	 static final String Type = "type";
	 static final String DateTime = "dateTime";
	 static final String PicUri = "picUri";
	 static final int UserType = 0;
	 static final int GroupType = 1;
	 static final String Message = "message";
	 static final String MessageType = "messageType";
	 static final String Owner = "owner";
	 static final String ID = "id";
	 static final String Status="status";
	
	 static final String GroupId="groupId";
	 
	 static final String Sender="sender";
	 static final String LastMessage="lastMessage";
	 static final String UsersTableName="Users";
	 static final String GroupsTableName="Groups";
}
