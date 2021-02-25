package me.elgamer.publicbuilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Utils;

public class CreateArea implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lavel, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("&cThis command can only run as a player!");
			return true;
		}

		Player p = (Player) sender;

		if (!(p.hasPermission("publicbuilds.createarea"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		WorldEditPlugin we = Main.getWorldEdit();
		LocalSession s = we.getSession(p);
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
		region.getPoints();

		return false;
	}

}
