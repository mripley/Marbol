package com.marbol.marbol;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;

/**
 * Contains the logic to run the Stats fragment
 */
public class HistoryFragment extends ListFragment implements OnClickListener{

	private SimpleCursorAdapter dbAdapter;
	private AdventureDataSource dSource;
	private Cursor dbCursor;
	private View rootView;
	
	public HistoryFragment() {
		Log.i("Histroy", "History fragment created!");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.history_fragment_layout, container, false);
		
		ImageButton newAdvButton = (ImageButton)rootView.findViewById(R.id.new_adventure_button);
		newAdvButton.setOnClickListener(this);
		dSource = new AdventureDataSource(this.getActivity());
		dbAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.adventure_list_item_layout, null,
                new String[] { "adventure_name", "adventure_dist", "adventure_area", "adventure_date" },
                new int[] { R.id.adventure_name, R.id.adventure_distance, R.id.adventure_area, R.id.adventure_date}, 0);
		
		
		dSource.open();
		dbCursor= dSource.getAdventures();
		dSource.close();
		dbAdapter.changeCursor(dbCursor);
		this.setListAdapter(dbAdapter);

		return rootView; 
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		final Activity activity = this.getActivity();
		
		super.onActivityCreated(savedInstanceState);
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
				Adventure adv = (Adventure)AdventureDataSource.cursorToAdventure((Cursor)dbAdapter.getItem(position));
				Intent launcher = new Intent(activity, AdventureActivity.class);
				launcher.putExtra("com.marbol.marbol.curAdventure", adv.getAdvID());
				activity.startActivity(launcher);
			}
		});	
		
	}
	@Override
	public void onResume(){
		super.onResume();
		updateListView();
	}

	@Override
	public void onClick(View v){
		
		switch(v.getId()){
			case R.id.new_adventure_button:
				
				updateListView();
				
				Log.i("DB", "adding new adventure entry to DB");
				
				Intent launcher = new Intent(this.getActivity(), AdventureActivity.class);
				launcher.putExtra("curAdventure", -1);;
				this.startActivity(launcher);
				
				break;
			default:
				Log.e("ERROR", "No click handler found!");
				break;
		}
	}
	
	private void updateListView(){
		
		// open the data base
		dSource.open();
		
		// refresh the query
		dbCursor = dSource.getAdventures();
		dbAdapter.changeCursor(dbCursor);
		// refresh the views
		dbAdapter.notifyDataSetChanged();
				
		// before we go jumping to the new activity close the DB as we have nothing left to write.
		dSource.close();
	}
	
//	@Override
//	public void orientationChange(Configuration newConfig) {
//		
//		RelativeLayout rootHistoryLayout = (RelativeLayout)rootView.findViewById(R.id.historyRootLayout);
//		
//		RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) rootView.findViewById(R.id.new_adventure_button).getLayoutParams();
//		RelativeLayout.LayoutParams listParams = (RelativeLayout.LayoutParams) rootView.findViewById(android.R.id.list).getLayoutParams();
//		
//		ListView listView = (ListView)rootView.findViewById(android.R.id.list);
//		ImageButton newAdventureButton = (ImageButton)rootView.findViewById(R.id.new_adventure_button);
//		
//		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			listParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//			buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		}
//		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//			// undo the landscape rules			
//			listParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
//			buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);	
//		}
//		
//		listView.setLayoutParams(listParams);
//		newAdventureButton.setLayoutParams(buttonParams);
//		
//		rootHistoryLayout.updateViewLayout(listView, listParams);
//		rootHistoryLayout.updateViewLayout(newAdventureButton, buttonParams);
//		
//		String listParamString = listParams.debug(getTag());
//		String buttonParamString = buttonParams.debug(getTag());
//		
//		Log.i("AL:KJFLDS", "list Params: " + listParamString);
//		Log.i("AL:KJFLDS", "button params: " + buttonParamString);
//		
//		rootView.invalidate();
//	}

//	@Override
//	public void updateAdventure(Adventure adv) {
//		// TODO Auto-generated method stub
//		
//	}
}