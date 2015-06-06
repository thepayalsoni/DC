package com.fl.ps.parsing;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Categories implements Serializable {
	
	private static final long serialVersionUID = -5423221727488998761L;
	
	@SerializedName("subCategoryId")
	int id;
	
	@SerializedName("mainCategory")
	String categoryName;
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	

}
