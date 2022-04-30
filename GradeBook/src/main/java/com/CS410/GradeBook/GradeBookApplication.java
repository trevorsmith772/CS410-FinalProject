package com.CS410.GradeBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;
import org.springframework.shell.Availability;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
//import org.springframework.boot.Banner;

/**
 * @brief This application uses Spring Shell to provide a 
 * 		command line shell interface for grade management.
 * 		This application is currently only usable with an 
 * 		SSH connection to onyx.boisestate.edu in order to 
 * 		retrieve the JDBC connector and database, but can be
 * 		configured with an alternate database and connector.
 * 		This application was created for the CS410 Databases
 * 		course at Boise State University.
 * 
 * @author Trevor Smith (trevorsmith772)
 * @author Berto Cisneros (bertocisneros)
 * @date 04/28/2022
 */
@SpringBootApplication
public class GradeBookApplication {
	public static void main(String[] args) {
		
		SpringApplication application = new SpringApplication(GradeBookApplication.class);
        //application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
	}
}

/**
 * Helper class to mainly provide current selected
 * course that is used by other commands.
 */
class Helpers {
	
	// This int represents the class_id of the current selected course.
	private static int selectedCourse = 0; // 0 is default value if no class is selected

	/**
	 * Getter for the selected course.
	 * @return selectedCourse
	 */
	public static int getSelectedCourse() {
		return selectedCourse;
	}

	/**
	 * Setter for the selected course.
	 * @param selectedCourse - class_id of the selected course
	 */
	public static void setSelectedCourse(int selectedCourse) {
		Helpers.selectedCourse = selectedCourse;
	}
}

@ShellComponent
class ClassManagement {

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

				System.out.println(classID + ", " + courseNum + ", " + term + ", " + section + ", " + desc + ", " + count);
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
 * Commands must be made in the context of a class
 */
@ShellComponent
class ClassAndAssignmentManagement {

	@Autowired
	JdbcTemplate jdbc;

	/**
	 * Used to check availability of a command
	 * based on whether a class is activated.
	 * @return availability of command
	 */
	public Availability availabilityCheck(){
		if(Helpers.getSelectedCourse() == 0) {
			return Availability.unavailable("no class is selected/active");
		}
		return Availability.available();
	}

	/**
	 * Shows all the categories for the activated class
	 * along with the weights for each category
	 * @throws SQLException
	 */
	@ShellMethod("Show categories")
	@ShellMethodAvailability("availabilityCheck")
	public void showCategories() throws SQLException{
		String query = "SELECT categories.name, weight " +  
						"FROM categories, weights " +
						"WHERE weights.category_id=categories.category_id " +
						"AND class_id = " + Helpers.getSelectedCourse();
		Connection con = jdbc.getDataSource().getConnection();
		System.out.println("Name | Weight");
		try(Statement stmt = con.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String catName = rs.getString("categories.name");
				int weight = rs.getInt("weight");

				System.out.println(catName + ", " + weight);
			}
			con.close();
		}
		catch (SQLException e){
			System.out.println("Error: " + e);
			con.close();
		}
	}

