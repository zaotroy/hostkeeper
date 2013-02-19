package com.sysiq.hostkeeper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;

public class TabAllStatusesFragment extends ListFragment {
	
	private static final String[] FROM = {
		HostkeeperHelper.KEY_HOST,
		HostkeeperHelper.KEY_STATUS,
		HostkeeperHelper.KEY_DATA
	};
	
	private static final int[] TO = {
		R.id.host_name_label,
		R.id.status_image,
		R.id.date_label
	};
	
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
		cursor = HostkeeperApplication.getInstance().getDB().query(HostkeeperHelper.T_STATUS, null, null, null, null, null, HostkeeperHelper.KEY_DATA + " desc");
		simpleCursorAdapter = new HostkeeperCursorAdapter(getActivity().getApplicationContext(), R.layout.status_row, cursor, FROM, TO, 0);
		setListAdapter(simpleCursorAdapter);
	}
	
}
