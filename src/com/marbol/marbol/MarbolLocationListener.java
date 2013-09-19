package com.marbol.marbol;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MarbolLocationListener implements LocationListener{

	private Location curLocation;
	@Override
	public void onLocationChanged(Location loc) {
		if (loc != null){
			curLocation = loc;		
		}
	}

	
	@Override
	public void onProviderDisabled(String arg0) {
		Log.i("GPS", "Location unavailable due to GPS provider being disabled");
	}

	@Override
	public void onProviderEnabled(String arg0) {	
		Log.i("GPS", "GPS provider enabled");
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	public Location getLocation(){
		return this.curLocation;
	}
}
