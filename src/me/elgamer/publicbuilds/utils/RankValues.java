package me.elgamer.publicbuilds.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.Main;

public class RankValues {

	public static int plotLimit(Player p) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		
		if (p.hasPermission("group.builder")) {
			return config.getInt("builderLimit");
		} else if (p.hasPermission("group.jr builder")) {
			return config.getInt("jrbuilderLimit");
		} else if (p.hasPermission("group.apprentice")) {
			return config.getInt("apprenticeLimit");
		} else {
			return config.getInt("guestLimit");
		}
	}

	public static int maxDis(Player p) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		
		if (p.hasPermission("group.builder")) {
			return config.getInt("builderDistance");
		} else if (p.hasPermission("group.jr builder")) {
			return config.getInt("jrbuilderDistance");
		} else if (p.hasPermission("group.apprentice")) {
			return config.getInt("apprenticeDistance");
		} else {
			return config.getInt("guestDistance");
		}
	}

}
