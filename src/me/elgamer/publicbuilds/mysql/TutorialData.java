package me.elgamer.publicbuilds.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.User;

public class TutorialData {

	public static void createPlayerInstance(String uuid, int tutorial_type, int tutorial_stage, boolean first_time) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.tutorialData + " (ID,TUTORIAL_TYPE,TUTORIAL_STAGE,FIRST_TIME) VALUE (?,?,?,?)");
			statement.setString(1, uuid);
			statement.setInt(2, tutorial_type);
			statement.setInt(3, tutorial_stage);
			statement.setBoolean(4, first_time);
			statement.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

	public static boolean tutorialComplete(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.tutorialData + " WHERE ID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				
				if (results.getInt("TUTORIAL_TYPE") == 10) {
					return true;
				} else {
					return false;
				}
				
			} else {
				createPlayerInstance(uuid, 1, 1, true);
				return false;
			}


		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}


	}
	
	public static int getType(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.tutorialData + " WHERE ID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getInt("TUTORIAL_TYPE"));


		} catch (SQLException sql) {
			sql.printStackTrace();
			return 1;
		}

	}
	
	public static int getStage(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.tutorialData + " WHERE ID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getInt("TUTORIAL_STAGE"));


		} catch (SQLException sql) {
			sql.printStackTrace();
			return 1;
		}

	}
	
	public static boolean getTime(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.tutorialData + " WHERE ID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			results.next();
			return (results.getBoolean("FIRST_TIME"));


		} catch (SQLException sql) {
			sql.printStackTrace();
			return true;
		}

	}
	
	public static void updateValues(User u) {
		
		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.tutorialData + " SET TUTORIAL_TYPE=?,TUTORIAL_STAGE=?,FIRST_TIME=? WHERE ID=?");
			statement.setInt(1, u.tutorial.tutorial_type);
			statement.setInt(2, u.tutorial.tutorial_stage);
			statement.setBoolean(3, u.tutorial.first_time);
			statement.setString(4, u.uuid);
			
			statement.executeUpdate();


		} catch (SQLException sql) {
			sql.printStackTrace();
		}
		
	}

}
