package me.elgamer.publicbuilds.reviewing;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

import me.elgamer.publicbuilds.Main;

public class EditBook implements Listener {
	
	Review review;
	
	public EditBook(Main main, Review review) {

		Bukkit.getServer().getPluginManager().registerEvents(this, main);
		this.review = review;
			
	}

	@EventHandler
	public void onBookEdit(PlayerEditBookEvent e) {
		
		if (e.isSigning()) {
			e.getPlayer().closeInventory();
			e.getPlayer().openInventory(ReviewGui.GUI(Main.getInstance().getUser(e.getPlayer())));
		}
		
		review.bookMeta = e.getNewBookMeta();
		
	}
}
