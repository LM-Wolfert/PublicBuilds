package me.elgamer.publicbuilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.mysql.HologramData;
import me.elgamer.publicbuilds.mysql.HologramText;

public class CustomHolo implements CommandExecutor {
	
	HologramData hologramData;
	HologramText hologramText;
	
	public CustomHolo(HologramData hologramData, HologramText hologramText) {
		this.hologramData = hologramData;
		this.hologramText = hologramText;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
				
		if (!(sender instanceof Player)) {
			
			sender.sendMessage(ChatColor.RED + "This command can only be run as a player ");
			return true;
			
		}
		
		Player p = (Player) sender;
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				
				return true;
			} else {
				p.sendMessage(help());
				return true;
			}
		}
		
		if (args.length < 2) {
			p.sendMessage(help());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("create")) {
			
		}
		
		
		return true;
		
	}

	
	private String help() {
		return null;
	}
}
