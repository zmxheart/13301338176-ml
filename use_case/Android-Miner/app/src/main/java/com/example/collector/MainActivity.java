package com.example.collector;

import com.example.collector.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Telephony;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.R;
///Get brightness data

public class MainActivity extends Activity {
	
	private String address = "";
	/*Timer timer;
	TextView lat;
	TextView lon;
	TextView Address;
	TextView collecting;*/
	
	private PendingIntent pIntent;
	private AlarmManager aManager;
	
	private double mLong = 0.0;
	private double mLat  = 0.0;
	TextView activities;
	
	//@SuppressLint("SdCardPath")
	//File logFile = new File("/mnt/sdcard/","myFile.txt");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button start = (Button)this.findViewById(R.id.start);
		Button stop = (Button)this.findViewById(R.id.stop);
		activities = (TextView)this.findViewById(R.id.activitiesTextView);
		
		//Create alarm manager
		/*final AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		//Create pending intent & register it to your alarm notifier class
		Intent intent = new Intent(this, AlarmReceiver.class);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		//set time for the task to start 
		final Calendar timeOff = Calendar.getInstance();
		timeOff.set(Calendar.HOUR_OF_DAY, 23);// 23
		timeOff.set(Calendar.MINUTE, 59); // 59
		timeOff.set(Calendar.SECOND, 0);*/
		
		activities.setText("placeholder");
		
		Intent alarmIntent = new Intent(this, RecurringAlarmReceiver.class);
	    pIntent = PendingIntent.getBroadcast(this, 1234, alarmIntent, 0);
	    
	    startAlarm();

