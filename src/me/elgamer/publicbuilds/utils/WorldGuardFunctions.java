package me.elgamer.publicbuilds.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.elgamer.publicbuilds.Main;

public class WorldGuardFunctions {

	public static Location getCurrentLocation(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World buildWorld = Bukkit.getServer().getWorld(config.getString("worlds.build"));

		//Get worldguard instance
		WorldGuard wg = WorldGuard.getInstance();

		//Get worldguard region data
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager buildRegions = container.get(BukkitAdapter.adapt(buildWorld));

		//Get the worldguard region and teleport to player to one of the corners.
		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));
		BlockVector3 bv = region.getMinimumPoint();
		Location l = new Location(buildWorld, bv.getX(), bv.getY(), bv.getZ());

		l.setY(buildWorld.getHighestBlockYAt(l)+1);
		return(l);

	}

	public static Location getBeforeLocation(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World saveWorld = Bukkit.getServer().getWorld(config.getString("worlds.save"));

		//Get worldguard instance
		WorldGuard wg = WorldGuard.getInstance();

		//Get worldguard region data
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager buildRegions = container.get(BukkitAdapter.adapt(saveWorld));

		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));
		BlockVector3 bv = region.getMinimumPoint();
		Location l = new Location(saveWorld, bv.getX(), bv.getY(), bv.getZ());

		l.setY(saveWorld.getHighestBlockYAt(l)+1);
		return(l);

	}

	public static List<BlockVector2> getPoints(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World saveWorld = Bukkit.getServer().getWorld(config.getString("worlds.save"));

		//Get worldguard instance
		WorldGuard wg = WorldGuard.getInstance();

		//Get worldguard region data
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager buildRegions = container.get(BukkitAdapter.adapt(saveWorld));

		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));

		return region.getPoints();

	}

	public static boolean inRegion(BlockVector3 v) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World saveWorld = Bukkit.getServer().getWorld(config.getString("worlds.save"));

		//Get worldguard instance
		WorldGuard wg = WorldGuard.getInstance();

		//Get worldguard region data
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager saveRegions = container.get(BukkitAdapter.adapt(saveWorld));


		//Check whether the region overlaps an existing plot, if true stop the process.
		ApplicableRegionSet set = saveRegions.getApplicableRegions(v);
		if (set.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public static ArrayList<Integer> getNearbyPlots(User u) {

		//Get plugin instance and config.
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds.
		World saveWorld = Bukkit.getWorld(config.getString("worlds.save"));

		//Get instance of WorldGuard.
		WorldGuard wg = WorldGuard.getInstance();

		//Get regions.
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager saveRegions = container.get(BukkitAdapter.adapt(saveWorld));

		//Create HashMap
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		//Create region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(u.name, 
				BlockVector3.at(u.player.getLocation().getX()-100, 1, u.player.getLocation().getZ()-100),
				BlockVector3.at(u.player.getLocation().getX()+100, 256, u.player.getLocation().getZ()+100));

		//Check whether the region overlaps an existing plot, if true stop the process.
		ApplicableRegionSet set = saveRegions.getApplicableRegions(region);
		
		if (set.size() == 0) {
			return null;
		}
		
		for (ProtectedRegion entry : set) {
			if (!(entry.getOwners().contains(UUID.fromString(u.uuid)))) {
				list.add(Integer.parseInt(entry.getId()));
			}
		}


		return list;
	}

}
