package com.sysiq.hostkeeper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class TabAllStatusesFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final String[] FROM = { StatusContract.Columns.HOST, StatusContract.Columns.STATUS, StatusContract.Columns.DATE };

	private static final int[] TO = { R.id.host_name_label, R.id.status_image, R.id.date_label };

	private SimpleCursorAdapter simpleCursorAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		simpleCursorAdapter = new HostkeeperCursorAdapter(getActivity().getApplicationContext(), R.layout.status_row, null, FROM, TO, 0);
		setListAdapter(simpleCursorAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity().getApplicationContext(), StatusContract.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		simpleCursorAdapter.swapCursor(cursor);
		simpleCursorAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		simpleCursorAdapter.swapCursor(null);
	}

}
