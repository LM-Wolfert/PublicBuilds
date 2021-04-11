package me.elgamer.publicbuilds.utils;

import org.bukkit.World;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.mysql.PlayerData;

public class User {

	public Player player;
	public String uuid;
	public String name;
	public int tutorialStage;
	public Plots plots;
	public Accept accept;
	public int reviewing = 0;
	public String reasonType;
	public int currentPlot = 0;
	public String currentStatus = null;
	
	public int inPlot = 0;
	public String plotOwner = null;
	
	public World world;
	
	public User(Player player) {
		
		//Set player, uuid and name variable.
		this.player = player;
		uuid = player.getUniqueId().toString();
		name = player.getName();
		
		//Update player data.
		updatePlayerData();
			
		//Continue the tutorial from where they last were.
		tutorialStage = PlayerData.getTutorialStage(uuid);
		
		//Create plots map.
		plots = new Plots();
		
		//Set current world
		world = player.getWorld();

	}
	
	//Update playerdata or create a new instance if it's their first time joining the server.
	private void updatePlayerData() {
		
		if (PlayerData.playerExists(uuid)) {
			
			//If true then update their last online time and username.
			PlayerData.updateTime(player.getUniqueId().toString());
			PlayerData.updatePlayerName(player.getUniqueId().toString(), player.getName());
			
		} else {
			
			String role = "guest";
			
			if (player.hasPermission("group.builder")) {
				role = "builder";
			} else if (player.hasPermission("group.jrbuilder")) {
				role = "jrbuilder";
			} else if (player.hasPermission("group.apprentice")) {
				role = "apprentice";
			}
			
			PlayerData.createPlayerInstance(player.getUniqueId().toString(), player.getName(), role);
			
		}
		
	}
	
	//Create accept
	public void createAccept() {
		accept = new Accept();
	}
}
