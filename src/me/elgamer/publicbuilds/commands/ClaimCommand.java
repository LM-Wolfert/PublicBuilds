package me.elgamer.publicbuilds.commands;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.AnvilGui;
import me.elgamer.publicbuilds.gui.AnvilGui.AnvilClickEvent;
import me.elgamer.publicbuilds.mysql.MySQLReadWrite;

@SuppressWarnings("unused")
public class ClaimCommand implements CommandExecutor {
	
	private Main instance = Main.getInstance();
	private FileConfiguration config = instance.getConfig();
		
	private int guestArea = config.getInt("guestArea");
	private int apprenticeArea = config.getInt("apprenticeArea");
	private int builderArea = config.getInt("builderArea");
	
	private int guestLimit = config.getInt("guestLimit");
	private int apprenticeLimit = config.getInt("apprenticeLimit");
	private int builderLimit = config.getInt("builderLimit");

	private int area = 0;
	private int width = 0;
	private int length = 0;
	
	private int i = 0;
	
	private String plotName;

	Pattern wordPattern = Pattern.compile("\\w+");
	Matcher wordMatcher;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot create a plot!");
			return true;
		}
		
		Player p = (Player) sender;
		
		createRegion(builderArea, p);
		/*switch (Main.getPermissions().getPrimaryGroup("claimWorld",p)) {
		
		case "builder":
			if (createRegion(builderArea, p)) {return true;}
			
		case "apprentice":
			if (createRegion(apprenticeArea,p)) {return true;}
			
		case "guest":
			if (createRegion(guestArea,p)) {return true;}
				
		}*/
		return true;
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}

	private WorldEditPlugin getWorldEdit() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			return null;
		}

		return (WorldEditPlugin) plugin;
	}
	
	private int getArea(Selection sel) {

		width = sel.getWidth();
		length = sel.getLength();

		area = width * length;
		return area;
	}

	private boolean createRegion(int maxArea, Player p) {

		Selection sel = getWorldEdit().getSelection(p);

		if (sel == null) {
			p.sendMessage(ChatColor.RED + "You must make a selection!");
			return true;
		}

		area = getArea(sel);

		if (area > maxArea) {
			p.sendMessage(ChatColor.RED + "Your selection exceeds the maximum of " + maxArea + " blocks");
			return true;
		}

		World claimWorld = Bukkit.getServer().getWorld("claimWorld");
		World buildWorld = Bukkit.getServer().getWorld("buildWorld");

		RegionContainer container = getWorldGuard().getRegionContainer();
		RegionManager claimRegions = container.get(claimWorld);

		ProtectedRegion testRegion = new ProtectedCuboidRegion("testRegion",
				new BlockVector(sel.getNativeMinimumPoint()), new BlockVector(sel.getNativeMaximumPoint()));

		ApplicableRegionSet set = claimRegions.getApplicableRegions(testRegion);
		if (set.size() != 0) {
			p.sendMessage(ChatColor.RED + "Your selection overlaps with another claim!");
			return true;
		}
		
		p.closeInventory();

		AnvilGui gui = new AnvilGui(p, new AnvilGui.AnvilClickEventHandler(){
			
			@Override
			public void onAnvilClick(AnvilClickEvent e) {
				if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {
					e.setWillClose(true);
					e.setWillDestroy(true);
					String name = e.getName();
					wordMatcher = wordPattern.matcher(name);
					if (wordMatcher.matches()) {
						MySQLReadWrite mysql = new MySQLReadWrite();
						if (mysql.checkDuplicateName(p.getUniqueId(), name)) {
							p.sendMessage(ChatColor.RED + "You already have a claim with this name!");
						} else {
							Selection sel = getWorldEdit().getSelection(p);
							RegionContainer container = getWorldGuard().getRegionContainer();
							RegionManager claimRegions = container.get(claimWorld);
							RegionManager buildRegions = container.get(buildWorld);
							
							ProtectedRegion region = new ProtectedCuboidRegion(p.getUniqueId() + "," + name,
									new BlockVector(sel.getNativeMinimumPoint()), new BlockVector(sel.getNativeMaximumPoint()));
									
							DefaultDomain owners = new DefaultDomain();
							owners.addPlayer(getWorldGuard().wrapPlayer(p));

							region.setOwners(owners);

							claimRegions.addRegion(region);
							buildRegions.addRegion(region);
							
							mysql.addClaim(p.getUniqueId(), name);

							p.sendMessage(ChatColor.GREEN + "Created plot with ID: " + name);
						}
					} else {
						p.sendMessage(ChatColor.RED + "Please use a name that consists of 1 word and run the command again!");
					}
				} else {
					e.setWillClose(false);
					e.setWillDestroy(false);
				}
			}
			
			public void onInventoryClose(InventoryCloseEvent e) {
				p.sendMessage("Plot creation cancelled!");
			}
			
		},"Please input Plot Name!");
		
		ItemStack i = new ItemStack(Material.NAME_TAG);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("");
		i.setItemMeta(im);
		
		gui.setSlot(AnvilGui.AnvilSlot.INPUT_LEFT, i);
		
		gui.open();
		
		return true;
	}
}
