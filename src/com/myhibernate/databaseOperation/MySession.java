package com.myhibernate.databaseOperation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.myhibernate.factory.XmlFactory;
import com.myhibernate.mapping.ClassMapping;
import com.myhibernate.mapping.PropertyMapping;
import com.myhibernate.util.JdbcUtil;

import java.sql.*;

public class MySession {
	static private Map<String, ClassMapping> mapping = XmlFactory.getMapping();
	private Queue<String> SQLList = new LinkedList<String>(); 


	public void save(Object ob) {
		ClassMapping classMapping = getClassMapping(ob.getClass().getName()); 
		List<PropertyMapping> propertyMapping = classMapping.getProperties(); 
		String columns = "";
		String values = "";
		for (int i = 0; i < propertyMapping.size(); i++) {
			String methodString = getMethodString(propertyMapping.get(i)
					.getName());
			Object returnValue = getReturnValue(ob, methodString);
			columns = columns + "," + propertyMapping.get(i).getColumn();
			if (!(returnValue instanceof String))
				values = values + "," + returnValue;
			else
				values = values + "," + "'" + returnValue + "'";
		}
		

		String sql = "insert into " + classMapping.getTableName() + "("
				+ columns.substring(1) + ")" + "values(" + values.substring(1)
				+ ");";
		SQLList.add(sql);
		System.out.println(SQLList);
	}


	public void delete(Object ob) {
		ClassMapping classMapping = getClassMapping(ob.getClass().getName());
		List<PropertyMapping> propertyMapping = classMapping.getProperties();
		for (int i = 0; i < propertyMapping.size(); i++) {
			if (propertyMapping.get(i).isID()) {
				String methodString = getMethodString(propertyMapping.get(i)
						.getName());
				Object returnValue = getReturnValue(ob, methodString);
				String sql = "delete from " + classMapping.getTableName()
						+ " where " + propertyMapping.get(i).getColumn()
						+ " = ";
				if (!(returnValue instanceof String))
					sql = sql + returnValue + ";";
				else
					sql = sql + "'" + returnValue + "';";
				SQLList.add(sql);
				break;
			}
		}
	}

	public void delete(Class<?> clazz, Object key) {
		ClassMapping classMapping = getClassMapping(clazz.getName());
		List<PropertyMapping> propertyMapping = classMapping.getProperties();
		for (int i = 0; i < propertyMapping.size(); i++) {
			if (propertyMapping.get(i).isID()) {
				String sql = "delete from " + classMapping.getTableName()
						+ " where " + propertyMapping.get(i).getColumn()
						+ " = ";
				if (!(key instanceof String))
					sql = sql + key + ";";
				else
					sql = sql + "'" + key + "';";
				SQLList.add(sql);
				break;
			}
		}
	}

	public void update(Object ob) {
		ClassMapping classMapping = getClassMapping(ob.getClass().getName());
		List<PropertyMapping> propertyMapping = classMapping.getProperties();
		String columns = "";
		String condition = "";
		for (int i = 0; i < propertyMapping.size(); i++) {
			String methodString = getMethodString(propertyMapping.get(i)
					.getName());
			Object returnValue = getReturnValue(ob, methodString);
			if (propertyMapping.get(i).isID()) {
				condition = propertyMapping.get(i).getColumn() + " = ";
				if (!(returnValue instanceof String))
					condition = condition + returnValue;
				else
					condition = condition + "'" + returnValue + "'";
			} else {
				if (!(returnValue instanceof String))
					columns = columns + ","
							+ propertyMapping.get(i).getColumn() + " = "
							+ returnValue;
				else
					columns = columns + ","
							+ propertyMapping.get(i).getColumn() + " = '"
							+ returnValue + "'";
			}
		}
		

		String sql = " update " + classMapping.getTableName() + " set "
				+ columns.substring(1) + " where " + condition + ";";
		SQLList.add(sql);
		System.out.println(SQLList);
	}


	public List<?> query(Class<?> clazz) {

		ClassMapping classMapping = getClassMapping(clazz.getName());
		List<PropertyMapping> propertyMapping = classMapping.getProperties();
		String sql = "select * from " + classMapping.getTableName() + " ; ";
		return getObjects(clazz, sql, propertyMapping);
	}

	public List<?> query(Class<?> clazz, Object key) {
		ClassMapping classMapping = getClassMapping(clazz.getName());
		List<PropertyMapping> propertyMapping = classMapping.getProperties();
		String sql = "select * from " + classMapping.getTableName();
		for (int i = 0; i < propertyMapping.size(); i++) {
			if (propertyMapping.get(i).isID()) {
				sql = sql + " where " + propertyMapping.get(i).getColumn()
						+ " = ";
				if (!(key instanceof String))
					sql = sql + key + ";";
				else
					sql = sql + "'" + key + "';";
				break;
			}
		}
		return getObjects(clazz, sql, propertyMapping);
	}

	private List<?> getObjects(Class<?> clazz, String sql,
			List<PropertyMapping> propertyMapping) {
		List<Object> resultList = new ArrayList<Object>();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = JdbcUtil.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				Object obj = clazz.newInstance();
				for (int i = 0; i < propertyMapping.size(); i++) {
					Field field = clazz.getDeclaredField(propertyMapping.get(i)
							.getName());
					String methodString = setMethodString(propertyMapping
							.get(i).getName());
					Method method = clazz.getMethod(methodString,
							field.getType());
					Object value = resultSet.getObject(propertyMapping.get(i)
							.getColumn());
					method.invoke(obj, value);
				}
				resultList.add(obj);
			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			JdbcUtil.close(resultSet, statement, null);
		}
		return resultList;
	}


	private ClassMapping getClassMapping(String key) {
		if (mapping.get(key) == null)
			try {
				throw new Exception("No information compair to   " + key + " ！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		return mapping.get(key);
	}


	private String getMethodString(String propertyName) {
		String firstChar = propertyName.substring(0, 1);
		String lastString = propertyName.substring(1);
		return "get" + firstChar.toUpperCase() + lastString;
	}


	private String setMethodString(String propertyName) {
		String firstChar = propertyName.substring(0, 1);
		String lastString = propertyName.substring(1);
		return "set" + firstChar.toUpperCase() + lastString;
	}


	private Object getReturnValue(Object ob, String methodString) {
		try {
			Class<?> clazz = ob.getClass();
			Method method = clazz.getMethod(methodString);
			Object returnValue = method.invoke(ob);
			return returnValue;
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
		return null;
	}


	public boolean commit() {
		Statement statement = null;
		try {
			Connection connection = JdbcUtil.getConnection();
			statement = connection.createStatement();
			while (SQLList.size() > 0) {
				statement.executeUpdate(SQLList.poll());
			}
			return true;
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			JdbcUtil.close(null, statement, null);
		}
		return false;
	}

}
