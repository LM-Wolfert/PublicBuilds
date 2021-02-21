package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.DenyGui;
import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.gui.PlotGui;
import me.elgamer.publicbuilds.gui.ReviewGui;

public class InventoryClicked implements Listener {
	
	public InventoryClicked(Main plugin) {
	
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		String title = e.getView().getTitle();
		if (title.equals(MainGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(MainGui.inventory_name)) {
				MainGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(ReviewGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ReviewGui.inventory_name)) {
				ReviewGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(PlotGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(PlotGui.inventory_name)) {
				PlotGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(DenyGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(DenyGui.inventory_name)) {
				DenyGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else {
			
		}
	}

}
