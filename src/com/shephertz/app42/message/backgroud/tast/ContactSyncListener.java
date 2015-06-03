/**
 * -----------------------------------------------------------------------
 *     Copyright © 2012 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.message.backgroud.tast;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.shephertz.app42.message.backgroud.tast.WakefulIntentService.AlarmListener;

/**
 * @author Vishnu Garg
 *
 */
public class ContactSyncListener implements AlarmListener{ 

	/* (non-Javadoc)
	 * @see com.shephertz.app42.paas.sdk.android.keepAlive.WakefulIntentService.AlarmListener#scheduleAlarms(android.app.AlarmManager, android.app.PendingIntent, android.content.Context)
	 */
	@Override
	public void scheduleAlarms(AlarmManager alarmManager, PendingIntent pi,
			Context context) {
	            Log.i("DailyListener", "Schedule update check...");
	            // every day at 9 am
	            Calendar calendar = Calendar.getInstance();
	            // if it's after or equal 9 am schedule for next day
//	            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 14) {
//	                calendar.add(Calendar.DAY_OF_YEAR, 1); // add, not set!
//	            }
	            calendar.set(Calendar.HOUR_OF_DAY, 21);
	            calendar.set(Calendar.MINUTE, 0);
	            calendar.set(Calendar.SECOND, 0);
	            alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
	                        60*1000, pi);
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.paas.sdk.android.keepAlive.WakefulIntentService.AlarmListener#sendWakefulWork(android.content.Context)
	 */
	@Override
	public void sendWakefulWork(Context context) {
		// TODO Auto-generated method stub
		ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // only when connected or while connecting...
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                Log.d("DailyListener", "We have internet, start update check directly now!");
                Intent backgroundIntent = new Intent(context, ContactSysncService.class);
                WakefulIntentService.sendWakefulWork(context, backgroundIntent);
        } else {
            Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");
            // enable receiver to schedule update when internet is available!
            AlarmReceiver.enableReceiver(context);
        }
	}
	/* (non-Javadoc)
	 * @see com.shephertz.app42.paas.sdk.android.keepAlive.WakefulIntentService.AlarmListener#getMaxAge()
	 */
	@Override
	public long getMaxAge() {
		// TODO Auto-generated method stub
		return (AlarmManager.INTERVAL_DAY + 60 * 1000);
	}

}
