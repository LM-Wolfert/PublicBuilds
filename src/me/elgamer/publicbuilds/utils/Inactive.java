package me.elgamer.publicbuilds.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import net.md_5.bungee.api.ChatColor;

public class Inactive {
	
	public static void cancelInactivePlots(Player p) {
		
		//Get list of inactive players
		List<String> inactivePlayers = PlayerData.getInactivePlayers();
		
		//If there are no inactive players, end the method.
		if (inactivePlayers == null || inactivePlayers.isEmpty()) {
			return;
		}
		
		//Get all plots claimed by inactive players.
		List<Integer> inactivePlots = PlotData.getInactivePlots(inactivePlayers);
		
		//If there are no inactive plots, end the method.
		if (inactivePlots == null || inactivePlots.isEmpty()) {
			return;
		}
		
		
		//Get config.
		FileConfiguration config = Main.getInstance().getConfig();
		
		//Iterate through all inactive plots and cancel them.
		for (int plot : inactivePlots) {
					
			WorldEditor.updateWorld(WorldGuardFunctions.getPoints(plot), Bukkit.getWorld(config.getString("worlds.save")), Bukkit.getWorld(config.getString("worlds.build")));
			ClaimFunctions.removeClaim(plot);
			PlotData.setStatus(plot, "cancelled");
			Bukkit.broadcastMessage(ChatColor.RED + "Plot " + plot + " has been removed due to inactivity!");
		}
		
		
	}

}
