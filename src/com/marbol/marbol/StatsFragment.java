package com.marbol.marbol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;

/**
 * Contains the logic to run the Stats fragment
 */
public class StatsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private SimpleCursorAdapter DBAdapter;
	
	public StatsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.stats_fragment_layout, container, false);
		
		DBAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.adventure_list_item_layout, null,
                new String[] { "Adventure Name", "total distance", "total area"},
                new int[] { R.id.adventure_name, R.id.total_distance, R.id.total_area}, 0);

		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        DBAdapter.swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	    // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        DBAdapter.swapCursor(null);
		
	}
}