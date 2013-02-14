package com.sysiq.hostkeeper;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class HostkeeperService extends Service {

	private String hostURL = "";

	private Integer refreshPeriod = 1;
	
	private String prefURLKey;
	
	private String prefRefreshPeriodKey;

	Timer timer = new Timer();
	HostkeeperTask hostkeeperTask = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		hostkeeperTask = new HostkeeperTask();
		prefURLKey =  getApplication().getString(R.string.pref_host_url_key);
		prefRefreshPeriodKey = getApplication().getString(R.string.pref_refresh_time_key);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		hostURL = preference.getString(prefURLKey, "");
		String refreshPeriodString = preference.getString(prefRefreshPeriodKey, "1");
		try{
			refreshPeriod = Integer.parseInt(refreshPeriodString);
		}catch(Exception ex){
			refreshPeriod = 1;
		}
		timer.schedule(hostkeeperTask, 0, refreshPeriod * 60 * 1000);
		Toast.makeText(this, R.string.status_service_started, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, R.string.status_service_stoped, Toast.LENGTH_SHORT).show();
		timer.cancel();
		super.onDestroy();
	}

	private class HostkeeperTask extends TimerTask {

		@Override
		public void run() {
			HostStatus hostStatus = checkHostOnline(hostURL);
			ContentValues contentValues = new ContentValues();
			contentValues.put(KeeperHelper.KEY_DATA, new Date().toGMTString());
			contentValues.put(KeeperHelper.KEY_HOST, hostURL);
			contentValues.put(KeeperHelper.KEY_ID, UUID.randomUUID().toString());
			switch (hostStatus) {
			case APP_OFLINE:
				contentValues.put(KeeperHelper.KEY_STATUS, HostStatus.APP_OFLINE.toString());
				break;
			case CONNECTION_ERROR: {
				contentValues.put(KeeperHelper.KEY_STATUS, HostStatus.CONNECTION_ERROR.toString());
				break;
			}
			case HOST_OFLINE:
				contentValues.put(KeeperHelper.KEY_STATUS, HostStatus.HOST_OFLINE.toString());
				break;
			case HOST_ONLINE:
				contentValues.put(KeeperHelper.KEY_STATUS, HostStatus.HOST_ONLINE.toString());
				break;
			}
			HostkeeperApplication.getInstance().getDB().insert(KeeperHelper.T_STATUS, null, contentValues);
		}
	}

	public HostStatus checkHostOnline(String url) {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			return HostStatus.APP_OFLINE;
		}
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			return HostStatus.CONNECTION_ERROR;
		} catch (IOException e) {
			return HostStatus.CONNECTION_ERROR;
		}
		int stat_code = response.getStatusLine().getStatusCode();
		if (stat_code != 200)
			return HostStatus.HOST_OFLINE;
		else
			return HostStatus.HOST_ONLINE;
	}
}
