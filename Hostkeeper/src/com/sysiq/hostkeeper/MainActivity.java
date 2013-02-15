package com.sysiq.hostkeeper;

import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {

	private static final String HOSTKEEPER_SERVICE_NAME = "com.sysiq.hostkeeper.HostkeeperService";
	private ToggleButton serviceButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		serviceButton = (ToggleButton) findViewById(R.id.service_button);
		serviceButton.setOnClickListener(this);
		if (isMyServiceRunning(this.getApplicationContext())) {
			serviceButton.setChecked(true);
		} else {
			serviceButton.setChecked(false);
		}
		ImageView image = (ImageView) findViewById(R.id.current_status_image);
		TextView timeLabel = (TextView) findViewById(R.id.time_label);
		TextView hostLabel = (TextView) findViewById(R.id.host_name_label);
		StatusRecord statusRecord = getLastStatusRecord();
		if (null != statusRecord) {
			timeLabel.setText(DateUtils.getRelativeTimeSpanString(this, new Date(statusRecord.getDate()).getTime()));
			hostLabel.setText(statusRecord.getHost());
			if (HostStatus.APP_OFLINE.equals(statusRecord.getHostStatus()) || HostStatus.CONNECTION_ERROR.equals(statusRecord.getHostStatus())) {
				image.setImageResource(R.drawable.ic_question);
			} else if (HostStatus.HOST_OFLINE.equals(statusRecord.getHostStatus())) {
				image.setImageResource(R.drawable.ic_cross);
			} else if (HostStatus.HOST_ONLINE.equals(statusRecord.getHostStatus())) {
				image.setImageResource(R.drawable.ic_tick);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_preferences:
			Intent intent = new Intent(this, PrefsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.service_button:
			Intent intent = new Intent(this.getApplicationContext(), HostkeeperService.class);
			if (serviceButton.isChecked()) {
				startService(intent);
			} else {
				stopService(intent);
			}
			break;
		default:
			break;
		}
	}

	private boolean isMyServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (HOSTKEEPER_SERVICE_NAME.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private StatusRecord getLastStatusRecord() {
		StatusRecord statusRecord = null;
		String query = "select " + KeeperHelper.KEY_ID + ", " +
				KeeperHelper.KEY_HOST + ", " +
				KeeperHelper.KEY_STATUS + ", " +
				KeeperHelper.KEY_DATA +
				" from " + KeeperHelper.T_STATUS + " order by " + KeeperHelper.KEY_ID + " desc limit 1;";
		Cursor cursor = HostkeeperApplication.getInstance().getDB().rawQuery(query, null);
		if (null != cursor && cursor.moveToFirst()) {
			statusRecord = new StatusRecord();
			statusRecord.setId(cursor.getInt(0));
			statusRecord.setHost(cursor.getString(1));
			String statusString = cursor.getString(2);
			if(HostStatus.APP_OFLINE.toString().equals(statusString)){
				statusRecord.setHostStatus(HostStatus.APP_OFLINE);
			}else if(HostStatus.CONNECTION_ERROR.toString().equals(statusString)){
				statusRecord.setHostStatus(HostStatus.CONNECTION_ERROR);
			}else if(HostStatus.HOST_OFLINE.toString().equals(statusString)){
				statusRecord.setHostStatus(HostStatus.HOST_OFLINE);
			}else if(HostStatus.HOST_ONLINE.toString().equals(statusString)){
				statusRecord.setHostStatus(HostStatus.HOST_ONLINE);
			}
			statusRecord.setDate(cursor.getString(3));
		}
		return statusRecord;
	}
}
