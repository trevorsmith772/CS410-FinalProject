import java.sql.*;

public class GradeBook{
    public static Connection makeConnection() {
		try {
			Connection conn = null;
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:54051/test?verifyServerCertificate=false&useSSL=true", "msandbox",
					"dbpass30");
			// Do something with the Connection
			System.out.println("Database [test db] connection succeeded!");
			System.out.println();
			return conn;
		} catch (SQLException ex) {
			// handle any errors
			System.err.println("SQLException: " + ex.getMessage());
			System.err.println("SQLState: " + ex.getSQLState());
			System.err.println("VendorError: " + ex.getErrorCode());
		}
		return null;
	}
}