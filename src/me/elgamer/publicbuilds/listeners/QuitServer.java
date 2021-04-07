package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.User;
import net.md_5.bungee.api.ChatColor;

public class QuitServer implements Listener {

	public QuitServer(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void quitEvent(PlayerQuitEvent e) {

		//Get instance of plugin.
		Main instance = Main.getInstance();

		//Get user from the list.
		User u = instance.getUser(e.getPlayer());

		//If no user was found print error in console.
		if (u == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error: User " + e.getPlayer().getName() + " not found in the list of online users!" );
		}

		//Get player instance.
		Player p = e.getPlayer();

		//Set tutorial stage in PlayerData
		PlayerData.setTutorialStage(u.uuid, u.tutorialStage);

		//Update the last online time of player.
		PlayerData.updateTime(p.getUniqueId().toString());

		//If the player is in a review, cancel it.
		if (u.reviewing != 0) {

			PlotData.setStatus(u.reviewing, "submitted");

		}

		//Remove user from list
		instance.getUsers().remove(u);

	}

}
