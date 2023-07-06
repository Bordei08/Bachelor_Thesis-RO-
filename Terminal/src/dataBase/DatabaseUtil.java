package dataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import utilObjects.*;

public class DatabaseUtil {

	private DateProvider currentDate;

	public DatabaseUtil(DateProvider date) {
		this.currentDate = date;
	}

	public String getNameFromDB(Integer id) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "SELECT name FROM employees WHERE id = ?";
			statement = connection.prepareStatement(query);

			statement.setInt(1, id);

			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				String name = resultSet.getString("name");
				return name;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}// end of getNameFromDB method

	public Integer[] getHoursWorkedTodayFromDB(Integer id) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();
			String query = "SELECT hours_worked, minutes_worked FROM worked_days WHERE id_employee = ? AND day_date = TO_DATE(?, 'YYYY-MM-DD')";

			statement = connection.prepareStatement(query);

			statement.setInt(1, id);
			statement.setString(2, currentDate.getDate());

			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				Integer[] result = new Integer[2];
				result[0] = resultSet.getInt("hours_worked");
				result[1] = resultSet.getInt("minutes_worked");
				return result;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}// end of getHoursWorkedTodayFromDB method

	public Integer getHoursWorkedCurrentMonthFromDB(Integer id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Integer result = 0;
		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "SELECT * FROM worked_days WHERE id_employee = ? AND EXTRACT(MONTH FROM day_date) = ?";

			statement = connection.prepareStatement(query);

			statement.setInt(1, id);
			statement.setInt(2, currentDate.getCurrentMonth());

			resultSet = statement.executeQuery();
			int minutes = 0;
			while (resultSet.next()) {
				int hoursWorked = resultSet.getInt("hours_worked");
				minutes += resultSet.getInt("minutes_worked");
				result += hoursWorked;
			}
			return result + minutes / 60;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}// end of getHoursWorkedCurrentMonthFromDB method

	public boolean[] getAreasAccesFromDB(Integer id) {
		Connection connection = null;
		PreparedStatement statement = null;
		boolean[] result = new boolean[8];
		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "SELECT area0, area1, area2, area3, area4, area5, area6, area7 FROM employees WHERE id = ?";
			statement = connection.prepareStatement(query);

			statement.setInt(1, id);

			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				result[0] = (resultSet.getInt("area0") == 1) ? true : false;
				result[1] = (resultSet.getInt("area1") == 1) ? true : false;
				result[2] = (resultSet.getInt("area2") == 1) ? true : false;
				result[3] = (resultSet.getInt("area3") == 1) ? true : false;
				result[4] = (resultSet.getInt("area4") == 1) ? true : false;
				result[5] = (resultSet.getInt("area5") == 1) ? true : false;
				result[6] = (resultSet.getInt("area6") == 1) ? true : false;
				result[7] = (resultSet.getInt("area7") == 1) ? true : false;
				return result;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;

	}// end of getAreasAccesFromDB method

	public String getPinFromDB(Integer id) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "SELECT pin FROM employees WHERE id = ?";
			statement = connection.prepareStatement(query);

			statement.setInt(1, id);

			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				String pin = resultSet.getString("pin");
				return pin;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}// end of getPinFromDB method

	public void insertNewDate(Integer id) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "INSERT INTO worked_days (id_employee, day_date, hours_worked, minutes_worked) VALUES (?, to_date(?, 'YYYY-MM-DD'), ?, ?)";
			statement = connection.prepareStatement(query);

			statement.setInt(1, id);
			statement.setString(2, currentDate.getDate());
			// System.out.println(currentDate.getDate());
			statement.setInt(3, 0);
			statement.setInt(4, 0);

			int rowsAffected = statement.executeUpdate();
			System.out.println("Rows affected: " + rowsAffected);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}// end of insertDate method

	public boolean isDateAlreadyExists(Integer id) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "SELECT COUNT(*) FROM worked_days WHERE day_date = to_date(?, 'YYYY-MM-DD') AND id_employee = ? ";
			statement = connection.prepareStatement(query);

			statement.setInt(2, id);
			statement.setString(1, currentDate.getDate());

			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				return count > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}// end of isDateAlreadyExists method

	public void updateNewDate(Integer id, Integer hours, Integer minutes) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "UPDATE worked_days  SET hours_worked = ?, minutes_worked = ? WHERE day_date = to_date(?, 'YYYY-MM-DD') AND id_employee = ? ";
			statement = connection.prepareStatement(query);

			statement.setInt(1, hours);
			statement.setInt(2, hours);
			statement.setString(3, currentDate.getDate());
			statement.setInt(4, id);

			int rowsAffected = statement.executeUpdate();
			System.out.println("Rows affected: " + rowsAffected);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}// end of updateDate method

	public ArrayList<Integer> getEmployeesId() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "SELECT id FROM employees";

			statement = connection.prepareStatement(query);

			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				result.add(id);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public void updatePIN(Integer id, String pin) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			DBConnection.createConnection();
			connection = DBConnection.getConnection();

			String query = "UPDATE employees SET pin = ? WHERE id = ? ";
			statement = connection.prepareStatement(query);

			statement.setString(1, pin);
			statement.setInt(2, id);

			int rowsAffected = statement.executeUpdate();
			System.out.println("Rows affected: " + rowsAffected);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}// end of updatePIN method

}
