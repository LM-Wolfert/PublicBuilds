package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;

public class ReviewGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = Utils.chat("Review");

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.LIGHT_BLUE_CONCRETE, 1, 14, "&aBefore view!");
		Utils.createItem(inv, Material.BLUE_CONCRETE, 1, 14, "&aCurrent view!");
		Utils.createItem(inv, Material.LIME_CONCRETE, 1, 14, "&aAccept!");
		Utils.createItem(inv, Material.RED_CONCRETE, 1, 14, "&aDeny!");

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cBefore view"))) {
			//Get the plot that is being reviewed and teleport the player do the plot in the saveWorld.
			p.teleport(WorldGuardFunctions.getCurrentLocation(u.reviewing));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cCurrent view"))) {
			//Get the plot that is being reviwed and teleport the player to the plot in the buildWorld.
			p.teleport(WorldGuardFunctions.getBeforeLocation(u.reviewing));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cAccept"))) {
			//Open the acceptgui with default values.
			p.closeInventory();
			u.createAccept();
			p.openInventory(AcceptGui.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cDeny"))) {
			//Open the denygui.
			p.closeInventory();
			p.openInventory(DenyGui.GUI(p));
		} else {}
	}

}
