package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.RankValues;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class MainGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Building Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Player p = u.player;

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.SPRUCE_BOAT, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Navigation Menu", 
				Utils.chat("&fOpens the navigation menu."),
				Utils.chat("&fFrom here you can choose where to go."),
				Utils.chat("&fBuild, go to spawn or switch to a different server."));

		Utils.createItem(inv, Material.EMERALD, 1, 20, ChatColor.AQUA + "" + ChatColor.BOLD + "Create Plot",
				Utils.chat("&fWill create a plot with the points you have selected."),
				Utils.chat("&fA minimum of 3 points are required for a valid plot."));

		Utils.createItem(inv, Material.BLAZE_ROD, 1, 21, ChatColor.AQUA + "" + ChatColor.BOLD + "Selection Tool", 
				Utils.chat("&fGives you the selection tool."),
				Utils.chat("&fIt is used to create your plot outline."));

		Utils.createItem(inv, Material.CHEST, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Menu", 
				Utils.chat("&fShows all your current active plots."),
				Utils.chat("&fThis is where you can submit, remove"),
				Utils.chat("&fand teleport to your plot."));

		Utils.createPlayerSkull(inv, p, 1, 24, ChatColor.AQUA + "" + ChatColor.BOLD + "Stats", 
				Utils.chat("&fBuilding Points: " + PlayerData.getPoints(u.uuid)),
				Utils.chat("&fCompleted Plots: " + PlotData.completedPlots(u.uuid)));

		Utils.createItem(inv, Material.WHITE_BANNER, 1, 25, ChatColor.AQUA + "" + ChatColor.BOLD + "Banner Maker", 
				Utils.chat("&fOpens the Banner Maker!"));

		Utils.createItem(inv, Material.SKELETON_SKULL, 1, 26, ChatColor.AQUA + "" + ChatColor.BOLD + "Head Database", 
				Utils.chat("&fOpens the Head Database!"));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Navigation Menu")) {
			
			p.closeInventory();
			p.openInventory(NavigationGUI.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Selection Tool")) {
			Plots.giveSelectionTool(u);
			p.closeInventory();

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Menu")) {
			//Open the plot gui.
			p.closeInventory();
			if (u.tutorial.tutorial_type <= 9 && u.tutorial.first_time) {
				u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
				return;
			}
			p.openInventory(PlotGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Create Plot")) {
			
			//If they are in the last stage of the tutorial check if the plot is valid.
			if (u.tutorial.tutorial_type <= 8 && u.tutorial.first_time) {
				u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
				return;
			}
			if (u.tutorial.tutorial_type == 9) {

				if (u.plots.vector.size() < 3) {
					p.sendMessage(Utils.chat("&cYou must select a minimum of 3 points!"));
					return;
				}

				if (u.tutorial.stage9Corners(u)) {

					u.player.teleport(Main.spawn);
					Utils.spawnFireWork(u.player);
					u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial Complete", "Good luck building!", 10, 100, 50);
					u.tutorial.tutorial_type = 10;
					u.tutorial.tutorial_stage = 0;
					u.tutorial.first_time = false;
					u.tutorial.complete = true;
					u.plots = new Plots();
					return;
				} else {
					p.sendMessage(Utils.chat("&cYour selection does not include all of the property, please try again!"));
					return;
				}
			}
			
			//Check whether the player has a plot.
			if (PlotData.hasPlot(p.getUniqueId().toString())) {
				//Count all active plots, if they exceed the limit then end the method.
				if (PlotData.activePlotCount(p.getUniqueId().toString()) >= RankValues.plotLimit(p)) {
					p.sendMessage(Utils.chat("&cYou have reached your plot limit, please complete and existing plot to create a new one!"));
					p.closeInventory();
					return;
				}

				//Count total plots, this includes claimed, submitted and under review, if they exceed the limit then end the method.
				if (PlotData.totalPlotCount(p.getUniqueId().toString()) >= Main.getInstance().getConfig().getInt("plot_maximum")) {
					p.sendMessage(Utils.chat("&cYou have too many plots submitted/claimed, please wait for at least 1 to be reviewed!"));
					p.closeInventory();
					return;
				}
			}

			//Check whether the player has selected the corners correctly.
			//Has selected at least 3 points to create a polygon.
			if (u.plots.vector.size() >= 3) {

				//Does not exceed the size.
				if (Plots.largestDistance(u.plots.vector) > RankValues.maxDis(p)) {
					p.sendMessage(Utils.chat("&cYour selection is too large!"));
					p.closeInventory();
					return;

				}

				//Is not too small.
				if (Plots.largestDistance(u.plots.vector) < 5) {
					p.sendMessage(Utils.chat("&cYour selection is too small!"));
					p.closeInventory();
					return;

				}

			} else {
				p.sendMessage(Utils.chat("&cYou must select a minimum of 3 points to create a plot!"));
				p.closeInventory();
				return;
			}

			//Create claim
			p.sendMessage(ClaimFunctions.createClaim(u, u.plots.vector));
			p.closeInventory();
			return;	

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Head Database")) {

			p.closeInventory();
			p.performCommand("headdb");

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Banner Maker")) {

			p.closeInventory();
			p.performCommand("bm");

		}
	}

}
