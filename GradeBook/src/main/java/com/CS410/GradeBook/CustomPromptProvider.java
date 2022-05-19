package com.CS410.GradeBook;

import java.sql.*;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.shell.jline.PromptProvider;

/**
 * Customizes the shell prompt
 */
@Component
class CustomPromptProvider implements PromptProvider {
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
