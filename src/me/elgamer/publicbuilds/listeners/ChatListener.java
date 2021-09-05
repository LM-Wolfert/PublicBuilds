package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.mysql.PlotMessage;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import me.elgamer.publicbuilds.utils.WorldEditor;
import me.elgamer.publicbuilds.utils.WorldGuardFunctions;
import net.md_5.bungee.api.ChatColor;

public class ChatListener implements Listener {

	PlotData plotData;
	PlotMessage plotMessage;
	
	public ChatListener(Main plugin, PlotData plotData, PlotMessage plotMessage) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		this.plotData = plotData;
		this.plotMessage = plotMessage;

	}

	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent e) {

		Player p = e.getPlayer();

		User u = Main.getInstance().getUser(e.getPlayer());

		/*
		if (u.tutorialStage == 5) {
			e.setCancelled(true);
			try {
				Double h = Double.parseDouble(e.getMessage());
				if (Tutorial.getHeight(h)) {
					
					p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial Complete!", null, 10, 200, 50);
					
					u.tutorialStage = 6;
				} else {
					p.sendMessage(Utils.chat("&cThis is not close enough, please try again!"));
				}
			} catch (NumberFormatException ex) {
				p.sendMessage(Utils.chat("&cThis is not a valid number, please try again!"));
			}
		}
		*/

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
				
				if (u.reasonType.equals("resized")) {
					if (ClaimFunctions.resizePlot(u.reviewing, plotData.getOwner(u.reviewing), u.plots.vector)) {
						u.reasonType = "claimed";
					} else {
						p.sendMessage(ChatColor.RED + "An error occured with resizing the plot, please try again.");
						return;
					}
				}
				
				//Sets the deny message and returns or cancels the plot.
				plotMessage.addDenyMessage(u.reviewing, plotData.getOwner(u.reviewing), e.getMessage(), u.reasonType);
				plotData.setStatus(u.reviewing, u.reasonType);

				p.sendMessage(ChatColor.GREEN + "Plot " + u.reviewing + " denied with reason: " + e.getMessage());
				plotData.setLastVisit(u.reviewing);
				//Set the reasonType and reviewing back to default values.
				u.reasonType = null;
				u.reviewing = 0;
				u.plots.vector.clear();
				
				if (p.getWorld().equals(Bukkit.getWorld(config.getString("worlds.save")))) {
					Location l = p.getLocation();
					l.setWorld(Bukkit.getWorld(config.getString("worlds.build")));
					Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> p.teleport(l), 10); //20 ticks equal 1 second
				}
				
			}
		}


	}

}
