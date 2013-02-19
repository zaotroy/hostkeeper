package com.sysiq.hostkeeper;

import java.util.HashMap;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.ToggleButton;

public class MainActivity extends FragmentActivity implements OnClickListener, TabHost.OnTabChangeListener {

	private static final String HOSTKEEPER_SERVICE_NAME = "com.sysiq.hostkeeper.HostkeeperService";

	private TabHost tabHost;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
	private TabInfo lastTab = null;
	private ToggleButton serviceButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		serviceButton = (ToggleButton) findViewById(R.id.service_button);
		serviceButton.setOnClickListener(this);
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		if (isMyServiceRunning(this.getApplicationContext())) {
			serviceButton.setChecked(true);
		} else {
			serviceButton.setChecked(false);
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", tabHost.getCurrentTabTag());
		super.onSaveInstanceState(outState);
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

	@Override
	public void onTabChanged(String tag) {
		TabInfo newTab = mapTabInfo.get(tag);
		if (lastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			if (lastTab != null) {
				if (lastTab.fragment != null) {
					ft.detach(lastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this, newTab.clazz.getName(), newTab.bundle);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}
			lastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
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

	//
	// private StatusRecord getLastStatusRecord() {
	// StatusRecord statusRecord = null;
	// String query = "select " + KeeperHelper.KEY_ID + ", " +
	// KeeperHelper.KEY_HOST + ", " +
	// KeeperHelper.KEY_STATUS + ", " +
	// KeeperHelper.KEY_DATA +
	// " from " + KeeperHelper.T_STATUS + " order by " + KeeperHelper.KEY_ID +
	// " desc limit 1;";
	// Cursor cursor =
	// HostkeeperApplication.getInstance().getDB().rawQuery(query, null);
	// if (null != cursor && cursor.moveToFirst()) {
	// statusRecord = new StatusRecord();
	// statusRecord.setId(cursor.getInt(0));
	// statusRecord.setHost(cursor.getString(1));
	// String statusString = cursor.getString(2);
	// if(HostStatus.APP_OFLINE.toString().equals(statusString)){
	// statusRecord.setHostStatus(HostStatus.APP_OFLINE);
	// }else if(HostStatus.CONNECTION_ERROR.toString().equals(statusString)){
	// statusRecord.setHostStatus(HostStatus.CONNECTION_ERROR);
	// }else if(HostStatus.HOST_OFLINE.toString().equals(statusString)){
	// statusRecord.setHostStatus(HostStatus.HOST_OFLINE);
	// }else if(HostStatus.HOST_ONLINE.toString().equals(statusString)){
	// statusRecord.setHostStatus(HostStatus.HOST_ONLINE);
	// }
	// statusRecord.setDate(cursor.getString(3));
	// }
	// return statusRecord;
	// }

	private void initialiseTabHost(Bundle bundle) {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabInfo tabInfo = null;
		tabInfo = addTab(this, tabHost, tabHost.newTabSpec("Tab1").setIndicator(getString(R.string.tab_all_label)), new TabInfo("Tab1", TabAllStatusesFragment.class, bundle));
		mapTabInfo.put(tabInfo.tag, tabInfo);
		tabInfo = addTab(this, tabHost, tabHost.newTabSpec("Tab2").setIndicator(getString(R.string.tab_failure_statuses)), new TabInfo("Tab2", TabFailureStatusesFragment.class, bundle));
		mapTabInfo.put(tabInfo.tag, tabInfo);
		onTabChanged("Tab1");
		tabHost.setOnTabChangedListener(this);
	}

	private TabInfo addTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();
		tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}
		tabHost.addTab(tabSpec);
		return tabInfo;
	}

	private class TabInfo {
		private String tag;
		private Class<?> clazz;
		private Bundle bundle;
		private Fragment fragment;

		TabInfo(String tag, Class<?> clazz, Bundle bundle) {
			this.tag = tag;
			this.clazz = clazz;
			this.bundle = bundle;
		}

	}

	private class TabFactory implements TabContentFactory {

		private final Context context;

		public TabFactory(Context context) {
			this.context = context;
		}

		public View createTabContent(String tag) {
			View v = new View(context);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}
}
