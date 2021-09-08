package me.elgamer.publicbuilds.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class TutorialGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;
	
	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorials";
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (User u) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		Utils.createItem(inv, Material.ARROW, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Tpll",
				Utils.chat("&fTeleports you to the tpll tutorial."));
		
		Utils.createItem(inv, Material.BLAZE_ROD, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Creation", 
				Utils.chat("&fTeleports you to the plot creation tutorial."));	
		
		Utils.createItem(inv, Material.ORANGE_CONCRETE, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Creation and Building Outlines", 
				Utils.chat("&fWill send you a link to a video tutorial"),
				Utils.chat("&fwith a detailed walkthrough of how to create"),
				Utils.chat("&fa plot and make accurate building outlines."));	
		
		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the building menu."));	
		
		
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {
		
		Player p = u.player;
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Tpll")) {
			u.player.closeInventory();
			u.tutorial.complete = false;
			u.tutorial.tutorial_type = 2;
			u.tutorial.tutorial_stage = 1;
			u.tutorial.continueTutorial(u);
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Creation")) {
			p.closeInventory();
			u.tutorial.complete = false;
			u.tutorial.tutorial_type = 9;
			u.tutorial.tutorial_stage = 1;
			u.tutorial.continueTutorial(u);
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(MainGui.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Creation and Building Outlines")) {
			p.closeInventory();
			p.sendMessage(ChatColor.WHITE + "Plot Creation and Building Outlines Tutorial: https://youtu.be/U8_60BJ5qO8");
		}
	}
}
