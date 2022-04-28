package com.CS410.GradeBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ShellComponent;


@SpringBootApplication
public class GradeBookApplication {
	public static void main(String[] args) {
		SpringApplication.run(GradeBookApplication.class, args);
	}
	// @Autowired
	// JdbcTemplate jdbc;

	// @Override
	// public void run(String... strings) throws Exception {
	// 	jdbc.execute("INSERT INTO categories(category_id, name) VALUES (1,'bitch')");
	// }
}


@ShellComponent
class ClassManagement {

	@Autowired
	JdbcTemplate jdbc;

	@ShellMethod("Activate a class")
	public String selectClass(String course){
		//TODO
		return "";
	}

	@ShellMethod("Create a class")
	public String newClass(String courseNum, String term, int section, String description){
		//TODO
		return "";
	}

	@ShellMethod("List classes")
	public String listClasses(){
		//TODO
		
		return "";
	}

	/**
	 * Probably don't need this
	 * @return
	 */
	@ShellMethod("Show class")
	public String showClass(){
		//TODO
		String query = "";
		return query;
	}
}
/**
 * Commands must be made in the context of a class
 */
@ShellComponent
class ClassAndAssignmentManagement {
	
	@ShellMethod("Show categories")
	public String showCategories(){
		//TODO
		return "";
	}

	@ShellMethod("Add category")
	public String addCategory(String name, double weight){
		//TODO
		return "";
	}

	@ShellMethod("Show assignments")
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
	public String addStudent(String username, @ShellOption(defaultValue = "") int studentID,
							 @ShellOption(defaultValue = "") String lastName, @ShellOption(defaultValue = "") String firstName){
		//TODO
		return "";
	}

	@ShellMethod("Show students")
	public String showStudents(@ShellOption(defaultValue = "") String username){
		//TODO
		return "";
	}

	@ShellMethod("Assign Grade")
	public String grade(String assignmentname, String username, double grade) {
		//TODO
		return "";
	}
}

@ShellComponent
class GradeReporting{
	
	@ShellMethod("Show student's grades")
	public String studentGrades(String username){
		//TODO
		return "";
	}

	@ShellMethod("Show gradebook")
	public String gradebook(){
		//TODO
		return "";
	}
}
