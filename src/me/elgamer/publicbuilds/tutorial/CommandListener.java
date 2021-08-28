package me.elgamer.publicbuilds.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.elgamer.UKnetUtilities.projections.ModifiedAirocean;
import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class CommandListener implements Listener {

	public CommandListener(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void chatEvent(PlayerCommandPreprocessEvent e) {

		User u = Main.getInstance().getUser(e.getPlayer());
		
		if (u.tutorial.tutorial_type == 2) {
			if (!e.getMessage().startsWith("/tpll") && !e.getMessage().startsWith("/ll")) {
				e.setCancelled(true);
				u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
				return;
			} else if (u.tutorial.tutorial_stage == 1 && e.getMessage().startsWith("/ll")) {
				u.tutorial.tutorial_stage = 2;
				u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Step 1/2 Complete", "Now you have to /tpll to all 4 corners of the warehouse.", 10, 100, 50);
				return;
			} else if (u.tutorial.tutorial_stage == 2 && e.getMessage().startsWith("/tpll")) {
				
				String[] message = e.getMessage().split("\\s+");
				if (message.length == 1) {
					return;
				}
				
				String[] args = new String[message.length-1];
				for (int i = 0; i<message.length-1; i++) {
					args[i] = message[i+1];
				}
				
				if(args.length==0) {
					return;
				}

				String[] splitCoords = args[0].split(",");
				if(splitCoords.length==2&&args.length<3) { // lat and long in single arg
					args = splitCoords;
				}

				if(args[0].endsWith(",")) {
					args[0] = args[0].substring(0, args[0].length() - 1);
				}

				if(args.length>1&&args[1].endsWith(",")) {
					args[1] = args[1].substring(0, args[1].length() - 1);
				}

				if(args.length!=2) {
					return;
				}

				double lon, lat;
				try {
					lat = Double.parseDouble(args[0]);
					lon = Double.parseDouble(args[1]);
				} catch(Exception ex) {
					return;
				}

				if (lat>90 || lat<-90) {
					return;
				}

				if (lon>180 || lon<-180) {
					return;
				}

				ModifiedAirocean projection = new ModifiedAirocean();
				double[] coords = projection.fromGeo(lon, lat);

				u.player.sendMessage((u.tutorial.stage2Corner(coords, u.player.getWorld())));
				if (u.tutorial.corner_sum == 4) {
					u.tutorial.tutorial_type = 3;
					u.tutorial.tutorial_stage = 1;
					u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tpll Tutorial Complete", "Well Done!", 10, 100, 50);
					Utils.spawnFireWork(u.player);
					u.tutorial.continueTutorial(u);
					
				}
				return;
				
			}	
			return;
		}
		
		if (u.tutorial.tutorial_type == 9) {
			if (e.getMessage().startsWith("/tpll") || e.getMessage().startsWith("/ll")) {
				return;
			}
		}
		
		if (u.tutorial.tutorial_type <= 9 && u.tutorial.first_time) {
			e.setCancelled(true);
			u.player.sendMessage(ChatColor.RED + "Please continue the tutorial first!");
		}
	}
}