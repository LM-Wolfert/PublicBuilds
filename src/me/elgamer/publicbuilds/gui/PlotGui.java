package me.elgamer.publicbuilds.gui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.mysql.MySQLReadWrite;
import me.elgamer.publicbuilds.utils.Utils;

public class PlotGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;
	
	public static void initialize() {
		inventory_name = Utils.chat("Plots");
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (Player p) {
		
		UUID uuid = p.getUniqueId();
		String claims;
		String[] names;
		int size;
		MySQLReadWrite mysql = new MySQLReadWrite();
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		Utils.createItemByte(inv, 251, 5, 1, 3, "&cCreate plot", "&7A plot will be created in your selection!");
		Utils.createItemByte(inv, 251, 4, 1, 5, "&cSubmit plot", "&7Your plot will be up for review!");
		Utils.createItemByte(inv, 251, 14, 1, 7, "&cRemove plot", "&7All current progress in the plot will be reset!");
		if (mysql.playerExists(uuid) && mysql.returnClaims(uuid) != null) {
			claims = mysql.returnClaims(uuid);
			names = claims.split(",");
			size = names.length;
			if (size == 1) {
					Utils.createItemByte(inv, 251, 1, 1, 23, ChatColor.RED + names[0], "&7Click to teleport to your plot!");
				} else if (size == 2) {
					Utils.createItemByte(inv, 251, 1, 1, 22, ChatColor.RED + names[0], "&7Click to teleport to your plot!");
					Utils.createItemByte(inv, 251, 1, 1, 24, ChatColor.RED + names[1], "&7Click to teleport to your plot!");
				} else if (size == 3) {
					Utils.createItemByte(inv, 251, 1, 1, 21, ChatColor.RED + names[0], "&7Click to teleport to your plot!");
					Utils.createItemByte(inv, 251, 1, 1, 23, ChatColor.RED + names[1], "&7Click to teleport to your plot!");
					Utils.createItemByte(inv, 251, 1, 1, 25, ChatColor.RED + names[2], "&7Click to teleport to your plot!");
				}
		} else {
			Utils.createItem(inv, 166, 1, 23, "&cYou do not own a plot!", "&7Create a selection with WorldEdit and click on the Create Plot button in the GUI!");
			
		}
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cCreate plot"))) {
			p.performCommand("createPlot");
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cSubmit plot"))) {
			p.performCommand("submitPlot");
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cRemove plot"))) {
			p.performCommand("removePlot");
		}
	}
}
