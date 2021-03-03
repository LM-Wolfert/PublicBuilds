package me.elgamer.publicbuilds.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.Main;

public class RankValues {

	public static int plotLimit(Player p) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		
		if (p.hasPermission("group.builder")) {
			return config.getInt("plot_limit.builder");
		} else if (p.hasPermission("group.jr builder")) {
			return config.getInt("plot_limit.jrbuilder");
		} else if (p.hasPermission("group.apprentice")) {
			return config.getInt("plot_limit.apprentice");
		} else {
			return config.getInt("plot_limit.guest");
		}
	}

	public static int maxDis(Player p) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		
		if (p.hasPermission("group.builder")) {
			return config.getInt("plot_distance.builder");
		} else if (p.hasPermission("group.jr builder")) {
			return config.getInt("plot_distance.jrbuilder");
		} else if (p.hasPermission("group.apprentice")) {
			return config.getInt("plot_distance.apprentice");
		} else {
			return config.getInt("plot_distance.guest");
		}
	}

}
