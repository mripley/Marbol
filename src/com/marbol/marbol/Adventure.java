package com.marbol.marbol;

import java.util.ArrayList;
import java.util.Date;


import android.location.Location;

public class Adventure {

	private String advName;
	private double advDistance;
	private double advArea;
	private Date advDate;
	private long AdvID;
	private ArrayList<Location> gpsPoints;
	private long advTime;
	private double elevationChange;
	private double averageSpeed;

	public Adventure(String name, double distance, double area, Date date){
		this.advName = name;
		this.advDistance = distance;
		this.advArea = area;
		this.advDate = date;
		gpsPoints = new ArrayList<Location>();
	}
	
	public Adventure(){
		this.advName = "New Adventure";
		this.advDistance = 0;
		this.advArea = 0;
		this.advDate = new Date();
		this.AdvID = -1;
		gpsPoints = new ArrayList<Location>();
	}
	
	public String getAdvName() {
		return advName;
	}

	public void setAdvName(String advName) {
		this.advName = advName;
	}

	public double getAdvArea() {
		return advArea;
	}

	public void setAdvArea(double advArea) {
		this.advArea = advArea;
	}

	public Date getAdvDate() {
		return advDate;
	}

	public void setAdvDate(Date advDate) {
		this.advDate = advDate;
	}

	public long getAdvID() {
		return AdvID;
	}

	public void setAdvID(long advID) {
		AdvID = advID;
	}
	
	public ArrayList<Location> getGpsPoints() {
		return gpsPoints;
	}

	public void addGpsPoint(Location curLocation){
		this.gpsPoints.add(curLocation);
	}
	
	public long getAdvTime() {
		return advTime;
	}

	public void setAdvTime(long advTime) {
		this.advTime = advTime;
	}

	public Integer getNumGpsPoints() {
		return this.gpsPoints.size();	
	}
	
	public void setGpsPoints(ArrayList<Location> points) {
		this.gpsPoints = points;
	}

	public void setAverageSpeed(double avgSpeed) {
		this.averageSpeed = avgSpeed;
	}

	public void setAdvDistance(double distance) {
		this.advDistance = distance;
	}
	
	public void setElevationChange(double elevationChange) {
		this.elevationChange = elevationChange;
	}
	
	@Override
	public String toString()
	{
		String str = "ID: " + this.AdvID + " Adventure: " + this.advName + " time: " + this.advTime + " area: " + this.advArea;
		str += " distance: " + this.advDistance + " num GPS points: " + this.gpsPoints.size();
		return str;
	}
	
	// compute the total distance traveled in meters and set the instance variable of this class 
	public double getDistanceInMeters(){
		
		advDistance = 0;
		
		if (this.gpsPoints.size() < 2){
			return advDistance;
		}
		
		Location start = gpsPoints.get(0);
		Location curEnd;
		for (int i = 1; i < gpsPoints.size(); i++){
			curEnd = gpsPoints.get(i);
			float results[] = new float[1];
			Location.distanceBetween(start.getLatitude(), start.getLongitude(), curEnd.getLatitude(), curEnd.getLongitude(), results);
			advDistance += results[0];
		}
		return advDistance;
	}
	
	// compute the average speed and set the instance variable of this class
	public double getAverageSpeed(){
		averageSpeed = 0;
		
		if (this.gpsPoints.size() == 0){
			return averageSpeed;
		}
		
		for (Location l : gpsPoints){
			averageSpeed += l.getSpeed();
		}
		
		averageSpeed = averageSpeed / (float)gpsPoints.size();
		return averageSpeed;
	}

	// compute the elevation difference and set the variable of the instance class
	public double getElevationDiff()
	{
		elevationChange = 0;
		double elevationMin = Double.MAX_VALUE;
		double elevationMax = Double.MIN_NORMAL;
		
		if (this.gpsPoints.size() == 0){
			return this.elevationChange;
		}
		
		for (Location l : gpsPoints){
			double alt = l.getAltitude();
			elevationMin = alt < elevationMin ? alt : elevationMin;
			elevationMax = alt > elevationMax ? alt : elevationMax;
		}
		
		elevationChange = elevationMax - elevationMin;
		return elevationChange;
	}
	
	// compute the total area and set the instance variable of this class
	// @param radius the radius of the view in meters
	public double getArea(double radius){
		advArea = 0;
		
		double length = this.getDistanceInMeters();
		
		// compute the "rectangular" part of the path
		advArea = length * (2 * radius);
		
		// add on the "end caps" 
		advArea += Math.PI * (radius * radius);
		return advArea;
	}
}
