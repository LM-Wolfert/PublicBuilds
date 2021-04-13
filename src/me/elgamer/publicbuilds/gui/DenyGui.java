package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class DenyGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;
	
	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Deny Plot";
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (Player p) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", Utils.chat("&fGo back to the review menu."));
		
		Utils.createItem(inv, Material.LIME_CONCRETE, 1, 13, ChatColor.AQUA + "" + ChatColor.BOLD + "Another Chance", Utils.chat("&fDeny the plot and return it to the builder."));
		
		//Utils.createItem(inv, Material.YELLOW_CONCRETE, 1, 24, Utils.chat("&9Resize plot"), Utils.chat("&1Edit the plot boundaries to include more or less!"));
		
		Utils.createItem(inv, Material.RED_CONCRETE, 1, 15, ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Plot", Utils.chat("&fDeny the plot and return it to its orginal state."));
			
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {
		
		Player p = u.player;
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			//Open the review gui.
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(u));
			return;
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Another Chance")) {

			//Will prompt the reviewer to input a reason.
			p.closeInventory();
			u.reasonType = "claimed";
			p.sendMessage(Utils.chat("&aBefore the plot gets denied you must give a reason!"));
			p.sendMessage(Utils.chat("&aType the reason in chat, the first message sent will count!"));
						
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cResize plot"))) {
			
			p.closeInventory();
			p.sendMessage(Utils.chat("&cCurrently Not Implemented!"));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Plot")) {
			
			//Will prompt the reviewer to to input a reason.
			p.closeInventory();
			u.reasonType = "deleted";
			p.sendMessage(Utils.chat("&aBefore the plot gets denied you must give a reason!"));
			p.sendMessage(Utils.chat("&aType the reason in chat, the first message sent will count!"));
			
		}
	}

}
