package me.elgamer.publicbuilds.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DenyData {
	
	DataSource dataSource;

	public DenyData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}
	
	public boolean insert(int plot, String uuid, String reviewer, int feedback, String type) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO deny_data(plot, attempt, uuid, reviewer, feedback, type) VALUES(?, ?, ?, ?, ?, ?);"
		)){
			statement.setInt(1, plot);
			statement.setInt(2, getAttempt(plot));
			statement.setString(3, uuid);
			statement.setString(4, reviewer);
			statement.setInt(5, feedback);
			statement.setString(6, type);
			statement.executeUpdate();
			
			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}

	}
	
	private int getAttempt(int plot) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT MAX(attempt) as attempt FROM deny_data WHERE plot = ?;"
		)){
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getInt("attempt");
			} else {
				return 1;
			}

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 1;
		}
	}

}
