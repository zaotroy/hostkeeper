package com.sysiq.hostkeeper;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/*
 * 
 */
public class HostkeeperApplication extends Application {
	
	private static HostkeeperApplication instance;
	
	private HostkeeperHelper keeperHelper;
	
	public static HostkeeperApplication getInstance() {
		return instance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		keeperHelper = new HostkeeperHelper(this);
	}
	
	public SQLiteDatabase getDB(){
		return keeperHelper.getWritableDatabase();
	}
}
