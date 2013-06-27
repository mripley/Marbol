package com.marbol.marbol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment containing the logic to handle the "Map" Fragment
 */
public class MapFragment extends Fragment implements MarbolUIFragment {

	public MapFragment() {
		Log.i("Map", "Map fragment created");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);

		return rootView;
	}

	public void updateUI(Adventure adv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAdventure(Adventure adv) {
		// TODO Auto-generated method stub
		
	}
}