package com.example.collector;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class RecurringAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.v("VERBOSE", "Alarm almost Received");
		if (intent.getAction() != null) {
			PendingIntent pIntent;
			AlarmManager aManager;
			Intent alarmIntent = new Intent(context, RecurringAlarmReceiver.class);
		    pIntent = PendingIntent.getBroadcast(context, 1234, alarmIntent, 0);
			
			aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		    int interval = 900000;

		    aManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pIntent);
        }else{
        	//Log.v("VERBOSE", "Alarm Received");
        	Intent recorder = new Intent(context, FeatureCollector.class);
        	context.startService(recorder);
        }

	}

}
