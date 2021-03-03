package me.elgamer.publicbuilds.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector2;

public class Plots {
	
	//Creates a hashmap to stop the player and the corners that a player can create.
	HashMap<Player, BlockVector2[]> plots = new HashMap<Player, BlockVector2[]>();
	
	//Adds the player to the hashmap with an array of BlockVector2D of size 4, which will be the 4 corners of a plot.
	public void addPlayer(Player p) {
		plots.put(p, new BlockVector2[4]);
	}
	
	//Returns all 4 BlockVector2D locations stored in the hashmap of the player.
	//It'll be returned as a list as this is how claims are creates.
	//If there are less than 4 entered points then some of the values will be null.
	public List<BlockVector2> getLocations(Player p) {
		List<BlockVector2> vector = new ArrayList<BlockVector2>();
		BlockVector2[] points = plots.get(p);
		
		for (int i=0; i<points.length; i++) {
			vector.add(points[i]);
		}
		
		return vector;
	}
	
	//Adds a location to one of the 4 points.
	public void addLocation(Player p, BlockVector2 point, int position) {
		
		BlockVector2[] points = plots.get(p);
		points[position-1] = point;
		plots.replace(p, points);
	
	}
	
	//Removed the player from the hashmap.
	public void removePlayer(Player p) {
		plots.remove(p);
	}
	
	//Checks whether all 4 locations are entered into the array.
	public boolean hasLocations(Player p) {
		
		BlockVector2[] locations = plots.get(p);
		if (locations[0] == null || locations[1] == null || locations[2] == null || locations[3] == null) {
			return false;
		} else {
			return true;
		}
		
	}
	
	//Calculates the smallest distance between any two points, if this is less than 5 meters then a plot will not be created.
	public boolean minDis(Player p) {
		
		BlockVector2[] points = plots.get(p);
		double dis = 10;
		double x = 0;
		double z = 0;
		
		for (int i = 0; i < 4; i++) {			
			for (int j = 0; j < 4; j++) {
				
				if (i == j) { continue; }
				
				x = (int) Math.abs((points[i].getX() - points[j].getX()));
				z = (int) Math.abs((points[i].getZ() - points[j].getZ()));
				
				dis = Math.sqrt(x*x+z*z);
				
				if (dis < 5) { return true; }
				
			}
		}
		
		return false;
		
	}
	
	//Calculated the largest distance between any two points.
	//If it is larger than is allowed for the players rank then a plot will not be created.
	public boolean maxDis(Player p) {
		
		BlockVector2[] points = plots.get(p);
		double dis = 0;
		double x = 0;
		double z = 0;
		
		for (int i = 0; i < 4; i++) {			
			for (int j = 0; j < 4; j++) {
				
				if (i == j) { continue; }
				
				x = (int) Math.abs((points[i].getX() - points[j].getX()));
				z = (int) Math.abs((points[i].getZ() - points[j].getZ()));
				
				dis = Math.sqrt(x*x+z*z);
				
				if (dis > RankValues.maxDis(p)) { return true; }
				
			}
		}
		
		return false;
		
	}
	
	//Checks whether a line between any two points intersects with a different line between two points.
	//If this is the case then a plot will have a usable shape and the plot will not be created.
	public boolean hasIntersect(Player p) {
		
		BlockVector2[] points = plots.get(p);
		
		double x1,x2,x3,x4,z1,z2,z3,z4,a1,a2,b1,b2,x;
		
		for (int i = 0; i<points.length; i++) {
			for (int j = 0; j<points.length; j++) {
				
				if (i == j) { continue; }
				
				x1 = points[i].getX();
				z1 = points[i].getZ();
				x2 = points[(i+1)%4].getX();
				z2 = points[(i+1)%4].getZ();
				
				x3 = points[j].getX();
				z3 = points[j].getZ();
				x4 = points[(j+1)%4].getX();
				z4 = points[(j+1)%4].getZ();
				
				a1 = (z2-z1)/(x2-x1);
				b1 = z1-(a1*x1);
				a2 = (z4-z3)/(x4-x3);
				b2 = z3-(a2*x3);
				
				x = (b2-b1)/(a1-a2);
				
				if (a1 == a2 || b1 == b2) {
					return true;
				} else if (a1 == a2) {
					continue;
				} else if (Math.abs(x2-x)<Math.abs(x2-x1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
