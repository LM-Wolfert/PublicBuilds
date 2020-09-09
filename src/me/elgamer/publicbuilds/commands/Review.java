package me.elgamer.publicbuilds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.gui.ReviewGui;
import me.elgamer.publicbuilds.mysql.MySQLReadWrite;
import me.elgamer.publicbuilds.utils.PlotTeleport;
import net.md_5.bungee.api.ChatColor;

public class Review implements CommandExecutor {
	
	MySQLReadWrite mysql = new MySQLReadWrite();
	PlotTeleport tp = new PlotTeleport();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot create a plot!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (p.hasPermission("publicbuilds.review")) {
			p.openInventory(ReviewGui.GUI(p));
		}
		return true;
		
	}
	
	public void newReview(Player p) {

		MySQLReadWrite mysql = new MySQLReadWrite();

		if (mysql.getReview(p.getUniqueId()) == null) {
			p.closeInventory();
			p.sendMessage(ChatColor.RED + "There are currently no plots for review!");
		} else {
			
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(p));
		
		}
	}	
	
	public void toPlot(Player p, String world) {
	
		String name = mysql.currentReview(p.getUniqueId());
		tp.toReview(p, name, world);

	}
	
	public void acceptPlot(Player p) {
		
	}
	
	public void denyPlot(Player p) {
		
		MySQLReadWrite mysql = new MySQLReadWrite();
		
		String plot = mysql.currentReview(p.getUniqueId());
		mysql.denyReview(p, plot);
		
		p.sendMessage(ChatColor.RED + "");
		
	}

}
