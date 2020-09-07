package me.elgamer.publicbuilds.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import me.elgamer.publicbuilds.Main;

public class MySQLReadWrite {

	Main instance = Main.getInstance(); 


	//In player_data
	public boolean playerExists(UUID uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, uuid.toString());

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	//In player_data - update Column 2: REGION_NAMES
	public void addClaim(UUID uuid, String name) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();

			//If player doesn't exist - set default values
			if (playerExists(uuid) != true) {
				PreparedStatement insert = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.plotTable + " (UUID,REGION_NAMES) VALUE (?,?)");
				insert.setString(1, uuid.toString());
				insert.setString(2, name);
				insert.executeUpdate();

				//If player does exist - update values	
			} else {
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.plotTable + " SET REGION_NAMES=? WHERE UUID=?");
				String regionNames = results.getString("REGION_NAMES");
				if (regionNames == null) {
					regionNames = name;
				} else {
					regionNames = regionNames + "," + name;
				}
				statement.setString(1, regionNames);
				statement.setString(2, uuid.toString());
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Checks for duplicate plot names in player_data
	public boolean checkDuplicateName(UUID uuid, String name) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();

			//If player doesn't exist they have no plots
			if (playerExists(uuid) != true) {
				return false;

				//If player does exist check both REGION_NAMES and REVIEW_NAMES
			} else {
				String regionNames = results.getString("REGION_NAMES");
				String reviewNames = results.getString("REVIEW_NAMES");

				//If both are empty, there are no duplicates
				if (regionNames == null && reviewNames == null) {
					return false;

					//Else check both regionNames and reviewNames for duplicate
				} else {
					if (checkDuplicateNames(regionNames, name) || (checkDuplicateNames(reviewNames, name))) {
						return true;
					} else { return false; }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} return false;
	}

	//Check for duplicate name in String
	private boolean checkDuplicateNames(String names, String name) {

		if (names == null) {
			return false;
		} else {

			String[] nameString = names.split(",");

			for (int i = 0; i < nameString.length; i++) {
				if (nameString[i].equals(name)) {
					return true;
				}
			}
			return false;
		}
	}

	//Remove plot
	public void removeClaim(UUID uuid, String name) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();

			String regionNames = results.getString("REGION_NAMES");
			String[] names = regionNames.split(",");
			regionNames = null;

			//Rebuilding REGION_NAMES String without removed claim
			for (int i = 0; i < names.length; i++) {
				if (names[i].equals(name)) {
				} else {
					if (regionNames == null) {
						regionNames = names[i];
					} else {
						regionNames = regionNames + "," + names[i];
					}
				}
			}

			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotTable + " SET REGION_NAMES=? WHERE UUID=?");
			statement.setString(1, regionNames);
			statement.setString(2, uuid.toString());
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Returns String of plotNames
	public String returnClaims(UUID uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("REGION_NAMES");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	//Submit plot - Update REVIEW_NAMES in player_data - Add entry to review
	public void submitClaim(UUID uuid, String name) {
		try {

			//If player is in player_data, continue
			if (playerExists(uuid)) {
				removeClaim(uuid, name);

				// Update player_data
				PreparedStatement statement = instance.getConnection().prepareStatement
						("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
				statement.setString(1, uuid.toString());
				ResultSet results = statement.executeQuery();
				results.next();

				String reviewNames = results.getString("REVIEW_NAMES");

				if (reviewNames == null) {
					reviewNames = name;
				} else {
					reviewNames = reviewNames + "," + name;
				}
				
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.plotTable + " SET REVIEW_NAMES=? WHERE UUID=?");
				statement.setString(2, uuid.toString());
				statement.setString(1, reviewNames);
				statement.executeUpdate();

				// Add entry to review
				PreparedStatement insert = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.reviewTable + " (PLOT_ID,UUID,PLOT_NAME) VALUE (?,?,?)");
				insert.setString(1, uuid.toString() + "," + name);
				insert.setString(2, uuid.toString());
				insert.setString(3, name);
				insert.executeUpdate();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Find plot for player to review
	public String getReview(UUID uuid) {
		try {

			//Get plots without reviewer
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.reviewTable + " WHERE REVIEWER_UUID=?");
			statement.setString(1, null);
			ResultSet results = statement.executeQuery();

			//If there is an available plot for review
			if (results.next()) {
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.reviewTable + " SET REVIEW_UUID WHERE PLOT_ID=?");
				String name = results.getString("PLOT_ID");
				statement.setString(2, name);
				statement.setString(1, uuid.toString());
				statement.executeUpdate();
				return name;
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
