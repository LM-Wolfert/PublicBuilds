package me.elgamer.publicbuilds.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils {

	public static String chat (String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static ItemStack createItem(Inventory inv, Material material, int amount, int invSlot, String displayName, String... loreString) {

		ItemStack item;

		List<String> lore = new ArrayList<String>();

		item = new ItemStack(material);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.chat(displayName));
		for (String s : loreString) {
			lore.add(Utils.chat(s));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);

		inv.setItem(invSlot - 1,  item);

		return item;

	}

	public static ItemStack createPlayerSkull(Inventory inv, Player p, int amount, int invSlot, String displayName, String... loreString) {

		ItemStack item;

		List<String> lore = new ArrayList<String>();

		item = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(Utils.chat(displayName));
		for (String s : loreString) {
			lore.add(Utils.chat(s));
		}
		meta.setLore(lore);
		meta.setOwningPlayer(p);
		item.setItemMeta(meta);

		inv.setItem(invSlot - 1,  item);

		return item;

	}

	public static boolean isPlayerInGroup(Player player, String group) {
		return player.hasPermission("group." + group);
	}
}