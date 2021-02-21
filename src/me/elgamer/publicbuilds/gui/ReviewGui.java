package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Review;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldGuard;

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

		Utils.createItem(inv, "", 1, 14, "&aBefore view!");
		Utils.createItem(inv, "", 1, 14, "&aCurrent view!");
		Utils.createItem(inv, "", 1, 14, "&aAccept!");
		Utils.createItem(inv, "", 1, 14, "&aDeny!");

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cBefore view"))) {
			Review review = Main.getInstance().getReview();
			int plot = review.getReview(p);
			p.teleport(WorldGuard.getCurrentLocation(plot));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cCurrent view"))) {
			Review review = Main.getInstance().getReview();
			int plot = review.getReview(p);
			p.teleport(WorldGuard.getBeforeLocation(plot));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cAccept"))) {
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cDeny"))) {
			p.closeInventory();
			p.openInventory(DenyGui.GUI(p));
		} else {}
	}

}
