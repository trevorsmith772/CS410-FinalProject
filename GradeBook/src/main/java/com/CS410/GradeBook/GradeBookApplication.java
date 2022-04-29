package com.CS410.GradeBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Color;
import java.lang.Thread.State;
import java.sql.*;

import javax.validation.constraints.Null;

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
import org.springframework.boot.Banner;


@SpringBootApplication
public class GradeBookApplication {
	public static void main(String[] args) {
		
		SpringApplication application = new SpringApplication(GradeBookApplication.class);
        //application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
	}
}

class Helpers {

	private static int selectedCourse = 0; // 0 is default value if no class is selected

	public static int getSelectedCourse() {
		return selectedCourse;
	}

	public static void setSelectedCourse(int selectedCourse) {
		Helpers.selectedCourse = selectedCourse;
	}

	public Availability availabilityCheck(){
		if(selectedCourse == 0) {
			return Availability.unavailable("No class selected");
		}
		return Availability.available();
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
	public String showAssignments() throws SQLException{
		String query = "SELECT assignments.name, point_value, categories.name " +  
						"FROM assignments, categories, classes, curriculum " +
						"WHERE assignments.assignment_id = curriculum.assignment_id " +
						"AND classes.class_id = curriculum.class_id " +
						"AND categories.category_id = assignments.categories_id " +
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
		return "";
	}

	/**
	 * 
	 * @param name
	 * @param category May need to change this parameter type
	 * @param description
	 * @param points
	 * @return
	 */
	@ShellMethod("Add assignment")
	@ShellMethodAvailability("availabilityCheck")
	public String addAssignment(String name, int category, String description, int points){
		//TODO
		return "";
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
	 * adds a student and enrolls them in the current class.
	 * If the student already exists, enroll them in the class; 
	 * if the name provided does not match their stored name, 
	 * update the name but print a warning that the name is being changed.
	 * @param username
	 * @param studentID
	 * @param lastName
	 * @param firstName
	 * @return
	 */
	@ShellMethod("Add student")
	@ShellMethodAvailability("availabilityCheck")
	public void addStudent(String username, @ShellOption(defaultValue = "") int studentID,
							 @ShellOption(defaultValue = "") String lastName, @ShellOption(defaultValue = "") String firstName) throws SQLException{

		String insert = "INSERT INTO students (username, student_id, last_name, first_name) " +
						"VALUES (\"" + username + "\", " + studentID + ", \"" + lastName + "\", \"" + firstName + "\")";

		Connection con = jdbc.getDataSource().getConnection();
		try {
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(insert);
			con.commit();
			System.out.println("Student added");
		} catch (SQLException e) {
			System.out.println("Error: " + e);
		}
		finally {
			con.setAutoCommit(true);
			con.close();
		}
	}

	@ShellMethod("Show students")
	@ShellMethodAvailability("availabilityCheck")
	public void showStudents() throws SQLException {
		String query = "SELECT username, last_name, first_name, student_id " +
						"FROM students " +
						"WHERE class_id = " + Helpers.getSelectedCourse();

		Connection con = jdbc.getDataSource().getConnection();
		System.out.println("Username | Last Name | First Name | Student ID");
		try(Statement stmt = con.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String userName = rs.getString("username");
				String lastName = rs.getString("last_name");
				String firstName = rs.getString("first_name");
				int studentID = rs.getInt("student_id");

				System.out.println(userName + ", " + lastName + ", " + firstName + ", " + studentID);
			}
			con.close();
		}
		catch (SQLException e){
			System.out.println("Error: " + e);
			con.close();
		}
	}

	@ShellMethod("Assign Grade")
	@ShellMethodAvailability("availabilityCheck")
	public String grade(String assignmentname, String username, double grade) {
		//TODO
		return "";
	}
}

@ShellComponent
class GradeReporting{
	
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

	@ShellMethod("Show student's grades")
	@ShellMethodAvailability("availabilityCheck")
	public String studentGrades(String username){
		//TODO
		return "";
	}

	@ShellMethod("Show gradebook")
	@ShellMethodAvailability("availabilityCheck")
	public String gradebook(){
		//TODO
		return "";
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