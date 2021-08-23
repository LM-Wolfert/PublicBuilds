package me.elgamer.publicbuilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//import me.elgamer.publicbuilds.Main;
//import me.elgamer.publicbuilds.tutorial.Tutorial;
//import me.elgamer.publicbuilds.utils.User;

public class SkipTutorial implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("&cThis command can only run as a player!");
			return true;
		}

		Player p = (Player) sender;

		if (!(p.hasPermission("publicbuilds.skiptutorial"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}
		
		//User u = Main.getInstance().getUser(p);
		//u.tutorialStage = 6;
		//Tutorial.continueTutorial(u);
		
		return true;
		
	}

}
