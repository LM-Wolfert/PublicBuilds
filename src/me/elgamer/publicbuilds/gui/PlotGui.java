package me.elgamer.publicbuilds.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.Utils;

public class PlotGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;
	
	public static void initialize() {
		inventory_name = Utils.chat("&9Menu");
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (Player p) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		HashMap<Integer, String> plots = PlotData.getPlots(p.getUniqueId().toString());
		
		int i = 2;
		int j = 2;
		
		for (Map.Entry<Integer, String> entry : plots.entrySet()) {
			
			if (entry.getValue().equalsIgnoreCase("claimed")) {			
				
				Utils.createItem(inv, "LIME_TERRACOTTA", 1, ((j-1)*7)+i, Utils.chat("&9" + String.valueOf(entry.getKey())), Utils.chat("&1Click to open plot functions!"));
				
				i += 1;
				
				if (i>6) {
					i -= 5;
					j += 1;
				}				
			}			
		}
			
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		
		int id = Integer.parseInt(clicked.getItemMeta().getDisplayName());
		Main.getInstance().getCurrentPlot().addPlayer(p, id);
		p.closeInventory();
		p.openInventory(PlotInfo.GUI(p));
	}		
}
