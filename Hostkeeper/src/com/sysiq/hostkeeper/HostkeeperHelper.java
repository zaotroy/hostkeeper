package com.sysiq.hostkeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class HostkeeperHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "hostkeeper.db";
	public static final Integer DB_VERSION = 4;
	public static final String T_STATUS = "status";
	public static final String KEY_ID = BaseColumns._ID;
	public static final String KEY_HOST = "host";
	public static final String KEY_STATUS = "status";
	public static final String KEY_DATA = "date";

	private static final String DB_CREATE = "create table " + T_STATUS + " ( " + KEY_ID + " integer primary key autoincrement, " + KEY_HOST + " text, " + KEY_STATUS + " text, " + KEY_DATA + " integer " + " );";

	public HostkeeperHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + T_STATUS);
		onCreate(db);
	}

}
