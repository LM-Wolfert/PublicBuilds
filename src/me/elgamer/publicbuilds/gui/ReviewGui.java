package me.elgamer.publicbuilds.gui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.commands.Review;
import me.elgamer.publicbuilds.mysql.MySQLReadWrite;
import me.elgamer.publicbuilds.utils.Utils;

public class ReviewGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = Utils.chat("Review");

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		UUID uuid = p.getUniqueId();

		MySQLReadWrite mysql = new MySQLReadWrite();

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		if (mysql.inReview(uuid)) {
			Utils.createItem(inv, 166, 1, 14, "&cCurrently reviewing", "&7To start a new review please finish this one!");
			Utils.createItemByte(inv, 251, 3, 1, 4, "&cBefore view", "&7Teleport to plot in original state!");
			Utils.createItemByte(inv, 251, 11, 1, 6, "&cCurrent view", "&7Teleport to plot on submit!");
			Utils.createItemByte(inv, 251, 5, 1, 22, "&cAccept", "&7Click to accept and input number of points!");
			Utils.createItemByte(inv, 251, 14, 1, 24, "&cDeny", "&7Click the deny and input reason!");
		}
		else {
			Utils.createItemByte(inv, 251, 1, 1, 14, "&cReview plot", "&7Click here to start reviewing a plot!");
		}

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		Review review = new Review();
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cReview plot"))) {
			review.newReview(p);
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cBefore view"))) {
			review.toPlot(p, "claimWorld");
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cCurrent view"))) {
			review.toPlot(p, "buildWorld");
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cAccept"))) {
			review.acceptPlot(p);
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cDeny"))) {
			review.denyPlot(p);
		} else {}
	}

}
