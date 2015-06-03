/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.util;

import android.util.Log;

/**
 * @author Vishnu
 *
 */
public class Logger {
public static final String LOG_TAG = "App42Messanger";
	/**
	 * @param msg
	 */
	public static void d(String msg) {
		Log.d(LOG_TAG, msg);
	}
	/**
	 * @param msg
	 * @param tr
	 */
	public static void d(String msg, Throwable tr) {
		Log.d(LOG_TAG, msg, tr);
	}
	/**
	 * @param msg
	 */
	public static void v(String msg) {
		Log.v(LOG_TAG, msg);
	}
	/**
	 * @param msg
	 * @param tr
	 */
	public static void v(String msg, Throwable tr) {
		Log.v(LOG_TAG, msg, tr);
	}
	/**
	 * @param msg
	 */
	public static void i(String msg) {
		Log.i(LOG_TAG, msg);
	}
	/**
	 * @param msg
	 */
	public static void i(Object object) {
		Log.i(LOG_TAG, object.toString());
	}
	/**
	 * @param msg
	 * @param tr
	 */
	public static void i(String msg, Throwable tr) {
		Log.i(LOG_TAG, msg, tr);
	}
	/**
	 * @param msg
	 */
	public static void w(String msg) {
		Log.w(LOG_TAG, msg);
	}
	/**
	 * @param msg
	 * @param tr
	 */
	public static void w(String msg, Throwable tr) {
		Log.w(LOG_TAG, msg, tr);
	}
	/**
	 * @param msg
	 */
	public static void e(String msg) {
		Log.e(LOG_TAG, msg);
	}
	/**
	 * @param msg
	 * @param tr
	 */
	public static void e(String msg, Throwable tr) {
		Log.e(LOG_TAG, msg, tr);
	}
}
