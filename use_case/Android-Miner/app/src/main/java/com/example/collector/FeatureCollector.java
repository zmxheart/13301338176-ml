package com.example.collector;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class FeatureCollector extends IntentService {

	public FeatureCollector() {
		super("FeatureCollectorService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
		//applications
		ActivityManager manager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		String packageName = manager.getRunningTasks(1).get(0).topActivity.getPackageName();
		List<RunningTaskInfo> tasksInfo = manager.getRunningTasks(20);
		
		String runningApplications = "";
		
		runningApplications += tasksInfo.get(0).topActivity.getPackageName();
				
		for (int i=1; i < tasksInfo.size(); i++){
			runningApplications += (", " + tasksInfo.get(i).topActivity.getPackageName());
		}
		
		int numberOfRunningApplications = tasksInfo.size();
			
		//orientation
		int orientationNum = getResources().getConfiguration().orientation;
		String orientation;
		if (orientationNum == 1)
			orientation = "Portrait";
		else if (orientationNum == 2)
			orientation = "Landscape";
		else
			orientation = "undefined";
		
		//loaction
		double latitude = 0.0;
		double longitude = 0.0;
		String address = "";
		
		GPSTracker mGPS = new GPSTracker(getApplicationContext());
		
		int attempts = 0;
		while(attempts < 2){ // try 2 times, stop if location resolved
			attempts++;
			
			// get longitude and latitude
			if(mGPS.canGetLocation){
				latitude  = mGPS.getLatitude();
				longitude = mGPS.getLongitude();
			}else{
				Toast.makeText(getApplicationContext(), "Lat/Long failed", Toast.LENGTH_SHORT).show();
			} // end else
	
		    // reverse geocode coordinates
			// GeoCoder test to convert coordinates into a street address
		    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
		    List<Address> addresses = null;
			try{
				addresses = gcd.getFromLocation(latitude, longitude, 1);
			}catch (IOException e) {
				e.printStackTrace();
			} // end catch
			if(addresses.size() > 0){
		    	address = addresses.get(0).getThoroughfare() + "_" +
	    			      addresses.get(0).getLocality();
		    	break; // exit while loop if location resolved
		    }else{address = "";}
		} // end while
		mGPS.stopUsingGPS();
		
		//memory
		ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		long availableMemory = memoryInfo.availMem;
		
		//battery
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, ifilter);
		
		// Are we charging / charged?
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
		String charger = "";
		if (isCharging){
			// How are we charging?
			int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB)
				charger = "USB";
			else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC)
				charger = "AC";
		}else
			charger = "None";
			
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	
		float battery = level / (float)scale;
		
		//Network/Connections
		String networks = "";
		String connections = "";
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (int i = 0; i < netInfo.length; i++){
			networks += (netInfo[i].getTypeName() + netInfo[i].getSubtypeName() + "_");
			connections += (netInfo[i].getState() + "_");
		}
		
		networks = networks.substring(0, networks.length() - 2);
		connections = connections.substring(0, connections.length() - 2);
		
		FeatureDataOpenHelper db = new FeatureDataOpenHelper(this);
		
		//bytes sent/received
		long bytesReceived = TrafficStats.getMobileRxBytes();
		long bytesTransmitted = TrafficStats.getMobileTxBytes();
		/*if (bytesReceived - db.getLastBytesReceived() >= 0){
			bytesReceived = bytesReceived - db.getLastBytesReceived();
		}
		if (bytesTransmitted - db.getLastBytesTransmitted() >= 0){
			bytesTransmitted = bytesTransmitted - db.getLastBytesTransmitted();
		}*/
		
		//record
		db.recordFeatures(new CollectionInstance(packageName, numberOfRunningApplications,
				runningApplications, orientation, latitude, longitude, address, 
				availableMemory, charger, battery, networks, connections, bytesReceived,
				bytesTransmitted));
		
		
		List<CollectionInstance> records = db.getAllRecords();
		
		for (int i = 0; i < records.size(); i++)
		{
			Log.v("Database Rows", "Row " + i + ":\n");
			Log.v("Id: ",records.get(i).getId() + "\n");
			Log.v("dateTime: ",records.get(i).getDateTime() + "\n");
			Log.v("Foreground App: ",records.get(i).getForegroundApp() + "\n");
			Log.v("Number of Running Apps: ",records.get(i).getNumberOfRunningApps() + "\n");
			Log.v("List of Running Apps: ",records.get(i).getAppsRunning() + "\n");
			Log.v("Orientation: ",records.get(i).getOrientation() + "\n");
			Log.v("Latitude: ",records.get(i).getLatitude() + "\n");
			Log.v("Longitude: ",records.get(i).getLongitude() + "\n");
			Log.v("Address: ",records.get(i).getAddress() + "\n");
			Log.v("Available Memory: ",records.get(i).getAvailableMemory() + "\n");
			Log.v("Charger: ",records.get(i).getCharger() + "\n");
			Log.v("Battery Life: ",records.get(i).getBattery() + "\n");
			Log.v("Networks: ",records.get(i).getNetworks() + "\n");
			Log.v("Connections: ",records.get(i).getConnections() + "\n");
			Log.v("Bytes Received: ",records.get(i).getBytesReceived() + "\n");
			Log.v("Bytes Transmitted: ",records.get(i).getBytesTransmitted() + "\n");
		}
	}
}
