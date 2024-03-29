package me.elgamer.publicbuilds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import com.earth2me.essentials.Essentials;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

//import me.elgamer.publicbuilds.commands.Apply;
import me.elgamer.publicbuilds.commands.BuildingPoints;
//import me.elgamer.publicbuilds.commands.Corner;
import me.elgamer.publicbuilds.commands.CreateArea;
import me.elgamer.publicbuilds.commands.CustomHolo;
import me.elgamer.publicbuilds.commands.OpenGui;
//import me.elgamer.publicbuilds.commands.SkipTutorial;
import me.elgamer.publicbuilds.commands.Spawn;
//import me.elgamer.publicbuilds.commands.TutorialHelp;
import me.elgamer.publicbuilds.gui.ConfirmCancel;
import me.elgamer.publicbuilds.gui.LocationGUI;
import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.gui.PlotGui;
import me.elgamer.publicbuilds.gui.PlotInfo;
import me.elgamer.publicbuilds.gui.SwitchServerGUI;
import me.elgamer.publicbuilds.listeners.ClaimEnter;
import me.elgamer.publicbuilds.listeners.InventoryClicked;
import me.elgamer.publicbuilds.listeners.ItemSpawn;
import me.elgamer.publicbuilds.listeners.JoinServer;
import me.elgamer.publicbuilds.listeners.PlayerInteract;
import me.elgamer.publicbuilds.listeners.QuitServer;
import me.elgamer.publicbuilds.mysql.AcceptData;
import me.elgamer.publicbuilds.mysql.AreaData;
import me.elgamer.publicbuilds.mysql.BookData;
import me.elgamer.publicbuilds.mysql.DenyData;
import me.elgamer.publicbuilds.mysql.HologramData;
import me.elgamer.publicbuilds.mysql.HologramText;
import me.elgamer.publicbuilds.mysql.MessageData;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.mysql.PointsData;
import me.elgamer.publicbuilds.mysql.TutorialData;
import me.elgamer.publicbuilds.reviewing.AcceptGui;
import me.elgamer.publicbuilds.reviewing.DenyGui;
import me.elgamer.publicbuilds.reviewing.FeedbackGui;
import me.elgamer.publicbuilds.reviewing.ReviewGui;
import me.elgamer.publicbuilds.tutorial.CommandListener;
import me.elgamer.publicbuilds.tutorial.MoveEvent;
import me.elgamer.publicbuilds.tutorial.Tutorial;
import me.elgamer.publicbuilds.tutorial.TutorialCommand;
import me.elgamer.publicbuilds.tutorial.TutorialConstants;
import me.elgamer.publicbuilds.tutorial.TutorialGui;
import me.elgamer.publicbuilds.tutorial.TutorialSelectionGui;
import me.elgamer.publicbuilds.tutorial.TutorialStage;
import me.elgamer.publicbuilds.tutorial.TutorialTabCompleter;
import me.elgamer.publicbuilds.tutorial.TutorialVideoGui;
import me.elgamer.publicbuilds.utils.Holograms;
import me.elgamer.publicbuilds.utils.Particles;
import me.elgamer.publicbuilds.utils.Ranks;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	//MySQL
	public String host, database, username, password;
	public int port;

	private DataSource dataSource;
	public PlayerData playerData;
	public PlotData plotData;
	public TutorialData tutorialData;
	public AreaData areaData;
	public DenyData denyData;
	public AcceptData acceptData;
	public BookData bookData;
	public MessageData messageData;
	public PointsData pointsData;
	public HologramData hologramData;
	public HologramText hologramText;


	//Other
	public static Permission perms = null;

	static Main instance;
	static FileConfiguration config;

	ArrayList<User> users;

	Tutorial tutorial;
	HashMap<Integer, String> pl;
	List<BlockVector2> pt;
	Location lo;
	ArrayList<Integer> pls;
	
	ArrayList<Location> blocks;
	public static BlockData stone;

	int hasMessage;
	
	public static Boolean POINTS_ENABLED;

	public static StateFlag CREATE_PLOT_GUEST;
	public static StateFlag CREATE_PLOT_APPRENTICE;
	public static StateFlag CREATE_PLOT_JRBUILDER;

	public static ItemStack selectionTool;
	public static ItemStack gui;
	public static ItemStack tutorialGui;

	int interval;

	//Locations
	public static Location spawn;
	public static Location cranham;
	public static Location monkspath;

	//Holograms
	Holograms holograms;

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
		
		POINTS_ENABLED = config.getBoolean("points_enabled");

		interval = 10*60;

		//MySQL		
		try {
			dataSource = mysqlSetup();
			initDb();

			playerData = new PlayerData(dataSource);
			plotData = new PlotData(dataSource);
			tutorialData = new TutorialData(dataSource);
			areaData = new AreaData(dataSource);
			denyData = new DenyData(dataSource);
			acceptData = new AcceptData(dataSource);
			bookData = new BookData(dataSource);
			messageData = new MessageData(dataSource);
			pointsData = new PointsData(dataSource);
			hologramData = new HologramData(dataSource);
			hologramText = new HologramText(dataSource);

		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		plotData.clearReview();

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

		//Create tutorial type skip item				
		tutorialGui = new ItemStack(Material.LECTERN);
		ItemMeta meta3 = gui.getItemMeta();
		meta3.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial Menu");
		tutorialGui.setItemMeta(meta3);

		//Holograms
		holograms = new Holograms(hologramData, hologramText, playerData);
		holograms.create();
		
		stone = Bukkit.createBlockData(Material.STONE);

		//Listeners
		new JoinServer(this);
		new QuitServer(this, tutorialData, playerData, plotData);
		new InventoryClicked(this);
		new ClaimEnter(this, plotData, playerData);
		new PlayerInteract(this, selectionTool);

		new ItemSpawn(this);

		//Tutorial listeners
		new CommandListener(this);
		new MoveEvent(this);

		//Commands
		getCommand("gui").setExecutor(new OpenGui());
		//getCommand("corner").setExecutor(new Corner());
		getCommand("createarea").setExecutor(new CreateArea());
		//getCommand("skiptutorial").setExecutor(new SkipTutorial());
		getCommand("buildingpoints").setExecutor(new BuildingPoints());
		getCommand("spawn").setExecutor(new Spawn());
		getCommand("tutorial").setExecutor(new TutorialCommand());
		getCommand("tutorialStage").setExecutor(new TutorialStage());
		//getCommand("apply").setExecutor(new Apply());
		//getCommand("converttutorial").setExecutor(new ConvertTutorial());
		//getCommand("tutorialhelp").setExecutor(new TutorialHelp());

		getCommand("customholo").setExecutor(new CustomHolo(hologramData, hologramText, holograms));

		//Tab Completer
		getCommand("tutorial").setTabCompleter(new TutorialTabCompleter());

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
		SwitchServerGUI.initialize();
		TutorialGui.initialize();
		TutorialVideoGui.initialize();
		TutorialSelectionGui.initialize();
		FeedbackGui.initialize();

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
		monkspath = new Location(Bukkit.getWorld(config.getString("worlds.build")), 
				config.getDouble("location.monkspath.map.x"),
				config.getDouble("location.monkspath.map.y"),
				config.getDouble("location.monkspath.map.z"),
				180f, 45f);

		//Setup Tutorial Constants
		new TutorialConstants(config);

		//1 second timer.
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				for (User u : users) {

					//If the player has a correct line set in tutorial 2.3 set fake blocks every second as indicator.
					if (u.tutorial.tutorial_type == 2 && u.tutorial.tutorial_stage == 3) {
						
						if (u.tutorial.line_1) {
							blocks = TutorialConstants.TUTORIAL_2_LINE1.vectorBlocks();
							for (Location l : blocks) {
								u.player.sendBlockChange(l, stone);
							}
						}
						
						if (u.tutorial.line_2) {
							blocks = TutorialConstants.TUTORIAL_2_LINE2.vectorBlocks();
							for (Location l : blocks) {
								u.player.sendBlockChange(l, stone);
							}
						}
						
						if (u.tutorial.line_3) {
							blocks = TutorialConstants.TUTORIAL_2_LINE3.vectorBlocks();
							for (Location l : blocks) {
								u.player.sendBlockChange(l, stone);
							}
						}
						
						if (u.tutorial.line_4) {
							blocks = TutorialConstants.TUTORIAL_2_LINE4.vectorBlocks();
							for (Location l : blocks) {
								u.player.sendBlockChange(l, stone);
							}
						}		
					}

					//Increase buildingTime for each second the player is in a buildable claim and is not AFK
					if (!(u.plotOwner == null) && POINTS_ENABLED) {
						if (ess.getUser(u.player).isAfk() == false && u.plotOwner.equals(u.uuid)) {

							u.buildingTime += 1;

							if (u.buildingTime >= interval) {
								u.buildingTime -= interval;

								me.elgamer.btepoints.utils.Points.addPoints(u.uuid, 1);
							}


						}
					}


					//Spawn particles at plot markers so they are visible.
					pt = u.plots.vector;

					if (pt != null) {

						for (BlockVector2 point : pt) {

							if (u.world.getName().equals(config.getString("worlds.tutorial"))) {
								lo = new Location(Bukkit.getWorld(config.getString("worlds.tutorial")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.tutorial")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							} else {
								lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							}
							Particles.spawnRedParticles(u.player, lo);

						}

					}

					//Spawn particles at all plots owned by the player, if they are in the buildWorld.
					if (u.world.equals(Bukkit.getWorld(config.getString("worlds.build")))) {

						pl = plotData.getPlots(u.uuid);

						for (Entry<Integer, String> i : pl.entrySet()) {

							pt = WorldGuardFunctions.getPoints(i.getKey());

							for (BlockVector2 point : pt) {

								lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
								Particles.spawnBlueParticles(u.player, lo);

							}

						}
					}

					//Spawn particles at all plots owned by other players near you.
					if (u.world.equals(Bukkit.getWorld(config.getString("worlds.build")))) {

						pls = WorldGuardFunctions.getNearbyPlots(u);

						if (pls == null) {

						} else {

							for (int i : pls) {

								if (u.review != null) {
									if (i == u.review.plot) {
										continue;
									}
								}

								pt = WorldGuardFunctions.getPoints(i);

								for (BlockVector2 point : pt) {

									lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
									Particles.spawnGreenParticles(u.player, lo);

								}

							}
						}
					}

					//If the player is reviewing a plot spawn particles in both the buildWorld and saveWorld.
					if (u.review != null) {

						pt = WorldGuardFunctions.getPoints(u.review.plot);

						for (BlockVector2 point : pt) {

							lo = new Location(Bukkit.getWorld(config.getString("worlds.build")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							Particles.spawnRedParticles(u.player, lo);
							lo = new Location(Bukkit.getWorld(config.getString("worlds.save")), point.getX()+0.5, Bukkit.getWorld(config.getString("worlds.build")).getHighestBlockYAt(point.getX(), point.getZ())+1.5, point.getZ()+0.5);
							Particles.spawnRedParticles(u.player, lo);

						}

					}


					//Set the world of the player.
					u.world = u.player.getWorld();
					if (!(u.world.getName().equals(config.getString("worlds.tutorial"))) && u.tutorial.first_time == false) {
						u.tutorial.tutorial_stage = 0;
						u.tutorial.tutorial_type = 10;
						u.tutorial.complete = true;
					} else if (!(u.world.getName().equals(config.getString("worlds.tutorial"))) && u.tutorial.first_time == true) {
						Bukkit.getScheduler().runTaskLater (instance, () -> u.tutorial.continueTutorial(), 60);
					}

					//Send deny or accept message if a plot has been accepted or denied that they own.
					//Will not send if they are afk.
					if (!(ess.getUser(u.player).isAfk())) {

						hasMessage = messageData.hasMessage(u.uuid);

						if (hasMessage != 0) {
							switch (messageData.getType(hasMessage)) {

							case "returned":
								u.player.sendMessage(ChatColor.RED + "Your plot " + messageData.getPlot(hasMessage) + " was denied and returned, see feedback in the plot menu.");
								break;
							case "resized":
								u.player.sendMessage(ChatColor.RED + "Your plot " + messageData.getPlot(hasMessage) + " was denied and resized, see feedback in the plot menu.");
								break;
							case "deleted":
								u.player.sendMessage(ChatColor.RED + "Your plot " + messageData.getPlot(hasMessage) + " was denied and deleted, see feedback in the plot menu.");
								break;
							case "accepted":
								u.player.sendMessage(ChatColor.GREEN + "Your plot " + messageData.getPlot(hasMessage) + " was accepted, see feedback in the plot menu.");
								break;
							case "inactive":
								u.player.sendMessage(ChatColor.RED + "Your plot " + messageData.getPlot(hasMessage) + " has been deleted due to inactivity.");
								break;
							default:
								break;							
							}

							messageData.delete(hasMessage);
						}
					}

					Ranks.checkRankup(u);

					u.slot5 = u.player.getInventory().getItem(4);
					u.slot9 = u.player.getInventory().getItem(8);

					if (!(u.slot9 == null)) {
						if (u.slot9.equals(gui)) {
						} else {
							u.player.getInventory().setItem(8, gui);
						}
					} else {
						u.player.getInventory().setItem(8, gui);
					}

					if (u.tutorial.complete) {
						if (!(u.slot5 == null)) {
							if (u.slot5.equals(tutorialGui)) {
								u.player.getInventory().setItem(4, null);
							}
						}
					} else {
						if (!(u.slot5 == null)) {
							if (u.slot5.equals(tutorialGui)) {
							} else {
								u.player.getInventory().setItem(4, tutorialGui);
							}
						} else {
							u.player.getInventory().setItem(4, tutorialGui);
						}
					}
					
					if (u.tutorial.tutorial_type == 10) {
						u.tutorial.removeHologramVisibility();
					}
				}

			}
		}, 0L, 20L);

		//1 minute timer.
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {

				holograms.reload("scoreboard");
				try {
					testDataSource(dataSource);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, 0L, 1200L);
	}

	public void onDisable() {

		//Remove all players who are in review.
		for (User u: users) {

			//Set tutorialStage in PlayData.
			tutorialData.updateValues(u);

			//Update the last online time of player.
			playerData.updateTime(u.uuid);

			//If the player is in a review, cancel it.
			if (u.review != null) {

				WorldGuardFunctions.removeMember(u.review.plot, u.uuid);
				plotData.setStatus(u.review.plot, "submitted");
				u.review.editBook.unregister();
				u.player.getInventory().setItem(4, u.review.previousItem);
				u.review = null;

			}
		}

		Bukkit.getConsoleSender().sendMessage("Disabled PublicBuilds");
	}

	//Creates the mysql connection.
	private DataSource mysqlSetup() throws SQLException {

		host = config.getString("MySQL_host");
		port = config.getInt("MySQL_port");
		database = config.getString("MySQL_database");
		username = config.getString("MySQL_username");
		password = config.getString("MySQL_password");

		MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setServerName(host);
		dataSource.setPortNumber(port);
		dataSource.setDatabaseName(database + "?&useSSL=false&");
		dataSource.setUser(username);
		dataSource.setPassword(password);

		testDataSource(dataSource);
		return dataSource;

	}

	private void testDataSource(DataSource dataSource) throws SQLException{
		try (Connection connection = dataSource.getConnection()) {
			if (!connection.isValid(1000)) {
				throw new SQLException("Could not establish database connection.");
			}
		}
	}

	private void initDb() throws SQLException, IOException {
		// first lets read our setup file.
		// This file contains statements to create our inital tables.
		// it is located in the resources.
		String setup;
		try (InputStream in = getClassLoader().getResourceAsStream("dbsetup.sql")) {
			// Legacy way
			setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
			throw e;
		}
		// Mariadb can only handle a single query per statement. We need to split at ;.
		String[] queries = setup.split(";");
		// execute each query to the database.
		for (String query : queries) {
			// If you use the legacy way you have to check for empty queries here.
			if (query.trim().isEmpty()) continue;
			try (Connection conn = dataSource.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.execute();
			}
		}
		getLogger().info("�2Database setup complete.");
	}

	//Returns an instance of the plugin.
	public static Main getInstance() {
		return instance;
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
	
	public Holograms getHolograms() {
		return holograms;
	}
}