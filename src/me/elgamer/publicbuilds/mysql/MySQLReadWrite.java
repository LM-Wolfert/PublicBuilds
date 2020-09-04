package me.elgamer.publicbuilds.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import me.elgamer.publicbuilds.Main;

public class MySQLReadWrite {
	
	Main instance = Main.getInstance(); 
	
	public boolean playerExists(UUID uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.table + " WHERE UUID=?");
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
	
	public void addClaim(UUID uuid, String name) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			if (playerExists(uuid) != true) {
				PreparedStatement insert = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.table + " (UUID,REGION_NAMES,REVIEW_COUNT) VALUE (?,?,?)");
				insert.setString(1, uuid.toString());
				insert.setString(2, name);
				insert.setInt(3, 0);
				insert.executeUpdate();		
				
			} else {
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.table + " SET REGION_NAMES=? WHERE UUID=?");
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
	
	public boolean checkDuplicateName(UUID uuid, String name) {
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			if (playerExists(uuid) != true) {
				return false;
			} else {
				String regionNames = results.getString("REGION_NAMES");
				if (regionNames == null) {
					return false;
				} else {
				String[] names = regionNames.split(",");
				
				for (int i = 0; i < names.length; i++) {
					if (names[i].equals(name)) {
						return true;
						}
					}
				}
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void removeClaim(UUID uuid, String name) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.table + " SET REGION_NAMES=? WHERE UUID=?");
				String regionNames = results.getString("REGION_NAMES");
				String[] names = regionNames.split(",");
				regionNames = null;
				
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
				
				statement.setString(1, regionNames);
				statement.setString(2, uuid.toString());
				statement.executeUpdate();
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String returnClaims(UUID uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("REGION_NAMES");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean playerReviewExists(UUID uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.review + " WHERE UUID=?");
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
	
	public void submitClaim(UUID uuid, String name) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.review + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			if (playerReviewExists(uuid) != true) {
				PreparedStatement insert = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.review + " (UUID,PLOT_NAMES) VALUE (?,?)");
				insert.setString(1, uuid.toString());
				insert.setString(2, name);
				insert.executeUpdate();	
				
				PreparedStatement statement2 = instance.getConnection().prepareStatement
						("SELECT * FROM " + instance.table + " WHERE UUID=?");
				statement2.setString(1, uuid.toString());
				ResultSet results2 = statement2.executeQuery();
				results2.next();
				
				statement2 = instance.getConnection().prepareStatement
						("UPDATE " + instance.table + " SET REVIEW_COUNT=? WHERE UUID=?");
				int reviewCount = results2.getInt("REVIEW_COUNT");
				
				reviewCount++;
				
				statement2.setInt(1, reviewCount);
				statement2.setString(2, uuid.toString());
				statement2.executeUpdate();
				
			} else {
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.review + " SET PLOT_NAMES=? WHERE UUID=?");
				String plotNames = results.getString("PLOT_NAMES");
				if (plotNames == null) {
					plotNames = name;
				} else {
				plotNames = plotNames + "," + name;
				}
				statement.setString(1, plotNames);
				statement.setString(2, uuid.toString());
				statement.executeUpdate();
				
				PreparedStatement statement2 = instance.getConnection().prepareStatement
						("SELECT * FROM " + instance.table + " WHERE UUID=?");
				statement2.setString(1, uuid.toString());
				ResultSet results2 = statement2.executeQuery();
				results2.next();
				
				statement2 = instance.getConnection().prepareStatement
						("UPDATE " + instance.table + " SET REVIEW_COUNT=? WHERE UUID=?");
				int reviewCount = results2.getInt("REVIEW_COUNT");
				
				reviewCount++;
				
				statement2.setInt(1, reviewCount);
				statement2.setString(2, uuid.toString());
				statement2.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
