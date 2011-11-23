package com.forever.bike;

/**
 * 
 * @author zhulicong89@gmail.com
 *
 */
public class Station {
	private double lat,lng;
	private String name,address;
	private String pillar_num;
	private String bike_num;
	public double getlat() {
		return lat;
	}
	public double getlng() {
		return lng;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getPillar_num() {
		return pillar_num;
	}
	public String getBike_num() {
		return bike_num;
	}
	public void setlat(double lat) {
		this.lat = lat;
	}
	public void setlng(double lng) {
		this.lng = lng;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPillar_num(String pillar_num) {
		this.pillar_num = pillar_num;
	}
	public void setBike_num(String bike_num) {
		this.bike_num = bike_num;
	}
	
	public Station(String name,String address,double x,double y,String pillar_num,String bike_num){
		this.name=name;
		this.address=address;
		this.lat=x;
		this.lng=y;
		this.pillar_num=pillar_num;
		this.bike_num=bike_num;
	}
	
	public Station(){
		this.name="";
		this.address="";
		this.lat=(double) 31.102021;
		this.lng=(double) 121.3906;
		this.pillar_num="";
		this.bike_num="";
	}
	
}
