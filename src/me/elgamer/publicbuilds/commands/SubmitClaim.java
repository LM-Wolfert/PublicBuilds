package me.elgamer.publicbuilds.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.elgamer.publicbuilds.gui.AnvilGui;
import me.elgamer.publicbuilds.gui.AnvilGui.AnvilClickEvent;
import me.elgamer.publicbuilds.mysql.MySQLReadWrite;

public class SubmitClaim implements CommandExecutor {

	Pattern wordPattern = Pattern.compile("\\w+");
	Matcher wordMatcher;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Check if the sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot submit a plot!");
			return true;
		}

		Player p = (Player) sender;
		String uuid = p.getUniqueId().toString();
		MySQLReadWrite mysql = new MySQLReadWrite();

		//Check if player owns a plot in both REGION_NAMES and DENIED_NAMES
		if (mysql.hasPlot(uuid) != true) {
			p.sendMessage(ChatColor.RED + "You do not own a plot!");
			return true;
		}

		//Close PlotGui and open AnvilGui
		p.closeInventory();

		AnvilGui gui = new AnvilGui(p, new AnvilGui.AnvilClickEventHandler(){

			@Override
			public void onAnvilClick(AnvilClickEvent e) {

				if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {
					e.setWillClose(true);
					e.setWillDestroy(true);
					String name = e.getName();
					String plotID = uuid + "," + name;
					wordMatcher = wordPattern.matcher(name);

					//Check if name is valid
					if (wordMatcher.matches()) {

						//Check if name is a plot you own
						if (mysql.checkDuplicateName(plotID)) {

							String plots = mysql.returnClaims(uuid);

							if (plots == null) {
								p.sendMessage(ChatColor.RED + "There is no plot with the name: " + name);
							} else {
								String[] names = plots.split(",");
								for (int i = 0; i < names.length; i++) {
									if (names[i].equals(name)) {
										
										/*If player has plots that were denied
										 * they first have to submit or remove those before
										 * continuing to submit new plots */
										if (mysql.returnDeniedClaims(uuid) != null && mysql.checkAttempt(plotID) == 1) {
											p.sendMessage(ChatColor.RED + "You must first submit/remove all plots that have been denied!");
										} else {

											//Submits claim in database
											mysql.submitClaim(uuid, name, plotID);

											p.sendMessage(ChatColor.RED + "Plot " + name + " has been submitted for review!");

											Bukkit.broadcast(ChatColor.GREEN + "A plot has been submitted!", "group.veteran");

											break;
										}
									}
								}

							}

						} else {
							p.sendMessage(ChatColor.RED + "There is no plot with the name: " + name);

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
