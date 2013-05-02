package com.marbol.marbol;

import java.util.Date;

public class Adventure {

	private String advName;
	private double advDistance;
	private double advArea;
	private Date advDate;
	private long AdvID;
	
	public Adventure(String name, double distance, double area, Date date){
		this.advName = name;
		this.advDistance = distance;
		this.advArea = area;
		this.advDate = date;
	}
	
	public Adventure(){
		this.advName = "New Adventure";
		this.advDistance = 0;
		this.advArea = 0;
		this.advDate = new Date();
		this.AdvID = -1;
	}
	
	public String getAdvName() {
		return advName;
	}

	public void setAdvName(String advName) {
		this.advName = advName;
	}

	public double getAdvDistance() {
		return advDistance;
	}

	public void setAdvDistance(double advDistance) {
		this.advDistance = advDistance;
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
}
