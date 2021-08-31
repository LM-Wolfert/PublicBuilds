package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;

public class NavigationGUI {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;
	
	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Navigation Menu";
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (User u) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		Utils.createItem(inv, Material.BRICK, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Build",
				Utils.chat("&fChoose a location you would like to build."),
				Utils.chat("&fThen you can create a plot and start building."),
				Utils.chat("&fAll ranks can build here."));	
		
		Utils.createItem(inv, Material.ENDER_EYE, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server",
				Utils.chat("&fTeleport to a different server."));	
		
		Utils.createItem(inv, Material.GRASS_BLOCK, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Spawn",
				Utils.chat("&fTeleport to spawn."));
		
		Utils.createItem(inv, Material.WRITABLE_BOOK, 1, 23, ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial", 
				Utils.chat("&fPick a tutorial you want to do."),
				Utils.chat("&fYou can leave it at any time."));	
		
		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the building menu."));	
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {
		
		Player p = u.player;
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Build")) {
			
			if (u.tutorial.tutorial_type <= 9 && u.tutorial.first_time) {
				u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
				return;
			}
			
			p.closeInventory();
			p.openInventory(LocationGUI.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(MainGui.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server")) {
			p.closeInventory();
			p.openInventory(SwitchServerGUI.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Spawn")) {
			if (u.tutorial.tutorial_type <= 9 && u.tutorial.first_time) {
				u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
				return;
			}
			p.closeInventory();
			p.teleport(Main.spawn);
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial")) {
			if (u.tutorial.tutorial_type <= 9 && u.tutorial.first_time) {
				u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
				return;
			}
			p.closeInventory();
			p.openInventory(TutorialGui.GUI(u));
		}
	}


}
