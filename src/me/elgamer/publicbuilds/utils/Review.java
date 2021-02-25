package me.elgamer.publicbuilds.utils;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class Review {
	
	HashMap<Player, Integer> review = new HashMap<Player, Integer>();

	public void addReview(Player p, int plot) {
				
		review.put(p, plot);
		
	}
	
	public void removePlayer(Player p) {
		
		review.remove(p);
		
	}
	
	public boolean inReview(Player p) {
		
		return (review.containsKey(p));
		
	}
	
	public int getReview(Player p) {
		
		return (review.get(p));
		
	}
}
