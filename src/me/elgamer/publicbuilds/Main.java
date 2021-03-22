package me.elgamer.publicbuilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
import me.elgamer.publicbuilds.utils.Accept;
import me.elgamer.publicbuilds.utils.CurrentPlot;
import me.elgamer.publicbuilds.utils.Particles;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.Reason;
import me.elgamer.publicbuilds.utils.Review;
import me.elgamer.publicbuilds.utils.Tutorial;
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
	
	Tutorial tutorial;
	Review review;
	Map<Player, Plots> plots;
	Reason reason;
	CurrentPlot currentPlot;
	Map<Player, Accept> accept;
	Plots pl;
	List<Location> ls;

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

		//Create Tutorial Hashmap.
		tutorial = new Tutorial();

		//Create Review Hashmap.
		review = new Review();
		
		//Create Plots Hashmap.
		plots = new HashMap<Player, Plots>();
		
		//Create Reason Hashmap.
		reason = new Reason();
		
		//Create CurrentPlot Hashmap.
		currentPlot = new CurrentPlot();

		//Create Accept Hashmap.
		accept = new HashMap<Player, Accept>();
				
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
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					pl = plots.get(p);
					ls = pl.getMarkers();
					
					if (ls != null) {
						for (Location l : ls) {							
							Particles.spawnParticles(p, l);
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
		for (Player p: Bukkit.getOnlinePlayers()) {

			//Remove player from the tutorial map.
			Tutorial t = instance.getTutorial();
			if (t.inTutorial(p)) {
				PlayerData.setTutorialStage(p.getUniqueId().toString(), t.getStage(p));
				t.removePlayer(p);
			}

			//Remove player from plots map.
			Main.getInstance().getPlots().remove(p);
			
			//Remove player from currentPlot map.
			CurrentPlot cp = instance.getCurrentPlot();
			cp.removePlayer(p);

			//Update the last online time of player.
			PlayerData.updateTime(p.getUniqueId().toString());

			//If the player is in a review, cancel it.
			Review r = instance.getReview();
			if (r.inReview(p)) {

				int plot = r.getReview(p);
				PlotData.setStatus(plot, "submitted");
				r.removePlayer(p);

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
	
	//Returns tutorial.
	public Tutorial getTutorial() {
		return tutorial;
	}
	
	//Returns review.
	public Review getReview() {
		return review;
	}
	
	//Returns plots.
	public Map<Player, Plots> getPlots() {
		return plots;
	}
	
	//Returns reason.
	public Reason getReason() {
		return reason;
	}
	
	
	//Returns accept.
	public Map<Player, Accept> getAccept(){
		return accept;
	}
	
	//Returns currentPlot.
	public CurrentPlot getCurrentPlot() {
		return currentPlot;
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
}