		/**
		 * handle startService click
		 * - start the broadcast receiver that collects location info on USER_PRESENT
		 */
		start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*registerReceiver(wakeReceiver, new IntentFilter("android.intent.action.USER_PRESENT"));
				alarmMgr.cancel(pendingIntent); // cancel any already existing alarms
				alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, 
					 timeOff.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);*/
				
				// test func 
				//activities.setText(getCurrentlyRunningProccessesOfDevice());
				//String temp = getCurrentlyRunningProccessesOfDevice();
				
				//startService(new Intent(getBaseContext(),ListenSmsMmsService.class));
				//startService(new Intent(getBaseContext(),FeatureCollector.class));
			    ComponentName receiver = new ComponentName(MainActivity.this, RecurringAlarmReceiver.class);
				PackageManager pm = MainActivity.this.getPackageManager();

				pm.setComponentEnabledSetting(receiver,
				        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				        PackageManager.DONT_KILL_APP);
				startAlarm();
			} // end onClick
		});
	
		
		/**
		 * stop the on wake task
		 */
		stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//unregisterReceiver(wakeReceiver);// stop the wakeReceiver
				//alarmMgr.cancel(pendingIntent);
				aManager.cancel(pIntent);
				ComponentName receiver = new ComponentName(MainActivity.this, RecurringAlarmReceiver.class);
				PackageManager pm = MainActivity.this.getPackageManager();

				pm.setComponentEnabledSetting(receiver,
				        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				        PackageManager.DONT_KILL_APP);
			} // end onClick
		});// end Listener


	
	} // end onCreate
	
	public void startAlarm() {
	    aManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	    int interval = 300000;

	    aManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pIntent);
	    //Log.v("VERBOSE", "Alarm Set");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * func to get the processes that are currently running on the device
	 * @return string of the currently running processes.
	 */
	private String getCurrentlyRunningProccessesOfDevice(){
		String info = "";
		/*TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		//CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo()
		//CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
		//info = ("gsm strnegth: " + cellSignalStrengthGsm.getDbm());
		info = telephonyManager.getAllCellInfo().toString();
		List<String> lstSms = new ArrayList<String>();
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(Telephony.Sms.Conversations.CONTENT_URI, 
				new String[] {Telephony.Sms.Conversations.BODY},
				null,
				null,
				Telephony.Sms.Conversations.DEFAULT_SORT_ORDER);
		int totalSMS = c.getCount();
		
		if (c.moveToFirst()){
			for (int i = 0; i < 2; i++) {
				lstSms.add(c.getString(0));
				c.moveToNext();
			}
		}
		c.close();
		//info = (totalSMS + " " + Telephony.Sms.Conversations.MESSAGE_COUNT + "\n\n" + lstSms.toString());
		//SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    /*Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	    SensorEventListener listener = new SensorEventListener() {
	        @Override
	        public void onSensorChanged(SensorEvent event) {
	            float lightQuantity = event.values[0];
	            activities.setText("\nLight: " + lightQuantity);
	        }

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
	    };

	    // Register the listener with the light sensor -- choosing
	    // one of the SensorManager.SENSOR_DELAY_* constants.
	    sensorManager.registerListener(
	            listener, lightSensor, SensorManager.SENSOR_DELAY_UI);*/
	    
	    
	    
	    
	    
	    
	    /*Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
	    SensorEventListener pressurelistener = new SensorEventListener() {
	        @Override
	        public void onSensorChanged(SensorEvent event) {
	            float pressureQuantity = event.values[0];
	            activities.setText("\npressure: " + pressureQuantity);
	        }

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
	    };

	    // Register the listener with the light sensor -- choosing
	    // one of the SensorManager.SENSOR_DELAY_* constants.
	    sensorManager.registerListener(
	    		pressurelistener, pressureSensor, SensorManager.SENSOR_DELAY_UI);
	    
	    sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
	    List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
	    List<String> sensorNameList = new ArrayList<String>();
	    for (int i = 0; i < sensorList.size(); i++){
	    	sensorNameList.add(sensorList.get(i).getName());
	    }
	    activities.setText(sensorNameList.toString());
	    //Log.v("SENSORS", list.toString());*/
	    
	    
	    
		return info;
		
	}
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * inner class for handling broadcastReceivers 
	 * - used for performing the location data collection when the device 
	 *   is unlocked, via ACTION_USER_PRESENT
	 *   
	 */
	/*
	private BroadcastReceiver wakeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
	        
        	///*
        	// try and hope for the best, will fail gracefully 
        	// if there is no Internet connection
        	try{
        		getLocality(); 
        		writeDataToExternalFile();
            }catch(Exception e){
            	//e.printStackTrace();
            } // end catch
        	//*/
	/*
        } // end onReceive
	}; // end wakeReceiver 
	
	
	/**
	 * get the current location info of the device
	 * 
	 * if it looks complicated, and works, don't change it. 
	 */
	/*
	public void getLocality(){
		
		//double mLong = 0.0;
		//double mLat  = 0.0;
		//context = "";
		
		GPSTracker mGPS = new GPSTracker(getApplicationContext());
		
		int attempts = 0;
		while(attempts < 2){ // try 2 times, stop if location resolved
			attempts++;
			
			// get longitude and latitude
			if(mGPS.canGetLocation){
				mLat  = mGPS.getLatitude();
				mLong = mGPS.getLongitude();
			}else{
				Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
			} // end else
	
		    // reverse geocode coordinates
			// GeoCoder test to convert coordinates into a street address
		    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
		    List<Address> addresses = null;
			try{
				addresses = gcd.getFromLocation(mLat, mLong, 1);
			}catch (IOException e) {
				e.printStackTrace();
			} // end catch
			
		    if(addresses.size() > 0){
		    	address = addresses.get(0).getThoroughfare() + ", " + 
	    			      addresses.get(0).getLocality();
		    	break; // exit while loop if location resolved
		    }else{address = "";}
		} // end while
	    
	    // populate the textviews of the home layout with the location info
	    //lat.setText(String.valueOf(mLat));
	    //lon.setText(String.valueOf(mLong));
	    //Address.setText(context);
	    
	    // if an address was found we write it to the internal file
	    /*if(context != null){
        	writeToFile(context + " - ");
	    } */// end if
