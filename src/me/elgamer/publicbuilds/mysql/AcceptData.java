package me.elgamer.publicbuilds.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class AcceptData {
	
	DataSource dataSource;

	public AcceptData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}
	
	public boolean insert(int plot, String uuid, String reviewer, int feedback, int size, int accuracy, int quality, int points) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO accept_data(plot, uuid, reviewer, feedback, size, accuracy, quality, points) VALUES(?, ?, ?, ?, ?, ?, ?, ?);"
		)){
			statement.setInt(1, plot);
			statement.setString(2, uuid);
			statement.setString(3, reviewer);
			statement.setInt(4, feedback);
			statement.setInt(5, size);
			statement.setInt(6, accuracy);
			statement.setInt(7, quality);
			statement.setInt(8, quality);
			statement.executeUpdate();
			
			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}

	}
	
	public boolean insert(int plot, String uuid, String reviewer, int size, int accuracy, int quality, int points) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO accept_data(plot, uuid, reviewer, size, accuracy, quality, points) VALUES(?, ?, ?, ?, ?, ?, ?);"
		)){
			statement.setInt(1, plot);
			statement.setString(2, uuid);
			statement.setString(3, reviewer);
			statement.setInt(4, size);
			statement.setInt(5, accuracy);
			statement.setInt(6, quality);
			statement.setInt(7, quality);
			statement.executeUpdate();
			
			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}

	}

}
