package com.sysiq.hostkeeper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.Toast;

public class HostkeeperService extends Service {

	private RemoteViews remoteViews;

	private String hostURL = "";

	private Integer refreshPeriod = 1;

	Timer timer = new Timer();
	HostkeeperTask hostkeeperTask = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		hostkeeperTask = new HostkeeperTask(this.getApplicationContext());
	}

	@Override
	public void onStart(Intent intent, int startId) {
		remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.activity_main);
		hostURL = "http://google.com";
		refreshPeriod = 1;
		timer.schedule(hostkeeperTask, 1000, refreshPeriod * 1000);
		Toast.makeText(this, R.string.status_service_started, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, R.string.status_service_stoped, Toast.LENGTH_SHORT).show();
		timer.cancel();
		super.onDestroy();
	}

	private class HostkeeperTask extends TimerTask {

		private Context context;

		public HostkeeperTask(Context context) {
			this.context = context;
		}

		@Override
		public void run() {
			Looper.prepare();
			HostStatus hostStatus = checkHostOnline(hostURL);
			switch (hostStatus) {
			case APP_OFLINE:
				remoteViews.setImageViewResource(R.id.current_status_image, R.drawable.ic_cross);
				Toast.makeText(context, R.string.status_app_ofline, Toast.LENGTH_SHORT).show();
				break;
			case CONNECTION_ERROR: {
				remoteViews.setImageViewResource(R.id.current_status_image, R.drawable.ic_cross);
				Toast.makeText(context, R.string.status_connection_error, Toast.LENGTH_SHORT).show();
				break;
			}
			case HOST_OFLINE:
				remoteViews.setImageViewResource(R.id.current_status_image, R.drawable.ic_cross);
				Toast.makeText(context, R.string.status_host_ofline, Toast.LENGTH_SHORT).show();
				break;
			case HOST_ONLINE:
				remoteViews.setImageViewResource(R.id.current_status_image, R.drawable.ic_tick);
				Toast.makeText(context, R.string.status_host_online, Toast.LENGTH_SHORT).show();
			}
			Looper.loop();
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
