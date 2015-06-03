/**
 * 
 */
package com.shephertz.app42.message.listener;

import java.util.ArrayList;

import com.shephertz.app42.message.model.App42Message;

/**
 * @author Vishnu
 *
 */
public interface DBMessageListener {

	public void onMessagesFetched(ArrayList<App42Message> messages);
	public void onException(Throwable th);
}