	/**
	 * Adds a category with specified weight to active class.
	 * If this category is new for the database, it will be added 
	 * to the categories table. Otherwise, it will just add the 
	 * weighted relationship with the class to the weights table.
	 * @param name - e.g. Homework
	 * @param weight - e.g. 20 (as in 20%)
	 * @throws SQLException
	 */
	@ShellMethod("Add category to class with specified weight")
	@ShellMethodAvailability("availabilityCheck")
	public void addCategoryWithWeight(String name, double weight) throws SQLException{
		String query = "SELECT category_id " +  
						"FROM categories " +
						"WHERE name = \"" + name + "\"";
		Connection con = jdbc.getDataSource().getConnection();

		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) { //category already exists
				int catID = rs.getInt("category_id");
				String insert = "INSERT INTO weights (class_id, category_id, weight) "
								+ "VALUES (" + Helpers.getSelectedCourse() + ", " + catID + ", " + weight + ")";
				try {
					con.setAutoCommit(false);
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate(insert);
					con.commit();
					System.out.println("Category added");
				} catch (SQLException e) {
					System.out.println("Error: " + e);
				}
				finally {
					con.setAutoCommit(true);
					con.close();
				}
			}
			else {	// category is new
				// add category to categories table
				String insert = "INSERT INTO categories (name) "
								+ "VALUES (\"" + name + "\")";
				try {
					con.setAutoCommit(false);
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate(insert);
					con.commit();
					System.out.println("Category created");
				} catch (SQLException e) {
					System.out.println("Error: " + e);
				}
				finally {
					con.setAutoCommit(true);
					con.close();
				}
				// get category_id of new category
				query = "SELECT category_id " +  
						"FROM categories " +
						"WHERE name = \"" + name + "\"";
				con = jdbc.getDataSource().getConnection();
				try (Statement stmt3 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
					ResultSet rs2 = stmt3.executeQuery(query);
					if(rs2.next()) {
						int catID = rs2.getInt("category_id");
						// add weighted relationship to weights table
						String insert2 = "INSERT INTO weights (class_id, category_id, weight) "
										+ "VALUES (" + Helpers.getSelectedCourse() + ", " + catID + ", " + weight + ")";
						try {
							con.setAutoCommit(false);
							Statement stmt4 = con.createStatement();
							stmt4.executeUpdate(insert2);
							con.commit();
							System.out.println("Category added");
						} catch (SQLException e) {
							System.out.println("Error: " + e);
						}
						finally {
							con.setAutoCommit(true);
							con.close();
						}
					}
				}
			} 
		}catch (Exception e) {
			System.out.println("Error: " + e);
			con.close();
		}
	}

	/**
	 * Adds a category to the category table.
	 * This is independent of a class.
	 * @param name - e.g. Homework
	 * @throws SQLException
	 */
	@ShellMethod("Add new category")
	public void addCategory(String name) throws SQLException{
		String insert = "INSERT INTO GradeBook.categories (name) "
						+ "VALUES (\"" + name + "\")";
		Connection con= jdbc.getDataSource().getConnection();
		try {
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(insert);
			con.commit();
			System.out.println("Category created");
		} catch (SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.setAutoCommit(true);
			con.close();
		}
	}
	
	/**
	 * Shows all assignments in active class with 
	 * their respective categories and their point values
	 * @return
	 * @throws SQLException
	 */
	@ShellMethod("Show assignments")
	@ShellMethodAvailability("availabilityCheck")
	public void showAssignments() throws SQLException{
		String query = "SELECT assignments.name, point_value, categories.name " +  
						"FROM assignments, categories, classes " +
						"WHERE assignments.class_id = classes.class_id " +
						"AND assignments.categories_id = categories.category_id " +
						"AND classes.class_id = " + Helpers.getSelectedCourse();

		Connection con = jdbc.getDataSource().getConnection();
		System.out.println("Assignment | Point Value | Category");
		try(Statement stmt = con.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String assName = rs.getString("assignments.name");
				int point_value = rs.getInt("point_value");
				String catName = rs.getString("categories.name");

				System.out.println(assName + ", " + point_value + ", " + catName);
			}
			con.close();
		}
		catch (SQLException e){
			System.out.println("Error: " + e);
			con.close();
		}
	}

	/**
	 * Add a new assignment to the active class
	 * @param name - e.g. Homework 3
	 * @param category - e.g. Homework
	 * @param description - SQL Practice
	 * @param points - 80
	 * @throws SQLException
	 */
	@ShellMethod("Add assignment")
	@ShellMethodAvailability("availabilityCheck")
	public void addAssignment(String name, String category, String description, int points) throws SQLException{
		// get category_id of category
		String query = "SELECT category_id " +  
						"FROM categories " +
						"WHERE name = \"" + category + "\"";
		Connection con = jdbc.getDataSource().getConnection();
		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				int catID = rs.getInt("category_id");
				// add assignment to assignments table
				String insert = "INSERT INTO GradeBook.assignments (name, description, point_value, categories_id, class_id) "
								+ "VALUES (\"" + name + "\", \"" + description + "\", " + points + ", " + catID + ", " 
								+ Helpers.getSelectedCourse() + ")";
				try {
					con.setAutoCommit(false);
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate(insert);
					con.commit();
					System.out.println("Assignment created");
				} catch (SQLException e) {
					System.out.println("Error: " + e);
				}
				finally {
					con.setAutoCommit(true);
					con.close();
				}
			}
			else {
				System.out.println("Category not found. Use 'addCategory' to add a new category.");
			}
		}
		catch (SQLException e){
			System.out.println("Error: " + e);
			con.close();
		}
	}
}
/**
 * Commands must be made in the context of a class
 */
