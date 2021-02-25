package me.elgamer.publicbuilds.utils;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class Reason {
	
	HashMap<Player, String> reason = new HashMap<Player, String>();

	public void addReason(Player p, String type) {
				
		reason.put(p, type);
		
	}
	
	public void removePlayer(Player p) {
		
		reason.remove(p);
		
	}
	
	public boolean inReason(Player p) {
		
		if (reason.containsKey(p)) {
			return true;
		}
		
		return false;
		
	}
	
	public String getType(Player p) {
		return reason.get(p);
	}

}
