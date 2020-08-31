package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.utils.Utils;

public class PlotGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 4 * 9;
	
	public static void initialize() {
		inventory_name = Utils.chat("Plots");
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (Player p) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		Utils.createItem(inv, 4, 1, 1, "&cCreate plot", "&7A plot will be created in your selection", "&6Hi there");
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cCreate plot"))) {
			p.performCommand("createPlot");
		}
	}

}
