/**
 * 
 */
package com.shephertz.app42.message.listener;

import java.util.ArrayList;

import com.shephertz.app42.paas.sdk.android.push.PushNotification;

/**
 * @author Vishnu
 *
 */
public interface App42CallBackListener {
public void onImageUploadSuccess(String imageUrl);
public void onCallBackException(String messsage);
public void onGroupUsersFetched(ArrayList<PushNotification> pushNotification);
public void onUnsubscribedFromChannel();
}
