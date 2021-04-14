package me.elgamer.publicbuilds.utils;

import java.util.ArrayList;
import java.util.List;

//import org.bukkit.Location;
//import org.bukkit.Location;
import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.math.BlockVector2;

import me.elgamer.publicbuilds.Main;

//import me.elgamer.publicbuilds.Main;

public class Plots {

	public List<BlockVector2> vector = new ArrayList<BlockVector2>();
	//public List<Location> locations = new ArrayList<Location>();

	/*Point p1 = null;
	Point p2 = null;
	Point p3 = null;
	Point p4 = null;

	int cv = 1;


	//Adds a point to the correct p value.
	public void add(Point p) {

		switch(vector.size()-1) {

		case 1:
			p1 = p;
			cv = 2;
			break;

		case 2:
			p2 = p;
			cv = 3;
			break;

		case 3:
			p3 = p;
			cv = 4;
			break;

		case 4:
			p4 = p;
			cv = 1;
			break;

		}

	}

	//Returns 4 BlockVector2D locations stored in the hashmap of the player.
	//It'll be returned as a list as this is how claims are created.
	public List<BlockVector2> getLocations() {

		List<BlockVector2> vector = new ArrayList<BlockVector2>();

		Line l1 = new Line(p1, p2);
		Line l2 = new Line(p3, p4);

		if (hasIntersect(l1, l2)) {

			vector.add(BlockVector2.at(p1.x, p1.z));
			vector.add(BlockVector2.at(p3.x, p3.z));
			vector.add(BlockVector2.at(p2.x, p2.z));
			vector.add(BlockVector2.at(p4.x, p4.z));
			return vector;
		}

		Line l3 = new Line(p1, p3);
		Line l4 = new Line(p2, p4);

		if (hasIntersect(l3, l4)) {

			vector.add(BlockVector2.at(p1.x, p1.z));
			vector.add(BlockVector2.at(p2.x, p2.z));
			vector.add(BlockVector2.at(p3.x, p3.z));
			vector.add(BlockVector2.at(p4.x, p4.z));
			return vector;

		} else {

			vector.add(BlockVector2.at(p1.x, p1.z));
			vector.add(BlockVector2.at(p2.x, p2.z));
			vector.add(BlockVector2.at(p4.x, p4.z));
			vector.add(BlockVector2.at(p3.x, p3.z));
			return vector;
		}
	}

	//Checks whether all 4 locations are entered into the array.
	public boolean hasLocations() {

		if (p1 == null || p2 == null || p3 == null || p4 == null) {
			return false;
		} else {
			return true;
		}

	}

	//Calculates the smallest distance between any two points, if this is less than 5 meters then a plot will not be created.
	public boolean minDis(Player p) {

		if (distance(p1, p2) < Main.getInstance().getConfig().getInt("min_dis")) { return true; }
		if (distance(p1, p3) < Main.getInstance().getConfig().getInt("min_dis")) { return true; }
		if (distance(p1, p4) < Main.getInstance().getConfig().getInt("min_dis")) { return true; }
		if (distance(p2, p3) < Main.getInstance().getConfig().getInt("min_dis")) { return true; }
		if (distance(p2, p4) < Main.getInstance().getConfig().getInt("min_dis")) { return true; }
		if (distance(p3, p4) < Main.getInstance().getConfig().getInt("min_dis")) { return true; }

		return false;

	}

	//Calculated the largest distance between any two points.
	//If it is larger than is allowed for the players rank then a plot will not be created.
	public boolean maxDis(Player p) {

		if (distance(p1, p2) > RankValues.maxDis(p)) { return true; }
		if (distance(p1, p3) > RankValues.maxDis(p)) { return true; }
		if (distance(p1, p4) > RankValues.maxDis(p)) { return true; }
		if (distance(p2, p3) > RankValues.maxDis(p)) { return true; }
		if (distance(p2, p4) > RankValues.maxDis(p)) { return true; }
		if (distance(p3, p4) > RankValues.maxDis(p)) { return true; }		

		return false;

	}

	//Checks whether a line between any two points intersects with a different line between two points.
	//If this is the case then a plot will have a usable shape and the plot will not be created.
	public boolean hasIntersect(Line l1, Line l2) {

		if (Math.abs(l1.a) == Math.abs(l2.a)) {
			return false;
		}

		double x = (l2.b-l1.b)/(l1.a-l2.a);
		double y = (l1.a*x)+l1.b;

		Point ints = new Point(x,y);

		if ((distance(ints,l1.p1) > l1.dis) || (distance(ints,l1.p2) > l1.dis)) {
			return false;
		}

		if ((distance(ints,l2.p1) > l2.dis) || (distance(ints,l2.p2) > l2.dis)) {
			return false;
		}

		return true;

	}

	public double distance(Point p1, Point p2) {

		return (Math.sqrt(Math.pow((p2.x-p1.x),2)+Math.pow((p2.z-p1.z),2)));

	}

	public List<Location> getMarkers(){

		if (p1 == null) {
			return null;
		}

		List<Location> ls = new ArrayList<Location>();
		ls.add(p1.l);

		if (p2 == null) {
			return ls;
		}
		ls.add(p2.l);

		if (p3 == null) {
			return ls;
		}
		ls.add(p3.l);

		if (p4 == null) {
			return ls;
		}
		ls.add(p4.l);

		return ls;

	}

	public void clearCorners() {
		p1 = null;
		p2 = null;
		p3 = null;
		p4 = null;

		cv = 1;
	}*/

	public static void startSelection(User u, Block block) {

		BlockVector2 bv2 = BlockVector2.at(block.getX(), block.getZ());

		u.plots.vector = new ArrayList<BlockVector2>();
		//u.plots.locations = new ArrayList<Location>();

		u.plots.vector.add(bv2);

		//Location l = block.getLocation();
		//l.setY(l.getY()+1);
		//u.plots.locations.add(l);

	}

	public static void addPoint(User u, Block block) {

		BlockVector2 bv2 = BlockVector2.at(block.getX(), block.getZ());

		u.plots.vector.add(bv2);

		//Location l = block.getLocation();
		//l.setY(l.getY()+1);
		//u.plots.locations.add(l);

	}

	public static void giveSelectionTool(User u) {

		Inventory i = u.player.getInventory();

		for (ItemStack is : i.getContents()) {
			if (is == null) {
				continue;
			}
			if (is.equals(Main.selectionTool)) {
				u.player.sendMessage(Utils.chat("&aYou already have the selection tool in your inventory!"));
				return;
			}
		}


		int slot = i.firstEmpty();

		if (slot == -1) {
			u.player.sendMessage(Utils.chat("&cYour inventory is full, please make space first!"));
			return;
		} else {
			i.setItem(slot, Main.selectionTool);
		}

	}

	public static double largestDistance(List<BlockVector2> vector) {

		double size = 0;
		double val = 0;
		
		for (BlockVector2 bv : vector) {

			for (BlockVector2 bv2 : vector) {
				
				if (bv.equals(bv2)) {
					continue;
				}
				
				val = Math.sqrt(Math.pow(Math.abs(bv.getX()-bv2.getX()),2) + Math.pow(Math.abs(bv.getZ()-bv2.getZ()),2));
				
				if (val > size) {
					size = val;
				}
				
			}
			
		}
		
		return size;

	}

}
