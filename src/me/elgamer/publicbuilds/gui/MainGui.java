package me.elgamer.publicbuilds.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.BlockVector2D;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.mysql.PlotData;
import me.elgamer.publicbuilds.utils.ClaimFunctions;
import me.elgamer.publicbuilds.utils.Plots;
import me.elgamer.publicbuilds.utils.RankValues;
import me.elgamer.publicbuilds.utils.Review;
import me.elgamer.publicbuilds.utils.Tutorial;
import me.elgamer.publicbuilds.utils.Utils;

public class MainGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;
	
	public static void initialize() {
		inventory_name = Utils.chat("&9Menu");
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (Player p) {
		
		String uuid = p.getUniqueId().toString();
		Main instance = Main.getInstance();
		Review review = instance.getReview();
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		Utils.createItem(inv, "SPRUCE_BOAT", 1, 22, Utils.chat("&9Belfast"), Utils.chat("&1Click here to build in Befast!"));
		Utils.createItem(inv, "SHIELD", 1, 24, Utils.chat("&9London"), Utils.chat("&1Click here to build in London!"));

		Utils.createItem(inv, "GREEN_TERRACOTTA", 1, 5, Utils.chat("&9Create plot"), Utils.chat("&1Will create a new plot in the area selected with /corner."));
		
		if (PlotData.hasPlot(uuid)) {
			Utils.createItem(inv, "SPRUCE_DOOR", 1, 41, Utils.chat("&9Plot Menu"), Utils.chat("&1Show all your active plots!"));
		}
		
		if (Utils.isPlayerInGroup(p, "reviewer") && review.inReview(p)) {
			Utils.createItem(inv, "YELLOW_CONCRETE", 1, 41, Utils.chat("&9Review Plot"), Utils.chat("&1Opens the review gui!"));
		}
		
		else if (Utils.isPlayerInGroup(p, "reviewer") && PlotData.reviewExists()) {
			Utils.createItem(inv, "LIME_CONCRETE", 1, 41, Utils.chat("&9New Review"), Utils.chat("&1Start reviewing a new plot!"));
		}
			
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		
		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Belfast"))) {
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9London"))) {
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Plot Menu"))) {
			p.closeInventory();
			p.openInventory(PlotGui.GUI(p));			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Review Plot"))) {
			p.closeInventory();
			p.openInventory(ReviewGui.GUI(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9New Review"))) {
			if (PlotData.reviewExists()) {
				Main instance = Main.getInstance();
				Review review = instance.getReview();
				review.addReview(p, PlotData.newReview());
				p.closeInventory();
				p.openInventory(ReviewGui.GUI(p));
			} else {
				p.closeInventory();
				p.sendMessage(Utils.chat("&cThere are no plots available for review!"));
			}
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&9Create plot"))) {
			
			Tutorial tutorial = Main.getInstance().getTutorial();
			
			if (tutorial.inTutorial(p)) {
				if (tutorial.getStage(p) == 3) {
					
					Plots plots = Main.getInstance().getPlots();
					
					if (plots.hasIntersect(p)) {
						p.sendMessage(Utils.chat("&cYour corners are not in a valid order!"));
						p.closeInventory();
						return;
					}
					if (tutorial.containsCorners(p)) {
						p.sendMessage(Utils.chat("&9Plot was correctly created, well done!"));
						tutorial.updateStage(p, 4);
						tutorial.continueTutorial(p);
						return;
					} else {
						p.sendMessage(Utils.chat("&9Your selection does not include all of the building and garden, please try again!"));
						return;
					}
				}
			}
			
			
			
			if (PlotData.hasPlot(p.getUniqueId().toString())) {
				if (PlotData.activePlotCount(p.getUniqueId().toString()) > RankValues.plotLimit(p)) {
					p.sendMessage(Utils.chat("&cYou have reached your plot limit, please complete and existing plot to create a new one!"));
					p.closeInventory();
					return;
				}
				
				if (PlotData.totalPlotCount(p.getUniqueId().toString()) > Main.getInstance().getConfig().getInt("maxPlots")) {
					p.sendMessage(Utils.chat("&cYou have too many plots submitted/claimed, please wait for at least 1 to be reviewed!"));
					p.closeInventory();
					return;
				}
			}
			
			Plots plots = Main.getInstance().getPlots();
			
			if (plots.hasLocations(p)) {
				if (plots.minDis(p)) {
					p.sendMessage(Utils.chat("&cYour selection is too small!"));
					p.closeInventory();
					return;
				}
				if (plots.maxDis(p)) {
					p.sendMessage(Utils.chat("&cYour selection is too large!"));
					p.closeInventory();
					return;
				}
				
				if (plots.hasIntersect(p)) {
					p.sendMessage(Utils.chat("&cYour corners are not in a valid order!"));
					p.closeInventory();
					return;
				}
			} else {
				p.sendMessage(Utils.chat("&cTo create a plot you must select 4 corners with /corner <1|2|3|4>"));
				p.closeInventory();
				return;
			}
			
			List<BlockVector2D> vector = plots.getLocations(p);
			p.sendMessage(ClaimFunctions.createClaim(p.getUniqueId().toString(), vector));
			p.closeInventory();
			return;
			
		}
		else {}
	}

}