@ShellComponent
class StudentManagement{
	@Autowired
	JdbcTemplate jdbc;

	/**
	 * Used to check availability of a command
	 * based on whether a class is activated.
	 * @return availability of command
	 */
	public Availability availabilityCheck(){
		if(Helpers.getSelectedCourse() == 0) {
			return Availability.unavailable("no class is selected/active");
		}
		return Availability.available();
	}
	
	/**
	 * IF ALL PARAMS ARE SPECIFIED: adds a student and enrolls them in the current class.
	 * 								If the student already exists, enroll them in the class; 
	 * 								if the name provided does not match their stored name, 
	 * 								update the name but print a warning that the name is being changed.
	 * 
	 * IF ONLY USERNAME IS SPECIFIED: enrolls the specified student (by username) in the active class.
	 * 								  If the student does not exist, the user is prompted to create a new student.
	 * @param username - e.g. trevorsmith772
	 * @param studentID - e.g. 116572
	 * @param lastName - e.g. Smith
	 * @param firstName - e.g. Trevor
	 */
	@ShellMethod("Add student")
	@ShellMethodAvailability("availabilityCheck")
	public void addStudent(String username, @ShellOption(defaultValue="0") int studentID,
							@ShellOption(defaultValue="") String lastName, 
							@ShellOption(defaultValue="") String firstName) throws SQLException{
		// If only username is specified
		if(studentID == 0 || lastName.equals("") || firstName.equals("")) {
			if(studentID == 0 && lastName.equals("") && firstName.equals("")){ //ensures that they are all null if one of them is
				String query = "SELECT student_id "
							 + "FROM students " 
							 + "WHERE username = \"" + username + "\"";
				Connection con = jdbc.getDataSource().getConnection();
				try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
					ResultSet rs = stmt.executeQuery(query);
					if(rs.next()) {
						int studentID2 = rs.getInt("student_id");
						// check if student is already enrolled
						String query2 = "SELECT enrolled_in.student_id FROM students, classes, enrolled_in " +
										"WHERE students.student_id = enrolled_in.student_id " +
										"AND classes.class_id = enrolled_in.class_id " +
										"AND students.student_id = " + studentID2 + " " +
										"AND classes.class_id = " + Helpers.getSelectedCourse();
						con = jdbc.getDataSource().getConnection();
						Statement stmtX = con.createStatement();
						ResultSet rs2 = stmtX.executeQuery(query2);
						if(rs2.next()){
							System.out.println("Student already enrolled in class");
							con.close();
							return;
						}
						else {
							// enroll student in class
							String insert = "INSERT INTO enrolled_in (student_id, class_id) " +
											"VALUES (" + studentID2 + ", " + Helpers.getSelectedCourse() + ")";
							try {
								con.setAutoCommit(false);
								Statement stmt2 = con.createStatement();
								stmt2.executeUpdate(insert);
								con.commit();
								System.out.println("Student enrolled in class");
							} catch (SQLException e) {
								System.out.println("Error: " + e);
							}
							finally {
								con.setAutoCommit(true);
								con.close();
							}
						}
					}
					else {
						System.out.println("Student not found, please specify all parameters to create a new student.");
						return;
					}
				}
				catch (SQLException e){
					System.out.println("Error: " + e);
					con.close();
				}
			}
			else {
				System.out.println("Error: Must specify all student information or only username");
			}
		}
		else { // all fields are specified
			String query = "SELECT students.name FROM students WHERE student_id = " + studentID;
			String fullName = firstName + " " + lastName;
			
			Connection con = jdbc.getDataSource().getConnection();
			try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()){
					System.out.println("Student already exists");
					
					// check if student's name matches
					if(!fullName.equals(rs.getString("name"))){
						System.out.println("Warning: Full name is being changed");
						String update = "UPDATE students SET name = \"" + fullName + "\" WHERE student_id = " + studentID;
						try {
							con.setAutoCommit(false);
							Statement stmt3 = con.createStatement();
							stmt3.executeUpdate(update);
							con.commit();
							System.out.println("Full name updated");
						} catch (SQLException e) {
							System.out.println("Error: " + e);
						}
						finally {
							con.setAutoCommit(true);
							con.close();
						}
					}

					// check if student is already enrolled
					String query2 = "SELECT enrolled_in.student_id FROM students, classes, enrolled_in " +
									"WHERE students.student_id = enrolled_in.student_id " +
									"AND classes.class_id = enrolled_in.class_id " +
									"AND students.student_id = " + studentID + " " +
									"AND classes.class_id = " + Helpers.getSelectedCourse();
					con = jdbc.getDataSource().getConnection();
					Statement stmtX = con.createStatement();
					ResultSet rs2 = stmtX.executeQuery(query2);
					if(rs2.next()){
						System.out.println("Student already enrolled in class");
						con.close();
						return;
					}
					else {
						// enroll student in class
						String insert = "INSERT INTO enrolled_in (student_id, class_id) " +
										"VALUES (" + studentID + ", " + Helpers.getSelectedCourse() + ")";
						try {
							con.setAutoCommit(false);
							Statement stmt2 = con.createStatement();
							stmt2.executeUpdate(insert);
							con.commit();
							System.out.println("Student enrolled in class");
						} catch (SQLException e) {
							System.out.println("Error: " + e);
						}
						finally {
							con.setAutoCommit(true);
							con.close();
						}
					}
				}
				else {
					String insert = "INSERT INTO students (student_id, username, name) " +
									"VALUES (" + studentID + ", \"" + username + "\", \"" + fullName + "\")";
					try {
						con.setAutoCommit(false);
						Statement stmt2 = con.createStatement();
						stmt2.executeUpdate(insert);
						con.commit();
						System.out.println("Student added");
					} catch (SQLException e) {
						System.out.println("Error: " + e);
					}
					finally {
						con.setAutoCommit(true);
						con.close();
					}
					String insert2 = "INSERT INTO enrolled_in (student_id, class_id) " +
									"VALUES (" + studentID + ", " + Helpers.getSelectedCourse() + ")";
					try {
						con = jdbc.getDataSource().getConnection();
						con.setAutoCommit(false);
						Statement stmt3 = con.createStatement();
						stmt3.executeUpdate(insert2);
						con.commit();
						System.out.println("Student added to class");
					} catch (SQLException e) {
						System.out.println("Error: " + e);
					}
					finally {
						con.setAutoCommit(true);
						con.close();
					}
				}
			}
		}
	}

	/**
	 * show all students with ‘str’ in their full name or username (case-insensitive)
	 *
	 * @param str - e.g. trevor
	 */
	@ShellMethod("Show students")
	@ShellMethodAvailability("availabilityCheck")
	public void showStudents(@ShellOption(defaultValue = "") String str) throws SQLException {
		
		if(!str.equals("")){
			String query = "SELECT students.student_id, students.username, students.name " +
							"FROM students, enrolled_in, classes " +
							"WHERE students.student_id = enrolled_in.student_id " +
							"AND classes.class_id=enrolled_in.class_id " +
							"AND (students.name LIKE \"%" + str + "%\" " +
							"OR students.username LIKE \"%" + str + "%\") " +
							"AND enrolled_in.class_id = " + Helpers.getSelectedCourse();
							
			Connection con = jdbc.getDataSource().getConnection();
			System.out.println("Student ID | Username | Name ");
			try(Statement stmt = con.createStatement()){
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()){
					int studID = rs.getInt("students.student_id");
					String username = rs.getString("students.username");
					String name = rs.getString("students.name");

					System.out.println(studID + ", " + username + ", " + name);
				}
				con.close();
			}
			catch (SQLException e){
				System.out.println("Error: " + e);
				con.close();
			}
		}
		else{
			String query = "SELECT username, name, students.student_id " +
						"FROM students, enrolled_in, classes " +
						"WHERE students.student_id = enrolled_in.student_id " +
						"AND classes.class_id = enrolled_in.class_id " +
						"AND enrolled_in.class_id = " + Helpers.getSelectedCourse();

			Connection con = jdbc.getDataSource().getConnection();
			System.out.println("Username | Name | Student ID");
			try(Statement stmt = con.createStatement()){
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()){
					String userName = rs.getString("username");
					String name = rs.getString("name");
					int studentID = rs.getInt("student_id");

					System.out.println(userName + ", " + name + ", " + studentID);
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
	 * Assign the grade ‘grade’ for student with user name 
	 * ‘username’ for assignment ‘assignmentname’. If the 
	 * student already has a grade for that assignment, 
	 * replace it. If the number of points exceeds the 
	 * number of points configured for the assignment, 
	 * print a warning (showing the number of points configured).
	 *
	 * @param - assignmentName - e.g. Assignment 1
	 * @param - username - e.g. trevorsmith772
	 * @param - grade - e.g. 75 (this is their raw grade. i.e. if a student got 75/80 on an assignment, this would be 75)
	 */
	@ShellMethod("Assign Grade")
	@ShellMethodAvailability("availabilityCheck")
	public void grade(String assignmentname, String username, double grade) throws SQLException {
		String query = "SELECT assignments.class_id, point_value " +
						"FROM grades, assignments, students " + 
						"WHERE grades.assignment_id = assignments.assignment_id " +
						"AND students.student_id = grades.student_id " +
						"AND assignments.name = \"" + assignmentname + "\" " +
						"AND username = \"" + username + "\"";

		Connection con = jdbc.getDataSource().getConnection();

		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) { //grade already exists
				int classID = rs.getInt("class_id");
				if (classID != Helpers.getSelectedCourse()) {
					System.out.println("Error: Specified assignment is not in the active class. "
									+ "Please choose a valid assignment or switch to the class containing the specified "
									+ "assignment.");
					return;
				}
				int pointVal = rs.getInt("point_value");
				String insert = "UPDATE grades " +
								"SET grade = " + grade +
								"WHERE student_id = (SELECT student_id " +
													"FROM students " +
													"WHERE username = \"" + username + "\") " +
								"AND assignment_id = (SELECT assignment_id " +
													"FROM assignments " +
													"WHERE assignments.name = \"" + assignmentname + "\")";
				if(grade > pointVal) {
					System.out.println("Warning: Grade exceeds assignment point value");
					System.out.println("Points configured: " + pointVal);
				}
				try {
					con = jdbc.getDataSource().getConnection();
					con.setAutoCommit(false);
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate(insert);
					con.commit();
					System.out.println("Grade updated");
				} catch (SQLException e) {
					System.out.println("Error: " + e);
				}
				finally {
					con.setAutoCommit(true);
					con.close();
				}
			}
			else { //grade does not exist
				String insert = "INSERT INTO grades (assignment_id, student_id, grade) " +
								"VALUES ((SELECT assignment_id " +
										"FROM assignments " +
										"WHERE assignments.name = \"" + assignmentname + "\"), " +
										"(SELECT student_id FROM students WHERE username = \"" + username + "\"), " +
										 grade + ")";
				try {
					con = jdbc.getDataSource().getConnection();
					con.setAutoCommit(false);
					Statement stmt2 = con.createStatement();
					stmt2.executeUpdate(insert);
					con.commit();
					System.out.println("Grade updated");
				} catch (SQLException e) {
					System.out.println("Error: " + e);
				}
				finally {
					con.setAutoCommit(true);
					con.close();
				}
			}

		} catch (SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.close();
		}
	}
}

