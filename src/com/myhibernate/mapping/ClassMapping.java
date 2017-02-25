package com.myhibernate.mapping;

import java.util.ArrayList;
import java.util.List;


public class ClassMapping {
	private String className;		
	private String tableName;		
	private List<PropertyMapping> properties = new ArrayList<PropertyMapping>(); 	

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setProperties(List<PropertyMapping> properties) {
		this.properties = properties;
	}

	public List<PropertyMapping> getProperties() {
		return properties;
	}
}
