package me.elgamer.publicbuilds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Apply implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("&cThis command can only run as a player!");
			return true;
		}

		Player p = (Player) sender;

		p.sendMessage("Once you have completed 2 plots you can apply here:");
		p.sendMessage("https://buildtheearth.net/buildteams/98/join");
		
		return true;
		
	}
}
