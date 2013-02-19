package com.sysiq.hostkeeper;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;

public class PieChartActivity extends Activity {

	private static final String COUNT_QUERY = "select * from " + HostkeeperHelper.T_STATUS + " where " + HostkeeperHelper.KEY_STATUS + "=?";

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
		String[] param = new String[]{
			hostStatus.toString()
		};
		Cursor cursor = HostkeeperApplication.getInstance().getDB().rawQuery(COUNT_QUERY, param);
		int rowCount = cursor.getCount();
		return rowCount;
	}
}
