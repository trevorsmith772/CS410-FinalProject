package com.CS410.GradeBook;

import java.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;

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