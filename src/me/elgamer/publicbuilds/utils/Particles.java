package me.elgamer.publicbuilds.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Particles {
	
	public static void spawnParticles(Player p, Location l) {
		
		p.spawnParticle(Particle.REDSTONE, l, 10, new Particle.DustOptions(Color.RED, 5));
		
	}

}
