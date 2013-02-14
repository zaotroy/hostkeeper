package com.sysiq.hostkeeper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
}
