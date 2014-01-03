package com.marbol.marbol;

import java.util.HashMap;

public class MetricConverter implements MarbolUnitConverter {

	@Override
	public double[] convert(Adventure adv) {
		
		double[] retval = new double[4];
		retval[0] = adv.getArea(Adventure.STANDARD_RADIUS);
		retval[1] = (adv.getAverageSpeed() / 1000) * 60;
		retval[2] = adv.getElevationDiff();
		retval[3] = (adv.getDistanceInMeters() / 1000);
		return retval;
	}

	@Override
	public HashMap<String, String> getUnit() {
		
		HashMap<String, String> units = new HashMap<String, String>();
		units.put("area_unit", "km\u00b2");
		units.put("distance_unit", "km");
		units.put("elevation_unit", "m");
		units.put("speed_unit", "m/s");

		return units;
	}

}
