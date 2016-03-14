package com.stone.panoramaschool.entity;

public class Spot {
private int spotId;
private String spotName;
private String spotURL;
private String autherName;
private String spotImage;
private String spotPanorama;
private int commentCount;
private String summary;
private String spotInstruction;
private int viewCount;
private String score;
private String qq;
private double lon;
private double lat;

public Spot(){}
public Spot(int spotId,String spotName,String spotImage,String spotPanorama){
	this.spotId=spotId;
	this.spotName=spotName;
	this.spotImage=spotImage;
	this.spotPanorama=spotPanorama;
}
public int getSpotId() {
	return spotId;
}
public void setSpotId(int spotId) {
	this.spotId = spotId;
}
public String getSpotName() {
	return spotName;
}
public void setSpotName(String spotName) {
	this.spotName = spotName;
}
public String getSpotURL() {
	return spotURL;
}
public void setSpotURL(String spotURL) {
	this.spotURL = spotURL;
}
public String getAutherName() {
	return autherName;
}
public void setAutherName(String autherName) {
	this.autherName = autherName;
}
public String getSpotImage() {
	return spotImage;
}
public void setSpotImage(String spotImage) {
	this.spotImage = spotImage;
}
public String getSpotPanorama() {
	return spotPanorama;
}
public void setSpotPanorama(String spotPanorama) {
	this.spotPanorama = spotPanorama;
}
public int getCommentCount() {
	return commentCount;
}
public void setCommentCount(int commentCount) {
	this.commentCount = commentCount;
}
public int getViewCount() {
	return viewCount;
}
public void setViewCount(int viewCount) {
	this.viewCount = viewCount;
}
public String getScore() {
	return score;
}
public void setScore(String score) {
	this.score = score;
}
public String getSummary() {
	return summary;
}
public void setSummary(String summary) {
	this.summary = summary;
}
public double getLon() {
	return lon;
}
public void setLon(double lon) {
	this.lon = lon;
}
public double getLat() {
	return lat;
}
public void setLat(double lat) {
	this.lat = lat;
}
public String getSpotInstruction() {
	return spotInstruction;
}
public void setSpotInstruction(String spotInstruction) {
	this.spotInstruction = spotInstruction;
}
public String getQq() {
	return qq;
}
public void setQq(String qq) {
	this.qq = qq;
}



}
