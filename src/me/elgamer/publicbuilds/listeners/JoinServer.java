package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Inactive;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.User;

public class JoinServer implements Listener {

	Tutorial t;
	
	public JoinServer(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		
		//Create instance of User and add it to list.
		User u = new User(e.getPlayer());
		Main.getInstance().getUsers().add(u);
		
		//If the player is in the tutorial but not in the correct place, teleport them back.
		FileConfiguration config = Main.getInstance().getConfig();
		
		
		if (0 < u.tutorialStage && u.tutorialStage < 6) {
			if (u.world.getName().equals(config.getString("worlds.tutorial.before"))) {
				u.player.teleport(new Location(Bukkit.getWorld(config.getString("worlds.tutorial.before")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));
			}
			
			u.player.sendMessage("Stage: " + u.tutorialStage);
			Tutorial.continueTutorial(u);
		}
		
		u.player.sendMessage("Stage: " + u.tutorialStage);
		
		//Get player instance.
		Player player = e.getPlayer();

		//If a player hasn't been online for more than 14 days their plot will cancelled.
		Inactive.cancelInactivePlots(player);
					
	}

}
