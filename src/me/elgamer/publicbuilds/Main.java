package me.elgamer.publicbuilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.elgamer.publicbuilds.commands.ClaimCommand;
import me.elgamer.publicbuilds.commands.OpenGui;
import me.elgamer.publicbuilds.commands.RemoveClaim;
import me.elgamer.publicbuilds.commands.Review;
import me.elgamer.publicbuilds.commands.SubmitClaim;
import me.elgamer.publicbuilds.gui.PlotGui;
import me.elgamer.publicbuilds.gui.ReviewGui;
import me.elgamer.publicbuilds.listeners.ClaimEnter;
import me.elgamer.publicbuilds.listeners.InventoryClicked;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
	
	//MySQL
	private Connection connection;
	public String host, database, username, password, plotTable, reviewTable, completeTable;
	public int port;
	
	//Other
	public static Permission perms = null;
	
	static Main instance;
	static FileConfiguration config;
	
	@Override
	public void onEnable() {
		
		//Config Setup
		Main.instance = this;
		Main.config = this.getConfig();
		
		saveDefaultConfig();
		
		//MySQL		
		mysqlSetup();
		
		//Listeners		
		new InventoryClicked(this);
		new ClaimEnter(this);
		
		//Commands
		getCommand("createPlot").setExecutor(new ClaimCommand());
		getCommand("plot").setExecutor(new OpenGui());
		getCommand("removePlot").setExecutor(new RemoveClaim());
		getCommand("submitPlot").setExecutor(new SubmitClaim());
		getCommand("review").setExecutor(new Review());

		//GUI
		PlotGui.initialize();
		ReviewGui.initialize();
		
		//Vault
		setupPermissions();
	}
	
	public void onDisable() {
		
		//MySQL
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void mysqlSetup() {
		
		host = config.getString("MySQL_host");
		port = config.getInt("MySQL_port");
		database = config.getString("MySQL_database");
		username = config.getString("MySQL_username");
		password = config.getString("MySQL_password");
		plotTable = config.getString("MySQL_plotTable");
		reviewTable = config.getString("MySQL_reviewTable");
		completeTable = config.getString("MySQL_completeTable");
		
		try {
			
			synchronized (this) {
				if (getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" 
				+ this.port + "/" + this.database, this.username, this.password));
				
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	public static Permission getPermissions() {
        return perms;		
	}
	
	public static Main getInstance() {
		return instance;
	}		
}