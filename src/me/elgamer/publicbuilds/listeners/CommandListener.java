package me.elgamer.publicbuilds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.elgamer.UKnetUtilities.projections.ModifiedAirocean;
import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.User;
import me.elgamer.publicbuilds.utils.Utils;

public class CommandListener implements Listener {

	public CommandListener(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void chatEvent(PlayerCommandPreprocessEvent e) {

		User u = Main.getInstance().getUser(e.getPlayer());

		//If someone uses the /ll command check if they are in stage 1 of the tutorial since this is what they need to do.
		if (e.getMessage().startsWith("/ll")) {

			if (u.tutorialStage == 1) {
				u.tutorialStage = 2;
				Tutorial.continueTutorial(u);;
			}

			//If someone uses /tpll and are in stage 2 or 4 of the tutorial check if they input is correct and continue/redo the tutorial stage.
		} else if (e.getMessage().startsWith("/tpll")) {

			if (u.tutorialStage == 2) {
				String[] message = e.getMessage().split(" ");
				if (message.length == 3) {
					try
					{
						Double.parseDouble(message[2]);
					}
					catch(NumberFormatException ne)
					{return;}

					u.tutorialStage = 3;
					Tutorial.continueTutorial(u);
				}
			} else if (u.tutorialStage == 4) {

				String[] message = e.getMessage().split("\\s+");

				if (message.length == 1) {
					return;
				}

				String[] args = new String[message.length-1];
				for (int i = 0; i<message.length-1; i++) {
					args[i] = message[i+1];
				}

				Player p = e.getPlayer();
				p.sendMessage("args: " + args.length);
				p.sendMessage(args[0] + " " + args[1]);
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
				p.sendMessage("x: " + coords[0] + " z: " + coords[1]);

				if (Tutorial.nearCorners(coords)) {
					u.tutorialStage = 5;
					Tutorial.continueTutorial(u);
				} else {
					p.sendMessage(Utils.chat("&9This is not close enough to the corner of the building, please try again!"));
					return;
				}

			}

		}

	}
}
