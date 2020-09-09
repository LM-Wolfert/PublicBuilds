package me.elgamer.publicbuilds.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.DeleteClaim;

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
						("INSERT INTO " + instance.plotTable + " (UUID,REGION_NAMES,REVIEW_NAMES,ATTEMPT_2,ATTEMPT_3) VALUE (?,?,?,?,?)");
				insert.setString(1, uuid.toString());
				insert.setString(2, name);
				insert.setString(3, null);
				insert.setString(4, null);
				insert.setString(5, null);
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
				String attempt2 = results.getString("ATTEMPT_2");
				String attempt3 = results.getString("ATTEMPT_3");


				if (checkDuplicateNames(regionNames, name) || (checkDuplicateNames(reviewNames, name) || 
						checkDuplicateNames(attempt2, name) || checkDuplicateNames(attempt3, name))) {
					return true;
				} else { return false; }
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
	public int removeClaim(String uuid, String name) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();

			String regionNames = results.getString("REGION_NAMES");
			String attempt2 = results.getString("ATTEMPT_2");
			String attempt3 = results.getString("ATTEMPT_3");
			String regionNamesNew = null;
			String attempt2New = null;
			String attempt3New = null;
			int attempt = 0;
			String set = null;

			//Rebuilding REGION_NAMES String without removed claim
			if (regionNames != null) {
				String[] names = regionNames.split(",");
				for (int i = 0; i < names.length; i++) {
					if (names[i].equals(name)) {
						attempt = 1;
						set = " SET REGION_NAMES=? WHERE UUID=?";
					} else {
						if (regionNamesNew == null) {
							regionNamesNew = names[i];
						} else {
							regionNamesNew = regionNamesNew + "," + names[i];
						}
					}
				}
			}


			//If name wasn't in REGION_NAMES check attempt2
			if (attempt2 != null) {
				String[] names2 = attempt2.split(",");
				for (int i = 0; i < names2.length; i++) {
					if (names2[i].equals(name)) {
						attempt = 2;
						set = " SET ATTEMPT_2=? WHERE UUID=?";
					} else {
						if (attempt2New == null) {
							attempt2New = names2[i];
						} else {
							attempt2New = attempt2New + "," + names2[i];
						}
					}
				}
			}


			//If name wasn't in attempt2 check attempt3
			if (attempt3 != null) {
				String[] names3 = attempt3.split(",");
				for (int i = 0; i < names3.length; i++) {
					if (names3[i].equals(name)) {
						attempt = 3;
						set = " SET ATTEMPT_3=? WHERE UUID=?";
					} else {
						if (attempt3New == null) {
							attempt3New = names3[i];
						} else {
							attempt3New = attempt3New + "," + names3[i];
						}
					}
				}
			}	

			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotTable + set);
			
			switch (attempt) {
			case 1:
				statement.setString(1, attempt2New);
			case 2:
				statement.setString(1, attempt2New);
			case 3:
				statement.setString(1, attempt3New);
			}	
			statement.setString(2, uuid);
			attempt = 1;


			statement.executeUpdate();
			return attempt;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	//Returns String of plotNames
	public String returnClaims(UUID uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			String attempt1 = results.getString("REGION_NAMES");
			String attempt2 = results.getString("ATTEMPT_2");
			String attempt3 = results.getString("ATTEMPT_3");
			
			String result = null;
			
			if (attempt1 == null) {}
			else {result = attempt1;}
			
			if (attempt2 == null) {}
			else {if (result == null) {result = attempt2;}
			else {result = result + "," + attempt2;}
			}
			
			if (attempt3 == null) {return result;}
			else {if (result == null) {return attempt3;}
			else {return (result + "," + attempt3);}
			}
		
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
				int attempt = removeClaim(uuid.toString(), name);

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
						("INSERT INTO " + instance.reviewTable + " (PLOT_ID,UUID,PLOT_NAME,ATTEMPT) VALUE (?,?,?,?)");
				insert.setString(1, uuid.toString() + "," + name);
				insert.setString(2, uuid.toString());
				insert.setString(3, name);
				insert.setInt(4, attempt);
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
					("SELECT * FROM " + instance.reviewTable + " WHERE REVIEWER_UUID IS NULL");
			ResultSet results = statement.executeQuery();

			//If there is an available plot for review
			if (results.next()) {
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.reviewTable + " SET REVIEWER_UUID=? WHERE PLOT_ID=?");
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
	
	public boolean inReview(UUID uuid) {
		
		try {
			
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.reviewTable + " WHERE REVIEWER_UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			
			return(results.next());
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	public String currentReview(UUID uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.reviewTable + " WHERE REVIEWER_UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			return (results.getString("PLOT_ID"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void denyReview(Player p, String plotID) {

		try {

			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.reviewTable + " WHERE PLOT_ID=?");
			statement.setString(1, plotID);
			ResultSet results = statement.executeQuery();
			results.next();

			int attempt = results.getInt("ATTEMPT");
			String owner = results.getString("UUID");
			String plotName = results.getString("PLOT_NAME");

			switch (attempt) {
			case 1:
				returnClaim(owner, plotName, "ATTEMPT_2");
			case 2:
				returnClaim(owner, plotName, "ATTEMPT_3");
			case 3:
				DeleteClaim delete = new DeleteClaim();
				delete.deleteClaim(p, owner, plotID, plotName);		
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void returnClaim(String owner, String plotName, String attempt) {
		
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, owner);
			ResultSet results = statement.executeQuery();
			results.next();
			
			String plots = results.getString(attempt);
			
			String reviewNames = results.getString("REVIEW_NAMES");
			String[] review = results.getString("REVIEW_NAMES").split(",");
			reviewNames = null;
		
			if (plots == null) {
				plots = plotName;
			} else {
				plots = plots + "," + plotName;
			}
			
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotTable + " SET " + attempt + "=? WHERE UUID=?");
			statement.setString(2, owner);
			statement.setString(1, plots);
			statement.executeUpdate();
			
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotTable + " SET REVIEW_NAMES=? WHERE UUID=?");
			statement.setString(2, owner);
			
			for (int i = 0 ; i < review.length ; i++) {
				if (review[i].equals(plotName)) {}
				else if (reviewNames == null) {
					reviewNames = review[i];
				} else {
					reviewNames = reviewNames + "," + review[i];
				}
			}
			
			statement.setString(1, reviewNames);
			statement.executeUpdate();
			
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.reviewTable + "WHERE PLOT_ID=?");
			statement.setString(1, owner + "," + plotName);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeReview(String owner, String plotName) {
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotTable + " WHERE UUID=?");
			statement.setString(1, owner);
			ResultSet results = statement.executeQuery();
			results.next();
			String[] review = results.getString("REVIEW_NAMES").split(",");
			String reviewNames = null;
			
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotTable + " SET REVIEW_NAMES=? WHERE UUID=?");
			statement.setString(2, owner);
			
			for (int i = 0 ; i < review.length ; i++) {
				if (review[i].equals(plotName)) {}
				else if (reviewNames == null) {
					reviewNames = review[i];
				} else {
					reviewNames = reviewNames + "," + review[i];
				}
			}
			
			statement.setString(1, reviewNames);
			statement.executeUpdate();
			
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.reviewTable + "WHERE PLOT_ID=?");
			statement.setString(1, owner + "," + plotName);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
