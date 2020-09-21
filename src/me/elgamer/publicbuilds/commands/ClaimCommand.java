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
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.AnvilGui;
import me.elgamer.publicbuilds.gui.AnvilGui.AnvilClickEvent;
import me.elgamer.publicbuilds.mysql.MySQLReadWrite;
import me.elgamer.publicbuilds.utils.PlotTeleport;

@SuppressWarnings("unused")
public class ClaimCommand implements CommandExecutor {
	
	private Main instance = Main.getInstance();
	private FileConfiguration config = instance.getConfig();
		
	//Area limits as defined in the config.yml	
	private int guestArea = config.getInt("guestArea");
	private int apprenticeArea = config.getInt("apprenticeArea");
	private int builderArea = config.getInt("builderArea");
	
	//Plot limits as defined in the config.yml
	private int guestLimit = config.getInt("guestLimit");
	private int apprenticeLimit = config.getInt("apprenticeLimit");
	private int builderLimit = config.getInt("builderLimit");

	private int area, width, length, i, claimCount = 0;
	
	//Get claimWorld and buildWorld name from config.yml
	private String defaultWorld = config.getString("defaultWorld");
	private String editWorld = config.getString("editWorld");
	
	private String plotName;

	Pattern wordPattern = Pattern.compile("\\w+");
	Matcher wordMatcher;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Check is command sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot create a plot!");
			return true;
		}
		
		//Convert sender to player
		Player p = (Player) sender;
		
		String uuid = p.getUniqueId().toString();
		
		MySQLReadWrite mysql = new MySQLReadWrite();
		
		//Check if player is not over the plot limit
		if (mysql.playerExists(uuid)) {
			String claims = mysql.returnClaims(uuid);
			if (claims != null) {
				String[] names = claims.split(",");
				claimCount = names.length;
			}
		}

		//Get users PrimaryGroup to create a region with the correct limits.
		switch (Main.getPermissions().getPrimaryGroup("claimWorld",p)) {
		
		case "veteran":
			if (claimCount >= builderLimit) {
				p.sendMessage(ChatColor.RED + "You have reached the maximum number of plots, submit or remove an existing plot to be able to create a new one.");
				return true;
			} else {
				return createRegion(builderArea, p);
			}
		
		case "builder":
			if (claimCount >= builderLimit) {
				p.sendMessage(ChatColor.RED + "You have reached the maximum number of plots, submit or remove an existing plot to be able to create a new one.");
				return true;
			} else {
				return createRegion(builderArea, p);
			}
			
		case "apprentice":
			if (claimCount >= apprenticeLimit) {
				p.sendMessage(ChatColor.RED + "You have reached the maximum number of plots, submit or remove an existing plot to be able to create a new one.");
				return true;
			} else {
				return createRegion(apprenticeArea, p);
			}
			
		case "default":
			if (claimCount >= guestLimit) {
				p.sendMessage(ChatColor.RED + "You have reached the maximum number of plots, submit or remove an existing plot to be able to create a new one.");
			} else {
				return createRegion(guestArea, p);
			}
				
		}
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

		if (getArea(sel) > maxArea) {
			p.sendMessage(ChatColor.RED + "Your selection exceeds the maximum of " + maxArea + " blocks");
			return true;
		}

		World claimWorld = Bukkit.getServer().getWorld(defaultWorld);
		World buildWorld = Bukkit.getServer().getWorld(editWorld);

		RegionContainer container = getWorldGuard().getRegionContainer();
		RegionManager claimRegions = container.get(claimWorld);

		//Create testRegion to check if there is any overlap, if true cancel plot creation
		ProtectedRegion testRegion = new ProtectedCuboidRegion("testRegion",
				new BlockVector(sel.getNativeMinimumPoint()), new BlockVector(sel.getNativeMaximumPoint()));

		ApplicableRegionSet set = claimRegions.getApplicableRegions(testRegion);
		
		if (set.size() != 0) {
			p.sendMessage(ChatColor.RED + "Your selection overlaps with another claim!");
			return true;
		}
		
		//Close PlotGui to prompt user with AnvilGui to input name
		p.closeInventory();

		AnvilGui gui = new AnvilGui(p, new AnvilGui.AnvilClickEventHandler(){
			
			@Override
			public void onAnvilClick(AnvilClickEvent e) {
				if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {
					
					e.setWillClose(true);
					e.setWillDestroy(true);
					String name = e.getName();
					wordMatcher = wordPattern.matcher(name);
					
					//Check if name contains 1 word and only alphabetical characters
					if (wordMatcher.matches()) {
						
						MySQLReadWrite mysql = new MySQLReadWrite();
						String uuid = p.getUniqueId().toString();
						String plotID = uuid + "," + name;	
						
						if (mysql.checkDuplicateName(plotID)) {
							
							p.sendMessage(ChatColor.RED + "You already have a claim with this name!");
							
						} else {
							
							Selection sel = getWorldEdit().getSelection(p);
							RegionContainer container = getWorldGuard().getRegionContainer();
							RegionManager claimRegions = container.get(claimWorld);
							RegionManager buildRegions = container.get(buildWorld);
							
							ProtectedRegion region = new ProtectedCuboidRegion(plotID,
									new BlockVector(sel.getNativeMinimumPoint()), new BlockVector(sel.getNativeMaximumPoint()));
									
							DefaultDomain owners = new DefaultDomain();
							owners.addPlayer(getWorldGuard().wrapPlayer(p));

							region.setOwners(owners);

							claimRegions.addRegion(region);
							buildRegions.addRegion(region);
							
							//Save the new regions
							try {
								claimRegions.save();
								buildRegions.save();
							} catch (StorageException e1) {
								e1.printStackTrace();
							}
							
							//The regions have been created ingame, now they need to be added to the database
							mysql.addClaim(uuid, name, plotID, (int) sel.getMinimumPoint().getX(), (int) sel.getMinimumPoint().getZ(),
									(int) sel.getMaximumPoint().getX(), (int) sel.getMaximumPoint().getZ());

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
