package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
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
		Main.getInstance().getUsers().add(new User(e.getPlayer()));
		
		//Get player instance.
		Player player = e.getPlayer();

		//If a player hasn't been online for more than 14 days their plot will cancelled.
		Inactive.cancelInactivePlots(player);
					
	}

}
