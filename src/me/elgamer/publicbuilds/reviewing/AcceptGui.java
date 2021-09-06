package me.elgamer.publicbuilds.reviewing;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector2;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.AcceptData;
import me.elgamer.publicbuilds.mysql.BookData;
import me.elgamer.publicbuilds.mysql.MessageData;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.Accept;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldEditor;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;

public class AcceptGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 6 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Accept Plot";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();
		Accept ac = u.review.accept;

		int i;
		int j;

		for (j = 1; j<=3; j++) {			
			for (i = 1; i<=5; i++) {

				if (j == 1) {
					if (ac.size<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, 2*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Size: " + i);
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, 2*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Size: " + i);
					}
				}

				if (j == 2) {
					if (ac.accuracy<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, 3*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: " + i);
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, 3*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: " + i);
					}
				}

				if (j == 3) {
					if (ac.quality<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, 4*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: " + i);
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, 4*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: " + i);
					}
				}

			}
		}

		Utils.createItem(inv, Material.DIAMOND, 1, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "Submit", Utils.chat("&fAccept the plot with the current settings."));
		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 54, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", Utils.chat("&fGo back to the review gui."));



		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;
		Accept ac = u.review.accept;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			//Open the review gui.
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(u));
			return;
			//Set the value in the acceptgui based on the button that is clicked.
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 1")) {
			p.closeInventory();
			ac.size = 1;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 2")) {	
			p.closeInventory();
			ac.size = 2;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 3")) {
			p.closeInventory();
			ac.size = 3;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 4")) {
			p.closeInventory();
			ac.size = 4;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 5")) {
			p.closeInventory();
			ac.size = 5;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 1")) {
			p.closeInventory();
			ac.accuracy = 1;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 2")) {	
			p.closeInventory();
			ac.accuracy = 2;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 3")) {
			p.closeInventory();
			ac.accuracy = 3;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 4")) {
			p.closeInventory();
			ac.accuracy = 4;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 5")) {
			p.closeInventory();
			ac.accuracy = 5;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 1")) {
			p.closeInventory();
			ac.quality = 1;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 2")) {
			p.closeInventory();
			ac.quality = 2;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 3")) {
			p.closeInventory();
			ac.quality = 3;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 4")) {
			p.closeInventory();
			ac.quality = 4;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 5")) {
			p.closeInventory();
			ac.quality = 5;
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Submit")) {

			PlayerData playerData = Main.getInstance().playerData;
			PlotData plotData = Main.getInstance().plotData;
			BookData bookData = Main.getInstance().bookData;
			AcceptData acceptData = Main.getInstance().acceptData;
			MessageData messageData = Main.getInstance().messageData;

			FileConfiguration config = Main.getInstance().getConfig();
			
			p.closeInventory();

			//Calculate building points
			int buildingPoints = config.getInt("points_base") + ac.size*config.getInt("size_multiplier") + ac.accuracy*config.getInt("accuracy_multiplier") + ac.quality*config.getInt("quality_multiplier");

			if (u.review.bookMeta.hasPages()) {
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

				if (!(acceptData.insert(u.review.plot, plotData.getOwner(u.review.plot), u.uuid, bookID, ac.size, ac.accuracy, ac.quality, buildingPoints))) {
					p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
					return;
				}

			} else {
				if (!(acceptData.insert(u.review.plot, plotData.getOwner(u.review.plot), u.uuid, ac.size, ac.accuracy, ac.quality, buildingPoints))) {
					p.sendMessage(ChatColor.RED + "An error occured, please notify an admin.");
					return;
				}
			}

			messageData.addMessage(plotData.getOwner(u.review.plot), u.review.plot, "returned");			

			//Add building points and normal points
			playerData.addPoints(plotData.getOwner(u.review.plot), buildingPoints);
			me.elgamer.btepoints.utils.Points.addPoints(plotData.getOwner(u.review.plot), buildingPoints*config.getInt("points_multiplier"));

			//Remove reviewing status
			plotData.setStatus(u.review.plot, "completed");

			//Add plot to saveWorld
			List<BlockVector2> corners = WorldGuardFunctions.getPoints(u.review.plot);
			WorldEditor.updateWorld(corners, Bukkit.getWorld(config.getString("worlds.build")), Bukkit.getWorld(config.getString("worlds.save")));

			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Plot " + ChatColor.DARK_AQUA + u.review.plot + ChatColor.GREEN + " accepted for " + ChatColor.DARK_AQUA + buildingPoints + ChatColor.GREEN + " building points.");

			//Remove plot from worldguard
			ClaimFunctions.removeClaim(u.review.plot);
			
			p.sendMessage(ChatColor.GREEN + "Plot " + u.review.plot + " accepted for " + buildingPoints + " building points.");
			u.review.editBook.unregister();
			u.review = null;

			//If in the save world teleport back to the build world.
			if (p.getWorld().equals(Bukkit.getWorld(config.getString("worlds.save")))) {
				Location l = p.getLocation();
				l.setWorld(Bukkit.getWorld(config.getString("worlds.build")));
				p.teleport(l);
			}

		} else {}

	}

}
