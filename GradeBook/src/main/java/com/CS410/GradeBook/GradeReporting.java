package com.CS410.GradeBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;

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

	/**
	 * Extracts the following values for specified student, category, and class:
	 * 		- total points earned from assignments
	 * 		- total points possible from GRADED assignments
	 * 		- total points possible from ALL assignments
	 * 
	 * @param username - e.g. trevorsmith772
	 * @param category - e.g. Homework
	 * @return HashMap containing the above values
	 * @throws SQLException
	 */
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

	/**
	 * Calculates and prints the attempted grade and total grade
	 * for a specified instance of a student and class
	 * 
	 * @param maps - List containing a hashmap for each category
	 * @param categories - Map containing the categories and their respective weights
	 */
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
			/* Returns all assignments in the selected class and the grades the specified student has received for them.
				This includes assignments that have not been graded yet */
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
