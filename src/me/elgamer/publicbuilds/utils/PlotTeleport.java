package me.elgamer.publicbuilds.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.publicbuilds.mysql.MySQLReadWrite;

public class PlotTeleport {

	//Teleports the player to the MinimumPoint of the plot at the highest available y level
	public void toPlot(Player p, String plotID) {

		plotID = ChatColor.stripColor(plotID);

		World world = Bukkit.getServer().getWorld("buildWorld");

		MySQLReadWrite mysql = new MySQLReadWrite();
		
		int x = mysql.getX(plotID);
		int z = mysql.getZ(plotID);

		int y = world.getHighestBlockYAt(x, z);

		Location l = new Location(world, (double) x + 0.5, (double) y, (double) z + 0.5);

		p.teleport(l);
	}

	public void toReview(Player p, String name, String w) {

		name = ChatColor.stripColor(name);

		World world = Bukkit.getServer().getWorld(w);
		RegionManager regions = getWorldGuard().getRegionContainer().get(world);
		ProtectedRegion region = regions.getRegion(name);

		int x = region.getMinimumPoint().getBlockX();
		int z = region.getMinimumPoint().getBlockZ();

		int y = world.getHighestBlockYAt(x, z);

		Location l = new Location(world, (double) x + 0.5, (double) y, (double) z + 0.5);

		p.teleport(l);
	}



	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}



}
