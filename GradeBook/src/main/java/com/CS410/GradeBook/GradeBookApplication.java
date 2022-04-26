package com.CS410.GradeBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.*;

@SpringBootApplication
public class GradeBookApplication {
	public static Connection makeConnection() {
		try {
			Connection conn = null;
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:54051/test?verifyServerCertificate=false&useSSL=true", "msandbox",
					"dbpass30");
			// Do something with the Connection
			System.out.println("Database [test db] connection succeeded!");
			System.out.println();
			return conn;
		} catch (SQLException ex) {
			// handle any errors
			System.err.println("SQLException: " + ex.getMessage());
			System.err.println("SQLState: " + ex.getSQLState());
			System.err.println("VendorError: " + ex.getErrorCode());
		}
		return null;
	}

	public static void main(String[] args) {
		SpringApplication.run(GradeBookApplication.class, args);

		try {
			// The newInstance() call is a work around for some broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println();
			System.out.println("JDBC driver loaded");
			System.out.println();

			Connection conn = makeConnection();
			// runQuery(conn);

			conn.close();
			System.out.println();
			System.out.println("Database [test db] connection closed");
			System.out.println();
		} catch (Exception ex) {
			// handle the error
			System.err.println(ex);
		}
	}

}
