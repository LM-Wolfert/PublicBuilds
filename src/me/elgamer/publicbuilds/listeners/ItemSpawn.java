package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;

public class ItemSpawn implements Listener {
	
	public ItemSpawn(Main plugin) {
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		
		ItemStack item = e.getEntity().getItemStack();
		
		if (item.equals(Main.gui) || item.equals(Main.tutorialGui)) {
			e.setCancelled(true);
		}
		
	}
	
	
	

}
