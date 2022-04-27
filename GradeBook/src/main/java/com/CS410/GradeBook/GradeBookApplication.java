package com.CS410.GradeBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class GradeBookApplication {
//	public static Connection makeConnection() {
//		try {
//			Connection conn = null;
//			conn = DriverManager.getConnection(
//					"jdbc:mysql://localhost:54051/test?verifyServerCertificate=false&useSSL=true", "msandbox",
//					"dbpass30");
//			// Do something with the Connection
//			System.out.println("Database [test db] connection succeeded!");
//			System.out.println();
//			return conn;
//		} catch (SQLException ex) {
//			// handle any errors
//			System.err.println("SQLException: " + ex.getMessage());
//			System.err.println("SQLState: " + ex.getSQLState());
//			System.err.println("VendorError: " + ex.getErrorCode());
//		}
//		return null;
//	}

	public static void main(String[] args) {
		SpringApplication.run(GradeBookApplication.class, args);

//		try {
//			// The newInstance() call is a work around for some broken Java implementations
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//			System.out.println();
//			System.out.println("JDBC driver loaded");
//			System.out.println();
//
//			Connection conn = makeConnection();
//			// runQuery(conn);
//
//			conn.close();
//			System.out.println();
//			System.out.println("Database [test db] connection closed");
//			System.out.println();
//		} catch (Exception ex) {
//			// handle the error
//			System.err.println(ex);
//		}
	}
	@Autowired
	JdbcTemplate jdbc;

	public void run(String... strings) throws Exception {
		jdbc.execute("INSERT INTO classes (class_id) VALUES ('1') " +
				"course_number = 'CS410', " +
				"term = 'SP22', " +
				"section_number = '001', "+
				"description = 'Software Engineering', ");
	}


//	@Autowired
//	JdbcTemplate jdbcTemplate;

	//@Override
//	public void run(String... strings) throws Exception {
//
//		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
//		jdbcTemplate.execute("CREATE TABLE customers(" +
//				"id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
//
//		// Split up the array of whole names into an array of first/last names
//		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
//				.map(name -> name.split(" "))
//				.collect(Collectors.toList());
//
//		// Use a Java 8 stream to print out each tuple of the list
//		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));
//
//		// Uses JdbcTemplate's batchUpdate operation to bulk load data
//		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
//
//		//log.info("Querying for customer records where first_name = 'Josh':");
//		jdbcTemplate.query(
//				"SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { "Josh" },
//				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"),
//						rs.getString("last_name"))
//		).forEach(customer -> log.info(customer.toString()));
//	}

}
