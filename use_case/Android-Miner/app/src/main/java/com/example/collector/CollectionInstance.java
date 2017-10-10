package com.example.collector;

public class CollectionInstance {
	
	int id;
	String foregroundApp;
	int numberOfRunningApps;
	String appsRunning;
	String orientation;
	double latitude;
	double longitude;
	String address;
	long availableMemory;
	String dateTime;
	String charger;
	float battery;
	String networks;
	String connections;
	long bytesReceived;
	long bytesTransmitted;

	public CollectionInstance() {
		super();
	}
	public CollectionInstance(String foregroundApp, int numberOfRunningApps,
			String appsRunning, String orientation, double latitude, 
			double longitude, String address, long availableMemory,
			String charger, float battery, String networks, String connections,
			long bytesReceived, long bytesTransmitted) {
		super();
		this.foregroundApp = foregroundApp;
		this.numberOfRunningApps = numberOfRunningApps;
		this.appsRunning = appsRunning;
		this.orientation = orientation;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.availableMemory = availableMemory;
		this.charger = charger;
		this.battery = battery;
		this.networks = networks; 
		this.connections = connections;
		this.bytesReceived = bytesReceived;
		this.bytesTransmitted = bytesTransmitted;
	}
	public CollectionInstance(int id, String foregroundApp,
			int numberOfRunningApps, String appsRunning, String orientation,
			double latitude, double longitude, String address,  long availableMemory,
			String charger, float battery, String networks, String connections,
			long bytesReceived, long bytesTransmitted) {
		super();
		this.id = id;
		this.foregroundApp = foregroundApp;
		this.numberOfRunningApps = numberOfRunningApps;
		this.appsRunning = appsRunning;
		this.orientation = orientation;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.availableMemory = availableMemory;
		this.charger = charger;
		this.battery = battery;
		this.networks = networks; 
		this.connections = connections;
		this.bytesReceived = bytesReceived;
		this.bytesTransmitted = bytesTransmitted;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getForegroundApp() {
		return foregroundApp;
	}
	public void setForegroundApp(String foregroundApp) {
		this.foregroundApp = foregroundApp;
	}
	public int getNumberOfRunningApps() {
		return numberOfRunningApps;
	}
	public void setNumberOfRunningApps(int numberOfRunningApps) {
		this.numberOfRunningApps = numberOfRunningApps;
	}
	public String getAppsRunning() {
		return appsRunning.toString();
	}
	public void setAppsRunning(String appsRunning) {
		this.appsRunning = appsRunning;
	}
	
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getAvailableMemory() {
		return availableMemory;
	}
	public void setAvailableMemory(long availableMemory) {
		this.availableMemory = availableMemory;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getCharger() {
		return charger;
	}
	public void setCharger(String charger) {
		this.charger = charger;
	}
	public float getBattery() {
		return battery;
	}
	public void setBattery(float battery) {
		this.battery = battery;
	}
	public String getNetworks() {
		return networks;
	}
	public void setNetworks(String networks) {
		this.networks = networks;
	}
	public String getConnections() {
		return connections;
	}
	public void setConnections(String connections) {
		this.connections = connections;
	}
	public long getBytesReceived() {
		return bytesReceived;
	}
	public void setBytesReceived(long bytesReceived) {
		this.bytesReceived = bytesReceived;
	}
	public long getBytesTransmitted() {
		return bytesTransmitted;
	}
	public void setBytesTransmitted(long bytesTransmitted) {
		this.bytesTransmitted = bytesTransmitted;
	}
	
}
