package com.myhibernate.factory;

import com.myhibernate.databaseOperation.MySession;

public class MySessionFactory {
	private static String configFile = "/src/com/myhibernate/Xml/MyHibernate.xml";   //the path of the config file
	private static ThreadLocal<MySession> threadLocal = new ThreadLocal<MySession>();  
	static {
		try {
			XmlFactory configuration = new XmlFactory();
			configuration.configure(configFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MySession getSession() {
		MySession session = (MySession) threadLocal.get();
		if (session == null) {
			session = new MySession();
			threadLocal.set(new MySession());
		}
		return session;
	}
}
