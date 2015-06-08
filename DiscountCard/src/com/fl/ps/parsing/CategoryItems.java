package com.fl.ps.parsing;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CategoryItems implements Serializable {

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
	private String latitude;

	@SerializedName("longitude")
	private String longitude;

	@SerializedName("url")
	private String imageUrl;

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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
