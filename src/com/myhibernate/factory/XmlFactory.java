package com.myhibernate.factory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.myhibernate.mapping.ClassMapping;
import com.myhibernate.mapping.PropertyMapping;


public class XmlFactory {
	static private Map<String, String> connection = new HashMap<String, String>(); 
	static private Map<String, ClassMapping> mapping = new HashMap<String, ClassMapping>(); 

	static public Map<String, String> getConnection() {
		return connection;
	}

	static public Map<String, ClassMapping> getMapping() {
		return mapping;
	}

	public void configure(String path) throws Exception {
		configureXML(path);
		checkMapping();
	}


	private void configureXML(String path) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbFactory.newDocumentBuilder();
			String truePath = new File("").getAbsolutePath() + path;
			Document document = db.parse(new File(truePath));
			Element eroot = document.getDocumentElement();

			NodeList nodeList = eroot.getElementsByTagName("property");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String value = element.getAttribute("name").trim();
				String content = element.getTextContent();
				if (value.equals("user"))
					connection.put("user", content);
				else if (value.equals("password"))
					connection.put("password", content);
				else if (value.equals("connectionURL"))
					connection.put("connectionURL", content);
				else if (value.equals("driver"))
					connection.put("driver", content);
			}

			nodeList = eroot.getElementsByTagName("mapping");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String value = element.getAttribute("resource").trim();
				String p = new File("").getAbsolutePath() + value;
				buildMapping(p, db);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void buildMapping(String path, DocumentBuilder db) {
		try {
			Document document = db.parse(new File(path));
			Element eroot = document.getDocumentElement();
			NodeList nodeList = eroot.getElementsByTagName("class");
			for (int i = 0; i < nodeList.getLength(); i++) {
				ClassMapping classMapping = new ClassMapping();
				Element e = (Element) nodeList.item(i);
				String className = e.getAttribute("name").trim(); 
				String tableName = e.getAttribute("table").trim(); 
				classMapping.setClassName(className);
				classMapping.setTableName(tableName);

				NodeList keyList = e.getElementsByTagName("id");
				for (int j = 0; j < keyList.getLength(); j++) {
					Element keyNode = (Element) keyList.item(j);
					buildMappingObject(classMapping, keyNode, true);
				}

				NodeList propertyList = e.getElementsByTagName("property");
				for (int j = 0; j < propertyList.getLength(); j++) {
					Element propertyNode = (Element) propertyList.item(j);
					buildMappingObject(classMapping, propertyNode, false);
				}
				mapping.put(className, classMapping);

			}
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}


	private void buildMappingObject(ClassMapping classMapping, Element e,
			boolean isID) {
		String name = e.getAttribute("name").trim();
		String column = e.getAttribute("column").trim();
		PropertyMapping propertyMapping = new PropertyMapping();
		propertyMapping.setName(name);
		propertyMapping.setColumn(column);
		propertyMapping.setID(isID);
		classMapping.getProperties().add(propertyMapping);
	}

	
	private void checkMapping() throws Exception {

		for (int i = 0; i < connection.size(); i++) {
			if (!(checkProperty(connection.get("user"))
					|| checkProperty(connection.get("password"))
					|| checkProperty(connection.get("connectionURL")) || checkProperty(connection
						.get("driver")))) {
				throw new Exception("%%%% The database connection information has problems   %%%%");
			}
		}

		boolean flag = true;
		Iterator<String> iterator = mapping.keySet().iterator();
		while (iterator.hasNext()) {
			ClassMapping classMapping = mapping.get(iterator.next());
			if (!(checkProperty(classMapping.getClassName()) || checkProperty(classMapping
					.getTableName()))) {
				flag = false;
				break;
			}
			List<PropertyMapping> propertyMapping = classMapping
					.getProperties();
			for (int i = 0; i < propertyMapping.size(); i++) {
				if (!(checkProperty(propertyMapping.get(i).getName()) || checkProperty(propertyMapping
						.get(i).getColumn()))) {
					flag = false;
					break;
				}
			}
			if (!flag)
				break;
		}
		if (!flag)
			throw new Exception("%%%% The mapping file has problems.   %%%%");
	}

	private boolean checkProperty(String value) {
		if (value == null)
			return false;
		if (value.equals(""))
			return false;
		return true;
	}

}
