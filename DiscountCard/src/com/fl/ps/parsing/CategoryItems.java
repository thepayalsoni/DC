package com.fl.ps.parsing;

import java.io.Serializable;
import java.util.Comparator;

import com.google.gson.annotations.SerializedName;

public class CategoryItems implements Serializable,Comparator<CategoryItems> {

	private static final long serialVersionUID = -5423221727488998760L;

	@SerializedName("mainCategory")
	private String mainCategory;

	@SerializedName("id")
	private String id;

	@SerializedName("name")
	private String name;

	@SerializedName("address")
	private String address;

	@SerializedName("about")
	private String about;

	@SerializedName("discount")
	private String discount;

	@SerializedName("discription")
	private String description;

	@SerializedName("rating")
	private String rating;

	@SerializedName("latitude")
	private Double latitude;

	@SerializedName("longitude")
	private Double longitude;

	@SerializedName("url")
	private String imageUrl;

	private Double distance;

	public String getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	@Override
	public int compare(CategoryItems o1, CategoryItems o2) {
		// TODO Auto-generated method stub
		 return o1.distance > o2.distance ? 1 : (o1.distance < o2.distance ? -1 : 0);
	}

	

	

}
