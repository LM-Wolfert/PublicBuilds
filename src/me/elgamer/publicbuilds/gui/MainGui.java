package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.RankValues;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;

public class MainGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = Utils.chat("&9Menu");

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Player p = u.player;
		String uuid = u.uuid;

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.SPRUCE_BOAT, 1, 23, Utils.chat("&9Build"), Utils.chat("&1Click here if you want to build!"));

		Utils.createItem(inv, Material.GREEN_TERRACOTTA, 1, 5, Utils.chat("&9Create Plot"), Utils.chat("Start the plot creation process!"));

		Utils.createItem(inv, Material.BLAZE_ROD, 1, 40, Utils.chat("&9Selection Tool"), Utils.chat("&1Gives you the selection tool, used to create plot selections!"));

		if (PlotData.hasPlot(uuid) && PlotData.activePlotCount(uuid) > 0) {
			Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 42, Utils.chat("&9Plot Menu"), Utils.chat("&1Show all your active plots!"));
		}

		if (Utils.isPlayerInGroup(p, "reviewer") && (u.reviewing != 0)) {
			Utils.createItem(inv, Material.YELLOW_CONCRETE, 1, 41, Utils.chat("&9Review Plot"), Utils.chat("&1Opens the review gui!"));
		}

		else if (Utils.isPlayerInGroup(p, "reviewer") && PlotData.reviewExists()) {
			Utils.createItem(inv, Material.LIME_CONCRETE, 1, 41, Utils.chat("&9New Review"), Utils.chat("&1Start reviewing a new plot!"));
		}

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Build"))) {

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Selection Tool"))) {
			Plots.giveSelectionTool(u);
			p.closeInventory();

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Plot Menu"))) {
			//Open the plot gui if the player has at least 1 plot.
			if (PlotData.hasPlot(p.getUniqueId().toString())) {
				if (PlotData.activePlotCount(p.getUniqueId().toString()) > 0) {
					p.closeInventory();
					p.openInventory(PlotGui.GUI(p));
				}
			} else {
				p.sendMessage(Utils.chat("&cYou don't have any active plots!"));
			}
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Review Plot"))) {
			//Open the review gui.
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9New Review"))) {
			//If there is a plot available to review, create a new review and open the review gui.
			if (PlotData.reviewExists()) {
				u.reviewing = PlotData.newReview();
				p.closeInventory();
				p.openInventory(ReviewGui.GUI(p));
			} else {
				p.closeInventory();
				p.sendMessage(Utils.chat("&cThere are no plots available for review!"));
			}
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Create Plot"))) {

			//If they are in the third stage of the tutorial check if the plot is valid.
			if (u.tutorialStage == 3) {

				/*if (!(u.plots.hasLocations())) {
						p.sendMessage(Utils.chat("&9You have not selected 4 corners!"));
						return;
					}*/

				if (u.plots.vector.size() < 3) {
					p.sendMessage(Utils.chat("&9You must select a minimum of 3 points!"));
					return;
				}

				if (Tutorial.containsCorners(u)) {
					p.sendMessage(Utils.chat("&9Plot was correctly created, well done!"));
					u.tutorialStage = 4;
					Tutorial.continueTutorial(u);
					return;
				} else {
					p.sendMessage(Utils.chat("&9Your selection does not include all of the building and garden, please try again!"));
					return;
				}
			}

			//Check whether the player has a plot.
			if (PlotData.hasPlot(p.getUniqueId().toString())) {
				//Count all active plots, if they exceed the limit then end the method.
				if (PlotData.activePlotCount(p.getUniqueId().toString()) > RankValues.plotLimit(p)) {
					p.sendMessage(Utils.chat("&cYou have reached your plot limit, please complete and existing plot to create a new one!"));
					p.closeInventory();
					return;
				}

				//Count total plots, this includes claimed, submitted and under review, if they exceed the limit then end the method.
				if (PlotData.totalPlotCount(p.getUniqueId().toString()) > Main.getInstance().getConfig().getInt("maxPlots")) {
					p.sendMessage(Utils.chat("&cYou have too many plots submitted/claimed, please wait for at least 1 to be reviewed!"));
					p.closeInventory();
					return;
				}
			}

			//Check whether the player has selected the corners correctly.
			//Has selected all 4 corners.
			if (u.plots.vector.size() >= 3) {
				/*//Is not too small.
				if (u.plots.minDis(p)) {
					p.sendMessage(Utils.chat("&cYour selection is too small!"));
					p.closeInventory();
					return;
				}
				//Does not exceed the size.
				if (u.plots.maxDis(p)) {
					p.sendMessage(Utils.chat("&cYour selection is too large!"));
					p.closeInventory();
					return;
				}*/

			} else {
				p.sendMessage(Utils.chat("&cYou must move a minimum of 3 points to create a plot!"));
				p.closeInventory();
				return;
			}

			//Create claim
			p.sendMessage(ClaimFunctions.createClaim(p.getUniqueId().toString(), u.plots.vector));
			p.closeInventory();
			return;			
		}
	}

}
