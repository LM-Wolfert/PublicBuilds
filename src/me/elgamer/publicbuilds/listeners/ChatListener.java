package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.mysql.PlotMessage;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldEditor;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;
import net.md_5.bungee.api.ChatColor;

public class ChatListener implements Listener {

	public ChatListener(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent e) {

		Player p = e.getPlayer();

		User u = Main.getInstance().getUser(e.getPlayer());

		if (u.tutorialStage == 5) {
			e.setCancelled(true);
			try {
				Double h = Double.parseDouble(e.getMessage());
				if (Tutorial.getHeight(h)) {
					u.tutorialStage = 6;
				} else {
					p.sendMessage(Utils.chat("&cThis is not close enough, place try again!"));
				}
			} catch (NumberFormatException ex) {
				p.sendMessage("&cThis is not a valid number, please try again!");
			}
		}

		//If a reviewer has denied a plot they will need to provide a reason, they will be added to the reason map.
		//If they send a message and are in the reason map their message will be recorded as the reason for denying the plot.
		//If they type a command it'll be cancelled.
		if (u.reasonType != null) {
			e.setCancelled(true);
			if (e.getMessage().startsWith("/")) {
				p.sendMessage(Utils.chat("&cYou must give a reason for denying the plot."));
				p.sendMessage(Utils.chat("&cType the reason in chat, the first message sent will count."));
			} else {

				FileConfiguration config = Main.getInstance().getConfig();

				if (u.reasonType.equals("deleted")) {
					WorldEditor.updateWorld(WorldGuardFunctions.getPoints(u.reviewing), Bukkit.getWorld(config.getString("worlds.save")), Bukkit.getWorld(config.getString("worlds.build")));
					ClaimFunctions.removeClaim(u.reviewing);
				}
				
				//Sets the deny message and returns or cancels the plot.
				PlotMessage.addDenyMessage(u.reviewing, PlotData.getOwner(u.reviewing), e.getMessage(), u.reasonType);
				PlotData.setStatus(u.reviewing, u.reasonType);

				p.sendMessage(ChatColor.GREEN + "Plot " + u.reviewing + " denied with reason: " + e.getMessage());
				//Set the reasonType and reviewing back to default values.
				u.reasonType = null;
				u.reviewing = 0;
			}
		}


	}

}
