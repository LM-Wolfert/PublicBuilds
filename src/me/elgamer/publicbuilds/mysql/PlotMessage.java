package me.elgamer.publicbuilds.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class PlotMessage {

	DataSource dataSource;

	public PlotMessage(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	public  void addDenyMessage(int id, String uuid, String message, String type) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO deny_data(id, uuid, feedback, type) VALUES(?, ?, ?, ?);"
		)){
			statement.setInt(1, id);
			statement.setString(2, uuid);
			statement.setString(3, message);
			statement.setString(4, type);
			statement.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}

	}

	public  void addAccept(int id, String uuid, int points) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO accept_data(id, uuid, points) VALUES (?, ?, ?);"
		)){
			statement.setInt(1, id);
			statement.setString(2, uuid);
			statement.setInt(3, points);
			statement.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}

	}

	public  boolean hasDenyMessage(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM deny_data WHERE uuid = ?"
		)){
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	public  boolean hasAcceptMessage(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM accept_data WHERE uuid = ?"
		)){
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	public int getAccept(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM accept_data WHERE uuid = ?"
		)){
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			results.next();
			int id = results.getInt("id");

			if (!(deleteAccept(id))) {
				return 0;
			}

			return id;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}
	
	public boolean deleteAccept(int id) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM accept_data WHERE id = ?"
		)){
			statement.setInt(1, id);
			statement.executeUpdate();
			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	public  int getDenyPlot(String uuid) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM deny_data WHERE uuid = ?"
		)){
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getInt("id"));

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}

	public  String getDenyReason(int plot) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT feedback FROM deny_data WHERE id = ?"
		)){
			statement.setInt(1, plot);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getString("feedback"));

		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}
	}

	public  String getDenyType(int plot) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT type FROM deny_Data WHERE id = ?"
		)){
			statement.setInt(1, plot);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getString("type"));

		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}
	}

	public  void deleteDenyMessage(int plot) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM deny_data WHERE id = ?"
		)){
			statement.setInt(1, plot);
			statement.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

}
