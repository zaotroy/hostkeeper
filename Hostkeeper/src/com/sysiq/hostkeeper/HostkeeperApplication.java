package com.sysiq.hostkeeper;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class HostkeeperApplication extends Application {
	
	private static HostkeeperApplication instance;
	
	private KeeperHelper keeperHelper;
	
	private static Boolean serviceStatus = false;
	
	public static HostkeeperApplication getInstance() {
		return instance;
	}
	
	public static Boolean getServiceStatus() {
		return serviceStatus;
	}

	public static void setServiceStatus(Boolean serviceStatus) {
		HostkeeperApplication.serviceStatus = serviceStatus;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		keeperHelper = new KeeperHelper(this);
	}
	
	public SQLiteDatabase getDB(){
		return keeperHelper.getWritableDatabase();
	}
}
