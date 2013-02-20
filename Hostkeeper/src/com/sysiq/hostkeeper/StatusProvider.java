package com.sysiq.hostkeeper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class StatusProvider extends ContentProvider {

	public static final String DB_NAME = "hostkeeper.db";
	public static final Integer DB_VERSION = 5;
	public static final String T_STATUS = "status";
	private static final int STATUS_DIR = 1;
	private static final int STATUS_ITEM = 2;

	private static final UriMatcher URI_MATCHER;

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(StatusContract.AUTHORITY, "status", STATUS_DIR);
		URI_MATCHER.addURI(StatusContract.AUTHORITY, "status/#", STATUS_ITEM);

	}

	private HostkeeperHelper dbHelper;

	private class HostkeeperHelper extends SQLiteOpenHelper {

		private static final String DB_CREATE = "create table " + T_STATUS + " ( " + StatusContract.Columns._ID
				+ " integer primary key autoincrement, " + StatusContract.Columns.HOST + " text, " + StatusContract.Columns.STATUS + " text, "
				+ StatusContract.Columns.DATE + " integer " + " );";

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

	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new HostkeeperHelper(context);
		return (dbHelper == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case STATUS_DIR:
			return StatusContract.CONTENT_TYPE;
		case STATUS_ITEM:
			return StatusContract.CONTENT_ITEM_TYPE;
		default:
			return null;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(T_STATUS);
		switch (URI_MATCHER.match(uri)) {
		case STATUS_DIR:
			break;
		case STATUS_ITEM:
			queryBuilder.appendWhere(StatusContract.Columns._ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unsuported URI " + uri);
		}
		String orderBy = TextUtils.isEmpty(sortOrder) ? StatusContract.Columns.DEFAULT_SOTR_ODRER : sortOrder;
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri result = null;
		if (URI_MATCHER.match(uri) != STATUS_DIR) {
			throw new IllegalArgumentException("Unsuported URI " + uri);
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowID = db.insert(T_STATUS, null, values);
		if (rowID > 0) {
			result = ContentUris.withAppendedId(StatusContract.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(result, null);
		}
		return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (URI_MATCHER.match(uri)) {
		case STATUS_DIR:
			count = db.delete(T_STATUS, selection, selectionArgs);
			break;
		case STATUS_ITEM:
			String segment = uri.getLastPathSegment();
			String selectionClause = StatusContract.Columns._ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " and (" + selection + ")" : "");
			count = db.delete(T_STATUS, selectionClause, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsuported URI " + uri);
		}
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (URI_MATCHER.match(uri)) {
		case STATUS_DIR:
			count = db.update(T_STATUS, values, selection, selectionArgs);
			break;
		case STATUS_ITEM:
			String segment = uri.getLastPathSegment();
			String selectionClause = StatusContract.Columns._ID + "=" + segment + (!TextUtils.isEmpty(selection) ? " and (" + selection + ")" : "");
			count = db.update(T_STATUS, values, selectionClause, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsuported URI " + uri);
		}
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

}
