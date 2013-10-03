package com.marbol.marbol;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment containing the logic to handle the "Map" Fragment
 */
public class MarbolMapFragment extends Fragment implements MarbolUIFragment {
	private GoogleMap map;
	private SupportMapFragment mapFragment;
	
	public MarbolMapFragment() {
		Log.i("Map", "Map fragment created");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mapFragment = (SupportMapFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();
		
		if (map == null){
			Log.i("map", "Map is null!");
		}
		AdventureActivity act = (AdventureActivity)(this.getActivity());
		Adventure curAdventure = act.getCurAdventure();
		if (curAdventure != null)
		{
			updateAdventure(curAdventure);
		}
	}
	
	public void updateUI(Adventure adv) {

	}

	@Override
	public void updateAdventure(Adventure adv) {
		Log.i("UPDATES", "Updating the map");
		ArrayList<Location> locList = adv.getGpsPoints();
		
		// nothing to do. bail out now.
		if (locList == null || locList.size() == 0)
		{
			return;
		}
		
		Location lastLocation = locList.get(locList.size()-1); // get the last element in the list
		LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
		
		Log.i("UPDATES", "Updating map to "+ lastLocation.getLatitude() +", "+ lastLocation.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(),
				lastLocation.getLongitude()), 13));
		Log.i("UPDATES", "Going to draw " + locList.size() + " points");
		// if we have more than 2 points draw a line
		if (locList.size() > 2)
		{
			PolylineOptions lineOptions = new PolylineOptions();
			List<LatLng> latLongList = new ArrayList<LatLng>();
			
			// convert to a list of latlongs
			for (Location l : locList)
			{
				latLongList.add(new LatLng(l.getLatitude(), l.getLongitude()));
			}
			
			lineOptions.color(Color.argb(150, 0, 255, 0));
			lineOptions.width(10);
			lineOptions.addAll(latLongList);
			map.addPolyline(lineOptions);	
		}
		// dump a marker at our last position
		map.addMarker(new MarkerOptions().position(lastLatLng));
	}
}