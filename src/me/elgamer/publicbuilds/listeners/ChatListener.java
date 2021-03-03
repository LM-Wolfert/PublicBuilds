package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.Reason;
import me.elgamer.publicbuilds.utils.Review;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.Utils;

public class ChatListener implements Listener {

	public ChatListener(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent e) {

		Reason r = Main.getInstance().getReason();
		Review rv = Main.getInstance().getReview();
		Player p = e.getPlayer();
		Tutorial t = Main.getInstance().getTutorial();
		
		if (t.inTutorial(p)) {
			if (t.getStage(p) == 5) {
				e.setCancelled(true);
				try {
					Double h = Double.parseDouble(e.getMessage());
					if (t.getHeight(h)) {
						t.updateStage(p, 6);
						t.continueTutorial(p);
					}
				} catch (NumberFormatException ex) {
					p.sendMessage("&cThis is not a valid number, please try again!");
				}
			}
		}
		
		//If a reviewer has denied a plot they will need to provide a reason, they will be added to the reason map.
		//If they send a message and are in the reason map their message will be recorded as the reason for denying the plot.
		//If they type a command it'll be cancelled.
		if (r.inReason(p)) {
			e.setCancelled(true);
			if (e.getMessage().startsWith("/")) {
				p.sendMessage(Utils.chat("&cYou must give a reason for denying the plot!"));
				p.sendMessage(Utils.chat("&cType the reason in chat, the first message sent will count!"));
			} else {
				//Sets the deny message and returns or cancels the plot.
				int plot = rv.getReview(p);
				PlotData.setDenyMessage(plot, e.getMessage());
				PlotData.setStatus(plot, r.getType(p));
				
				r.removePlayer(p);
				rv.removePlayer(p);
			}
		}


	}

}
