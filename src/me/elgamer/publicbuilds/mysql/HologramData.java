package me.elgamer.publicbuilds.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.bukkit.Location;

public class HologramData {
	
	DataSource dataSource;

	public HologramData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	public boolean create(String name, Location l, boolean visible) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO hologram_data(name, x, y, z, visible) VALUES(?, ?, ?, ?, ?);"
				)){
			statement.setString(1, name);
			statement.setDouble(2, l.getX());
			statement.setDouble(3, l.getY());
			statement.setDouble(4, l.getZ());
			statement.setBoolean(5, visible);
			statement.executeUpdate();

			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}

	}

	public boolean nameExists(String name) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT name FROM hologram_data WHERE name = ?;"
				)){
			statement.setString(1, name);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}
	
	public void delete(String name) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM hologram_data where name = ?;"
				)){
			statement.setString(1, name);
			statement.executeUpdate();
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
		
	}
	
	public boolean move(String name, Location l) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE hologram_data SET x = ?, y = ?, z = ? WHERE name = ?;"
				)){
			statement.setDouble(1, l.getX());
			statement.setDouble(2, l.getY());
			statement.setDouble(3, l.getZ());
			statement.setString(4, name);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}
	
	public boolean toggleVisibility(String name) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE hologram_data SET visibility = 1 - visibility WHERE name = ?;"
				)){
			statement.setString(1, name);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

}
