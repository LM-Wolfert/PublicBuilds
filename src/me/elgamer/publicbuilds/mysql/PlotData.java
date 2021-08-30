package me.elgamer.publicbuilds.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Time;
import me.elgamer.publicbuilds.utils.User;

public class PlotData {

	//Will generate a new id as the lowest unused integer value.
	public static int getNewID() {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData);
			ResultSet results = statement.executeQuery();

			if (results.last()) {
				int last = results.getInt("ID");
				return (last+1);
			} else {
				return 1;
			}

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 1;
		}
	}

	//Created a new plot entry in the database.
	public static boolean createPlot(int id, String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement insert = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.plotData + " (ID,OWNER,STATUS,LAST_VISIT) VALUE (?,?,?,?)");

			insert.setInt(1, id);
			insert.setString(2, uuid);
			insert.setString(3, "claimed");
			insert.setLong(4, Time.currentTime());
			insert.executeUpdate();

			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;			
		}
	}

	//Checks whether the player has a plot that is claimed, submitted or under review.
	public static boolean hasPlot(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE OWNER=? AND (STATUS=? OR STATUS=? OR STATUS=?)");
			statement.setString(1, uuid);
			statement.setString(2, "claimed");
			statement.setString(3, "submitted");
			statement.setString(4, "reviewing");

			ResultSet results = statement.executeQuery();
			return results.next();

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	//Checks whether there is a submitted plot to review.
	public static boolean reviewExists(User u) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE STATUS=?");
			statement.setString(1, "submitted");

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				if (results.getString("OWNER").equals(u.uuid)) {
					continue;
				} else {
					return true;
				}
			}

			return false;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	//Changed the status of the specific plot.
	//Statuses options are: claimed, submitted, reviewing, cancelled, completed.
	public static void setStatus(int plot, String status) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement update = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotData + " SET STATUS=? WHERE ID=?");
			update.setString(1, status);
			update.setInt(2, plot);
			update.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}		
	}

	//Changes the status of all plots that are under review back to submitted.
	//This is run on server start to make sure nothing gets stuck.
	public static void clearReview() {

		Main instance = Main.getInstance();

		try {
			PreparedStatement update = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotData + " SET STATUS=? WHERE STATUS=?");
			update.setString(1, "submitted");
			update.setString(2, "reviewing");
			update.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}		
	}

	//Selects the lowest id plot and sets it to reviewing.
	//A reviewer will be assigned that plot.
	public static int newReview(User u) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE STATUS=?");
			statement.setString(1, "submitted");
			ResultSet results = statement.executeQuery();

			while (results.next()) {
				if (results.getString("OWNER").equals(u.uuid)) {
					continue;
				} else {
					int plot = results.getInt("ID");

					PreparedStatement update = instance.getConnection().prepareStatement
							("UPDATE " + instance.plotData + " SET STATUS=? WHERE ID=?");
					update.setString(1, "reviewing");
					update.setInt(2, plot);
					update.executeUpdate();

					return(plot);
				}
			}

			return 0;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}		
	}

	//Gets all plots claimed or submitted by a player and returns them in a hashmap with id and status.
	public static HashMap<Integer, String> getPlots(String uuid) {

		Main instance = Main.getInstance();
		HashMap<Integer, String> plots = new HashMap<Integer, String>();

		try {			
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE OWNER=? AND (STATUS=? OR STATUS=?)");
			statement.setString(1, uuid);
			statement.setString(2, "claimed");
			statement.setString(3, "submitted");
			ResultSet results = statement.executeQuery();

			while (results.next()) {
				plots.put(results.getInt("ID"), results.getString("STATUS"));
			}

			return plots;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}
	}

	//Returns the active plot count, this only includes plots that are claimed.
	public static int activePlotCount(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.plotData + " WHERE OWNER=? AND STATUS=?");
			statement.setString(1, uuid);
			statement.setString(2, "claimed");

			ResultSet results = statement.executeQuery();
			results.next();
			return results.getInt(1);

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}

	//Returns total plot count, this includes plots that are claimed, submitted and under review.
	public static int totalPlotCount(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.plotData + " WHERE OWNER=? AND (STATUS=? OR STATUS=? OR STATUS=?)");
			statement.setString(1, uuid);
			statement.setString(2, "claimed");
			statement.setString(3, "submitted");
			statement.setString(4, "reviewing");

			ResultSet results = statement.executeQuery();
			results.next();
			return results.getInt(1);

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}

	//Returns completed plot count.
	public static int completedPlots(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.plotData + " WHERE OWNER=? AND STATUS=?");
			statement.setString(1, uuid);
			statement.setString(2, "completed");

			ResultSet results = statement.executeQuery();
			results.next();
			return results.getInt(1);

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}

	//Returns the uuid of the plot owner.
	public static String getOwner(int plot) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE ID=?");
			statement.setInt(1, plot);

			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("OWNER");

		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}

	}

	//Returns a list of plots that are inactive.
	//Inactive implies that the owner of the plot has not been online in at least 14 days.
	public static List<Integer> getInactivePlots(long time) {

		//Create list for the inactive plots.
		List<Integer> plots = new ArrayList<Integer>();

		//Get instance of plugin.
		Main instance = Main.getInstance();

		//Get all plots that are owned by players that are inactive, only active plots will be counted, not submitted ones.
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE LAST_VISIT<=? AND STATUS=?");
			statement.setLong(1, time);
			statement.setString(2, "claimed");

			ResultSet results = statement.executeQuery();

			while (results.next()) {
				plots.add(results.getInt("ID"));
			}

			return plots;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}

	}

	public static void setLastVisit(String uuid, int plot) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement update = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotData + " SET LAST_VISIT=? WHERE ID=?");
			update.setLong(1, Time.currentTime());
			update.setInt(2, plot);
			update.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}

	}

	public static void setLastVisit(int plot) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement update = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotData + " SET LAST_VISIT=? WHERE ID=?");
			update.setLong(1, Time.currentTime());
			update.setInt(2, plot);
			update.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();
		}

	}

	public static void newSubmit(int id) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement insert = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.submitData + " (ID) VALUE (?)");

			insert.setInt(1, id);
			insert.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();			
		}

	}

	public static void removeSubmit(int id) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement insert = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.submitData + " WHERE ID=?");

			insert.setInt(1, id);
			insert.executeUpdate();

		} catch (SQLException sql) {
			sql.printStackTrace();			
		}

	}

}
