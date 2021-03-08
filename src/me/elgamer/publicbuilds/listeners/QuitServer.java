package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.CurrentPlot;
import me.elgamer.publicbuilds.utils.Review;
import me.elgamer.publicbuilds.utils.Tutorial;

public class QuitServer implements Listener {

	public QuitServer(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void quitEvent(PlayerQuitEvent e) {

		//Get instance of plugin.
		Main instance = Main.getInstance();

		//Get player instance.
		Player p = e.getPlayer();

		//Remove player from the tutorial map.
		Tutorial t = instance.getTutorial();
		if (t.inTutorial(p)) {
			PlayerData.setTutorialStage(p.getUniqueId().toString(), t.getStage(p));
			t.removePlayer(p);
		}

		//Remove player from plots map.
		Main.getInstance().getPlots().remove(p);
		
		//Remove player from currentPlot map.
		CurrentPlot cp = instance.getCurrentPlot();
		cp.removePlayer(p);

		//Update the last online time of player.
		PlayerData.updateTime(p.getUniqueId().toString());

		//If the player is in a review, cancel it.
		Review r = instance.getReview();
		if (r.inReview(p)) {

			int plot = r.getReview(p);
			PlotData.setStatus(plot, "submitted");
			r.removePlayer(p);

		}

	}

}
