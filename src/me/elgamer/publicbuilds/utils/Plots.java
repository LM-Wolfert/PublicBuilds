package me.elgamer.publicbuilds.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector2D;

public class Plots {
	
	HashMap<Player, BlockVector2D[]> plots = new HashMap<Player, BlockVector2D[]>();
	
	public void addPlayer(Player p) {
		plots.put(p, new BlockVector2D[4]);
	}
	
	public List<BlockVector2D> getLocations(Player p) {
		List<BlockVector2D> vector = new ArrayList<BlockVector2D>();
		BlockVector2D[] points = plots.get(p);
		
		for (int i=0; i<points.length; i++) {
			vector.add(points[i]);
		}
		
		return vector;
	}
	
	public void addLocation(Player p, BlockVector2D point, int position) {
		
		BlockVector2D[] points = plots.get(p);
		points[position-1] = point;
		plots.replace(p, points);
	
	}
	
	public void removePlayer(Player p) {
		plots.remove(p);
	}
	
	public boolean hasLocations(Player p) {
		
		BlockVector2D[] locations = plots.get(p);
		if (locations[0] == null || locations[1] == null || locations[2] == null || locations[3] == null) {
			return false;
		} else {
			return true;
		}
		
	}
	
	public boolean minDis(Player p) {
		
		BlockVector2D[] points = plots.get(p);
		double dis = 10;
		double x = 0;
		double z = 0;
		
		for (int i = 0; i < 4; i++) {			
			for (int j = 0; j < 4; j++) {
				
				if (i == j) { continue; }
				
				x = (int) Math.abs((points[i].getX() - points[j].getX()));
				z = (int) Math.abs((points[i].getZ() - points[j].getZ()));
				
				if (x < dis) { dis = x; }
				if (z < dis) { dis = z; }
				
				if (dis < 5) { return true; }
				
			}
		}
		
		return false;
		
	}
	
	public boolean maxDis(Player p) {
		
		BlockVector2D[] points = plots.get(p);
		double dis = 0;
		double x = 0;
		double z = 0;
		
		for (int i = 0; i < 4; i++) {			
			for (int j = 0; j < 4; j++) {
				
				if (i == j) { continue; }
				
				x = (int) Math.abs((points[i].getX() - points[j].getX()));
				z = (int) Math.abs((points[i].getZ() - points[j].getZ()));
				
				if (x > dis) { dis = x; }
				if (z > dis) { dis = z; }
				
				if (dis > RankValues.maxDis(p)) { return true; }
				
			}
		}
		
		return false;
		
	}
	
	public boolean hasIntersect(Player p) {
		
		BlockVector2D[] points = plots.get(p);
		
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
