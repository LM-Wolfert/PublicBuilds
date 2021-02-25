package me.elgamer.publicbuilds.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;

import me.elgamer.publicbuilds.Main;

public class WorldGuard {

	public static Location getCurrentLocation(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World buildWorld = Bukkit.getServer().getWorld(config.getString("buildWorld"));

		//Get worldguard instance from Main
		WorldGuardPlugin wg = Main.getWorldGuard();

		//Get worldguard region data
		RegionContainer container = wg.getRegionContainer();
		RegionManager buildRegions = container.get(buildWorld);

		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));
		BlockVector bv = region.getMinimumPoint();
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

		//Get worldguard instance from Main
		WorldGuardPlugin wg = Main.getWorldGuard();

		//Get worldguard region data
		RegionContainer container = wg.getRegionContainer();
		RegionManager buildRegions = container.get(saveWorld);

		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));
		BlockVector bv = region.getMinimumPoint();
		Location l = new Location(saveWorld, bv.getX(), bv.getY(), bv.getZ());

		l.setY(saveWorld.getHighestBlockYAt(l));
		return(l);

	}

	public static List<BlockVector2D> getCorners(int plot) {

		//Get instance of plugin and config
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		//Get worlds from config
		World saveWorld = Bukkit.getServer().getWorld(config.getString("saveWorld"));

		//Get worldguard instance from Main
		WorldGuardPlugin wg = Main.getWorldGuard();

		//Get worldguard region data
		RegionContainer container = wg.getRegionContainer();
		RegionManager buildRegions = container.get(saveWorld);

		ProtectedPolygonalRegion region = (ProtectedPolygonalRegion) buildRegions.getRegion(String.valueOf(plot));

		return region.getPoints();

	}

}
