package me.elgamer.publicbuilds.reviewing;

import org.bukkit.ChatColor;
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
	
	public ItemStack previousItem;
	
	public EditBook editBook;
	
	public Review(int plot) {
		
		this.plot = plot;
		
		accept = new Accept();
		
		book = new ItemStack(Material.WRITABLE_BOOK);
		bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setDisplayName(ChatColor.GREEN + "Feedback");

		editBook = new EditBook(Main.getInstance(), this);	
		
	}

}
