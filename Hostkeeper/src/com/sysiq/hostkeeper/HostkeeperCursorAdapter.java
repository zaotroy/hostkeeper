package com.sysiq.hostkeeper;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HostkeeperCursorAdapter extends SimpleCursorAdapter {
	
	private Context context;
	
	public HostkeeperCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(R.id.date_label);
        ImageView imageView = (ImageView) view.findViewById(R.id.status_image);
        Cursor cursorLocal = (Cursor) getItem(position);
        String status = cursorLocal.getString(cursorLocal.getColumnIndex(StatusContract.Columns.STATUS));
        String date = cursorLocal.getString(cursorLocal.getColumnIndex(StatusContract.Columns.DATE));
        if(HostStatus.HOST_OFLINE.toString().equals(status)){
        	imageView.setImageResource(R.drawable.ic_red_point);
        }else if(HostStatus.HOST_ONLINE.toString().equals(status)){
        	imageView.setImageResource(R.drawable.ic_green_point);
        }
        textView.setText(DateUtils.getRelativeTimeSpanString(context, new Date(date).getTime()));
        return view;
	}

}
