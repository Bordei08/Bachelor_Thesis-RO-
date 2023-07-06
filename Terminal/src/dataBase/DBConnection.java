package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
	private static final String USER = "student";
	private static final String PASSWORD = "student";
	private static Connection connection = null;

	private DBConnection() {
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void createConnection() {
		try {
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

			// Reference to connection interface
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	public static void closeConnection() throws SQLException {
		connection.close();
	}

}