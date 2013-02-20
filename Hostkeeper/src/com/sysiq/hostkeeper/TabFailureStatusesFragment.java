package com.sysiq.hostkeeper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;

public class TabFailureStatusesFragment extends ListFragment {

	private static final String[] FROM = { StatusContract.Columns.HOST, StatusContract.Columns.STATUS, StatusContract.Columns.DATE };

	private static final int[] TO = { R.id.host_name_label, R.id.status_image, R.id.date_label };

	private Cursor cursor;

	private SimpleCursorAdapter simpleCursorAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initListData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cursor.close();
	}

	@Override
	public void onPause() {
		super.onPause();
		cursor.deactivate();
	}

	@Override
	public void onResume() {
		super.onResume();
		initListData();
	}

	private void initListData() {
		String[] params = new String[] { HostStatus.HOST_OFLINE.toString() };
		cursor = getActivity().getContentResolver().query(StatusContract.CONTENT_URI, null, StatusContract.Columns.STATUS + "=?", params, null);
		simpleCursorAdapter = new HostkeeperCursorAdapter(getActivity().getApplicationContext(), R.layout.status_row, cursor, FROM, TO, 0);
		setListAdapter(simpleCursorAdapter);
	}

}
