package me.elgamer.publicbuilds.utils;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class CurrentPlot {
	
HashMap<Player, Integer> plots = new HashMap<Player, Integer>();
	
	public void addPlayer(Player p) {
		plots.put(p, 0);
	}
	
	public void setPlot(Player p, int id) {
		plots.replace(p, id);
	}
	
	public int getPlot(Player p) {
		return (plots.get(p));
	}
	
	public void removePlayer(Player p) {
		plots.remove(p);
	}

}
