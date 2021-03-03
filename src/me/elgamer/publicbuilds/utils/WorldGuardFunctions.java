package me.elgamer.publicbuilds.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.elgamer.publicbuilds.Main;

public class WorldGuardFunctions {

	public static Location getCurrentLocation(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World buildWorld = Bukkit.getServer().getWorld(config.getString("buildWorld"));

		//Get worldguard instance
		WorldGuard wg = WorldGuard.getInstance();

		//Get worldguard region data
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager buildRegions = container.get(BukkitAdapter.adapt(buildWorld));

		//Get the worldguard region and teleport to player to one of the corners.
		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));
		BlockVector3 bv = region.getMinimumPoint();
		Location l = new Location(buildWorld, bv.getX(), bv.getY(), bv.getZ());

		l.setY(buildWorld.getHighestBlockYAt(l));
		return(l);

	}

	public static Location getBeforeLocation(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World saveWorld = Bukkit.getServer().getWorld(config.getString("saveWorld"));

		//Get worldguard instance
		WorldGuard wg = WorldGuard.getInstance();

		//Get worldguard region data
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager buildRegions = container.get(BukkitAdapter.adapt(saveWorld));

		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));
		BlockVector3 bv = region.getMinimumPoint();
		Location l = new Location(saveWorld, bv.getX(), bv.getY(), bv.getZ());

		l.setY(saveWorld.getHighestBlockYAt(l));
		return(l);

	}

	public static List<BlockVector2> getCorners(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World saveWorld = Bukkit.getServer().getWorld(config.getString("saveWorld"));

		//Get worldguard instance
		WorldGuard wg = WorldGuard.getInstance();

		//Get worldguard region data
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager buildRegions = container.get(BukkitAdapter.adapt(saveWorld));

		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));

		return region.getPoints();

	}

}
