package me.elgamer.publicbuilds.utils;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import me.elgamer.publicbuilds.mysql.PlayerData;

public class Leaderboard {

	public static void printLeaderboard(User u) {

		LinkedHashMap<String, Integer> lead = PlayerData.pointsAboveBelow(u.uuid, u.name);

		if (lead == null) {
			u.player.sendMessage(ChatColor.RED + "Not enough entries to create a leaderboard!");
			return;

		}

		if (lead.size() == 0) {
			u.player.sendMessage(ChatColor.RED + "Nobody has points!");
			return;
		}

		u.player.sendMessage(String.format("%-6s%-8s%-16s", "#", "Points" , "Username"));
		u.player.sendMessage("------------------------");

		int i = 1;
		
		for (Entry<String, Integer> e : lead.entrySet()) {

			u.player.sendMessage(String.format("%-6s%-8s%-16s", i, e.getValue() , e.getKey()));
			i += 1;
			
		}
	}
	
	public static void printLeaderboardElse(User u, String uuid, String name) {

		LinkedHashMap<String, Integer> lead = PlayerData.pointsAboveBelow(uuid, name);

		if (lead == null) {
			u.player.sendMessage(ChatColor.RED + "Not enough entries to create a leaderboard!");
			return;

		}

		if (lead.size() == 0) {
			u.player.sendMessage(ChatColor.RED + "Nobody has points!");
			return;
		}

		u.player.sendMessage(String.format("%-6s%-8s%-16s", "#", "Points" , "Username"));
		u.player.sendMessage("------------------------");

		int i = 1;
		
		for (Entry<String, Integer> e : lead.entrySet()) {

			u.player.sendMessage(String.format("%-6s%-8s%-16s", i, e.getValue() , e.getKey()));
			i += 1;
			
		}
	}
	
	public static void printLeaderboardTop(User u) {

		LinkedHashMap<String, Integer> lead = PlayerData.pointsTop();

		if (lead == null) {
			u.player.sendMessage(ChatColor.RED + "Not enough entries to create a leaderboard!");
			return;

		}

		if (lead.size() == 0) {
			u.player.sendMessage(ChatColor.RED + "Nobody has points!");
			return;
		}

		u.player.sendMessage(String.format("%-6s%-8s%-16s", "#", "Points" , "Username"));
		u.player.sendMessage("------------------------");

		int i = 1;
		
		for (Entry<String, Integer> e : lead.entrySet()) {

			u.player.sendMessage(String.format("%-6s%-8s%-16s", i, e.getValue() , e.getKey()));
			i += 1;
			
		}		
		
	}
}
