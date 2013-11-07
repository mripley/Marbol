package com.marbol.marbol;

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

}
