package com.myhibernate.util;

import java.sql.*;
import java.util.Map;

import com.myhibernate.factory.XmlFactory;


public class JdbcUtil {
	private static Connection connection;
	static {
		Map<String, String> cnt = XmlFactory.getConnection(); 
		String driver = cnt.get("driver");
		String connectionURL = cnt.get("connectionURL");
		String user = cnt.get("user");
		String password = cnt.get("password");
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(connectionURL, user,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static Connection getConnection() throws Exception {
		return connection;
	}

	public static void close(ResultSet rs, Statement st, Connection con) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e2) {
				e2.printStackTrace();
			} finally {
				try {
					if (con != null) {
						con.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
