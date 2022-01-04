package me.elgamer.publicbuilds.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.HologramData;
import me.elgamer.publicbuilds.mysql.HologramText;
import me.elgamer.publicbuilds.mysql.PlayerData;
import net.md_5.bungee.api.ChatColor;

public class Holograms {
	
	private HashMap<String, Hologram> holos;
	private HologramData hologramData;
	private HologramText hologramText;
	private PlayerData playerData;
	
	public Holograms(HologramData hologramData, HologramText hologramText, PlayerData playerData) {
		
		holos = new HashMap<String, Hologram>();	
		this.hologramData = hologramData;
		this.hologramText = hologramText;
		this.playerData = playerData;
		
	}
	
	public void create() {
		
		ArrayList<CustomHologram> holograms = hologramData.getHolos();
		ArrayList<String> lines;
		Hologram hologram;
		Main instance = Main.getInstance();
		
		if (holograms == null) {return;} else if (holograms.isEmpty()) {return;}
		
		for (CustomHologram holo : holograms) {
			
			//Create hologram and set visibility
			hologram = HologramsAPI.createHologram(instance, holo.l);
			hologram.getVisibilityManager().setVisibleByDefault(holo.visible);
			
			if (hologramText.hasLine(holo.name)) {
				lines = hologramText.getLines(holo.name);
				
				if (lines == null) {
					hologram.appendTextLine(Utils.chat("&b&lHologram " + holo.name + " has text assigned, but an error occured."));
					continue;
				}
				
				for (String line : lines) {
					hologram.appendTextLine(Utils.chat(line));
				}
				
			} else {
				hologram.appendTextLine(Utils.chat("&b&lHologram " + holo.name + " has no text assigned, use /customholo setline to add text."));
			}
			
		}
		
		
	}
	
	public void reloadAll() {
		
	}
	
	public void reload(String name) {
		
		if (name.equals("scoreboard")) {
			
			updateScoreboard(holos.get(name));
			
		}
		
	}
	
	public void updateScoreboard(Hologram hologram) {

		hologram.clearLines();

		LinkedHashMap<String, Integer> lead = playerData.pointsTop();

		if (lead == null || lead.size() == 0) {
			return;
		}

		hologram.appendTextLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Building Points Leaderboard");

		for (Entry<String, Integer> e : lead.entrySet()) {

			hologram.appendTextLine(e.getKey() + ": " + e.getValue());

		}


	}

}
