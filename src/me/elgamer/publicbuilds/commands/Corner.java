package me.elgamer.publicbuilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.Utils;

public class Corner implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cThis command can only run as a player!");
			return true;
		}

		Player p = (Player) sender;

		WorldGuardPlugin wg = Main.getWorldGuard();

		RegionManager rm = wg.getRegionManager(Bukkit.getWorld(Main.getInstance().getConfig().getString("saveWorld")));
		ApplicableRegionSet ap = rm.getApplicableRegions(p.getLocation());

		if (ap.size() > 1) {
			p.sendMessage(Utils.chat("&cThis location is already part of another plot!"));
			return true;
		}

		if (args[0].equals("1") || args[0].equals("2") || args[0].equals("3") || args[0].equals("4")) {

			Plots plots = Main.getInstance().getPlots();
			BlockVector2D pos = new BlockVector2D(p.getLocation().getX(), p.getLocation().getZ());
			plots.addLocation(p, pos, Integer.valueOf(args[0]));
			return true;

		} else {
			p.sendMessage(Utils.chat("&c/corner <1|2|3|4>"));
			return true;
		}
	}
}
