package me.elgamer.publicbuilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import me.elgamer.publicbuilds.commands.BuildingPoints;
//import me.elgamer.publicbuilds.commands.Corner;
import me.elgamer.publicbuilds.commands.CreateArea;
import me.elgamer.publicbuilds.commands.OpenGui;
import me.elgamer.publicbuilds.commands.SkipTutorial;
import me.elgamer.publicbuilds.gui.AcceptGui;
import me.elgamer.publicbuilds.gui.ConfirmCancel;
import me.elgamer.publicbuilds.gui.DenyGui;
import me.elgamer.publicbuilds.gui.LocationGUI;
import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.gui.NavigationGUI;
import me.elgamer.publicbuilds.gui.PlotGui;
import me.elgamer.publicbuilds.gui.PlotInfo;
import me.elgamer.publicbuilds.gui.ReviewGui;
import me.elgamer.publicbuilds.gui.SwitchServerGUI;
import me.elgamer.publicbuilds.listeners.ChatListener;
import me.elgamer.publicbuilds.listeners.ClaimEnter;
import me.elgamer.publicbuilds.listeners.CommandListener;
import me.elgamer.publicbuilds.listeners.InventoryClicked;
import me.elgamer.publicbuilds.listeners.JoinServer;
import me.elgamer.publicbuilds.listeners.PlayerInteract;
import me.elgamer.publicbuilds.listeners.QuitServer;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.mysql.PlotMessage;
import me.elgamer.publicbuilds.utils.Particles;
import me.elgamer.publicbuilds.utils.Ranks;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	//MySQL
	private Connection connection;
	public String host, database, username, password, playerData, plotData, areaData, denyData, acceptData;
	public int port;

	//Other
	public static Permission perms = null;

	static Main instance;
	static FileConfiguration config;

	ArrayList<User> users;

	Tutorial tutorial;
	HashMap<Integer, String> pl;
	List<BlockVector2> pt;
	Location lo;

	public static StateFlag CREATE_PLOT_GUEST;
	public static StateFlag CREATE_PLOT_APPRENTICE;
	public static StateFlag CREATE_PLOT_JRBUILDER;
	public static ItemStack selectionTool;
	public static ItemStack gui;

	//Locations
	public static Location spawn;
	public static Location cranham;

	//Building Poins Hologram
	Hologram hologram;

	//Essentials
	public static Essentials ess;

	@Override
	public void onLoad() {

		//Setup worldguard flag
		createFlags();
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
		createDenyData();
		createAcceptData();
		createAreaData();
		PlotData.clearReview();

		//Bungeecord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		//Create list of users.
		users = new ArrayList<User>();

		//Create selection tool item				
		selectionTool = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = selectionTool.getItemMeta();
		meta.setDisplayName(Utils.chat("&aSelection Tool"));
		selectionTool.setItemMeta(meta);

		//Create gui item				
		gui = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta2 = gui.getItemMeta();
		meta2.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Building Menu");
		gui.setItemMeta(meta2);

		//Listeners
		new JoinServer(this);
		new QuitServer(this);
		new InventoryClicked(this);
		new ClaimEnter(this);
		new ChatListener(this);
		new CommandListener(this);
		new PlayerInteract(this, selectionTool);

		//Commands
		getCommand("gui").setExecutor(new OpenGui());
		//getCommand("corner").setExecutor(new Corner());
		getCommand("createarea").setExecutor(new CreateArea());
		getCommand("skiptutorial").setExecutor(new SkipTutorial());
		getCommand("buildingpoints").setExecutor(new BuildingPoints());

		//Get essentials
		ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");	

		//GUIs
		MainGui.initialize();
		ReviewGui.initialize();
		AcceptGui.initialize();
		DenyGui.initialize();
		PlotGui.initialize();
		PlotInfo.initialize();
		LocationGUI.initialize();
		ConfirmCancel.initialize();
		NavigationGUI.initialize();
		SwitchServerGUI.initialize();

		//Locations
		spawn = new Location(Bukkit.getWorld("Lobby"), 
				config.getDouble("location.spawn.x"),
				config.getDouble("location.spawn.y"),
				config.getDouble("location.spawn.z"));
		cranham = new Location(Bukkit.getWorld(config.getString("worlds.build")), 
				config.getDouble("location.cranham.map.x"),
				config.getDouble("location.cranham.map.y"),
				config.getDouble("location.cranham.map.z"),
				180f, 45f);

		//Holograms
		hologram = HologramsAPI.createHologram(this, new Location(Bukkit.getWorld("Lobby"),
				config.getDouble("location.hologram.x"),
				config.getDouble("location.hologram.y"),
				config.getDouble("location.hologram.z")));

		//1 second timer.
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				for (User u : users) {

					if (u.tutorialStage == 6) {
						Utils.spawnFireWork(u.player);
						Bukkit.getScheduler().runTaskLater (instance, () -> Tutorial.continueTutorial(u), 60); //20 ticks equal 1 second
					}

					//Spawn particles at plot markers so they are visible.
					pt = u.plots.vector;

					if (pt != null) {

						for (BlockVector2 point : pt) {

							if (u.world.getName().equals(config.getString("worlds.tutorial"))) {
								lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.tutorial")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							} else {
								lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							}
							Particles.spawnParticles(u.player, lo);

						}

					}

					//Spawn particles at all plots owned by the player, if they are in the buildWorld.
					if (u.world.equals(Bukkit.getWorld(config.getString("worlds.build")))) {

						pl = PlotData.getPlots(u.uuid);

						for (Entry<Integer, String> i : pl.entrySet()) {

							pt = WorldGuardFunctions.getPoints(i.getKey());

							for (BlockVector2 point : pt) {

								lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
								Particles.spawnParticles(u.player, lo);

							}

						}
					}

					//If the player is reviewing a plot spawn particles in both the buildWorld and saveWorld.
					if (u.reviewing != 0) {

						pt = WorldGuardFunctions.getPoints(u.reviewing);

						for (BlockVector2 point : pt) {

							lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							Particles.spawnParticles(u.player, lo);
							lo = new Location(Bukkit.getWorld(config.getString("worlds.save")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							Particles.spawnParticles(u.player, lo);

						}

					}


					//Set the world of the player.
					u.world = u.player.getWorld();
					if ((u.world.getName().equals(config.getString("worlds.build")) && u.tutorialStage < 6)) {
						if (u.tutorialStage == 0) {
							u.tutorialStage = 1;
							Bukkit.getScheduler().runTaskLater (instance, () -> Tutorial.continueTutorial(u), 60); //20 ticks equal 1 second
						} else {
							u.player.teleport(new Location(Bukkit.getWorld(config.getString("worlds.tutorial")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));
							Bukkit.getScheduler().runTaskLater (instance, () -> Tutorial.continueTutorial(u), 60);
						}
					}

					//Send deny or accept message if a plot has been accepted or denied that they own.
					//Will not send if they are afk.
					if (!(ess.getUser(u.player).isAfk())) {
						if (PlotMessage.hasAcceptMessage(u.uuid)) {
							int plot = PlotMessage.getAccept(u.uuid);
							u.player.sendMessage(ChatColor.GREEN + "Your plot with ID " + plot + " has been accepted.");
						}

						if (PlotMessage.hasDenyMessage(u.uuid)) {
							int plot = PlotMessage.getDenyPlot(u.uuid);
							String reason = PlotMessage.getDenyReason(plot);
							String type = PlotMessage.getDenyType(plot);
							PlotMessage.deleteDenyMessage(plot);

							if (type.equals("claimed")) {
								u.player.sendMessage(ChatColor.RED + "Your plot with ID " + plot + " has been denied and returned.");
								u.player.sendMessage(ChatColor.RED + "Reason: " + reason);
							} else if (type.equals("deleted")) {
								u.player.sendMessage(ChatColor.RED + "Your plot with ID " + plot + " has been denied and removed.");
								u.player.sendMessage(ChatColor.RED + "Reason: " + reason);
							}
						}
					}

					Ranks.checkRankup(u);

					u.slot9 = u.player.getInventory().getItem(8);
					
					if (!(u.slot9 == null)) {
						if (u.slot9.equals(gui)) {

						} else {
							u.player.getInventory().setItem(8, gui);
						}
					} else {
						u.player.getInventory().setItem(8, gui);
					}
				}

			}
		}, 0L, 20L);

		//1 minute timer.
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				updateHologram();
				getConnection();

			}
		}, 0L, 1200L);
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
		denyData = config.getString("MySQL_denyData");
		acceptData = config.getString("MySQL_acceptData");

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
							+ " ( ID VARCHAR(36) NOT NULL , NAME VARCHAR(20) NOT NULL , TUTORIAL_STAGE INT NOT NULL , BUILDING_POINTS INT NOT NULL , LAST_ONLINE BIGINT NOT NULL , BUILDER_ROLE VARCHAR(20) NOT NULL , LAST_SUBMIT BIGINT NOT NULL , UNIQUE (ID))");
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
							+ " ( ID INT NOT NULL , OWNER TEXT NOT NULL , STATUS TEXT NOT NULL , LAST_VISIT BIGINT NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Setup deny_data table for mysql database if it doesn't exist.
	public void createDenyData() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + denyData
							+ " ( ID INT NOT NULL , OWNER TEXT NOT NULL , MESSAGE TEXT NOT NULL , TYPE TEXT NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Setup accept_data table for mysql database if it doesn't exist.
	public void createAcceptData() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + acceptData
							+ " ( ID INT NOT NULL , OWNER TEXT NOT NULL , POINTS INT NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Setup area_data table for mysql database if it doesn't exist.
	public void createAreaData() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + areaData
							+ " ( ID INT NOT NULL , NAME TEXT NOT NULL , TYPE TEXT NOT NULL , STATUS TEXT NOT NULL , UNIQUE (ID))");
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

	public void createFlags() {

		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

		try {

			StateFlag flag = new StateFlag("create-plot-guest", true);
			registry.register(flag);
			CREATE_PLOT_GUEST = flag;

		} catch (FlagConflictException e) {

			Flag<?> existing = registry.get("create-plot-guest");
			if (existing instanceof StateFlag) {
				CREATE_PLOT_GUEST = (StateFlag) existing;
			} else {
				Bukkit.broadcastMessage("Plugin Conflict Error with PublicBuilds");
			}

		}

		try {

			StateFlag flag = new StateFlag("create-plot-apprentice", true);
			registry.register(flag);
			CREATE_PLOT_APPRENTICE = flag;

		} catch (FlagConflictException e) {

			Flag<?> existing = registry.get("create-plot-apprentice");
			if (existing instanceof StateFlag) {
				CREATE_PLOT_APPRENTICE = (StateFlag) existing;
			} else {
				Bukkit.broadcastMessage("Plugin Conflict Error with PublicBuilds");
			}

		}

		try {

			StateFlag flag = new StateFlag("create-plot-jrbuilder", true);
			registry.register(flag);
			CREATE_PLOT_JRBUILDER = flag;

		} catch (FlagConflictException e) {

			Flag<?> existing = registry.get("create-plot-jrbuilder");
			if (existing instanceof StateFlag) {
				CREATE_PLOT_JRBUILDER = (StateFlag) existing;
			} else {
				Bukkit.broadcastMessage("Plugin Conflict Error with PublicBuilds");
			}

		}

	}

	public void updateHologram() {

		hologram.clearLines();

		LinkedHashMap<String, Integer> lead = PlayerData.pointsTop();

		if (lead == null || lead.size() == 0) {
			return;
		}

		hologram.appendTextLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Building Points Leaderboard");

		for (Entry<String, Integer> e : lead.entrySet()) {

			hologram.appendTextLine(e.getKey() + ": " + e.getValue());

		}


	}
}