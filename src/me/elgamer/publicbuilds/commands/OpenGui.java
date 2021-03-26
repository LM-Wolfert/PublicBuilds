package me.elgamer.publicbuilds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.utils.User;

public class OpenGui implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot open the gui!");
			return true;
		}

		Player p = (Player) sender;
		User u = Main.getInstance().getUser(p);
		
		p.openInventory(MainGui.GUI(u));
		
		return true;

	}
}
