package com.CS410.GradeBook;

import java.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;

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
