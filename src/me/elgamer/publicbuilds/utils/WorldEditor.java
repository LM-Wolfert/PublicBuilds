package me.elgamer.publicbuilds.utils;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector2D;
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
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

import me.elgamer.publicbuilds.Main;

public class WorldEditor {

	public static boolean updateWorld(Player p, List<BlockVector2D> vector, World copy, World paste) {

		//Get instance of worldedit from Main
		WorldEditPlugin wep = Main.getWorldEdit();
		
		//Get session of player
		LocalSession session = wep.getSession(p);

		//Get the worlds in worldEdit format
		com.sk89q.worldedit.world.World copyWorld = new BukkitWorld(copy);
		com.sk89q.worldedit.world.World pasteWorld = new BukkitWorld(paste);

		//Create editSession in the world which will be copied from
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(copyWorld, -1);
		Polygonal2DRegion selection = new Polygonal2DRegion(copyWorld, vector, 1, 256);

		try {

			//Create clipboard of region
			BlockArrayClipboard cb = new BlockArrayClipboard(selection);
			ForwardExtentCopy c = new ForwardExtentCopy(editSession, selection, cb, selection.getMinimumPoint());
			Operations.completeLegacy(c);

			//Create editSession in the world which will be pasted in
			editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(pasteWorld, -1);
			ClipboardHolder holder = new ClipboardHolder(cb, pasteWorld.getWorldData());
			session.setClipboard(holder);

			//Paste clipboard of region
			Operation operation = holder.createPaste(editSession, pasteWorld.getWorldData()).to(selection.getMinimumPoint())
					.ignoreAirBlocks(false).build();
			Operations.completeLegacy(operation);

			return true;
		} catch (MaxChangedBlocksException ex) {
			ex.printStackTrace();
			return false;
		}	

	}

}
