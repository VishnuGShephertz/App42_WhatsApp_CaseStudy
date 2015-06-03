/**
 * -----------------------------------------------------------------------
 *     Copyright © 2012 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.backgroud.tast;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import com.shephertz.app42.paas.sdk.android.App42API;


abstract public class WakefulIntentService extends IntentService {
	abstract protected void doWakefulWork(Intent intent);

	public static final String Name = "com.shephertz.app42.paas.sdk.android.keepAlive.WakefulIntentService";
	public static final String LastAlarm = "lastKeepAlive";
	private static volatile PowerManager.WakeLock lockStatic = null;

	public WakefulIntentService(String name) {
		super(name);
		setIntentRedelivery(true);
	}

	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			PowerManager powerManager = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			lockStatic = powerManager.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, Name);
			lockStatic.setReferenceCounted(true);
		}
		return (lockStatic);
	}

	public static void sendWakefulWork(Context context, Intent intent) {
		getLock(context.getApplicationContext()).acquire();
		context.startService(intent);
	}

	public static void sendWakefulWork(Context context, Class<?> clsService) {
		sendWakefulWork(context, new Intent(context, clsService));
	}

	public static void scheduleAlarms(AlarmListener listener, Context ctxt) {
		scheduleAlarms(listener, ctxt, true);
	}

	public static void scheduleAlarms(){
		scheduleAlarms(new ContactSyncListener(), App42API.appContext, false);
	}
	public static void scheduleAlarms(AlarmListener listener, Context ctxt,
			boolean force) {
		
		SharedPreferences prefs = ctxt.getSharedPreferences(Name, 0);
		long lastAlarm = prefs.getLong(LastAlarm, 0);
		if (lastAlarm == 0
				|| force
				|| (System.currentTimeMillis() > lastAlarm && System
						.currentTimeMillis() - lastAlarm > listener.getMaxAge())) {
			AlarmManager mgr = (AlarmManager) ctxt
					.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(ctxt, AlarmReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
			listener.scheduleAlarms(mgr, pi, ctxt);
		}
	}

	public static void cancelAlarms(Context ctxt) {
		AlarmManager mgr = (AlarmManager) ctxt
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctxt, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
		mgr.cancel(pi);
		ctxt.getSharedPreferences(Name, 0).edit().remove(LastAlarm).commit();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) {
			lock.acquire();
		}
		super.onStartCommand(intent, flags, startId);
		return (START_REDELIVER_INTENT);
	}

	@Override
	final protected void onHandleIntent(Intent intent) {
		try {
			doWakefulWork(intent);
		} finally {
			PowerManager.WakeLock lock = getLock(this.getApplicationContext());
			if (lock.isHeld()) {
				try {
					lock.release();
				} catch (Exception e) {
					Log.e(getClass().getSimpleName(),
							"Exception when releasing wakelock", e);
				}
			}
		}
	}

	public interface AlarmListener {
		void scheduleAlarms(AlarmManager alarmManager, PendingIntent pi,
				Context context);

		void sendWakefulWork(Context context);

		long getMaxAge();
	}
}
