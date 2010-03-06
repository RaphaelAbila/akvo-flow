package com.gallatinsystems.survey.device.activity;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Lists out all the plots currently in the DB and allows the user to
 * edit/create a new one
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class ListPlotActivity extends AbstractListEditActivity {
	private static final String EDIT_PLOT_ACTIVITY_CLASS = "com.gallatinsystems.survey.device.activity.PlotEditActivity";

	/**
	 * when a list item is clicked, get the user id and name of the selected
	 * item and return it to the calling activity.
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
		Intent intent = new Intent();
		Cursor plot = databaseAdaptor.findPlot(id);
		startManagingCursor(plot);
		intent.putExtra(ConstantUtil.ID_KEY, plot.getString(plot
				.getColumnIndexOrThrow(SurveyDbAdapter.PK_ID_COL)));
		intent.putExtra(ConstantUtil.DISPLAY_NAME_KEY, plot.getString(plot
				.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
		intent.putExtra(ConstantUtil.STATUS_KEY, plot.getString(plot
				.getColumnIndexOrThrow(SurveyDbAdapter.STATUS_COL)));
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * lists all plots from the database
	 */
	@Override
	protected Cursor getData() {
		return databaseAdaptor.listPlots(null);

	}

	/**
	 * sets up the fields that will be used by the abstract class to hydrate the
	 * view
	 */
	@Override
	protected void initializeFields() {
		instructionsStringId = R.string.plotinstructions;
		emptyStringId = R.string.noplots;
		addStringId = R.string.addplot;
		editStringId = R.string.editplot;
		editActivityClassName = EDIT_PLOT_ACTIVITY_CLASS;

	}
}
