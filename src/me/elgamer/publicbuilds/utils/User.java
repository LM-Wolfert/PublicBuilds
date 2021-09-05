package me.elgamer.publicbuilds.utils;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.TutorialData;
import me.elgamer.publicbuilds.reviewing.Review;
import me.elgamer.publicbuilds.tutorial.Tutorial;

public class User {

	public Player player;
	public String uuid;
	public String name;
	
	public int buildingTime;
	
	public Plots plots;
	public int currentPlot = 0;
	public String currentStatus = null;
	
	public int inPlot = 0;
	public String plotOwner = null;
	
	public String role;
	
	public World world;
	
	public ItemStack slot5;
	public ItemStack slot9;
	
	public Tutorial tutorial;
	
	TutorialData tutorialData;
	PlayerData playerData;
	
	public Review review = null;
	
	public User(Player player) {
		
		//Set player, uuid and name variable.
		this.player = player;
		uuid = player.getUniqueId().toString();
		name = player.getName();
		
		tutorialData = Main.getInstance().tutorialData;
		playerData = Main.getInstance().playerData;
		
		//Update player data.
		updatePlayerData();
		
		//Get building time
		buildingTime = me.elgamer.btepoints.utils.PlayerData.getBuildTime(uuid);
			
		//Continue the tutorial from where they last were.
		if (!(tutorialData.tutorialComplete(uuid))) {
			tutorial = new Tutorial(this);
			tutorial.continueTutorial(this);
		} else {
			tutorial = new Tutorial(this, true);
		}
		
		//Create plots map.
		plots = new Plots();
		
		//Set current world
		world = player.getWorld();

	}
	
	//Update playerdata or create a new instance if it's their first time joining the server.
	public void updatePlayerData() {
		

		if (player.hasPermission("group.builder")) {
			role = "builder";
		} else if (player.hasPermission("group.jrbuilder")) {
			role = "jrbuilder";
		} else if (player.hasPermission("group.apprentice")) {
			role = "apprentice";
		} else {
			role = "guest";
		}
		
		if (playerData.playerExists(uuid)) {
			
			//If true then update their last online time and username.
			playerData.updateTime(uuid);
			playerData.updatePlayerName(uuid, player.getName());
			playerData.updateRole(uuid, role);
		} else {
			
			playerData.createPlayerInstance(player.getUniqueId().toString(), player.getName(), role);
			
		}
		
	}
}
