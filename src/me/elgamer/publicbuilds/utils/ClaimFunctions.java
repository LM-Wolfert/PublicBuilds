package me.elgamer.publicbuilds.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;

public class ClaimFunctions {

	public static String createClaim(User u, List<BlockVector2> vector) {

		//Get plugin instance and config.
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds.
		World saveWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.save")));
		World buildWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.build")));

		//Get instance of WorldGuard.
		WorldGuard wg = WorldGuard.getInstance();

		//Get regions.
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager saveRegions = container.get(saveWorld);
		RegionManager buildRegions = container.get(buildWorld);

		//Create new id
		int plotID = PlotData.getNewID();

		//Create region
		ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(String.valueOf(plotID), vector, 1, 256);

		//Check whether the region overlaps an existing plot, if true stop the process.
		ApplicableRegionSet set = saveRegions.getApplicableRegions(region);
		if (set.size() > 0) {
			return (ChatColor.RED + "This region overlaps with an existing plot, please create a different plot.");
		}

		//Check if the player is allowed to create a plot in this location.
		//They need to be in a valid area and also have the minimum rank required to create a plot here.
		for (BlockVector2 bv : vector) {

			set = buildRegions.getApplicableRegions(BlockVector3.at(bv.getX(), 64, bv.getZ()));
			if (set.testState(null, Main.NO_PLOT)) {
				return (ChatColor.RED + "You may not create a plot here!");
			}

			if (!(set.testState(null, Main.CREATE_PLOT_GUEST))) {
				if (!(set.testState(null, Main.CREATE_PLOT_APPRENTICE))) {
					if (!(set.testState(null, Main.CREATE_PLOT_JRBUILDER))) {
						return (ChatColor.RED + "You may not create a plot here!");
					}
				}
			}

			if (set.testState(null, Main.CREATE_PLOT_GUEST)) {
				continue;

			} else if (set.testState(null, Main.CREATE_PLOT_APPRENTICE) && !(u.player.hasPermission("group.apprentice"))) {
				return (ChatColor.RED + "You must be Apprentice or higher to create a plot here!");

			} else if (set.testState(null, Main.CREATE_PLOT_JRBUILDER) && !(u.player.hasPermission("group.jrbuilder"))) {
				return (ChatColor.RED + "You must be Jr.Builder or higher to create a plot here!");

			} 

		}

		//Create an entry in the database for the plot.
		if (!(PlotData.createPlot(plotID, u.uuid))) {
			return (ChatColor.RED + "An error occured, please try again!");
		}

		//Set owner of the region
		DefaultDomain owners = new DefaultDomain();
		owners.addPlayer(UUID.fromString(u.uuid));
		region.setOwners(owners);

		//Set the region priority to 1
		region.setPriority(1);

		//Add the regions to the worlds
		saveRegions.addRegion(region);
		buildRegions.addRegion(region);

		//Save the new regions
		try {
			saveRegions.save();
			buildRegions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

		u.plots.vector.clear();;
		return (ChatColor.GREEN + "Plot created with ID " + ChatColor.DARK_AQUA + plotID);

	}

	public static String editClaim(int id, String uuid, List<BlockVector2> vector) {

		//Check whether the player has selected the corners correctly.
		//Has selected at least 3 points to create a polygon.
		if (vector.size() >= 3) {

			//Does not exceed the size.
			if (Plots.largestDistance(vector) > RankValues.maxDis(PlayerData.getRole(uuid))) {
				return(Utils.chat("&cYour selection is too large!"));
			}

			//Is not too small.
			if (Plots.largestDistance(vector) < 5) {
				return(Utils.chat("&cYour selection is too small!"));
			}

			//Get plugin instance and config.
			Main instance = Main.getInstance();
			FileConfiguration config = instance.getConfig();

			//Get worlds.
			World saveWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.save")));
			World buildWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.build")));

			//Get instance of WorldGuard.
			WorldGuard wg = WorldGuard.getInstance();

			//Get regions.
			RegionContainer container = wg.getPlatform().getRegionContainer();
			RegionManager saveRegions = container.get(saveWorld);
			RegionManager buildRegions = container.get(buildWorld);

			//Create region
			ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(String.valueOf(id + "-test"), vector, 1, 256);

			//Check if the plot includes all of the existing area
			if (!(WorldGuardFunctions.includesRegion(id, region))) {
				return(ChatColor.RED + "Your selection does not include all of the existing plot.");
			}

			//Check whether the region overlaps an existing plot, if true stop the process.
			ApplicableRegionSet set = saveRegions.getApplicableRegions(region);
			if (set.size() > 1) {
				return(ChatColor.RED + "Your selection overlaps with a different plot.");
			}

			//Remove existing region
			saveRegions.removeRegion(String.valueOf(id));
			buildRegions.removeRegion(String.valueOf(id));

			//Save the removed regions
			try {
				saveRegions.save();
				buildRegions.save();
			} catch (StorageException e1) {
				e1.printStackTrace();
			}

			//Create region
			region = new ProtectedPolygonalRegion(String.valueOf(id), vector, 1, 256);

			//Set owner of the region
			DefaultDomain owners = new DefaultDomain();
			owners.addPlayer(UUID.fromString(uuid));
			region.setOwners(owners);

			//Set the region priority to 1
			region.setPriority(1);

			//Add the regions to the worlds
			saveRegions.addRegion(region);
			buildRegions.addRegion(region);

			//Save the new regions
			try {
				saveRegions.save();
				buildRegions.save();
			} catch (StorageException e1) {
				e1.printStackTrace();
			}

		} else {
			return(Utils.chat("&cYou must select a minimum of 3 points to create a plot!"));
		}

		return (ChatColor.GREEN + "Plot " + ChatColor.DARK_AQUA + id + ChatColor.GREEN + " updated with new area!");
	}

	public static boolean checkEdit(Player p, int id, String uuid, List<BlockVector2> vector) {

		//Check whether the player has selected the corners correctly.
		//Has selected at least 3 points to create a polygon.
		if (vector.size() >= 3) {

			//Does not exceed the size.
			if (Plots.largestDistance(vector) > RankValues.maxDis(PlayerData.getRole(uuid))) {
				p.sendMessage(Utils.chat("&cYour selection is too large!"));
				return false;
			}

			//Is not too small.
			if (Plots.largestDistance(vector) < 5) {
				p.sendMessage(Utils.chat("&cYour selection is too small!"));
				return false;
			}

			//Get plugin instance and config.
			Main instance = Main.getInstance();
			FileConfiguration config = instance.getConfig();

			//Get worlds.
			World saveWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.save")));

			//Get instance of WorldGuard.
			WorldGuard wg = WorldGuard.getInstance();

			//Get regions.
			RegionContainer container = wg.getPlatform().getRegionContainer();
			RegionManager saveRegions = container.get(saveWorld);

			//Create region
			ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(String.valueOf(id + "-test"), vector, 1, 256);

			//Check if the plot includes all of the existing area
			if (!(WorldGuardFunctions.includesRegion(id, region))) {
				p.sendMessage(ChatColor.RED + "Your selection does not include all of the existing plot.");
				return false;
			}

			//Check whether the region overlaps an existing plot, if true stop the process.
			ApplicableRegionSet set = saveRegions.getApplicableRegions(region);
			if (set.size() > 1) {
				p.sendMessage(ChatColor.RED + "Your selection overlaps with a different plot.");
				return false;
			}

			return true;

		} else {
			p.sendMessage(Utils.chat("&cYou must select a minimum of 3 points to create a plot!"));
			return false;
		}
	}

	public static boolean resizePlot(int id, String uuid, List<BlockVector2> vector) {

		//Check whether the player has selected the corners correctly.
		//Has selected at least 3 points to create a polygon.
		if (vector.size() >= 3) {

			//Does not exceed the size.
			if (Plots.largestDistance(vector) > RankValues.maxDis(PlayerData.getRole(uuid))) {
				return false;
			}

			//Is not too small.
			if (Plots.largestDistance(vector) < 5) {
				return false;
			}

			//Get plugin instance and config.
			Main instance = Main.getInstance();
			FileConfiguration config = instance.getConfig();

			//Get worlds.
			World saveWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.save")));
			World buildWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.build")));

			//Get instance of WorldGuard.
			WorldGuard wg = WorldGuard.getInstance();

			//Get regions.
			RegionContainer container = wg.getPlatform().getRegionContainer();
			RegionManager saveRegions = container.get(saveWorld);
			RegionManager buildRegions = container.get(buildWorld);

			//Create region
			ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(String.valueOf(id + "-test"), vector, 1, 256);

			//Check if the plot includes all of the existing area
			if (!(WorldGuardFunctions.includesRegion(id, region))) {
				return false;
			}

			//Check whether the region overlaps an existing plot, if true stop the process.
			ApplicableRegionSet set = saveRegions.getApplicableRegions(region);
			if (set.size() > 1) {
				return false;
			}

			//Remove existing region
			saveRegions.removeRegion(String.valueOf(id));
			buildRegions.removeRegion(String.valueOf(id));

			//Save the removed regions
			try {
				saveRegions.save();
				buildRegions.save();
			} catch (StorageException e1) {
				e1.printStackTrace();
			}

			//Create region
			region = new ProtectedPolygonalRegion(String.valueOf(id), vector, 1, 256);

			//Set owner of the region
			DefaultDomain owners = new DefaultDomain();
			owners.addPlayer(UUID.fromString(uuid));
			region.setOwners(owners);

			//Set the region priority to 1
			region.setPriority(1);

			//Add the regions to the worlds
			saveRegions.addRegion(region);
			buildRegions.addRegion(region);

			//Save the new regions
			try {
				saveRegions.save();
				buildRegions.save();
			} catch (StorageException e1) {
				e1.printStackTrace();
			}

		} else {
			return false;
		}

		return true;
	}

public static String removeClaim(int id) {

	//Get instance of plugin and config
	Main instance = Main.getInstance();
	FileConfiguration config = instance.getConfig();

	//Get worlds.
	World saveWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.save")));
	World buildWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.build")));

	//Get instance of WorldGuard.
	WorldGuard wg = WorldGuard.getInstance();

	//Get regions.
	RegionContainer container = wg.getPlatform().getRegionContainer();
	RegionManager saveRegions = container.get(saveWorld);
	RegionManager buildRegions = container.get(buildWorld);

	//If the regions exist continue
	if (!(saveRegions.hasRegion(String.valueOf(id)))) {
		return (ChatColor.RED + "This region does not exist!");
	}

	//Remove the regions from the worlds
	saveRegions.removeRegion(String.valueOf(id));
	buildRegions.removeRegion(String.valueOf(id));

	//Save the removed regions
	try {
		saveRegions.save();
		buildRegions.save();
	} catch (StorageException e1) {
		e1.printStackTrace();
	}

	return (ChatColor.GREEN + "Plot " + ChatColor.DARK_AQUA + id + ChatColor.GREEN + " removed!");
}

}
