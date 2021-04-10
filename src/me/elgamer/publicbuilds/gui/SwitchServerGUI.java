package me.elgamer.publicbuilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class SwitchServerGUI {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Servers";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.BEACON, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Lobby Server",
				Utils.chat("&fTeleports you back to the lobby server."));

		Utils.createItem(inv, Material.DIAMOND_BLOCK, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Earth Server",
				Utils.chat("&fTeleports you back to the Earth server."),
				Utils.chat("&fTo build here you need Jr.Builder or higher."),
				Utils.chat("&fBTE Modpack is recommended for building."));

		Utils.createItem(inv, Material.CARVED_PUMPKIN, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Minigames Server",
				Utils.chat("&fTeleports you to the minigames server."));

		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the navigation menu."));	

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		Player p = u.player;

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Lobby Server")) {

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("lobby");

			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(NavigationGUI.GUI(u));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Earth Server")) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("earth");

			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Minigames Server")) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("minigames");

			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		}
	}

}
