package me.elgamer.publicbuilds.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector2;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.mysql.PlotMessage;
import me.elgamer.publicbuilds.utils.Accept;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldEditor;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;
import net.md_5.bungee.api.ChatColor;

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
		Accept ac = u.accept;

		int i;
		int j;

		for (j = 1; j<=3; j++) {			
			for (i = 1; i<=5; i++) {

				if (j == 1) {
					if (ac.size()<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, 2*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Size: " + i);
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, 2*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Size: " + i);
					}
				}
				
				if (j == 2) {
					if (ac.accuracy()<i) {
						Utils.createItem(inv, Material.RED_CONCRETE, 1, 3*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: " + i);
					} else {
						Utils.createItem(inv, Material.LIME_CONCRETE, 1, 3*9+i+2, ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: " + i);
					}
				}
				
				if (j == 3) {
					if (ac.quality()<i) {
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
		Accept ac = u.accept;
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			//Open the review gui.
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(u));
			return;
		//Set the value in the acceptgui based on the button that is clicked.
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 1")) {
			p.closeInventory();
			ac.setSize(1);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 2")) {	
			p.closeInventory();
			ac.setSize(2);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 3")) {
			p.closeInventory();
			ac.setSize(3);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 4")) {
			p.closeInventory();
			ac.setSize(4);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Size: 5")) {
			p.closeInventory();
			ac.setSize(5);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 1")) {
			p.closeInventory();
			ac.setAccuracy(1);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 2")) {	
			p.closeInventory();
			ac.setAccuracy(2);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 3")) {
			p.closeInventory();
			ac.setAccuracy(3);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 4")) {
			p.closeInventory();
			ac.setAccuracy(4);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Accuracy: 5")) {
			p.closeInventory();
			ac.setAccuracy(5);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 1")) {
			p.closeInventory();
			ac.setQuality(1);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 2")) {
			p.closeInventory();
			ac.setQuality(2);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 3")) {
			p.closeInventory();
			ac.setQuality(3);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 4")) {
			p.closeInventory();
			ac.setQuality(4);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Quality: 5")) {
			p.closeInventory();
			ac.setQuality(5);
			p.openInventory(GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Submit")) {
			
			FileConfiguration config = Main.getInstance().getConfig();
			String uuid = PlotData.getOwner(u.reviewing);
			
			//Calculate building points and add them
			int buildingPoints = config.getInt("points_base") + ac.size()*config.getInt("size_multiplier") + ac.accuracy()*config.getInt("accuracy_multiplier") + ac.quality()*config.getInt("quality_multiplier");
			PlayerData.addPoints(uuid, buildingPoints);
			
			//Add points for building with multiplier
			me.elgamer.btepoints.utils.Points.addPoints(uuid, buildingPoints*config.getInt("points_multiplier"));
			
			//Remove reviewing status
			PlotData.setStatus(u.reviewing, "completed");
			
			PlotMessage.addAccept(u.reviewing, uuid, buildingPoints);
			
			//Add plot to saveWorld
			List<BlockVector2> corners = WorldGuardFunctions.getPoints(u.reviewing);
			WorldEditor.updateWorld(corners, Bukkit.getWorld(config.getString("worlds.build")), Bukkit.getWorld(config.getString("worlds.save")));
			
			//Remove plot from worldguard
			ClaimFunctions.removeClaim(u.reviewing);
			p.sendMessage(ChatColor.GREEN + "Plot " + u.reviewing + " accepted!");
			u.reviewing = 0;
			if (p.getWorld().equals(Bukkit.getWorld(config.getString("worlds.save")))) {
				Location l = p.getLocation();
				l.setWorld(Bukkit.getWorld(config.getString("worlds.build")));
				p.teleport(l);
			}

			p.closeInventory();
			
		} else {}
		
	}

}
