package me.elgamer.publicbuilds.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import me.elgamer.publicbuilds.utils.Time;

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
				"INSERT INTO deny_data(plot, attempt, uuid, reviewer, feedback, type, time) VALUES(?, ?, ?, ?, ?, ?, ?);"
				)){
			statement.setInt(1, plot);
			statement.setInt(2, attempt(plot));
			statement.setString(3, uuid);
			statement.setString(4, reviewer);
			statement.setInt(5, feedback);
			statement.setString(6, type);
			statement.setLong(7, Time.currentTime());
			statement.executeUpdate();

			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}

	}

	private int attempt(int plot) {

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

	public boolean hasEntry(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM deny_data WHERE uuid = ?;"
				)){
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	public ArrayList<Integer> getLatest(ArrayList<Integer> deny, String uuid){

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM accept_data WHERE uuid = ? ORDER BY id DESC;"
				)){
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			while (results.next()) {
				deny.add(results.getInt("id"));

				if (deny.size() >= 5) {
					return deny;
				}

			}

			return deny;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return deny;
		}

	}
	
	public int getPlot(int id) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT plot FROM deny_data WHERE id= ?;"
				)){
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();
			results.next();

			return results.getInt("plot");

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}
	
	public int getAttempt(int id) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT attempt FROM deny_data WHERE id= ?;"
				)){
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();
			results.next();

			return results.getInt("attempt");

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}
	
	public String getType(int id) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT type FROM deny_data WHERE id= ?;"
				)){
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();
			results.next();

			return results.getString("type");

		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}
	}

}
