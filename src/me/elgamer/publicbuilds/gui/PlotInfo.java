package me.elgamer.publicbuilds.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector2;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.CurrentPlot;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldEditor;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;

public class PlotInfo {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = Utils.chat("&9Plot Info");

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.OAK_BOAT, 1, 22, Utils.chat("&9Cancel plot"), Utils.chat("&1Cancel your plot, it will be removed from the world!"));
		Utils.createItem(inv, Material.OAK_BOAT, 1, 22, Utils.chat("&9Submit plot"), Utils.chat("&1Submit your plot, it be available for review!"));
		Utils.createItem(inv, Material.OAK_BOAT, 1, 22, Utils.chat("&9Teleport"), Utils.chat("&1Go to your plot!"));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		//Get plugin instance, config and current plot.
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		CurrentPlot cp = instance.getCurrentPlot();
		
		//Get the id of the current plot.
		int id = cp.getPlot(p);
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Teleport"))) {
			
			//Teleport the player to their plot.
			p.closeInventory();
			cp.removePlayer(p);
			p.teleport(WorldGuardFunctions.getCurrentLocation(id));
			p.sendMessage(Utils.chat("&1Teleported to plot: &9" + id));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Cancel plot"))) {

			//Cancels the plot.
			List<BlockVector2> vector = WorldGuardFunctions.getCorners(id);
			WorldEditor.updateWorld(vector, Bukkit.getWorld(config.getString("saveWorld")), Bukkit.getWorld(config.getString("buildWorld")));
			ClaimFunctions.removeClaim(id);
			PlotData.setStatus(id, "cancelled");
			
			p.closeInventory();
			cp.removePlayer(p);
			
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Submit plot"))) {
			
			//Submits the plot.
			PlotData.setStatus(id, "submitted");
			
			p.closeInventory();
			cp.removePlayer(p);

		} else {
			
		}

	}
}