/*
	    
	    //Globals.GlobalContext += context + " - ";
	} // end getLocality
	
	/**
	 * write all data saved in private internal storage to an external file
	 * 
	 * I don't know why I have so many write to file functions. But 
	 * they work together, so don't delete any of them.
	 */
	/*
	public void writeDataToExternalFile(){
		try{
			readFile();
			writeCollectedDataToFile(Globals.GlobalContext);
		}catch (IOException e){
			e.printStackTrace();
		} // end catch
	} // end writeDataToExternalFile()
	
		
	/**
	 * read from the data file that is stored in internal private memory
	 * 
	 * @throws IOException
	 */
	/*
	public void readFile() throws IOException{
		
		String thisLine;
        BufferedReader input = null;
        
        try{
            FileInputStream fin = new FileInputStream(this.getFileStreamPath("savedData.txt"));
            input = new BufferedReader(new InputStreamReader(fin));
            String st = input.readLine();

            while((thisLine = input.readLine()) != null){
                Globals.GlobalContext += thisLine + "\n";
            } // end while
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(input != null){input.close();}
        } // end finally
	} //end read

	
	/**
	 * write the data to an external user readable file
	 * @param str1
	 */
	/*
	@SuppressLint("SdCardPath")
	private void writeCollectedDataToFile(String str1){

		BufferedWriter output;
		
		try{ 			
			if(!logFile.exists()){
			     logFile.createNewFile();
			} // end if
			
			output = new BufferedWriter(new FileWriter(logFile));
			output.write(str1);
			output.close();
			
		}catch (IOException e){
			e.printStackTrace();
		} // end catch
	}// writeCollectedDataToFile
	
	/**
	 * write the data to an internal file
	 * @param context
	 */
	/*
	public void writeToFile(String context){
		
        try{
    		FileOutputStream fOut = openFileOutput("savedData.txt", MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(context);
	        osw.flush();
	        osw.close();
		}catch (IOException e){
			e.printStackTrace();
		} // end catch
	} // end writeToFile
	
} // end MainActivity class

/*	
@Override
protected void onPause(){
	super.onPause();
	disableAccelerometerListening();
} // end onPause


private void enableAccelerometerListening(){
	sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	sensorManager.registerListener(sensorEventListener, 
			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
			SensorManager.SENSOR_DELAY_NORMAL);
} // end of method enableAccelerometerListening()

@SuppressWarnings("deprecation")
private void disableAccelerometerListening(){
	// stop listening for sensor events
	if(sensorManager != null){
		sensorManager.unregisterListener(sensorEventListener, 
				sensorManager.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER));
	} // end if
} // end of method disableAccelerometerListening()


 // event handler for accelerometer events
   private SensorEventListener sensorEventListener = 
      new SensorEventListener(){
         // use accelerometer to determine whether user shook device 
         @Override
         public void onSensorChanged(SensorEvent event){  
        	 // get x, y, and z values for the SensorEvent
             float x = event.values[0];
             float y = event.values[1];
             float z = event.values[2];
      
             // save previous acceleration value
             lastAcceleration = currentAcceleration;
      
             // calculate the current acceleration
             currentAcceleration = x * x + y * y + z * z;
      
             // calculate the change in acceleration
             acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);
             acceleration = Math.abs(acceleration);
             
             // if the acceleration is above a certain threshold
             if (acceleration > ACCELERATION_THRESHOLD){
            	 moved = "Moving";
             }else{moved = "Not Moving";}
         } // end method onSensorChanged
      
         // required method of interface SensorEventListener
         @Override
         public void onAccuracyChanged(Sensor sensor, int accuracy){} // end method onAccuracyChanged
      }; // end anonymous inner class 

@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {}

@Override
public void onSensorChanged(SensorEvent event){}
*/		

/*
// get the current time
Time today = new Time(Time.getCurrentTimezone());
today.setToNow();
time = today.format("%k:%M:%S");
timeView.setText(time);

// get the current day of month
Date date  = new Date();
day = (String) android.text.format.DateFormat.format("EEEE", date);
dayView.setText(day);

// get network provider
provider = networkInfo.getTypeName();
providerTextView.setText(provider);
providerTextView.setText(provider);

// stop checking for accelerometer changes
movingTextView.setText(moved);
disableAccelerometerListening();
/*
context = tempLat +" - "+ tempLon +
				   " - "+ time +" - "+ day +
				   " - "+ moved +" - "+ provider +
				   " $$"+"\n";
 */





/*
----from line 223

@SuppressLint("SdCardPath")
private void writeCollectedDataToFile(String str1, String str2, 
		                              String str3, String str4, 
		                              String str5, String str6){
	 
	String context = str1 +" - " + str2 +" - " + str3 +" - " +
					 str4 + " - " + str5 + " - " + str6 + " - $$\n";
	
	BufferedWriter output;
	try {			

		if(!logFile.exists()) {
		     logFile.createNewFile();
		} // end if
		
		output = new BufferedWriter(new FileWriter(logFile));
		output.write(context);
		output.close();
		//Toast.makeText(getApplicationContext(), "written", Toast.LENGTH_SHORT).show();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} // end catch
}// writeCollectedDataToFile
*/

//startService(new Intent(getApplicationContext(), BackgroundLocationService.class));
/*
final Handler handler = new Handler();
timer = new Timer();
TimerTask doAsynchronousTask = new TimerTask() {       
	@Override
	public void run() {
		handler.post(new Runnable() {
		public void run() { 
    		try {
    			Toast.makeText(getApplicationContext(), "asyncTask", Toast.LENGTH_SHORT).show();
    			getLocality();
    		}catch (Exception e) {
    			// TODO Auto-generated catch block
    			Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
    		} // end catch
    	} // end void run
	}); // end runnable
}// end void run
}; // end timerTask
timer.schedule(doAsynchronousTask, 0, 30000);
*/
