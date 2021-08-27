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
		
		if (u.tutorial.tutorial_type == 1) {
			
			if (nearYes(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_9_START);
				u.tutorial.tutorial_type = 9;
				u.tutorial.tutorial_stage = 1;
			} else if (nearNo(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_2_START);
				u.tutorial.tutorial_type = 2;
				u.tutorial.tutorial_stage = 1;
				u.tutorial.continueTutorial(u);
			}
			
		} else if (u.tutorial.tutorial_type == 3) {
			
			if (nearWorldedit(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_4_START);
				u.tutorial.tutorial_type = 4;
				u.tutorial.tutorial_stage = 1;
				u.tutorial.continueTutorial(u);
			} else if(nearGep(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_5_START);
				u.tutorial.tutorial_type = 5;
				u.tutorial.tutorial_stage = 1;
				u.tutorial.continueTutorial(u);
			} else if (nearRoofs(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_6_START);
				u.tutorial.tutorial_type = 6;
				u.tutorial.tutorial_stage = 1;
				u.tutorial.continueTutorial(u);
			} else if (nearDetails(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_7_START);
				u.tutorial.tutorial_type = 7;
				u.tutorial.tutorial_stage = 1;
				u.tutorial.continueTutorial(u);
			} else if (nearTexture(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_8_START);
				u.tutorial.tutorial_type = 8;
				u.tutorial.tutorial_stage = 1;
				u.tutorial.continueTutorial(u);
			} else if (nearContinue(e.getTo())) {
				u.player.teleport(Main.TUTORIAL_9_START);
				u.tutorial.tutorial_type = 9;
				u.tutorial.tutorial_stage = 1;
				u.tutorial.continueTutorial(u);
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
	
	public boolean nearWorldedit(Location l) {
		if (l.distance(Main.TUTORIAL_3_WORLDEDIT) <= 0.5) {
			return true;
		} else {return false;}	
	}
	
	public boolean nearGep(Location l) {
		if (l.distance(Main.TUTORIAL_3_GEP) <= 0.5) {
			return true;
		} else {return false;}	
	}
	
	public boolean nearRoofs(Location l) {
		if (l.distance(Main.TUTORIAL_3_ROOFS) <= 0.5) {
			return true;
		} else {return false;}	
	}
	
	public boolean nearDetails(Location l) {
		if (l.distance(Main.TUTORIAL_3_DETAILS) <= 0.5) {
			return true;
		} else {return false;}	
	}
	
	public boolean nearTexture(Location l) {
		if (l.distance(Main.TUTORIAL_3_TEXTURE) <= 0.5) {
			return true;
		} else {return false;}	
	}
	
	public boolean nearContinue(Location l) {
		if (l.distance(Main.TUTORIAL_3_CONTINUE) <= 0.5) {
			return true;
		} else {return false;}	
	}
}