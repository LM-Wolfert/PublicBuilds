package me.elgamer.publicbuilds.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.DenyGui;
import me.elgamer.publicbuilds.utils.DeleteClaim;

public class MySQLReadWrite {

	Main instance = Main.getInstance(); 


	//In player_data
	public boolean playerExists(String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE UUID=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//Returns String of plotNames
	public String returnClaims(String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();

			return (results.getString("REGION_NAMES"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//Checks for duplicate plot names in plot_data
		public boolean checkDuplicateName(String plotID) {
			try {
				PreparedStatement statement = instance.getConnection().prepareStatement
						("SELECT * FROM " + instance.plotData + " WHERE PLOT_ID=?");
				statement.setString(1, plotID);
				ResultSet results = statement.executeQuery();
				
				//If there exists an entry with the plotID then it already exists.
				if (results.next()) {
					return true;
				}
				return false;
				
			} catch (SQLException e) {
				e.printStackTrace();
			} return false;
		}

	//Add the plot to the database
	public void addClaim(String uuid, String name, String plotID, int Xmin, int Zmin, int Xmax, int Zmax) {
		try {
		
			//First we need update REGION_NAMES in the playerData table
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			//If player has no value in the table create a new one
			if (results.next() != true) {
				
				PreparedStatement insert = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.playerData + " (UUID,REGION_NAMES) VALUE (?,?,?)");
				insert.setString(1, uuid);
				insert.setString(2, name);
				insert.executeUpdate();

			//If player has an entry in the table then update it
			} else {
				
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.playerData + " SET REGION_NAMES=? WHERE UUID=?");
				
				//Get regionNames from resultSet
				String regionNames = results.getString("REGION_NAMES");
				
				//If regionNames is null set it to the plotName, else add the new plotName to the string
				if (regionNames == null) {
					regionNames = name;
				} else {
					regionNames = regionNames + "," + name;
				}
				
				statement.setString(1, regionNames);
				statement.setString(2, uuid);
				statement.executeUpdate();
				
			//Secondly we need to add the plotID to the plotData
			PreparedStatement insert = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.plotData + 
							" (PLOT_ID,PLOT_OWNER,PLOT_NAME,X_MIN,Z_MIN,X_MAX,Z_MAX) VALUE (?,?,?,?,?,?,?)");
			insert.setString(1, plotID);
			insert.setString(2, uuid);
			insert.setString(3, name);
			insert.setInt(4, Xmin);
			insert.setInt(5, Zmin);
			insert.setInt(6, Xmax);
			insert.setInt(7, Zmax);
			insert.executeUpdate();	
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Get X at minimum point in specified plot
	public int getX(String plotID) {
		try {
		PreparedStatement statement = instance.getConnection().prepareStatement
				("SELECT * FROM " + instance.plotData + " WHERE PLOT_ID=?");
		statement.setString(1, plotID);
		ResultSet results = statement.executeQuery();
		results.next();
		 
		return results.getInt("X_MIN");
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	//Get Z at minimum point in specified plot
	public int getZ(String plotID) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE PLOT_ID=?");
			statement.setString(1, plotID);
			ResultSet results = statement.executeQuery();
			results.next();

			return results.getInt("Z_MIN");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
		
	//Check if player has a plot
	public boolean hasPlot(String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			//If player has an entry
			if (results.next()) {
				if (results.getString("REGION_NAMES") != null || results.getString("DENIED_NAMES") != null) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String returnDeniedClaims(String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();

			return (results.getString("DENIED_NAMES"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int checkAttempt(String plotID) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE PLOT_ID=?");
			statement.setString(1, plotID);
			ResultSet results = statement.executeQuery();
			results.next();
			
			return (results.getInt("ATTEMPT"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 1;	
	}
	
	//Submit plot - Update REVIEW_NAMES in player_data - Add entry to review
	public void submitClaim(String uuid, String name, String plotID) {
		try {

			// Update playerData
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();

			String reviewNames = results.getString("REVIEW_NAMES");
			String regionNames = results.getString("REGION_NAMES");

			//Add plotName to reviewNames
			if (reviewNames == null) {
				reviewNames = name;
			} else {
				reviewNames = reviewNames + "," + name;
			}
			
			//Remove plotName from regionNames
			String[] regions = regionNames.split(",");
			regionNames = null;
			for (int i = 0 ; i<regions.length ; i++) {
				if (regions[i].equals(name)) {}
				else if (regionNames == null){
					regionNames = regions[i];
				} else {
					regionNames = regionNames + "," + regions[i];
				}
			}
			
			//Update playerData
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.playerData + " SET REVIEW_NAMES=? SET REGION_NAMES=? WHERE UUID=?");
			statement.setString(3, uuid);
			statement.setString(1, reviewNames);
			statement.setString(2, regionNames);
			statement.executeUpdate();

			//Update entry in plotData
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotData + " SET SUBMITTED=? WHERE PLOT_ID=?");
			statement.setString(2, plotID);
			statement.setString(1, "true");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Remove plot
	public void removeClaim(String uuid, String plotID) {
		try {
			//Get attempt number to decide which table column to look in
			int attempt = checkAttempt(plotID);
			String select = null;
			String update = null;
			String get = null;

			if (attempt == 1) {
				select = "SELECT * FROM " + instance.playerData + " WHERE UUID=?";
				update = "UPDATE " + instance.playerData + " SET REGION_NAMES=? WHERE UUID=?";
				get = "REGION_NAMES";
			} else {
				select = "SELECT * FROM " + instance.playerData + " WHERE UUID=?";
				update = "UPDATE " + instance.playerData + " SET DENIED_NAMES=? WHERE UUID=?";
				get = "DENIED_NAMES";
			}
			
			PreparedStatement statement = instance.getConnection().prepareStatement
					(select);
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			
			String regionNames = results.getString(get);
			String[] regions = regionNames.split(",");
			regionNames = null;
			
			//Rebuilding regionNames without plotID
			for (int i = 0; i < regions.length; i++) {
				if (regions[i].equals(plotID)) {}
				else { if (regionNames == null) {
					regionNames = regions[i];
				} else {
					regionNames = regionNames + regions[i];
					}}
			}

			//Adding the regionNames back to the table
			statement = instance.getConnection().prepareStatement
					(update);
			
			statement.setString(1, regionNames);	
			statement.setString(2, uuid);
			statement.executeUpdate();
			
			//Remove plot from plotData
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.plotData + "WHERE PLOT_ID=?");
			statement.setString(1, plotID);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Find plot for player to review
	public String getReview(String uuid) {
		try {

			//Get plots without reviewer
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE SUBMITTED=true");
			ResultSet results = statement.executeQuery();

			//If there is an available plot for review
			if (results.next()) {
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.plotData + " SET SUBMITTED=? WHERE PLOT_ID=?");
				statement.setString(2, results.getString("PLOT_ID"));
				statement.setString(1, uuid);
				statement.executeUpdate();
				return results.getString("PLOT_ID");
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean inReview(String uuid) {
		
		try {
			
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE SUBMITTED=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			
			return(results.next());
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	public String currentReview(String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE SUBMITTED=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			return (results.getString("PLOT_ID"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String currentReviewPlotOwner(String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE SUBMITTED=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			return (results.getString("PLOT_OWNER"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void denyReview(Player p, String plotID) {

		int attempt = checkAttempt(plotID);

		if (attempt >= 3) {

			p.openInventory(DenyGui.GUI(p));
			
		} else {
			returnClaim(p.getUniqueId().toString(), attempt+1);
		}

	}
	
	public void returnClaim(String uuid, int attempt) {
		
		try {
			//get plot info
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.plotData + " WHERE REVIEWER=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			
			String plotID = results.getString("PLOT_ID");
			String plotOwner = results.getString("PLOT_OWNER");
			
			//remove reviewer from plotData
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotData + " SET REVIEWER=? WHERE PLOT_ID=?");
			statement.setString(1, null);
			statement.setString(2, plotID);
			statement.executeUpdate();
			
			//get review_names and denied_names from playerData
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE UUID=?");
			statement.setString(1, plotOwner);
			results = statement.executeQuery();
			results.next();
			
			String reviewNames = results.getString("REVIEW_NAMES");
			String deniedNames = results.getString("DENIED_NAMES");
			String[] review = results.getString("REVIEW_NAMES").split(",");
			reviewNames = null;
		
			//remove plotID from reviewNames
			for (int i = 0; i < review.length; i++) {
				if (review[i] == plotID) {}
				else {
					if (reviewNames == null) {
						reviewNames = review[i];
					} else {
						reviewNames = reviewNames + "," + review[i];
					}
				}
			}
			
			//add plotID to denied_names
			if (deniedNames == null) {
				deniedNames = plotID;
			} else {
				deniedNames = deniedNames  + "," + plotID;
			}

			//update review_names in playerData
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.playerData + " SET REVIEW_NAMES=? WHERE UUID=?");
			statement.setString(2, plotOwner);
			statement.setString(1, reviewNames);
			statement.executeUpdate();
			
			//update denied_names in playerData
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.playerData + " SET DENIED_NAMES=? WHERE UUID=?");
			statement.setString(2, plotOwner);
			statement.setString(1, deniedNames);
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeReview(String uuid) {
		
		try {
			String plotID = currentReview(uuid);
			
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.plotData + " SET REVIEWER=? WHERE PLOT_ID=?");
			statement.setString(1, plotID);
			statement.setString(2, "true");
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
