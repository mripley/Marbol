package com.marbol.marbol;


import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

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
		map.setMyLocationEnabled(true);
	}
	
	public void updateUI(Adventure adv) {

	}

	@Override
	public void updateAdventure(Adventure adv) {
		// TODO Auto-generated method stub
		Log.i("UPDATES", "Updating the map");
		ArrayList<Location> locList = adv.getGpsPoints();
		Location lastLocation = locList.get(locList.size()-1); // get the last element in the list
		
		Log.i("UPDATES", "Updating map to "+ lastLocation.getLatitude() +", "+ lastLocation.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 10));
	}
}