package me.elgamer.publicbuilds.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.gui.SwitchServerGUI;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class TutorialSelectionGui {
	
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

		Utils.createItem(inv, Material.WOODEN_AXE, 1, inv_rows, ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorials",
				Utils.chat("&fTeleport to a tutorial on the server."));

		Utils.createItem(inv, Material.LECTERN, 1, inv_rows, ChatColor.AQUA + "" + ChatColor.BOLD + "Videos",
				Utils.chat("&fWatch a tutorial video to help progress."),
				Utils.chat("&fIncludes tutorial walkthroughs and building guides."));

		Utils.createItem(inv, Material.ENDER_EYE, 1, inv_rows, ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server");

		if (u.previousGui.equals("main")) {
			Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
					Utils.chat("&fGo back to the building menu."));	
		}



		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server")) {
			p.closeInventory();
			u.previousGui = "tutorial";
			p.openInventory(SwitchServerGUI.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorials")) {
			p.closeInventory();
			p.openInventory(TutorialSelectionGui.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Videos")) {
			p.closeInventory();
			p.openInventory(TutorialVideoGui.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(MainGui.GUI(u));
		}
	}

}
