package me.elgamer.publicbuilds.reviewing;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.BookData;
import me.elgamer.publicbuilds.mysql.DenyData;
import me.elgamer.publicbuilds.mysql.MessageData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldEditor;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;
import net.md_5.bungee.api.ChatColor;

public class DenyGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Deny Plot";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", Utils.chat("&fGo back to the review menu."));

		Utils.createItem(inv, Material.LIME_CONCRETE, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Another Chance", Utils.chat("&fDeny the plot and return it to the builder."));

		Utils.createItem(inv, Material.YELLOW_CONCRETE, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Resize plot",
				Utils.chat("&fDeny the plot and return it with a larger area."),
				Utils.chat("&fYour selection must include all of the existing area."));

		Utils.createItem(inv, Material.RED_CONCRETE, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Plot", Utils.chat("&fDeny the plot and return it to its orginal state."));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;

		FileConfiguration config = Main.getInstance().getConfig();

		PlotData plotData = Main.getInstance().plotData;
		DenyData denyData = Main.getInstance().denyData;
		BookData bookData = Main.getInstance().bookData;
		MessageData messageData = Main.getInstance().messageData;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			//Open the review gui.
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(u));
			return;

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Another Chance")) {

			p.closeInventory();

			//Get the feedback written in the book.
			List<String> book = u.review.bookMeta.getPages();
			int bookID = bookData.newBookID();
			int i = 1;

			//Insert all pages of feedback to the database so they can be retrieved later.
			for(String text: book) {
				if (!(bookData.addPage(bookID, i, text))) {
					p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
					return;
				}
				i++;
			}

			//If the feedback was stored successfully log the plot deny.
			if (denyData.insert(u.review.plot, plotData.getOwner(u.review.plot), u.uuid, bookID, "returned")) {
				messageData.addMessage(plotData.getOwner(u.review.plot), u.review.plot, "returned");
				plotData.setStatus(u.review.plot, "claimed");
				plotData.setLastVisit(u.review.plot);
				u.review = null;

				//Teleport back to the build world if in the save world
				if (p.getWorld().equals(Bukkit.getWorld(config.getString("worlds.save")))) {
					Location l = p.getLocation();
					l.setWorld(Bukkit.getWorld(config.getString("worlds.build")));
					p.teleport(l);
				}

				p.sendMessage(ChatColor.GREEN + "Plot " + u.review.plot + " denied and returned to the plot owner.");				
			} else {
				p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
			}


		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Resize Plot")) {
			
			p.closeInventory();

			//Check if the selection for the resize is correct.
			if (!(ClaimFunctions.checkEdit(p, u.review.plot, plotData.getOwner(u.review.plot), u.plots.vector))) {
				return;
			}
			
			//Get the feedback written in the book.
			List<String> book = u.review.bookMeta.getPages();
			int bookID = bookData.newBookID();
			int i = 1;

			//Insert all pages of feedback to the database so they can be retrieved later.
			for(String text: book) {
				if (!(bookData.addPage(bookID, i, text))) {
					p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
					return;
				}
				i++;
			}

			//If the feedback was stored successfully log the plot deny.
			if (denyData.insert(u.review.plot, plotData.getOwner(u.review.plot), u.uuid, bookID, "resized")) {
				messageData.addMessage(plotData.getOwner(u.review.plot), u.review.plot, "resized");
				plotData.setStatus(u.review.plot, "claimed");
				plotData.setLastVisit(u.review.plot);
				
				//Attempt to resize the plot.
				if (!(ClaimFunctions.resizePlot(u.review.plot, plotData.getOwner(u.review.plot), u.plots.vector))) {
					p.sendMessage(ChatColor.RED + "An error occured while resizing the plot, please contact an admin.");
				} else {
					p.sendMessage(ChatColor.GREEN + "Plot resized successfully!");
				}
				
				u.plots.vector.clear();
				u.review = null;

				//Teleport back to the build world if in the save world
				if (p.getWorld().equals(Bukkit.getWorld(config.getString("worlds.save")))) {
					Location l = p.getLocation();
					l.setWorld(Bukkit.getWorld(config.getString("worlds.build")));
					p.teleport(l);
				}

				p.sendMessage(ChatColor.GREEN + "Plot " + u.review.plot + " denied and returned to the plot owner.");				
			} else {
				p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
			}

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Plot")) {

			p.closeInventory();

			//Get the feedback written in the book.
			List<String> book = u.review.bookMeta.getPages();
			int bookID = bookData.newBookID();
			int i = 1;

			//Insert all pages of feedback to the database so they can be retrieved later.
			for(String text: book) {
				if (!(bookData.addPage(bookID, i, text))) {
					p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
					return;
				}
				i++;
			}

			//If the feedback was stored successfully log the plot deny.
			if (denyData.insert(u.review.plot, plotData.getOwner(u.review.plot), u.uuid, bookID, "deleted")) {
				messageData.addMessage(plotData.getOwner(u.review.plot), u.review.plot, "deleted");
				plotData.setStatus(u.review.plot, "deleted");
				plotData.setLastVisit(u.review.plot);
				
				//Remove the plot contents
				WorldEditor.updateWorld(WorldGuardFunctions.getPoints(u.review.plot), Bukkit.getWorld(config.getString("worlds.save")), Bukkit.getWorld(config.getString("worlds.build")));
				ClaimFunctions.removeClaim(u.review.plot);
				
				u.review = null;

				//Teleport back to the build world if in the save world
				if (p.getWorld().equals(Bukkit.getWorld(config.getString("worlds.save")))) {
					Location l = p.getLocation();
					l.setWorld(Bukkit.getWorld(config.getString("worlds.build")));
					p.teleport(l);
				}

				p.sendMessage(ChatColor.GREEN + "Plot " + u.review.plot + " denied and deleted.");				
			} else {
				p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
			}

		}
	}

}
