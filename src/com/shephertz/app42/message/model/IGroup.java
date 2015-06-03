/**
 * 
 */
package com.shephertz.app42.message.model;

/**
 * @author Vishnu
 *
 */
public interface IGroup {
	public String getGroupId();
	public String getGroupName();
	public String getPicUri();
	public String getSenderName();
	public String getMessage();
	public long getLastCommTime();
}
