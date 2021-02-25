package me.elgamer.publicbuilds.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.BlockVector2D;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.CurrentPlot;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldEditor;
import me.elgamer.publicbuilds.utils.WorldGuard;

public class PlotInfo {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 7;

	public static void initialize() {
		inventory_name = Utils.chat("&9Plot Info");

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, "OAK_BOAT", 1, 22, Utils.chat("&9Cancel plot"), Utils.chat("&1Go to your plot!"));
		Utils.createItem(inv, "OAK_BOAT", 1, 22, Utils.chat("&9Submit plot"), Utils.chat("&1Go to your plot!"));
		Utils.createItem(inv, "OAK_BOAT", 1, 22, Utils.chat("&9Teleport"), Utils.chat("&1Go to your plot!"));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		CurrentPlot cp = instance.getCurrentPlot();
		
		int id = cp.getPlot(p);
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Teleport"))) {
			
			p.closeInventory();
			cp.removePlayer(p);
			p.teleport(WorldGuard.getCurrentLocation(id));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Cancel plot"))) {

			List<BlockVector2D> vector = WorldGuard.getCorners(id);
			WorldEditor.updateWorld(p, vector, Bukkit.getWorld(config.getString("saveWorld")), Bukkit.getWorld(config.getString("buildWorld")));
			ClaimFunctions.removeClaim(id);
			PlotData.setStatus(id, "removed");
			
			p.closeInventory();
			p.openInventory(PlotGui.GUI(p));
			cp.removePlayer(p);
			
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Submit plot"))) {
			
			PlotData.setStatus(id, "submitted");
			
			p.closeInventory();
			p.openInventory(PlotGui.GUI(p));
			cp.removePlayer(p);

		} else {
			
		}

	}
}
