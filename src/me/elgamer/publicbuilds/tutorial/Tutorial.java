package me.elgamer.publicbuilds.tutorial;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.TutorialData;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.User;

public class Tutorial {

	public int tutorial_type;
	public int tutorial_stage;

	public boolean first_time;
	public boolean complete;
	
	public boolean corner_1;
	public boolean corner_2;
	public boolean corner_3;
	public boolean corner_4;
	public int corner_sum;

	public Tutorial(User u) {

		tutorial_type = TutorialData.getType(u.uuid);
		tutorial_stage = TutorialData.getStage(u.uuid);

		first_time = TutorialData.getTime(u.uuid);
		complete = false;

	}

	public Tutorial(User u, boolean complete) {

		tutorial_type = 10;
		tutorial_stage = 0;

		first_time = false;
		this.complete = complete;
	}



	public void continueTutorial(User u) {

		if (u.tutorial.tutorial_type == 1) {
			u.player.teleport(Main.TUTORIAL_1_START);
		} else if (u.tutorial.tutorial_type == 2) {
			u.player.teleport(Main.TUTORIAL_2_START);
			startStage2(u);
		} else if (u.tutorial.tutorial_type == 3) {
			u.player.teleport(Main.TUTORIAL_3_START);
		} else if (u.tutorial.tutorial_type == 4) {
			
		} else if (u.tutorial.tutorial_type == 5) {
			
		} else if (u.tutorial.tutorial_type == 6) {
			
		} else if (u.tutorial.tutorial_type == 7) {
			
		} else if (u.tutorial.tutorial_type == 8) {
			
		} else if (u.tutorial.tutorial_type == 9) {
			u.player.teleport(Main.TUTORIAL_9_START);
			startStage9(u);
		} 
		

	}
	
	public void skipStage(User u) {
		if (u.tutorial.tutorial_type == 1) {
		} else if (u.tutorial.tutorial_type == 2) {
			u.tutorial.tutorial_type = 3;
			u.tutorial.tutorial_stage = 1;
			u.tutorial.continueTutorial(u);
		} else if (u.tutorial.tutorial_type == 3) {
		} else if (u.tutorial.tutorial_type == 4) {
			
		} else if (u.tutorial.tutorial_type == 5) {
			
		} else if (u.tutorial.tutorial_type == 6) {
			
		} else if (u.tutorial.tutorial_type == 7) {
			
		} else if (u.tutorial.tutorial_type == 8) {
			
		} else if (u.tutorial.tutorial_type == 9) {
			u.player.teleport(Main.spawn);
			u.tutorial.tutorial_type = 10;
			u.tutorial.tutorial_stage = 0;
			u.tutorial.first_time = false;
			u.tutorial.complete = true;
			u.plots = new Plots();
		} 
	}

	/*
	public static void stage1(User u) {

		Player p = u.player;
		FileConfiguration config = Main.getInstance().getConfig();
		p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial Stage 1/5", "/ll", 10, 100, 50);
		p.teleport(new Location(Bukkit.getWorld(config.getString("worlds.tutorial")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));

		p.sendMessage(Utils.chat("&fThe first thing you'll be wondering, where am I?"));
		p.sendMessage(Utils.chat("&fFor this we have the command /ll"));
		p.sendMessage(Utils.chat("&fThis will return your current coordinates in chat with a link to google maps."));
		p.sendMessage(Utils.chat("&fTry it out and continue the tutorial."));

	}
*/
	public void startStage2(User u) {

		u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tpll Tutorial", "/tpll is the most important command in this project", 10, 75, 10);
		corner_1 = false;
		corner_2 = false;
		corner_3 = false;
		corner_4 = false;
		corner_sum = 0;
		
		
	}
	
	public void startStage9(User u) {
		
		u.player.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Tutorial", "Before you can build, you need to create plot", 10, 75, 10);
	}
	
	public String stage2Corner(double[] coords, World world) {
		
		Location l = new Location(world, coords[0], 143, coords[1]);
		
		if (l.distance(Main.TUTORIAL_2_CORNER_1) <= 1) {
			if (corner_1) {
				return (ChatColor.RED + "You have already teleported to this corner, you have " + (4-corner_sum) + " corners left.");
			} else {
				corner_1 = true;
				corner_sum += 1;
				return (ChatColor.GREEN + "Correct, you now have " + (4-corner_sum) + " corners left.");
			}
		} else if (l.distance(Main.TUTORIAL_2_CORNER_2) <= 1) {
			if (corner_2) {
				return (ChatColor.RED + "You have already teleported to this corner, you have " + (4-corner_sum) + " corners left.");
			} else {
				corner_2 = true;
				corner_sum += 1;
				return (ChatColor.GREEN + "Correct, you now have " + (4-corner_sum) + " corners left.");
			}
		} else if (l.distance(Main.TUTORIAL_2_CORNER_3) <= 1) {
			if (corner_3) {
				return (ChatColor.RED + "You have already teleported to this corner, you have " + (4-corner_sum) + " corners left.");
			} else {
				corner_3 = true;
				corner_sum += 1;
				return (ChatColor.GREEN + "Correct, you now have " + (4-corner_sum) + " corners left.");
			}
		} else if (l.distance(Main.TUTORIAL_2_CORNER_4) <= 1) {
			if (corner_4) {
				return (ChatColor.RED + "You have already teleported to this corner, you have " + (4-corner_sum) + " corners left.");
			} else {
				corner_4 = true;
				corner_sum += 1;
				return (ChatColor.GREEN + "Correct, you now have " + (4-corner_sum) + " corners left.");
			}
		}
		
		return (ChatColor.RED + "This is not close enough to the corner of the building.");
		
		
	}
	
