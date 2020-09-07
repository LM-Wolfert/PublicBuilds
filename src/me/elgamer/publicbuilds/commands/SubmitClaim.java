package me.elgamer.publicbuilds.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
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

public class SubmitClaim implements CommandExecutor {
	
	Pattern wordPattern = Pattern.compile("\\w+");
	Matcher wordMatcher;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot submit a plot!");
			return true;
		}
		Player p = (Player) sender;
		MySQLReadWrite mysql = new MySQLReadWrite();
		
		
		if (mysql.playerExists(p.getUniqueId()) == false || mysql.returnClaims(p.getUniqueId()) == null) {
			p.sendMessage(ChatColor.RED + "You do not own a plot!");
			return true;
		}
		
		p.closeInventory();
		
		AnvilGui gui = new AnvilGui(p, new AnvilGui.AnvilClickEventHandler(){
			
			@Override
			public void onAnvilClick(AnvilClickEvent e) {
				if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {
					e.setWillClose(true);
					e.setWillDestroy(true);
					String name = e.getName();
					wordMatcher = wordPattern.matcher(name);
					if (wordMatcher.matches()) {
						if (mysql.checkDuplicateName(p.getUniqueId(), name)) {

							String plots = mysql.returnClaims(p.getUniqueId());
							if (plots == null) {
								p.sendMessage(ChatColor.RED + "There is no plot with the name: " + name);
							} else {
								String[] names = plots.split(",");
								for (int i = 0; i < names.length; i++) {
									if (names[i].equals(name)) {

										mysql.submitClaim(p.getUniqueId(), name);

										p.sendMessage(ChatColor.RED + "Plot " + name + " has been submitted for review!");

										Bukkit.broadcast(ChatColor.GREEN + "A plot has been submitted!", "group.veteran");
										
										break;
									}
								}

							}

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
