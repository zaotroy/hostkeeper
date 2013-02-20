package com.sysiq.hostkeeper;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;

public class PieChartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int oflineRowCount = getRowCountByStatus(HostStatus.HOST_OFLINE);
		int onlineRowCount = getRowCountByStatus(HostStatus.HOST_ONLINE);
		setContentView(R.layout.activity_pie_chart);
		LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);
		GraphicalView chartView = PieChartView.getNewInstance(this, onlineRowCount, oflineRowCount);
		chartContainer.addView(chartView);
	}

	private int getRowCountByStatus(HostStatus hostStatus) {
		String[] params = new String[]{
			hostStatus.toString()
		};
		Cursor cursor = getContentResolver().query(StatusContract.CONTENT_URI, null, StatusContract.Columns.STATUS + "=?", params, null);
		int rowCount = cursor.getCount();
		return rowCount;
	}
}
