package me.elgamer.publicbuilds.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;

public class Inactive {

	public static void cancelInactivePlots() {

		//Get config.
		FileConfiguration config = Main.getInstance().getConfig();	

		//Get all plots claimed by inactive players.

		long time = Time.currentTime();
		long timeCap = config.getLong("plot_inactive_cancel")*24*60*60*1000;
		long timeDif = time - timeCap; 
		
		List<Integer> inactivePlots = PlotData.getInactivePlots(timeDif);

		//If there are no inactive plots, end the method.
		if (inactivePlots == null || inactivePlots.isEmpty()) {
			return;
		}

		//Iterate through all inactive plots and cancel them.
		for (int plot : inactivePlots) {

			WorldEditor.updateWorld(WorldGuardFunctions.getPoints(plot), Bukkit.getWorld(config.getString("worlds.save")), Bukkit.getWorld(config.getString("worlds.build")));
			ClaimFunctions.removeClaim(plot);
			PlotData.setStatus(plot, "cancelled");
			Bukkit.broadcastMessage(ChatColor.RED + "Plot " + plot + " has been removed due to inactivity!");
		}


	}

}
