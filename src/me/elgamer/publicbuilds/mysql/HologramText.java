package me.elgamer.publicbuilds.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class HologramText {
	
	DataSource dataSource;

	public HologramText(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	public boolean addLine(String name, int line, String text) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO hologram_text(hologram_name, line, text) VALUES(?, ?, ?);"
				)){
			statement.setString(1, name);
			statement.setInt(2, line);
			statement.setString(3, text);
			statement.executeUpdate();

			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}

	}

	public int lines(String name) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT COUNT(id) FROM hologram_text WHERE name = ?;"
				)){
			statement.setString(1, name);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return (results.getInt(1));
			} else {
				return 0;
			}

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}

}
