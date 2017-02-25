package com.myhibernate.mapping;

public class PropertyMapping {
	private String name;		//name of the class
	private String column;		//element
	private boolean isID;		//whether is the key

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getColumn() {
		return column;
	}

	public void setID(boolean isID) {
		this.isID = isID;
	}

	public boolean isID() {
		return isID;
	}

}
