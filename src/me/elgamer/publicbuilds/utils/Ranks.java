package me.elgamer.publicbuilds.utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.elgamer.publicbuilds.mysql.PlayerData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.track.Track;

public class Ranks {

	public static void checkRankup(String uuid) {

		LuckPerms api = LuckPermsProvider.get();
		Track builder = api.getTrackManager().getTrack("builder");
		UserManager userManager = api.getUserManager();
		CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(UUID.fromString(uuid));

		int points = PlayerData.getPoints(uuid);
		String role = PlayerData.getRole(uuid);

		if (points >= 100 && role.equals("jrbuilder")) {
			
		} else if (points >= 40 && role.equals("apprentice")) {

		} else if (points >= 10 && role.equals("guest")) {

		} else {
			return;
		}

		userFuture.thenAcceptAsync(user -> {
			builder.promote(user, null);
		});
		
		PlayerData.promoteRole(uuid, role);
	}

}
