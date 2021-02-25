package me.elgamer.publicbuilds.listeners;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.Tutorial;

public class JoinServer implements Listener {

	Tutorial t;
	
	public JoinServer(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		Tutorial t = instance.getTutorial();
		Plots p = instance.getPlots();
		Player player = e.getPlayer();
		
		p.addPlayer(player);
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE ID=?");
			statement.setString(1, player.getUniqueId().toString());
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				
				PlayerData.updateTime(player.getUniqueId().toString());
				
				if (!(results.getString("NAME").equalsIgnoreCase(player.getName()))) {
					PlayerData.updatePlayerName(player.getUniqueId().toString(), player.getName());
				}
				
				if (results.getInt("TUTORIAL_STAGE") != config.getInt("tutorial_final_stage")) {
					t.addPlayer(player);
					t.continueTutorial(player);
				}
				
			} else {
				PlayerData.createPlayerInstance(player.getUniqueId().toString(), player.getName());
				t.addPlayer(player);
				t.continueTutorial(player);
			}
			
			
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
		
	}

}
