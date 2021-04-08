package me.elgamer.publicbuilds.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Utils;

public class CreateArea implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("&cThis command can only run as a player!");
			return true;
		}

		Player p = (Player) sender;

		if (!(p.hasPermission("publicbuilds.createarea"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		WorldEdit we = WorldEdit.getInstance();
		LocalSession s = we.getSessionManager().findByName(p.getName());;
		Region r = null;

		try {
			r = s.getSelection(s.getSelectionWorld());
		} catch (IncompleteRegionException e) {

			p.sendMessage(Utils.chat("&cSelection incomplete"));
			return true;
		}

		if (!(r instanceof Polygonal2DRegion)) {

			p.sendMessage(Utils.chat("&cYou must use a polygon selection"));
			return true;
		}

		Polygonal2DRegion region = (Polygonal2DRegion) r;
		List<BlockVector2> vector = region.getPoints();

		WorldGuard wg = WorldGuard.getInstance();

		//Get plugin instance and config.
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		World buildWorld = BukkitAdapter.adapt(Bukkit.getWorld(config.getString("worlds.build")));

		//Get regions.
		RegionContainer container = wg.getPlatform().getRegionContainer();
		RegionManager buildRegions = container.get(buildWorld);

		//Create region
		ProtectedPolygonalRegion polyregion = new ProtectedPolygonalRegion(args[0], vector, 1, 256);

		//Check whether the region overlaps an existing plot, if true stop the process.
		ApplicableRegionSet set = buildRegions.getApplicableRegions(polyregion);
		if (set.size() > 0) {
			p.sendMessage(ChatColor.RED + "This selection overlaps with an existing area, please try again!");
			return true;
		}
		
		polyregion.setFlag(Main.CREATE_PLOT, StateFlag.State.ALLOW);

		//Add the regions to the worlds
		buildRegions.addRegion(polyregion);

		//Save the new regions
		try {
			buildRegions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

		p.sendMessage(ChatColor.GREEN + "Area created with Name " + ChatColor.DARK_AQUA + args[0]);
		return true;
		
	}

}
