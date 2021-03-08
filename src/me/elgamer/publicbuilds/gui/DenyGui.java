package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Reason;
import me.elgamer.publicbuilds.utils.Utils;

public class DenyGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;
	
	public static void initialize() {
		inventory_name = Utils.chat("&9Deny");
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (Player p) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		Utils.createItem(inv, Material.LIME_CONCRETE, 1, 22, Utils.chat("&9Another chance"), Utils.chat("&1Deny the plot and give the builder another chance!"));
		
		Utils.createItem(inv, Material.YELLOW_CONCRETE, 1, 24, Utils.chat("&9Resize plot"), Utils.chat("&1Edit the plot boundaries to include more or less!"));
		
		Utils.createItem(inv, Material.RED_CONCRETE, 1, 24, Utils.chat("&9Remove plot"), Utils.chat("&1Will deny and reset the plot to its original state!"));
			
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		
		Reason r = Main.getInstance().getReason();
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Another chance"))) {

			//Will prompt the reviewer to input a reason.
			p.closeInventory();
			r.addReason(p,"claimed");
			p.sendMessage(Utils.chat("&aBefore the plot gets denied you must give a reason!"));
			p.sendMessage(Utils.chat("&aType the reason in chat, the first message sent will count!"));
						
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cResize plot"))) {
			
			p.closeInventory();
			p.sendMessage(Utils.chat("&cCurrently Not Implemented!"));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cRemove plot"))) {
			
			//Will prompt the reviewer to to input a reason.
			p.closeInventory();
			r.addReason(p,"deleted");
			p.sendMessage(Utils.chat("&aBefore the plot gets denied you must give a reason!"));
			p.sendMessage(Utils.chat("&aType the reason in chat, the first message sent will count!"));
			
		}
	}

}
