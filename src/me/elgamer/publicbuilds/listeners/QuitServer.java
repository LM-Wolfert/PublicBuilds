package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.CurrentPlot;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.Review;
import me.elgamer.publicbuilds.utils.Tutorial;

public class QuitServer implements Listener {

	public QuitServer(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void quitEvent(PlayerJoinEvent e) {
		
		Main instance = Main.getInstance();
		Tutorial t = instance.getTutorial();
		Review r = instance.getReview();
		Plots pl = instance.getPlots();
		CurrentPlot cp = instance.getCurrentPlot();
		Player p = e.getPlayer();

		t.removePlayer(p);
		pl.removePlayer(p);
		cp.removePlayer(p);
		
		PlayerData.updateTime(p.getUniqueId().toString());

		if (r.inReview(p)) {
			
			int plot = r.getReview(p);
			PlotData.cancelReview(plot);
			r.removePlayer(p);
			
		}

	}

}
