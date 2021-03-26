package me.elgamer.publicbuilds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Point;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;

public class Corner implements CommandExecutor {

	final String ERROR = Utils.chat("&c/corner <1|2|3|4>");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//If the command sender is from the console this command will cancel.
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cThis command can only run as a player!");
			return true;
		}

		//Get instance of the player.
		Player p = (Player) sender;
		
		//Get instance of WorldGuard.
		WorldGuard wg = WorldGuard.getInstance();

		//Check whether the point where the command is run is not in existing plot.
		RegionQuery query = wg.getPlatform().getRegionContainer().createQuery();
		ApplicableRegionSet ap = query.getApplicableRegions(BukkitAdapter.adapt(p.getLocation()));

		//If it is in an existing plot then cancel.
		if (ap.size() > 1) {
			p.sendMessage(Utils.chat("&cThis location is already part of another plot!"));
			return true;
		}
		
		User u = Main.getInstance().getUser(p);
		
		//Adds a new point to plots.
		u.plots.add(new Point(p.getLocation().getX(), p.getLocation().getZ()));
		p.sendMessage(Utils.chat("&aSet corner to x: " + (int) p.getLocation().getX() + " z: " + (int) p.getLocation().getZ()));
		
		return true;
	}
}
