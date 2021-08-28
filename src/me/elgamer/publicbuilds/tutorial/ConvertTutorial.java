package me.elgamer.publicbuilds.tutorial;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.publicbuilds.mysql.PlayerData;
import me.elgamer.publicbuilds.mysql.TutorialData;

public class ConvertTutorial implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			sender.sendMessage("&cThis command can only be run from console!");
			return true;
		}

		ResultSet results = PlayerData.getTutorialCompleters();
		
		if (results != null) {
			
			try {
				while (results.next()) {
					
					TutorialData.createPlayerInstance(results.getString("ID"), 10, 0, false);					
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return true;
			}
			
			sender.sendMessage("Conversion complete!");
			return true;			
					
		} else {
			sender.sendMessage("Nobody has completed the tutorial!");
			return true;
		}
		

	}

}
