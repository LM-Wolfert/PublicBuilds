package me.elgamer.publicbuilds.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.elgamer.publicbuilds.Main;

public class PlotMessage {

	public static void addDenyMessage(int id, String uuid, String message, String type) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.denyData + " (ID,OWNER,MESSAGE,TYPE) VALUE (?,?,?,?)");
			statement.setInt(1, id);
			statement.setString(2, uuid);
			statement.setString(3, message);
			statement.setString(4, type);
			statement.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}

	}

	public static void addAccept(int id, String uuid, int points) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.acceptData + " (ID,OWNER,POINTS) VALUE (?,?,?)");
			statement.setInt(1, id);
			statement.setString(2, uuid);
			statement.setInt(3, points);
			statement.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}

	}

	public static boolean hasDenyMessage(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.denyData + " WHERE OWNER=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	public static boolean hasAcceptMessage(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.acceptData + " WHERE OWNER=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}
	
	public static int getAccept(String uuid) {
		
		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.acceptData + " WHERE OWNER=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			results.next();
			int id = results.getInt("ID");
			
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.acceptData + " WHERE ID=?");
			statement.setInt(1, id);
			statement.executeUpdate();

			return id;
			
		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}
	
	public static int getDenyPlot(String uuid) {
		
		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.denyData + " WHERE OWNER=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getInt("ID"));
			
		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}
	
	public static String getDenyReason(int plot) {
		
		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.denyData + " WHERE ID=?");
			statement.setInt(1, plot);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getString("MESSAGE"));
			
		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}
	}
	
	public static String getDenyType(int plot) {
		
		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.denyData + " WHERE ID=?");
			statement.setInt(1, plot);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getString("TYPE"));
			
		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}
	}
	
	public static void deleteDenyMessage(int plot) {
		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.denyData + " WHERE ID=?");
			statement.setInt(1, plot);
			statement.executeUpdate();
			
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

}
