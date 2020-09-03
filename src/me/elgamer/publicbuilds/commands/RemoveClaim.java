package me.elgamer.publicbuilds.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
//import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

//import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.gui.AnvilGui;
import me.elgamer.publicbuilds.gui.AnvilGui.AnvilClickEvent;
import me.elgamer.publicbuilds.mysql.MySQLReadWrite;

public class RemoveClaim implements CommandExecutor {
	
	//Prompt User with name
	
	//Check if name exists in database
	
	//Reset claim
	
	//Remove claim
	
	//private Main instance = Main.getInstance();
	//private FileConfiguration config = instance.getConfig();
	
	Pattern wordPattern = Pattern.compile("\\w+");
	Matcher wordMatcher;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot remove a plot!");
			return true;
		}
		Player p = (Player) sender;
		MySQLReadWrite mysql = new MySQLReadWrite();
		
		
		if (mysql.playerExists(p.getUniqueId()) == false || mysql.returnClaims(p.getUniqueId()) == null) {
			p.sendMessage(ChatColor.RED + "You do not own a plot!");
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
						if (mysql.checkDuplicateName(p.getUniqueId(), name)) {
							
							String regionName = p.getUniqueId() + "," + name;		
							WorldEditPlugin wep = getWorldEdit();
							
							World claimWorld = Bukkit.getServer().getWorld("claimWorld");
							World buildWorld = Bukkit.getServer().getWorld("buildWorld");
							
							RegionContainer container = getWorldGuard().getRegionContainer();
							RegionManager claimRegions = container.get(claimWorld);
							RegionManager buildRegions = container.get(buildWorld);
							
							ProtectedRegion region = claimRegions.getRegion(regionName);
							
							BlockVector pos1 = region.getMinimumPoint();
							BlockVector pos2 = region.getMaximumPoint();
							
							LocalSession session = wep.getSession(p);
							com.sk89q.worldedit.world.World c = new BukkitWorld(claimWorld);
							com.sk89q.worldedit.world.World b = new BukkitWorld(buildWorld);
							EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(c, -1);
							CuboidRegion selection = new CuboidRegion(c, pos1, pos2);
							
							try {
								BlockArrayClipboard cb = new BlockArrayClipboard(selection);
								ForwardExtentCopy copy = new ForwardExtentCopy(editSession, selection, cb, pos1);
								Operations.completeLegacy(copy);
								editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(b, -1);
								ClipboardHolder holder = new ClipboardHolder(cb, b.getWorldData());
								session.setClipboard(holder);
								
								Operation operation = holder.createPaste(editSession, b.getWorldData()).to(pos1)
										.ignoreAirBlocks(false).build();
								Operations.completeLegacy(operation);
							} catch (MaxChangedBlocksException ex) {
								ex.printStackTrace();
							}
							
							mysql.removeClaim(p.getUniqueId(), name);
							claimRegions.removeRegion(regionName);
							buildRegions.removeRegion(regionName);
							try {
								claimRegions.save();
								buildRegions.save();
							} catch (StorageException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							p.sendMessage(ChatColor.RED + "Plot " + name + " has been removed!");
								
							} else {
								p.sendMessage(ChatColor.RED + "There is no plot with the name: " + name);
							
							
							
							
						}
							//Reset claim
							//Remove claim
					} else {
						p.sendMessage(ChatColor.RED + "Please use a name that consists of 1 word and run the command again!");
					}
				} else {
					e.setWillClose(false);
					e.setWillDestroy(false);
				}
			}
			
			@SuppressWarnings("unused")
			public void onInventoryClose(InventoryCloseEvent e) {
				p.sendMessage("Plot deletion cancelled!");
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
	
}
