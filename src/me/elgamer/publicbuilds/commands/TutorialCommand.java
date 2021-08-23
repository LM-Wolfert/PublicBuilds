package me.elgamer.publicbuilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.Main;
import net.md_5.bungee.api.ChatColor;

public class TutorialCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("&cThis command can only run as a player!");
			return true;
		}

		Player p = (Player) sender;

		if (args.length != 1) {
			p.sendMessage(ChatColor.RED + "/tutorial <1-5>");
			return true;
		}

		int val;

		try {
			val = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "/tutorial <1-5>");
			return true;
		}
		
		if (val > 5 || val < 1) {
			p.sendMessage(ChatColor.RED + "/tutorial <1-5>");
			return true;
		}
		
		FileConfiguration config = Main.getInstance().getConfig();
		
		p.teleport(new Location(Bukkit.getWorld(config.getString("worlds.tutorial")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));

		return true;

	}

}
