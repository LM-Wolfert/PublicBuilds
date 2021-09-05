package me.elgamer.publicbuilds.reviewing;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import me.elgamer.publicbuilds.Main;
import me.elgamer.publicbuilds.utils.Accept;

public class Review {
	
	public int plot;
	public String reasonType;
	public Accept accept;
	
	//Feedback book
	public ItemStack book;
	public BookMeta bookMeta;
	
	public Review(int plot) {
		
		this.plot = plot;
		
		book = new ItemStack(Material.WRITABLE_BOOK);
		bookMeta = (BookMeta) book.getItemMeta();

		new EditBook(Main.getInstance(), this);		
		
	}

}
