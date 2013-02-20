package com.sysiq.hostkeeper;

import android.app.Application;

public class HostkeeperApplication extends Application {

	private static HostkeeperApplication instance;

	public static HostkeeperApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
}