	public boolean stage9Corners(User u) {
		//Checks whether the corners the player has set include all 4 corners of the minimum plot size.
				FileConfiguration config = Main.getInstance().getConfig();
				ProtectedPolygonalRegion region = new ProtectedPolygonalRegion("testregion", u.plots.vector, 1, 256);

				BlockVector2 pos1 = BlockVector2.at(config.getInt("tutorial_9.corner_1.x"), config.getInt("tutorial_9.corner_1.z"));
				BlockVector2 pos2 = BlockVector2.at(config.getInt("tutorial_9.corner_2.x"), config.getInt("tutorial_9.corner_2.z"));
				BlockVector2 pos3 = BlockVector2.at(config.getInt("tutorial_9.corner_3.x"), config.getInt("tutorial_9.corner_3.z"));
				BlockVector2 pos4 = BlockVector2.at(config.getInt("tutorial_9.corner_4.x"), config.getInt("tutorial_9.corner_4.z"));

				if (region.contains(pos1) && region.contains(pos2) && region.contains(pos3) && region.contains(pos4)) {
					return true;
				} else {
					return false;
				}
		
	}
/*
	public static void stage3(User u) {

		Player p = u.player;
		p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial Stage 3/5", "Creating a plot", 10, 100, 50);
		p.sendMessage(Utils.chat("&fNow that you know where you are you can pick out a building to build."));
		p.sendMessage(Utils.chat("&fFor this tutorial you should make a plot for 134+136 Marlborough Gardens."));
		p.sendMessage(Utils.chat("&fTo create a plot you need to mark out the corners with the selection tool."));
		p.sendMessage(Utils.chat("&fThe selection tool can be found in the gui, it's the blaze rod."));
		p.sendMessage(Utils.chat("&fIt is recommended to make your plot larger than the house and garden need."));
		p.sendMessage(Utils.chat("&fWhen you have selected the corners open the gui and click the emerald."));

	}

	public static void stage4(User u) {

		Player p = u.player;
		p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial Stage 4/5", "Creating outlines", 10, 100, 50);
		p.sendMessage(Utils.chat("&fOnce you have created a plot you can start to build."));
		p.sendMessage(Utils.chat("&fBut before you can build you need to find the outlines of the building."));
		p.sendMessage(Utils.chat("&fIn google maps right click on one of the corners of the building and copy the coordinates."));
		p.sendMessage(Utils.chat("&fKeep in mind that the roof often sticks out a bit so you may want to move inward a little."));
		p.sendMessage(Utils.chat("&fWith the coordinates again use /tpll to teleport to the corner of 134+136 Marlborough Gardens."));
	}

	public static void stage5(User u) {

		Player p = u.player;
		p.sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Tutorial Stage 5/5", "Estimating the height of the building.", 10, 100, 50);
		p.sendMessage(Utils.chat("&fThe final step is to make the walls the right height."));
		p.sendMessage(Utils.chat("&fGoogle earth pro has elevations (elev) in the bottom right corner."));
		p.sendMessage(Utils.chat("&fThis can be used to get the height, or you can estimate it."));
		p.sendMessage(Utils.chat("&fIf you choose to guess the height a good reference is the door, which is usually around 2 metres."));
		p.sendMessage(Utils.chat("&fPlease type in chat your estimated height for the front wall of 134+136 Marlborough Gardens."));
	}

	public static void stage6(User u) {

		Player p = u.player;
		FileConfiguration config = Main.getInstance().getConfig();
		u.plots = new Plots();
		p.teleport(new Location(Bukkit.getWorld(config.getString("worlds.build")), config.getDouble("starting_position.x"), config.getDouble("starting_position.y"), config.getDouble("starting_position.z")));
		p.sendMessage(Utils.chat("&aYou have completed the tutorial!"));
		p.sendMessage(Utils.chat("&fHere is the building built by one of our builders."));
		p.sendMessage(Utils.chat("&fUse the gui or tpll to leave this area and go back to wherever you want to go."));
		u.tutorialStage = 7;

	}
	*/

	public static boolean containsCorners(User u) {

		//Checks whether the corners the player has set include all 4 corners of the minimum plot size.
		FileConfiguration config = Main.getInstance().getConfig();
		ProtectedPolygonalRegion region = new ProtectedPolygonalRegion("testregion", u.plots.vector, 1, 256);

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

	public static boolean nearCorners(double[] coords) {

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

	public static boolean getHeight(Double h) {

		//Checks whether the player has given the correct height of the building.
		FileConfiguration config = Main.getInstance().getConfig();

		if (Math.floor(h) == config.getInt("building_height")) {
			return true;
		} else {
			return false;
		}
	}

}
