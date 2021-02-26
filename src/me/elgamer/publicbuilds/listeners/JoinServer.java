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
import me.elgamer.publicbuilds.utils.CurrentPlot;
import me.elgamer.publicbuilds.utils.Inactive;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.Tutorial;

public class JoinServer implements Listener {

	Tutorial t;
	
	public JoinServer(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		
		//Get instance of plugin and config.
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		
		//Get tutorial instance.
		Tutorial t = instance.getTutorial();
		
		//Get player instance.
		Player player = e.getPlayer();

		//If a player hasn't been online for more than 14 days their plot will cancelled.
		Inactive.cancelInactivePlots(player);
		
		//Add player to the plots map.
		Plots p = instance.getPlots();
		p.addPlayer(player);
		
		//Add player to the currentPlot map.
		CurrentPlot cp = instance.getCurrentPlot();
		cp.addPlayer(player);
		
		//Check whether the player has an entry in the player_data table.
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.playerData + " WHERE ID=?");
			statement.setString(1, player.getUniqueId().toString());
			ResultSet results = statement.executeQuery();
			
			//If they have an entry in player_data then.
			if (results.next()) {
				
				//If true then update their last online time.
				PlayerData.updateTime(player.getUniqueId().toString());
				
				//If they changed their username update that in the player_data table.
				if (!(results.getString("NAME").equalsIgnoreCase(player.getName()))) {
					PlayerData.updatePlayerName(player.getUniqueId().toString(), player.getName());
				}
				
				//If they haven't completed the tutorial continue the tutorial.
				if (results.getInt("TUTORIAL_STAGE") != config.getInt("tutorial_final_stage")) {
					t.addPlayer(player);
					t.continueTutorial(player);
				}
				
			//If they are not yet in player_data, the player is new and thus create a new entry and start the tutorial.
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
