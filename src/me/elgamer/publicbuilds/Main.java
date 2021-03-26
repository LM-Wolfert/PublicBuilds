package me.elgamer.publicbuilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import me.elgamer.publicbuilds.commands.Corner;
import me.elgamer.publicbuilds.commands.CreateArea;
import me.elgamer.publicbuilds.commands.OpenGui;
import me.elgamer.publicbuilds.gui.AcceptGui;
import me.elgamer.publicbuilds.gui.DenyGui;
import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.gui.PlotGui;
import me.elgamer.publicbuilds.gui.PlotInfo;
import me.elgamer.publicbuilds.gui.ReviewGui;
import me.elgamer.publicbuilds.listeners.ChatListener;
import me.elgamer.publicbuilds.listeners.ClaimEnter;
import me.elgamer.publicbuilds.listeners.CommandListener;
import me.elgamer.publicbuilds.listeners.InventoryClicked;
import me.elgamer.publicbuilds.listeners.JoinServer;
import me.elgamer.publicbuilds.listeners.QuitServer;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.Particles;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.User;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	//MySQL
	private Connection connection;
	public String host, database, username, password, playerData, plotData, areaData;
	public int port;

	//Other
	public static Permission perms = null;

	static Main instance;
	static FileConfiguration config;
	
	ArrayList<User> users;
	
	Tutorial tutorial;
	Plots pl;
	List<Location> ls;
	
	public static StateFlag CREATE_PLOT;

	@Override
	public void onLoad() {
		
		//Setup worldguard flag
		createFlag();
	}
	
	@Override
	public void onEnable() {
		
		//Config Setup
		Main.instance = this;
		Main.config = this.getConfig();

		saveDefaultConfig();

		//MySQL		
		mysqlSetup();
		createPlayerData();
		createPlotData();
		PlotData.clearReview();
		
		//Create list of users.
		users = new ArrayList<User>();
		
		//Listeners
		new JoinServer(this);
		new QuitServer(this);
		new InventoryClicked(this);
		new ClaimEnter(this);
		new ChatListener(this);
		new CommandListener(this);

		//Commands
		getCommand("gui").setExecutor(new OpenGui());
		getCommand("corner").setExecutor(new Corner());
		getCommand("createarea").setExecutor(new CreateArea());

		//GUIs
		MainGui.initialize();
		ReviewGui.initialize();
		AcceptGui.initialize();
		DenyGui.initialize();
		PlotGui.initialize();
		PlotInfo.initialize();
		
		//1 second timer.
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				
				for (User u : users) {
					pl = u.plots;
					ls = pl.getMarkers();
					
					if (ls != null) {
						for (Location l : ls) {							
							Particles.spawnParticles(u.player, l);
						}
					}
					
				}
				
			}
		}, 0L, 20L);
	}

	public void onDisable() {

		//MySQL
		try {
			if (connection != null && !connection.isClosed()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL disconnected from " + config.getString("MySQL_database"));
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Remove all players who are in review.
		for (User u: users) {

			//Set tutorialStage in PlayData.
			PlayerData.setTutorialStage(u.uuid, u.tutorialStage);

			//Update the last online time of player.
			PlayerData.updateTime(u.uuid);

			//If the player is in a review, cancel it.
			if (u.reviewing != 0) {

				PlotData.setStatus(u.reviewing, "submitted");

			}
		}
	}

	//Creates the mysql connection.
	public void mysqlSetup() {

		host = config.getString("MySQL_host");
		port = config.getInt("MySQL_port");
		database = config.getString("MySQL_database");
		username = config.getString("MySQL_username");
		password = config.getString("MySQL_password");
		playerData = config.getString("MySQL_playerData");
		plotData = config.getString("MySQL_plotData");
		areaData = config.getString("MySQL_areaData");

		try {

			synchronized (this) {
				if (connection != null && !connection.isClosed()) {
					return;
				}

				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" 
						+ this.port + "/" + this.database, this.username, this.password));

				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL connected to " + config.getString("MySQL_database"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	//Returns the mysql connection.
	public Connection getConnection() {

		try {
			if (connection == null || connection.isClosed()) {
				mysqlSetup();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	//Sets the mysql connection as the variable 'connection'.
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	//Returns an instance of the plugin.
	public static Main getInstance() {
		return instance;
	}
	
	//Setup player_data table for mysql database if it doesn't exist.
	public void createPlayerData() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + playerData
							+ " ( ID VARCHAR(36) NOT NULL , NAME VARCHAR(20) NOT NULL , TUTORIAL_STAGE INT NOT NULL , BUILDING_POINTS INT NOT NULL , LAST_ONLINE BIGINT NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Setup plot_data table for mysql database if it doesn't exist.
	public void createPlotData() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + plotData
							+ " ( ID INT NOT NULL , OWNER TEXT NOT NULL , STATUS TEXT NOT NULL , MESSAGE TEXT NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Returns the User ArrayList.
	public ArrayList<User> getUsers() {
		return users;
	}
	
	//Returns the specific user based on Player instance.
	public User getUser(Player p) {
		
		for (User u : users) {
			
			if (u.player.equals(p)) {
				return u;
			}
			
		}
		
		return null;
	}
	
	public void createFlag() {

		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		
		try {
			
			StateFlag flag = new StateFlag("create-plot", true);
			registry.register(flag);
			CREATE_PLOT = flag;
			
		} catch (FlagConflictException e) {
			
			Flag<?> existing = registry.get("create-plot");
			if (existing instanceof StateFlag) {
				CREATE_PLOT = (StateFlag) existing;
			} else {
				Bukkit.broadcastMessage("Plugin Conflict Error with PublicBuilds");
			}
			
		}



	}

	
}