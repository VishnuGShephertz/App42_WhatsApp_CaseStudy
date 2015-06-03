/**
 * 
 */
package com.shephertz.app42.message.listener;

import java.util.ArrayList;

import com.shephertz.app42.message.model.AppUser;
import com.shephertz.app42.message.model.Group;

/**
 * @author Vishnu
 *
 */
public interface DBListener {
	public void onAppContactsFetched(ArrayList<AppUser> appusers);
	public void onAppGroupsFetched(ArrayList<Group> appGroups);
	public void onException(Throwable th);
}
