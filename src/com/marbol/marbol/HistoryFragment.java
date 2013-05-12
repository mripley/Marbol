package com.marbol.marbol;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.database.Cursor;

/**
 * Contains the logic to run the Stats fragment
 */
public class HistoryFragment extends ListFragment implements OnClickListener{

	private SimpleCursorAdapter dbAdapter;
	private AdventureDataSource dSource;
	private Cursor dbCursor;
	public HistoryFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.history_fragment_layout, container, false);
		
		Button newAdvButton = (Button)rootView.findViewById(R.id.new_adventure_button);
		newAdvButton.setOnClickListener(this);
		dSource = new AdventureDataSource(this.getActivity());
		dbAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.adventure_list_item_layout, null,
                new String[] { "adventure_name", "adventure_dist", "adventure_area", "adventure_date" },
                new int[] { R.id.adventure_name, R.id.adventure_distance, R.id.adventure_area, R.id.adventure_date}, 0);
		
		
		dSource.open();
		dbCursor= dSource.getAdventures();
		dbAdapter.changeCursor(dbCursor);
		this.setListAdapter(dbAdapter);
		return rootView;
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.new_adventure_button:
			Adventure adv = new Adventure();
			adv.setAdvName("Test Adventure");
			adv.setAdvDistance(42.0);
			adv.setAdvArea(36.0);
			adv.setAdvTime(1000);
			dSource.addAdventure(adv);
			
			// close the old cursor
			dbCursor.close();
			
			// refresh the query
			dbCursor = dSource.getAdventures();
			dbAdapter.changeCursor(dbCursor);
			// refresh the views
			dbAdapter.notifyDataSetChanged();
			
			Log.i("DB", "ADDED NEW ENTRY!");
			Intent launcher = new Intent(this.getActivity(), AdventureActivity.class);
			launcher.putExtra("curAdventure", -1);
			this.startActivity(launcher);
			
			break;
		default:
			Log.e("ERROR", "No click handler found!");
		}
		
	}
}