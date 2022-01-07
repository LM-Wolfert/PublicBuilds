package me.elgamer.publicbuilds.utils;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.math.BlockVector3;

public class Vector {
	
	BlockVector3 p1;
	BlockVector3 p2;
	
	public Vector(BlockVector3 p1, BlockVector3 p2) {
		
		this.p1 = p1;
		this.p2 = p2;
		
	}
	
	public boolean equals(BlockVector3 p3, BlockVector3 p4) {
		
		if (p1.distance(p3) == 0 && p2.distance(p4) == 0) {
			return true;
		} else if (p1.distance(p4) == 0 && p2.distance(p3) == 0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public ArrayList<Location> vectorBlocks() {

		int diffX = abs(p1.getX()-p2.getX());
		int diffZ = abs(p1.getZ()-p2.getZ());
		
		double divider = max(diffX, diffZ);
		int minX = min(p1.getX(), p2.getX());
		int minZ = min(p2.getX(), p2.getZ());
		
		ArrayList<Location> line = new ArrayList<Location>();
		Location l;
		
		for (int i = 1; i < divider; i++) {
			
			l = new Location(Bukkit.getWorld("tutorialWorld"), minX + i * (diffX/divider), p1.getY(), minZ + i * (diffZ/divider));
			line.add(l);
			
		}
		
		return line;		
		
	}

}
