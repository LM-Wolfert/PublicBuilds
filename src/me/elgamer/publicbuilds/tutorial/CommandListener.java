package me.elgamer.publicbuilds.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.User;
import net.md_5.bungee.api.ChatColor;

public class CommandListener implements Listener {

	public CommandListener(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void chatEvent(PlayerCommandPreprocessEvent e) {

		User u = Main.getInstance().getUser(e.getPlayer());

		if (u.tutorial.tutorial_stage == 1) {
			u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
		}
	}
}