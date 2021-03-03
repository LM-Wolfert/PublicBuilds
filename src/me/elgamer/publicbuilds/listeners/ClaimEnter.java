package me.elgamer.publicbuilds.listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import me.elgamer.publicbuilds.Main;

public class ClaimEnter implements Listener{
	
	public WorldGuardPlugin worldGuardPlugin;

	public  ClaimEnter(Main plugin) {
		
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	private ArrayList<Player> entered = new ArrayList<>();
	private ArrayList<Player> left = new ArrayList<>();
	
	@EventHandler
	public void quitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(entered.contains(player) || left.contains(player)) {
			left.remove(player);
			entered.remove(player);
		}
	}
	
	@EventHandler
	public void moveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		enterRegion(player);
	}
	
	
	
	
	public void enterRegion(Player player) {
		
		Location l = player.getLocation();
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(BukkitAdapter.adapt(l));
		
		for(ProtectedRegion regions: applicableRegionSet) {
			if(regions.contains(BlockVector3.at(l.getX(), l.getY(), l.getZ()))) {
				if(!entered.contains(player)) {
					try {
						left.remove(player);
						entered.add(player);
						
						String owners = regions.getOwners().toPlayersString();
						owners = owners.replace("uuid:", "");
						
						Player regionPlayer = Bukkit.getServer().getPlayer(UUID.fromString(owners));
						player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
						player.sendMessage(ChatColor.GREEN + "Now Entering: ");
						player.sendMessage(ChatColor.BLUE + regionPlayer.getName() + "'s plot");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (!left.contains(player)) {
			if (applicableRegionSet.size() == 0) {
				entered.remove(player);
				left.add(player);
				player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
			}
		}
	}
}