@ShellComponent
class GradeReporting{
  
	@Autowired
	JdbcTemplate jdbc;

	/**
	 * Used to check availability of a command
	 * based on whether a class is activated.
	 * @return availability of command
	 */
	public Availability availabilityCheck(){
		if(Helpers.getSelectedCourse() == 0) {
			return Availability.unavailable("no class is selected/active");
		}
		return Availability.available();
	}

	public HashMap<String, Double> getTotals (String username, String category) throws SQLException {
		HashMap<String, Double> totals = new HashMap<String, Double>();

		/* Get subtotal*/
		String query = "SELECT SUM(grade) " +
					"FROM assignments a, grades g, students s, categories " +
					"WHERE a.assignment_id = g.assignment_id " +
					"AND g.student_id = s.student_id " +
					"AND username = \"" + username + "\" " +
					"AND a.categories_id = categories.category_id " +
					"AND categories.name = \"" + category + "\" " +
					"AND a.class_id = " + Helpers.getSelectedCourse();

		Connection con = jdbc.getDataSource().getConnection();
		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				totals.put("Subtotal", rs.getDouble(1));
			}
		}
		catch(SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.close();
		}
		/* Get graded assignments possible point total */
		query = "SELECT SUM(point_value) " +
				"FROM (SELECT a.assignment_id, point_value " +
						"FROM assignments a " +
						"LEFT JOIN categories c ON c.category_id = a.categories_id " +
						"RIGHT JOIN grades g ON g.assignment_id = a.assignment_id " +
						"WHERE a.class_id = " + Helpers.getSelectedCourse() + " " +
						"AND c.name = \"" + category + "\" " +
						"GROUP BY(a.assignment_id))t1";
		con = jdbc.getDataSource().getConnection();
		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				totals.put("Attempted total", rs.getDouble(1));
			}
		}
		catch(SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.close();
		}

		/* Get ungraded assignments possible point total */
		query = "SELECT SUM(point_value) " +
				"FROM assignments a " +
				"LEFT JOIN categories c ON c.category_id = a.categories_id " +
				"WHERE class_id = " + Helpers.getSelectedCourse() +
				" AND c.name = \"" + category + "\"";
		con = jdbc.getDataSource().getConnection();
		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				totals.put("Ungraded total", rs.getDouble(1));
			}
		}
		catch(SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.close();
		}

		return totals;
	}

	public void printGrades(ArrayList<HashMap<String, Double>> maps, Map<String, Integer> categories) {
		double attemptedSum = 0;
		double totalSum = 0;
		int iter = 0;
		List<Integer> weights = new ArrayList<Integer>(categories.values());
		for(HashMap<String, Double> map : maps) {
			attemptedSum += (map.get("Subtotal") / map.get("Attempted total")) * weights.get(iter);
			totalSum += (map.get("Subtotal") / map.get("Ungraded total")) * weights.get(iter);
			iter++;
		}
		System.out.println("Attempted grade: " + attemptedSum + "%");
		System.out.println("Total grade: " + totalSum + "%");
	}

	/**
	 * show student’s current grade: all assignments, visually grouped 
	 * by category, with the student’s grade (if they have one). Show subtotals for each category, 
	 * along with the overall grade in the class.
	 * @param - username - e.g. trevorsmith772
	 * @throws SQLException
	 */
	@ShellMethod("Show student's grades")
	@ShellMethodAvailability("availabilityCheck")
	public void studentGrades(String username) throws SQLException{
		
		/* Check if student is enrolled in active class */
		String query = "SELECT class_id " +
						"FROM students, enrolled_in " +
						"WHERE students.username = \"" + username + "\" " +
						"AND enrolled_in.class_id = " + Helpers.getSelectedCourse() +
						" AND students.student_id = enrolled_in.student_id";
		Connection con = jdbc.getDataSource().getConnection();
		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			if(!rs.next()) {
				System.out.println("Error: Student is not enrolled in the active class.");
				return;
			}
		} catch (SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.close();
		}

		/* Map categories and weights that are in current class */
		Map<String, Integer> categories = new HashMap<String, Integer>();
		query = "SELECT categories.name, weight " +  
				"FROM categories, weights " +
				"WHERE weights.category_id=categories.category_id " +
				"AND weights.class_id = " + Helpers.getSelectedCourse();
		con = jdbc.getDataSource().getConnection();
		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);
			categories = new HashMap<String, Integer>();
			while(rs.next()) {
				categories.put(rs.getString("name"), rs.getInt("weight"));
			}
		}
		catch (SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.close();
		}
		if(categories.size() == 0) {
			System.out.println("Error: No assignemnts found for this class");
			return;
		}

		/* Confirmed that student is enrolled in active class and the class has categories */
		System.out.println("Grades for " + username + ":");
		System.out.println("Row entries are in the format: assignment, points received\n");

		/* Iterate through each category in class */
		// key = category name, value = weight
		ArrayList<HashMap<String, Double>> maps = new ArrayList<>();
		for (Map.Entry<String, Integer> mapElement : categories.entrySet()) {
			System.out.println(mapElement.getKey().toUpperCase() + " -- weight: " + mapElement.getValue() + "%");
			System.out.println("-----------------------------------------------------");
			query = "SELECT assignments.name, grade " +
					"FROM " + 
					"(SELECT a.name, grade, a.categories_id " +
					"FROM assignments a, grades g, students s, categories " +
					"WHERE a.assignment_id = g.assignment_id " +
					"AND g.student_id = s.student_id " +
					"AND username = \"" + username + "\" " +
					"AND a.categories_id = categories.category_id " +
					"AND categories.name = \"" + mapElement.getKey() + "\")t1 " +
					"RIGHT JOIN assignments ON t1.name = assignments.name " +
					"JOIN categories ON categories.category_id = assignments.categories_id " +
					"AND categories.name = \"" + mapElement.getKey() + "\" " +
					"AND assignments.class_id = " + Helpers.getSelectedCourse();

			con = jdbc.getDataSource().getConnection();
			try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					System.out.println(rs.getString("name") + " -- " + rs.getString("grade"));
				}
			}
			catch (SQLException e) {
				System.out.println("Error: " + e);
			}
			finally {
				con.close();
			}

			HashMap<String, Double> totals = getTotals(username, mapElement.getKey());
			maps.add(totals);
			System.out.println("Subtotal: " + totals.get("Subtotal"));

			System.out.println();
		}
		printGrades(maps, categories);
	}

	/**
	 * show the current class’s gradebook: students (username, student ID, and name), 
	 * along with their total grades in the class.
	 */
	@ShellMethod("Show gradebook")
	@ShellMethodAvailability("availabilityCheck")
	public void gradebook() throws SQLException {

		String query = "SELECT s.student_id, s.username, s.name, i.point_value, g.grade "
						+ "FROM assignments i " 
						+ "LEFT JOIN grades g ON g.assignment_id = i.assignment_id "
						+ "LEFT JOIN students s ON g.student_id = s.student_id  "
						+ "LEFT JOIN enrolled_in enr ON s.student_id = enr.student_id "
						+ "LEFT JOIN categories cat ON i.categories_id = cat.category_id "
						+ "LEFT JOIN weights wt ON i.class_id = wt.class_id "
						+ "LEFT JOIN classes c ON wt.class_id = c.class_id " 
						+ "WHERE c.class_id = " + Helpers.getSelectedCourse() + " "
						+ "GROUP BY s.student_id, s.username, s.name, i.point_value, g.grade "
						+ "ORDER BY s.student_id DESC";

		Connection con = jdbc.getDataSource().getConnection();
		System.out.format("%-20s%-20s%-20s%-40s%-40s\n", "ID", "Username", "Name", "Completed Assignment Grade", "Overall Grade");

		try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){
			ResultSet rs = stmt.executeQuery(query);

			int studID = 0;
			String username = "";
			String name = "";
			double totalScore = 0;
			int totalPointVal = 0;
			int attemptedPointVal = 0;

			// Get total point value of the assignments in the class
			String pval = "SELECT SUM(point_value) AS Total " +
							"FROM assignments, categories, classes  " +
							"WHERE assignments.class_id = classes.class_id " +
							"AND assignments.categories_id = categories.category_id " +
							"AND classes.class_id = " + Helpers.getSelectedCourse();
			con = jdbc.getDataSource().getConnection();
			Statement stmtX = con.createStatement();
			ResultSet rs2 = stmtX.executeQuery(pval);
			if(rs2.next()){
				totalPointVal = rs2.getInt("Total");
			}
			
			// Go through each student and corresponding assignment to calculate grades
			while(rs.next()){
				if (rs.getInt("student_id") != studID && studID != 0) { //new student - print curr student and reset score
					System.out.format("%-20d%-20s%-20s%-40f%-40f\n", studID, username, name, totalScore/attemptedPointVal, totalScore/totalPointVal);
					attemptedPointVal = 0;
					totalScore = 0;
					// Populate new student values
					studID = rs.getInt("student_id");
					username = rs.getString("username");
					name = rs.getString("name");
					totalScore += rs.getDouble("grade");
					attemptedPointVal += rs.getInt("point_value");
				} 
				else if (!rs.isLast()) {
					if(rs.isFirst()){
						// Populate new student values
						studID = rs.getInt("student_id");
						username = rs.getString("username");
						name = rs.getString("name");
						totalScore += rs.getDouble("grade");
						attemptedPointVal += rs.getInt("point_value");
					}
					else {
						// Add to score
						totalScore += rs.getDouble("grade");
						attemptedPointVal += rs.getInt("point_value");
					}
				} else {
					attemptedPointVal += rs.getInt("point_value");
					totalScore += rs.getDouble("grade");
					System.out.format("%-20d%-20s%-20s%-40f%-40f\n", studID, username, name, totalScore/attemptedPointVal, totalScore/totalPointVal);
				}
			}
		}
			catch (SQLException e){
				System.out.println("Error: " + e);
				con.close();
			}
		}
	
}

/**
 * Customizes the shell prompt
 */
@Component
class CustomPromptProvideer implements PromptProvider {
	@Autowired
	JdbcTemplate jdbc;

	@Override
	public AttributedString getPrompt() {
		String selectedCourse = "";
		if(Helpers.getSelectedCourse() == 0){
			selectedCourse = "No Class Selected";
		}
		else {
			String query = "SELECT course_number, term, description, section_number"
						+ " FROM classes"
						+ " WHERE class_id = " + Helpers.getSelectedCourse();
			try(Connection con = jdbc.getDataSource().getConnection();
				Statement stmt = con.createStatement()) {
				ResultSet rs = stmt.executeQuery(query);
				rs.next();
				String str = rs.getString("course_number") + " " + rs.getString("term") + " " 
				+ rs.getString("description") + " " + rs.getString("section_number");
				selectedCourse = str;
				con.close();
			}
			catch (SQLException e){
				System.out.println("Error: " + e);
			}
		}
		
		AttributedString prompt = new AttributedString("gradebook: " + selectedCourse + " > ", 
							AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));

		return prompt;
	}
}