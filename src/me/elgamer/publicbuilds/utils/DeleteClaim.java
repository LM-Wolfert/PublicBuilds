package me.elgamer.publicbuilds.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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

import me.elgamer.publicbuilds.Main;

public class DeleteClaim {

	private Main instance = Main.getInstance();
	private FileConfiguration config = instance.getConfig();
	
	//Get claimWorld and buildWorld name from config.yml
	private String defaultWorld = config.getString("defaultWorld");
	private String editWorld = config.getString("editWorld");

	public void removeClaim(Player p, String plotID) {

		WorldEditPlugin wep = getWorldEdit();

		World claimWorld = Bukkit.getServer().getWorld(defaultWorld);
		World buildWorld = Bukkit.getServer().getWorld(editWorld);

		RegionContainer container = getWorldGuard().getRegionContainer();
		RegionManager claimRegions = container.get(claimWorld);
		RegionManager buildRegions = container.get(buildWorld);

		ProtectedRegion region = claimRegions.getRegion(plotID);

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

		claimRegions.removeRegion(plotID);
		buildRegions.removeRegion(plotID);
		try {
			claimRegions.save();
			buildRegions.save();
		} catch (StorageException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
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
