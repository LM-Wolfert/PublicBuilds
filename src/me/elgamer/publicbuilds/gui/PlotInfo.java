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
import me.elgamer.publicbuilds.utils.Time;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;
import net.md_5.bungee.api.ChatColor;

public class PlotInfo {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Info";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		if (u.currentStatus.equals("claimed")) {
			Utils.createItem(inv, Material.LIGHT_BLUE_CONCRETE, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Submit Plot",
					Utils.chat("&fSubmit your plot and put it up for review."),
					Utils.chat("&fIf you change your mind you can always retract the submission."));
		} else if (u.currentStatus.equals("submitted")) {
			Utils.createItem(inv, Material.ORANGE_CONCRETE, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Retract Submission",
					Utils.chat("&fSubmit your plot and put it up for review."),
					Utils.chat("&fIf you change your mind you can always retract the submission."));
		}
		
		Utils.createItem(inv, Material.YELLOW_CONCRETE, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Resize Plot",
				Utils.chat("&fWith your current Selection Tool selection edit the area of your plot."),
				Utils.chat("&fYour new selection must include all of the current area else it will not work."));

		Utils.createItem(inv, Material.RED_CONCRETE, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Plot",
				Utils.chat("&fCancels your plot and removes your progress."),
				Utils.chat("&fWARNING will revert all blocks placed to how it was before claiming!"));
		Utils.createItem(inv, Material.SPRUCE_BOAT, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Teleport",
				Utils.chat("&fTeleports you to your plot."));
		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fGo back to the plot menu."));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		//Get player
		Player p = u.player;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Teleport")) {

			//Teleport the player to their plot.
			p.closeInventory();
			p.teleport(WorldGuardFunctions.getCurrentLocation(u.currentPlot));
			p.sendMessage(Utils.chat("&aTeleported to plot: " + u.currentPlot));
			return;

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Plot")) {

			//Open the confirm gui to make sure the player didn't misclick.
			p.closeInventory();
			p.openInventory(ConfirmCancel.GUI(u));
			return;


		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Submit Plot")) {

			long lon_min = Main.getInstance().getConfig().getInt("submit_cooldown")*60*1000;

			if (Time.currentTime() - PlayerData.getSubmit(u.uuid) <= lon_min) {

				long lon_dif = lon_min - (Time.currentTime() - PlayerData.getSubmit(u.uuid));

				int sec = (int) ((lon_dif / 1000) % 60);
				int min = (int) ((lon_dif / 1000) / 60);

				String time;

				if (min == 0) {
					time = sec + " second";
				} else {
					if (sec == 0) {
						time = min + " minute";
					} else {
						time = min + " minute and " + sec + " second";
					}
				}

				p.sendMessage(ChatColor.RED + "You have a " + time + " cooldown before you can submit a plot!");
				return;
			}

			//Submits the plot.
			PlotData.setStatus(u.currentPlot, "submitted");
			PlotData.newSubmit(u.currentPlot);
			PlayerData.newSubmit(u.uuid);
			p.sendMessage(ChatColor.GREEN + "Plot " + u.currentPlot + " has been submitted.");

			p.closeInventory();
			return;

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Retract Submission")) {

			//Retracts a plot submission.
			PlotData.setStatus(u.currentPlot, "claimed");
			PlotData.removeSubmit(u.currentPlot);
			p.sendMessage(ChatColor.RED + "The submission for plot " + u.currentPlot + " has been retracted.");

			p.closeInventory();
			return;
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Resize Plot")) {
			
			p.sendMessage(ClaimFunctions.editClaim(u.currentPlot, u.uuid, u.plots.vector));
			p.closeInventory();
			u.plots.vector.clear();;
			return;

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(PlotGui.GUI(u));
			return;
		}

	}
}
