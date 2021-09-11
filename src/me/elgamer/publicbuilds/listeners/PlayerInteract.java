package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.MainGui;
import me.elgamer.publicbuilds.tutorial.Tutorial;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;

public class PlayerInteract implements Listener {

	Tutorial t;

	public PlayerInteract(Main plugin, ItemStack item) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void interactEvent(PlayerInteractEvent e) {

		User u = Main.getInstance().getUser(e.getPlayer());

		if (e.getPlayer().getOpenInventory().getType() != InventoryType.CRAFTING && e.getPlayer().getOpenInventory().getType() != InventoryType.CREATIVE) {
		    return;
		}
		
		if (e.getPlayer().getInventory().getItemInMainHand().equals(Main.gui)) {
			e.setCancelled(true);
			e.getPlayer().closeInventory();
			e.getPlayer().openInventory(MainGui.GUI(u));
		} else if (e.getPlayer().getInventory().getItemInMainHand().equals(Main.tutorialSkip)) {
			e.setCancelled(true);
			u.tutorial.skipStage(u);
			return;
		}
		
		if (e.getPlayer().getInventory().getItemInMainHand().equals(Main.selectionTool)) {
			if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {

				e.setCancelled(true);
				
				if (Plots.inRegion(e.getClickedBlock()) /*&& u.tutorialStage == 7*/) {
					u.player.sendMessage(Utils.chat("&cThis position already belongs to another plot"));
					return;
				}
				
				Plots.startSelection(u, e.getClickedBlock());
				u.player.sendMessage(Utils.chat("&aStarted a new selection at " + e.getClickedBlock().getX() + ", " + e.getClickedBlock().getZ()));

			} else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND)) {

				e.setCancelled(true);

				if (Plots.inRegion(e.getClickedBlock()) /*&& u.tutorialStage == 7*/) {
					u.player.sendMessage(Utils.chat("&cThis position already belongs to another plot"));
					return;
				}
				
				if (u.plots.vector.size() > 20) {
					u.player.sendMessage(Utils.chat("&cYou have reached the maximum number of points allowed!"));
					return;
				}

				if (u.plots.vector.size() == 0) {
					u.player.sendMessage(Utils.chat("&cYou must start your selection by left clicking a block!"));
					return;
				}

				Plots.addPoint(u, e.getClickedBlock());
				u.player.sendMessage(Utils.chat("&aAdded point at " + e.getClickedBlock().getX() + ", " + e.getClickedBlock().getZ()));

			}


		}

	}
	
	@EventHandler
	public void swapHands(PlayerSwapHandItemsEvent e) {
		
		if (e.getOffHandItem().equals(Main.gui)) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void dropItem(PlayerDropItemEvent e) {
		
		if (e.getItemDrop().getItemStack().equals(Main.gui)) {
			e.setCancelled(true);
		}
		
	}
}