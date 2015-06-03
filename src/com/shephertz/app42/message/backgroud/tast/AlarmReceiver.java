/**
 * -----------------------------------------------------------------------
 *     Copyright © 2012 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.backgroud.tast;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.shephertz.app42.message.backgroud.tast.WakefulIntentService.AlarmListener;
import com.shephertz.app42.paas.sdk.android.App42API;

/**
 * @author Vishnu Garg
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		App42API.appContext = context;
	
		if(intent.getAction()!=null&&intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		schuduleAlarm(context, intent);
		if (intent.getAction()!=null&&intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Log.d("AlarmReceiver", "ConnectivityReceiver invoked...");
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if (!noConnectivity) {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				// only when connected or while connecting...
				if (netInfo != null && netInfo.isConnectedOrConnecting()) {
					Log.d("AlarmReceiver",
							"We have internet, start update check and disable receiver!");
					// Start service with wakelock by using WakefulIntentService
					Intent backgroundIntent = new Intent(context,
							ContactSysncService.class);
					WakefulIntentService.sendWakefulWork(context,
							backgroundIntent);
					// disable receiver after we started the service
					disableReceiver(context);
				}
			}
		}
	}


	private void schuduleAlarm(Context context, Intent intent) {
		AlarmListener listener = null;
		try {
			Class<AlarmListener> cls = (Class<AlarmListener>) Class
					.forName(ContactSyncListener.class.getName());
			try {
				listener = cls.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (listener != null) {
			if (intent.getAction() == null) {
				SharedPreferences prefs = context.getSharedPreferences(
						WakefulIntentService.Name, 0);
				prefs.edit()
						.putLong(WakefulIntentService.LastAlarm,
								System.currentTimeMillis()).commit();
				listener.sendWakefulWork(context);
			} else {
				WakefulIntentService.scheduleAlarms(listener, context, true);
			}
		}
	}

	/**
	 * Enables KeepAliveReceiver
	 * 
	 * @param context
	 */
	public static void enableReceiver(Context context) {
		ComponentName component = new ComponentName(context,
				AlarmReceiver.class);
		context.getPackageManager().setComponentEnabledSetting(component,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	/**
	 * Disables KeepAliveReceiver
	 * 
	 * @param context
	 */
	public static void disableReceiver(Context context) {
		ComponentName component = new ComponentName(context,
				AlarmReceiver.class);
		context.getPackageManager().setComponentEnabledSetting(component,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}


}
