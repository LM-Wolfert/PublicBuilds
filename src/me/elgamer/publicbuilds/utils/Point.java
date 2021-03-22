package me.elgamer.publicbuilds.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.elgamer.publicbuilds.Main;

public class Point {
	
	double x;
	double z;
	Location l;
	
	public Point(double x, double z) {
		
		this.x = x;
		this.z = z;
		this.l = new Location
			(Bukkit.getWorld(Main.getInstance().getConfig().getString("worlds.build")),
			x, 
			Bukkit.getWorld(Main.getInstance().getConfig().getString("worlds.build")).getHighestBlockYAt((int) x, (int) z)+2,
			z);
		
	}
}
