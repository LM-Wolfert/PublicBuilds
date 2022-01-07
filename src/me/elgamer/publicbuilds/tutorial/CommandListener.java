package me.elgamer.publicbuilds.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;

import me.elgamer.UKnetUtilities.projections.ModifiedAirocean;
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
		
		if (e.getMessage().startsWith("/tutorialhelp")) {
			return;
		}
		
		if (u.tutorial.tutorial_type == 2) {
			if (u.tutorial.tutorial_stage == 1 && e.getMessage().startsWith("/ll")) {
				u.tutorial.tutorial_stage = 2;
				u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Step 1 Complete", "/tpll is next, the most useful command.", 10, 75, 10);
				u.tutorial.continueTutorial();
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
					e.setCancelled(true);
					u.tutorial.tutorial_stage = 3;
					u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Step 2 Complete", "Well Done, just one more step.", 10, 75, 10);
					u.tutorial.corner_sum = 0;
					u.tutorial.continueTutorial();
					return;
					
				}
				return;
				
			} else if (e.getMessage().startsWith("/ll")) {
				return;
			} else if (u.tutorial.tutorial_stage == 3 && e.getMessage().startsWith("//line")) {
				String[] command = e.getMessage().split(" ");
				e.setCancelled(true);
				
				if (command[1].equalsIgnoreCase("stone")) {
					
					Player actor = BukkitAdapter.adapt(u.player);
					SessionManager manager = WorldEdit.getInstance().getSessionManager();
					LocalSession localSession = manager.get(actor);
					Region region;
					
					World selectionWorld = localSession.getSelectionWorld();
					try {
						if (selectionWorld == null) throw new IncompleteRegionException();
						region = localSession.getSelection(selectionWorld);
					} catch (IncompleteRegionException ex) {
						actor.printError(TextComponent.of("Please make a region selection first."));
						return;
					}
					
					BlockVector3 p1 = region.getMinimumPoint();
					BlockVector3 p2 = region.getMaximumPoint();	
					
					u.player.sendMessage(u.tutorial.stage2Line(p1, p2));
					if (u.tutorial.line_sum == 4) {
						u.tutorial.tutorial_stage = 1;
						u.tutorial.tutorial_type = 3;
						u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Outline Tutorial Complete", "You will be teleported shortly.", 10, 75, 10);
						u.tutorial.line_sum = 0;
						Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> u.tutorial.continueTutorial() , 20); //20 ticks equal 1 second
					}
					
				} else {
					u.player.sendMessage(ChatColor.RED + "For this tutorial please use //line stone");
				}
			}
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