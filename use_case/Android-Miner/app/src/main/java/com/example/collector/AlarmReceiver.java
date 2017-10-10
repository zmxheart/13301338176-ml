package com.example.collector;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * class for creating an alarmReceiver that adds a newline to the
 * file at a set time.
 * 
 * @author zac
 */
public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// add " $$\n" to the file
		Globals.GlobalContext += "$$\n";		
	} // end onRecieve()
} // end AlarmReceiver