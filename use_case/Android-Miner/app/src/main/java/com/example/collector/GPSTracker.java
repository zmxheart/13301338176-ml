package com.example.collector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * class for getting GPS information
 * 
 * @author zac
 */
public class GPSTracker implements LocationListener{

	private final Context mContext;
	public boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location;
	double latitude;
	double longitude;

	//  minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// declare a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
	    this.mContext = context;
	    getLocation();
	} // contructor

	/**
	 * Function to get the user's current location
	 * @return
	 */
	public Location getLocation() {
	    try{
	        locationManager = (LocationManager) mContext
	                .getSystemService(Context.LOCATION_SERVICE);

	        // getting GPS status
	        isGPSEnabled = locationManager
	                .isProviderEnabled(LocationManager.GPS_PROVIDER);

	        Log.v("isGPSEnabled", "=" + isGPSEnabled);

	        // getting network status
	        isNetworkEnabled = locationManager
	                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	        Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

	        if(isGPSEnabled == false && isNetworkEnabled == false){
	            // no network provider is enabled
	        }else{
	            this.canGetLocation = true;
	            if (isNetworkEnabled){
	                locationManager.requestLocationUpdates(
	                        LocationManager.NETWORK_PROVIDER,
	                        MIN_TIME_BW_UPDATES,
	                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                Log.d("Network", "Network");
	                if(locationManager != null){
	                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    if(location != null){
	                        latitude  = location.getLatitude();
	                        longitude = location.getLongitude();
	                    } // end if 3
	                } // end if 2
	            } // end if 1
	            // if GPS Enabled get lat/long using GPS Services
	            if(isGPSEnabled){
	                if(location == null){
	                    locationManager.requestLocationUpdates(
	                            LocationManager.GPS_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                    Log.d("GPS Enabled", "GPS Enabled");
	                    if(locationManager != null){
	                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        if(location != null){
	                            latitude  = location.getLatitude();
	                            longitude = location.getLongitude();
	                        } // end if 4
	                    } // end if 3
	                } // end if 2
	            }// end if 1
	        } // end else

	    }catch (Exception e){
	        e.printStackTrace();
	    } // end catch

	    return location;
	} // end getLocation

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS(){
	    if(locationManager != null){
	        locationManager.removeUpdates(GPSTracker.this);
	    }// end if
	} // end stopUsingGPS

	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
	    if(location != null){
	        latitude = location.getLatitude();
	    } // end if

	    return latitude;
	} // end getLatitude

	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
	    if(location != null){
	        longitude = location.getLongitude();
	    } // end ifs

	    return longitude;
	} // end getLongitude

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation(){return this.canGetLocation;}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert(){
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

	    alertDialog.setTitle("GPS is settings");

	    alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

	    // On pressing Settings button
	    alertDialog.setPositiveButton("Settings",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    Intent intent = new Intent(
	                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                    mContext.startActivity(intent);
	                } // end onClick
	            });

	    // on pressing cancel button
	    alertDialog.setNegativeButton("Cancel",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.cancel();
	                } // end onCLick
	            });

	    alertDialog.show();
	} // end showSettingsAlert

	@Override
	public void onLocationChanged(Location location){}

	@Override
	public void onProviderDisabled(String provider){}

	@Override
	public void onProviderEnabled(String provider){}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){}
} // end class