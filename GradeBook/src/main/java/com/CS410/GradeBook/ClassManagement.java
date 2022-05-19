package com.CS410.GradeBook;

import java.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
public class ClassManagement {

	@Autowired
	JdbcTemplate jdbc;
	
	/**
	 * Activates specified class to be used in other commands.
	 * 
	 * Specifying only the course parameter will select the most recent 
	 * term, and fail if there are multiple sections in that term.
	 * 
	 * Specifying the course and term will fail if there are multiple
	 * sections in that term.
	 * 
	 * Specifying all parameters will only fail if that class is not found.
	 * @param course - e.g. CS410
	 * @param term - e.g. Fa19
	 * @param section - e.g. 1
	 * @throws SQLException
	 */
	@ShellMethod("Activate a class")
	public void selectClass(String course, @ShellOption(defaultValue = "") String term, 
							@ShellOption(defaultValue = "0") int section) throws SQLException{
								
		if(section != 0){	//user specified all parameters
			String query = "SELECT class_id, course_number, term, description, section_number " 
							+ "FROM classes "
							+ "WHERE course_number = \"" + course + "\" "
							+ "AND term = \"" + term + "\" "
							+ "AND section_number = \"" + section + "\" "
							+ "ORDER BY SUBSTRING(term,2,3) DESC";
			Connection con = jdbc.getDataSource().getConnection();
			try(Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
				ResultSet rs = stmt.executeQuery(query);
				int size =0;
				if (rs != null) {
					rs.last();    // moves cursor to the last row
					size = rs.getRow(); // get row id
					rs.beforeFirst(); // moves cursor to the first row
				}
				if(size == 0){
					System.out.println("Select Failed: No classes with that course number");
					return;
				}
				while(rs.next()){
					if(size>1){
						System.out.println("Select Failed: Multiple classes with same course number and term");
						return;
					}
					System.out.println("Course Number | Term | Description | Section Number");
					Helpers.setSelectedCourse(rs.getInt("class_id"));
					String courseNum = rs.getString("course_number");
					String termVal = rs.getString("term");
					String desc = rs.getString("description");
					int sectionNum = rs.getInt("section_number");

					System.out.println(courseNum + ", " + termVal + ", "  + desc + ", " + sectionNum);
				}
				con.close();
			}
			catch (SQLException e){
				System.out.println("Error: " + e);
				con.close();
			}
		} else if(!term.equals("")){ //User specified course number and term
			String query = "SELECT class_id, course_number, term, description, section_number "  
							+ "FROM classes "
							+ "WHERE course_number = \"" + course + "\" "
							+ "AND term = \"" + term + "\" "
							+ "ORDER BY SUBSTRING(term,2,3) DESC";
			Connection con = jdbc.getDataSource().getConnection();
			try(Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
				ResultSet rs = stmt.executeQuery(query);
				int size =0;
				if (rs != null) {
					rs.last();    // moves cursor to the last row
					size = rs.getRow(); // get row id
					rs.beforeFirst(); // moves cursor to the first row
				}
				if(size == 0){
					System.out.println("Select Failed: No classes with that course number");
					return;
				}
				while(rs.next()){
					if(size>1){
						System.out.println("Select Failed: Multiple classes with same course number and term");
						return;
					}
					System.out.println("Course Number | Term | Description | Section Number");
					Helpers.setSelectedCourse(rs.getInt("class_id"));
					String courseNum = rs.getString("course_number");
					String termVal = rs.getString("term");
					String desc = rs.getString("description");
					int sectionNum = rs.getInt("section_number");

					System.out.println(courseNum + ", " + termVal + ", "  + desc + ", " + sectionNum);
				}
				con.close();
			}
			catch (SQLException e){
				System.out.println("Error: " + e);
				con.close();
			}
		} else{ //User specified only the course number
			String query = "SELECT class_id, course_number, term, description, section_number "  
							+ "FROM classes "
							+ "WHERE course_number = \"" + course + "\" "
							+ "ORDER BY SUBSTRING(term,2,3) DESC";
			Connection con = jdbc.getDataSource().getConnection();
			try(Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
				ResultSet rs = stmt.executeQuery(query);
				int size =0;
				if (rs != null) {
					rs.last();    // moves cursor to the last row
					size = rs.getRow(); // get row id
					rs.beforeFirst(); // moves cursor to the first row
				}
				if(size == 0){
					System.out.println("Select Failed: No classes with that course number");
					return;
				}
				while(rs.next()){
					if(size>1){
						System.out.println("Select Failed: Multiple classes with same course number");
						return;
					}
					System.out.println("Course Number | Term | Description | Section Number");
					Helpers.setSelectedCourse(rs.getInt("class_id"));
					String courseNum = rs.getString("course_number");
					String termVal = rs.getString("term");
					String desc = rs.getString("description");
					int sectionNum = rs.getInt("section_number");

					System.out.println(courseNum + ", " + termVal + ", "  + desc + ", " + sectionNum);
				}
				con.close();
			}
			catch (SQLException e){
				System.out.println("Error: " + e);
				con.close();
			}
		}
	}

	/**
	 * This method is used to add a new class to the database.
	 * @param courseNum - e.g. CS410
	 * @param term - e.g. Fa19
	 * @param section - e.g. 1
	 * @param description - e.g. Databases
	 * @throws SQLException
	 */
	@ShellMethod("Create a class")
	public void newClass(String courseNum, String term, int section, String description) throws SQLException{
		String insert = "INSERT INTO GradeBook.classes (course_number, term, section_number, description) "
						+ "VALUES (\"" + courseNum + "\", \"" + term + "\", " + section + ", \"" + description + "\")";
		Connection con= jdbc.getDataSource().getConnection();
		try {
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(insert);
			con.commit();
			System.out.println("Class created");
		} catch (SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.setAutoCommit(true);
			con.close();
		}
	}

	/**
	 * Lists all classes with the number of 
	 * students enrolled in each class
	 * @throws SQLException
	 */
	@ShellMethod("List classes")
	public void listClasses() throws SQLException{
		String query = "SELECT classes.*, COUNT(student_id) " +  
						"FROM enrolled_in " +
						"RIGHT JOIN classes ON classes.class_id = enrolled_in.class_id " +
						"GROUP BY(class_id)";
		Connection con = jdbc.getDataSource().getConnection();
		System.out.println("Class ID | Course Number | Term | Section | Description | Enrolled Students");
		try(Statement stmt = con.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				int classID = rs.getInt("class_id");
				String courseNum = rs.getString("course_number");
				String term = rs.getString("term");
				int section = rs.getInt("section_number");
				String desc = rs.getString("description");
				int count = rs.getInt("COUNT(student_id)");

				//Print the class info with spacing
				System.out.println();
				System.out.format("%-8d | %-13s | %-4s | %-7d | %-11s | %-10d %n", classID, courseNum, term, section, desc, count);
				// System.out.println(classID + " | " + courseNum + " | " + term + " | " + section + " | " + desc + " | " + count);
			}
			con.close();
		}
		catch (SQLException e){
			System.out.println("Error: " + e);
			con.close();
		}
	}
}
