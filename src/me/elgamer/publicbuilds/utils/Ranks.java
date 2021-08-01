package me.elgamer.publicbuilds.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import net.md_5.bungee.api.ChatColor;

public class Ranks {

	public static void checkRankup(User u) {

		int points = PlayerData.getPoints(u.uuid);
		FileConfiguration config = Main.getInstance().getConfig();
		
		if (points >= config.getInt("rankup.builder") && u.role.equals("jrbuilder")) {
			u.role = "builder";
		} else if (points >= config.getInt("rankup.jrbuilder") && u.role.equals("apprentice")) {
			u.role = "jrbuilder";
		} else if (points >= config.getInt("rankup.apprentice") && u.role.equals("guest")) {
			u.role = "apprentice";
		} else {
			return;
		}

		//Promote the player
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		
		String command = "lp user " + u.name + " promote builder";
		Bukkit.dispatchCommand(console, command);
		Bukkit.broadcastMessage(ChatColor.GREEN + u.name + " has been promoted to " + u.role);
		PlayerData.updateRole(u.uuid, u.role);
		checkRankup(u);
	}

}
