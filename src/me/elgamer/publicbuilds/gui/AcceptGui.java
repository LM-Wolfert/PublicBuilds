package me.elgamer.publicbuilds.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector2;

import me.elgamer.publicbuilds.Main;
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
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = Utils.chat("&9Accept");

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();
		Accept ac = u.accept;

		int i;
		int j;
		int row = 2;

		for (j = 1; j<=3; j++) {			
			for (i = 1; i<=5; i++) {

				if (j == 1) {
					if (ac.size()<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, row*9+i+2, Utils.chat("&9Size: " + i));
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, row*9+i+2, Utils.chat("&9Size: " + i));
					}
				}
				
				if (j == 2) {
					if (ac.accuracy()<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, row*9+i+2, Utils.chat("&9Accuracy: " + i));
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, row*9+i+2, Utils.chat("&9Accuracy: " + i));
					}
				}
				
				if (j == 3) {
					if (ac.quality()<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, row*9+i+2, Utils.chat("&9Quality: " + i));
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, row*9+i+2, Utils.chat("&9Quality: " + i));
					}
				}

			}
		}
		
		Utils.createItem(inv, Material.ORANGE_CONCRETE, 1, 5, Utils.chat("&9Submit"));



		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;
		Accept ac = u.accept;
		
		//Set the value in the acceptgui based on the button that is clicked.
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Size: 1"))) {
			p.closeInventory();
			ac.setSize(1);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Size: 2"))) {	
			p.closeInventory();
			ac.setSize(2);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Size: 3"))) {
			p.closeInventory();
			ac.setSize(3);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Size: 4"))) {
			p.closeInventory();
			ac.setSize(4);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Size: 5"))) {
			p.closeInventory();
			ac.setSize(5);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Accuracy: 1"))) {
			p.closeInventory();
			ac.setAccuracy(1);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Accuracy: 2"))) {	
			p.closeInventory();
			ac.setAccuracy(2);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Accuracy: 3"))) {
			p.closeInventory();
			ac.setAccuracy(3);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Accuracy: 4"))) {
			p.closeInventory();
			ac.setAccuracy(4);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Accuracy: 5"))) {
			p.closeInventory();
			ac.setAccuracy(5);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Quality: 1"))) {
			p.closeInventory();
			ac.setQuality(1);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Quality: 2"))) {
			p.closeInventory();
			ac.setQuality(2);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Quality: 3"))) {
			p.closeInventory();
			ac.setQuality(3);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Quality: 4"))) {
			p.closeInventory();
			ac.setQuality(4);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Quality: 5"))) {
			p.closeInventory();
			ac.setQuality(5);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Submit"))) {
			
			FileConfiguration config = Main.getInstance().getConfig();
			
			//Calculate building points and add them
			int buildingPoints = ac.size()*config.getInt("size_multiplier") + ac.accuracy()*config.getInt("accuracy_multiplier") + ac.quality()*config.getInt("quality_multiplier");
			PlayerData.addPoints(p.getUniqueId().toString(), buildingPoints);
			
			//Add points for building with multiplier
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			String name = PlotData.getOwner(u.reviewing);
			
			String command = "addpoints " + name + " " + buildingPoints*config.getInt("points_multiplier");
			Bukkit.dispatchCommand(console, command);
			
			//Remove reviewing status
			PlotData.setStatus(u.reviewing, "completed");
			
			//Add plot to saveWorld
			List<BlockVector2> corners = WorldGuardFunctions.getCorners(u.reviewing);
			WorldEditor.updateWorld(corners, Bukkit.getWorld(config.getString("buildWorld")), Bukkit.getWorld(config.getString("saveWorld")));
			
			//Remove plot from worldguard
			ClaimFunctions.removeClaim(u.reviewing);
			u.reviewing = 0;
			
		} else {}
		
	}

}
