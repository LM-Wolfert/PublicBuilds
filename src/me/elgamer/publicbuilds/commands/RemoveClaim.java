package me.elgamer.publicbuilds.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.elgamer.publicbuilds.gui.AnvilGui;
import me.elgamer.publicbuilds.gui.AnvilGui.AnvilClickEvent;
import me.elgamer.publicbuilds.mysql.MySQLReadWrite;
import me.elgamer.publicbuilds.utils.DeleteClaim;

public class RemoveClaim implements CommandExecutor {
	
	//private Main instance = Main.getInstance();
	//private FileConfiguration config = instance.getConfig();
	
	Pattern wordPattern = Pattern.compile("\\w+");
	Matcher wordMatcher;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Check if the sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot remove a plot!");
			return true;
		}
		
		Player p = (Player) sender;
		String uuid = p.getUniqueId().toString();
		MySQLReadWrite mysql = new MySQLReadWrite();
		
		
		if (mysql.hasPlot(uuid) != true) {
			p.sendMessage(ChatColor.RED + "You do not own a plot!");
			return true;
		}
		
		//Close PlotGui and open AnvilGui
		p.closeInventory();
		
		AnvilGui gui = new AnvilGui(p, new AnvilGui.AnvilClickEventHandler(){
			
			@Override
			public void onAnvilClick(AnvilClickEvent e) {
				
				if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {
					e.setWillClose(true);
					e.setWillDestroy(true);
					String name = e.getName();
					String plotID = uuid + "," + name;
					wordMatcher = wordPattern.matcher(name);
					//If name is in the right format
					if (wordMatcher.matches()) {
						//If the player owns the plotName continue
						if (mysql.checkDuplicateName(plotID)) {
							
							DeleteClaim del = new DeleteClaim();
							MySQLReadWrite mysql = new MySQLReadWrite();
							del.removeClaim(p, plotID);
							mysql.removeClaim(uuid, plotID);
							
							p.sendMessage(ChatColor.RED + "Plot " + name + " has been removed!");
								
							} else {
								p.sendMessage(ChatColor.RED + "There is no plot with the name: " + name);
						
						}
					} else {
						p.sendMessage(ChatColor.RED + "Please use a name that consists of 1 word and run the command again!");
					}
				} else {
					e.setWillClose(false);
					e.setWillDestroy(false);
				}
			}
			
		},"Please input Plot Name!");
		
		ItemStack i = new ItemStack(Material.NAME_TAG);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("");
		i.setItemMeta(im);
		
		gui.setSlot(AnvilGui.AnvilSlot.INPUT_LEFT, i);
		
		gui.open();
		
		return true;
		
	}
	
}
