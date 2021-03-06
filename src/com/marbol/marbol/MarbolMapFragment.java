package com.marbol.marbol;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.res.Configuration;
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
		
		// set a default zoom level
		map.moveCamera(CameraUpdateFactory.zoomTo(10));
		
		AdventureActivity act = (AdventureActivity)(this.getActivity());
		Adventure curAdventure = act.getCurAdventure();
		if (curAdventure != null)
		{
			updateAdventure(curAdventure);
		}
		
		// grab the nearest location and update the map accordingly
		Location startLocation = act.findNearestLocation();
		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(startLocation.getLatitude(), startLocation.getLongitude())));
	}

	@Override
	public void updateAdventure(Adventure adv) {
		Log.i("UPDATES", "Updating the map");
		ArrayList<Location> locList = adv.getGpsPoints();
		map.clear();
		// nothing to do. bail out now.
		if (locList == null || locList.size() == 0)
		{
			Log.i("UPDATE", " no locations!");
			return;
		}
		
		Location lastLocation = locList.get(locList.size()-1); // get the last element in the list
		Location firstLocation = locList.get(0);
		LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
		LatLng firstLatLng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
		
		Log.i("UPDATES", "Updating map to "+ lastLocation.getLatitude() +", "+ lastLocation.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));

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
		// put a marker at the first location we've sampled
		map.addMarker(new MarkerOptions().position(firstLatLng).title("Start"));
		
		// dump a marker at our last position
		map.addMarker(new MarkerOptions().position(lastLatLng).title("Last"));
	}

	@Override
	public void orientationChange(Configuration newConfig) {
		// TODO Auto-generated method stub
		
	}
}