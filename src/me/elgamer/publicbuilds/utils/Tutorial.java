package me.elgamer.publicbuilds.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlayerData;

public class Tutorial {

	HashMap<Player, Integer> players = new HashMap<Player, Integer>();

	public void addPlayer(Player p) {

		players.put(p, PlayerData.getTutorialStage(p.getUniqueId().toString()));

	}

	public void removePlayer(Player p) {

		players.remove(p);

	}

	public void updateStage(Player p, int stage) {

		players.replace(p, stage);

	}

	public boolean inTutorial(Player p) {

		if (players.containsKey(p)) {
			return true; 
		} else {
			return false;
		}
	}

	public int getStage(Player p) {

		return players.get(p);

	}

	public void continueTutorial(Player p) {

		switch (getStage(p)) {

		case 1:
			stage1(p);
			break;

		case 2:
			stage2(p);
			break;

		case 3:
			stage3(p);
			break;

		case 4:
			stage4(p);
			break;

		case 5:
			stage5(p);
			break;
			
		case 6:
			stage6(p);
			break;

		}

	}

	public void stage1(Player p) {

		FileConfiguration config = Main.getInstance().getConfig();
		p.teleport(new Location(Bukkit.getWorld(config.getString("worlds.tutorial.before")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));
		p.sendMessage(Utils.chat("&9Before you can create a plot you must know where you are."));
		p.sendMessage(Utils.chat("&9Using &7/ll &9your coordinates will show in chat with a link to google maps."));

	}

	public void stage2(Player p) {

		p.sendMessage(Utils.chat("&9In google maps you'll be able to see the area you are currently in."));
		p.sendMessage(Utils.chat("&9If you right click on the map you can copy the the coordinates where you cursor is."));
		p.sendMessage(Utils.chat("&9Try copying the coordinates and then on the server do &7/tpll <coordinates> &9."));

	}

	public void stage3(Player p) {

		p.sendMessage(Utils.chat("&9Now that you know where you are you can pick out a building to build."));
		p.sendMessage(Utils.chat("&9For this tutorial you should make a plot for 134+136 Marlborough Gardens, don't forget the garden."));
		p.sendMessage(Utils.chat("&9To create a plot you need to mark all 4 corners with the command &7/corner."));
		p.sendMessage(Utils.chat("&9It is recommended to keep a few blocks between the edge of the building and the edge of you selection."));
		p.sendMessage(Utils.chat("&9When you have all the corners set you can open the gui with &7/gui &9 and there is a button to create a plot."));

	}
	
	public void stage4(Player p) {
		
		p.sendMessage(Utils.chat("&9Now that you have created a plot you can start to build, the first step is creating the outlines of the building."));
		p.sendMessage(Utils.chat("&9This is again done with &7/tpll &9, right click on one of the corners of the building and teleport to that position."));
		p.sendMessage(Utils.chat("&9Try to right click slightly inside the corner of the building, this is due to the roof sticking out more than the walls of the building."));
	}
	
	public void stage5(Player p) {
		
		p.sendMessage(Utils.chat("&9The final step is to make the walls the right height, usually a door is around 2 meters/blocks tall, this is a useful reference."));
		p.sendMessage(Utils.chat("&9Google earth pro also has elevations in the bottom right corner, this can also be used."));
		p.sendMessage(Utils.chat("&9Please type in chat your estimated height for the front wall of the building."));
	}
	
	public void stage6(Player p) {
		
		FileConfiguration config = Main.getInstance().getConfig();
		p.teleport(new Location(Bukkit.getWorld(config.getString("worlds.tutorial.after")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));
		p.sendMessage(Utils.chat("&9You have completed the tutorial, here is the building built by one of our builders."));
		p.sendMessage(Utils.chat("&6To leave this area just enter the portal on to the right of the building."));
		removePlayer(p);
		
	}

	public boolean containsCorners(Player p) {

		//Checks whether the corners the player has set include all 4 corners of the minimum plot size.
		Plots plots = Main.getInstance().getPlots().get(p);
		FileConfiguration config = Main.getInstance().getConfig();
		List<BlockVector2> vector = plots.getLocations();
		ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(p.getName(), vector, 1, 256);

		BlockVector2 pos1 = BlockVector2.at(config.getInt("plot_corners.1.x"), config.getInt("plot_corners.1.z"));
		BlockVector2 pos2 = BlockVector2.at(config.getInt("plot_corners.2.x"), config.getInt("plot_corners.2.z"));
		BlockVector2 pos3 = BlockVector2.at(config.getInt("plot_corners.3.x"), config.getInt("plot_corners.3.z"));
		BlockVector2 pos4 = BlockVector2.at(config.getInt("plot_corners.4.x"), config.getInt("plot_corners.4.z"));

		if (region.contains(pos1) && region.contains(pos2) && region.contains(pos3) && region.contains(pos4)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean nearCorners(double[] coords) {
		
		//Checks whether the player has teleported near the 4 corners of the building using /tpll
		FileConfiguration config = Main.getInstance().getConfig();
		
		if ((Math.abs(coords[0]-config.getDouble("tpll_points.1.x")) <= 0.5) && (Math.abs(coords[1]-config.getDouble("tpll_points.1.z")) <= 0.5)) {
			return true;
		} else if ((Math.abs(coords[0]-config.getDouble("tpll_points.2.x")) <= 0.5) && (Math.abs(coords[1]-config.getDouble("tpll_points.2.z")) <= 0.5)) {
			return true;
		} else if ((Math.abs(coords[0]-config.getDouble("tpll_points.3.x")) <= 0.5) && (Math.abs(coords[1]-config.getDouble("tpll_points.3.z")) <= 0.5)) {
			return true;
		} else if ((Math.abs(coords[0]-config.getDouble("tpll_points.4.x")) <= 0.5) && (Math.abs(coords[1]-config.getDouble("tpll_points.4.z")) <= 0.5)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getHeight(Double h) {
		
		//Checks whether the player has given the correct height of the building.
		FileConfiguration config = Main.getInstance().getConfig();
		
		if (Math.floor(h) == config.getInt("building_height")) {
			return true;
		} else {
			return false;
		}
	}

}
