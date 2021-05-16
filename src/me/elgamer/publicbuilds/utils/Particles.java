package me.elgamer.publicbuilds.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Particles {

	public static void spawnRedParticles(Player p, Location l) {

		p.spawnParticle(Particle.REDSTONE, l, 10, new Particle.DustOptions(Color.RED, 5));

	}

	public static void spawnGreenParticles(Player p, Location l) {

		p.spawnParticle(Particle.REDSTONE, l, 10, new Particle.DustOptions(Color.YELLOW, 5));

	}

	public static void spawnBlueParticles(Player p, Location l) {

		p.spawnParticle(Particle.REDSTONE, l, 10, new Particle.DustOptions(Color.BLUE, 5));

	}

}
