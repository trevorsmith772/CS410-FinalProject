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
	
	@ShellMethod("Activate a class")
	public void selectClass(String course, @ShellOption(defaultValue = "") String term, 
							@ShellOption(defaultValue = "0") int section) throws SQLException{
								
		if(section != 0){
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
		} else if(!term.equals("")){
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
		} else{
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

	@ShellMethod("Create a class")
	public String newClass(String courseNum, String term, int section, String description){
		String query = "INSERT INTO classes (course_number, term, section, description) "
						+ "VALUES (\"" + courseNum + "\", \"" + term + "\", " + section + ", \"" + description + "\")";
		return query;
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

	public Availability availabilityCheck(){
		if(Helpers.getSelectedCourse() == 0) {
			return Availability.unavailable("no class is selected/active");
		}
		return Availability.available();
	}

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

	@ShellMethod("Add category to class with weight")
	@ShellMethodAvailability("availabilityCheck")
	public String addCategoryWithWeight(String name, double weight){
		//TODO
		return "";
	}

	@ShellMethod("Add new category")
	public String addCategory(String name){
		//TODO
		return "";
	}
	
	@ShellMethod("Show assignments")
	@ShellMethodAvailability("availabilityCheck")
	public String showAssignments(){
		//TODO
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
	public String addStudent(String username, @ShellOption(defaultValue = "") int studentID,
							 @ShellOption(defaultValue = "") String lastName, @ShellOption(defaultValue = "") String firstName){
		//TODO
		return "";
	}

	@ShellMethod("Show students")
	@ShellMethodAvailability("availabilityCheck")
	public String showStudents(@ShellOption(defaultValue = "") String username){
		//TODO
		return "";
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