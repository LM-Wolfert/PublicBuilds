package me.elgamer.publicbuilds.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.User;

public class MoveEvent implements Listener {

	public MoveEvent(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
			
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		
		User u = Main.getInstance().getUser(e.getPlayer());
		
		if (u.tutorial.tutorial_stage == 1) {
			
			if (nearYes(e.getTo())) {
				u.player.teleport();
				u.tutorial.tutorial_stage = 8;
			} else if (nearNo(e.getTo())) {
				u.player.teleport();
				u.tutorial.tutorial_stage = 2;
			}
			
		}
	}
	
	public boolean nearYes(Location l) {
		
		if (l.distance(Main.TUTORIAL_1_YES) <= 0.5) {
			return true;
		} else {return false;}		
	}
	
	public boolean nearNo(Location l) {
		if (l.distance(Main.TUTORIAL_1_NO) <= 0.5) {
			return true;
		} else {return false;}		
	}
}
