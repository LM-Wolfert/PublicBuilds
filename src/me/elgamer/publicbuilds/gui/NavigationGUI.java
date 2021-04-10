package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

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
		
		Utils.createItem(inv, Material.BRICK, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Build",
				Utils.chat("&fChoose a location you would like to build."),
				Utils.chat("&fThen you can create a plot and start building."),
				Utils.chat("&fAll ranks can build here."));	
		
		Utils.createItem(inv, Material.ENDER_EYE, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server",
				Utils.chat("&fTeleport to a different server."));	
		
		Utils.createItem(inv, Material.GRASS_BLOCK, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Spawn",
				Utils.chat("&fTeleport to spawn."));
		
		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the building menu."));	
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {
		
		Player p = u.player;
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Build")) {
			
			//Will open the build location gui.
			p.closeInventory();
			p.openInventory(LocationGUI.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(MainGui.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server")) {
			p.closeInventory();
			p.openInventory(SwitchServerGUI.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Spawn")) {
			p.teleport(Main.spawn);
		}
	}


}
