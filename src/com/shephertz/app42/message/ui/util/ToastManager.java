/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.ui.util;

import com.shephertz.app42.message.App42ChatApplication;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Vishnu
 *
 */
public class ToastManager {
	public static final void showShort(Context context, String msg) {
		show(context, msg, Toast.LENGTH_SHORT);
	}
	public static final void showLong(Context context, String msg) {
		show(context, msg, Toast.LENGTH_LONG);
	}
	private static final void show(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}
	public static final void showShort(Context context, int msgId) {
		show(context, msgId, Toast.LENGTH_SHORT);
	}
	public static final void showLong(Context context, int msgId) {
		show(context, msgId, Toast.LENGTH_LONG);
	}
	public static final void show(Context context, int msgId, int duration) {
		String msg = App42ChatApplication.getMessage(msgId);
		show(context, msg, duration);
	}
}
