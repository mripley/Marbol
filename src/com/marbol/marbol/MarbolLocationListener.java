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
	public void onProviderDisabled(String provider) {
		Log.i("GPS", "Location unavailable due to GPS provider being disabled. Provider: " + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {	
		Log.i("GPS", "GPS provider enabled " + provider );
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public Location getLocation(){
		return this.curLocation;
	}
}
