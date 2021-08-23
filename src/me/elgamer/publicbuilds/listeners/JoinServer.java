package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.tutorial.Tutorial;
import me.elgamer.publicbuilds.utils.Inactive;
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
		
		/*
		if (0 < u.tutorialStage && u.tutorialStage < 6) {
			if (!u.world.getName().equals(config.getString("worlds.tutorial"))) {
				u.player.teleport(new Location(Bukkit.getWorld(config.getString("worlds.tutorial")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));
			}

			Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> Tutorial.continueTutorial(u), 60);
		}
		*/
		
		//If a player hasn't been online for more than 14 days their plot will cancelled.
		Inactive.cancelInactivePlots();
					
	}

}
