package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.ConfirmCancel;
import me.elgamer.publicbuilds.gui.LocationGUI;
import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.gui.NavigationGUI;
import me.elgamer.publicbuilds.gui.PlotGui;
import me.elgamer.publicbuilds.gui.PlotInfo;
import me.elgamer.publicbuilds.gui.SwitchServerGUI;
import me.elgamer.publicbuilds.gui.TutorialGui;
import me.elgamer.publicbuilds.reviewing.AcceptGui;
import me.elgamer.publicbuilds.reviewing.DenyGui;
import me.elgamer.publicbuilds.reviewing.FeedbackGui;
import me.elgamer.publicbuilds.reviewing.ReviewGui;
import me.elgamer.publicbuilds.utils.User;

public class InventoryClicked implements Listener {
	
	public InventoryClicked(Main plugin) {
	
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if (e.getCurrentItem() == null) {
			return;
		}
		
		String title = e.getView().getTitle();
		
		User u = Main.getInstance().getUser((Player) e.getWhoClicked());
		if (title.equals(MainGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(MainGui.inventory_name)) {
				MainGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(LocationGUI.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(LocationGUI.inventory_name)) {
				LocationGUI.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(ReviewGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ReviewGui.inventory_name)) {
				ReviewGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(PlotGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(PlotGui.inventory_name)) {
				PlotGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(DenyGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(DenyGui.inventory_name)) {
				DenyGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(AcceptGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(AcceptGui.inventory_name)) {
				AcceptGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(PlotInfo.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(PlotInfo.inventory_name)) {
				PlotInfo.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(ConfirmCancel.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ConfirmCancel.inventory_name)) {
				ConfirmCancel.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(SwitchServerGUI.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(SwitchServerGUI.inventory_name)) {
				SwitchServerGUI.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(NavigationGUI.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(NavigationGUI.inventory_name)) {
				NavigationGUI.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(TutorialGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(TutorialGui.inventory_name)) {
				TutorialGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (title.equals(FeedbackGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(FeedbackGui.inventory_name)) {
				FeedbackGui.clicked(u, e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
		else if (e.getCurrentItem().equals(Main.gui)) {
			e.setCancelled(true);
			u.player.closeInventory();
			u.player.openInventory(MainGui.GUI(u));
		}
		else if (e.getCurrentItem().equals(Main.tutorialSkip)) {
			e.setCancelled(true);
			u.player.closeInventory();
			u.tutorial.skipStage(u);
			return;
		}
		else {
			
		}
	}

}
