package me.elgamer.publicbuilds.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.reviewing.Review;
import me.elgamer.publicbuilds.reviewing.ReviewGui;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PlotGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		PlotData plotData = Main.getInstance().plotData;
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", Utils.chat("&fGo back to the building menu."));



		//Get all plots owned by the player.
		HashMap<Integer, String> plots = plotData.getPlots(u.uuid);

		if (plots == null) {

			toReturn.setContents(inv.getContents());
			return toReturn;
		}

		//Add all the plots to the gui in rows of 7.
		int i = 2;
		int j = 2;

		for (Map.Entry<Integer, String> entry : plots.entrySet()) {

			//If the plot is claimed or submitted add it to the gui.
			if (entry.getValue().equalsIgnoreCase("claimed")) {			
				Utils.createItem(inv, Material.LIME_CONCRETE, 1, (j-1)*9+i, ChatColor.AQUA + "" + ChatColor.BOLD + String.valueOf(entry.getKey()),
						Utils.chat("&fClick to open plot functions."),
						Utils.chat("&fThis plot is currently claimed."),
						Utils.chat("&fYou can submit, remove"),
						Utils.chat("&fand teleport to this plot."));
			}

			if (entry.getValue().equalsIgnoreCase("submitted")) {
				Utils.createItem(inv, Material.LIGHT_BLUE_CONCRETE, 1, (j-1)*9+i, ChatColor.AQUA + "" + ChatColor.BOLD + String.valueOf(entry.getKey()),
						Utils.chat("&fClick to open plot functions."),
						Utils.chat("&fThis plot is currently submitted."),
						Utils.chat("&fYou can retract, remove"),
						Utils.chat("&fand teleport to this plot."));
			}
			
			i += 1;

			if (i>8) {
				i = 2;
				j += 1;
			}				
		}
		
		if (Utils.isPlayerInGroup(u.player, "reviewer") && (u.review == null)) {
			Utils.createItem(inv, Material.YELLOW_STAINED_GLASS_PANE, 1, 41, ChatColor.AQUA + "" + ChatColor.BOLD + "Review Plot", 
					Utils.chat("&fOpens the review menu."),
					Utils.chat("&fAllows you to accept and deny"),
					Utils.chat("&fas well as teleport to the before and after view."));
		}

		else if (Utils.isPlayerInGroup(u.player, "reviewer") && plotData.reviewExists(u)) {
			Utils.createItem(inv, Material.LIME_STAINED_GLASS_PANE, 1, 41, ChatColor.AQUA + "" + ChatColor.BOLD + "New Review", 
					Utils.chat("&fStart reviewing a new plot."),
					Utils.chat("&fWill instantly open the review menu."));
		}

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {
		
		PlotData plotData = Main.getInstance().plotData;
		Player p = u.player;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(MainGui.GUI(u));
			return;
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Review Plot")) {
			//Open the review gui.
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(u));
			return;
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "New Review")) {
			//If there is a plot available to review, create a new review and open the review gui.
			if (plotData.reviewExists(u)) {
				u.review = new Review(plotData.newReview(u));
				p.closeInventory();
				p.openInventory(ReviewGui.GUI(u));
				return;
			} else {
				p.closeInventory();
				p.sendMessage(Utils.chat("&cThere are no plots available for review!"));
				return;
			}
		}

		//Get plot id and status of the clicked plot in the gui, set that as the players current plot.
		//Open the plot info gui of that plot.
		String plotid = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
		int id = Integer.parseInt(plotid);
		u.currentPlot = id;

		if (clicked.getType() == Material.LIME_CONCRETE) {
			u.currentStatus = "claimed";
		} else if (clicked.getType() == Material.LIGHT_BLUE_CONCRETE){
			u.currentStatus = "submitted";
		}

		p.closeInventory();
		p.openInventory(PlotInfo.GUI(u));
	}		
}
