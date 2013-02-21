package com.sysiq.hostkeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public class StatusContract {
	
	private StatusContract(){}
	
	public static final String AUTHORITY = "com.sysiq.hostkeeper.provider";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/status");
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.hostkeeper.status";
	
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.hostkeeper.status";
	
	public final static class Columns implements BaseColumns{
		
		private Columns(){}
		
		public static final String HOST = "host";

		public static final String STATUS = "status";
		
		public static final String DATE = "date";
		
		public static final String DEFAULT_SOTR_ODRER = _ID + " desc";
		
	}
	
}
