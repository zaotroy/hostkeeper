package com.sysiq.hostkeeper;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class PieChartView extends GraphicalView {

	public static final int COLOR_GREEN = Color.parseColor("#00FF00");
	public static final int COLOR_ORANGE = Color.parseColor("#FF0000");

	private PieChartView(Context context, AbstractChart arg1) {
		super(context, arg1);
	}

	public static GraphicalView getNewInstance(Context context, int online, int ofline) {
		return ChartFactory.getPieChartView(context, getDataSet(context, online, ofline), getRenderer());
	}

	private static DefaultRenderer getRenderer() {
		int[] colors = new int[] { COLOR_GREEN, COLOR_ORANGE };

		DefaultRenderer defaultRenderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor(color);
			defaultRenderer.addSeriesRenderer(simpleRenderer);
		}
		defaultRenderer.setShowLabels(true);
		defaultRenderer.setShowLegend(false);
		return defaultRenderer;
	}

	private static CategorySeries getDataSet(Context context, int online, int ofline) {
		CategorySeries series = new CategorySeries("Chart");
		series.add(context.getString(R.string.label_pie_online), online);
		series.add(context.getString(R.string.label_pie_ofline), ofline);
		return series;
	}

}
