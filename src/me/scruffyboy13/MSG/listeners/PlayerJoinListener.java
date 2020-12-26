package me.scruffyboy13.MSG.listeners;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.scruffyboy13.MSG.MSG;
import me.scruffyboy13.MSG.utils.FileUtils;
import me.scruffyboy13.MSG.utils.SQLUtils;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if (!MSG.getDonotdisturb().keySet().contains(event.getPlayer().getUniqueId())) {
			
			if (MSG.getInstance().getConfig().getBoolean("mysql.use-mysql")) {
				try {
					SQLUtils.addPlayerToDatabase(event.getPlayer());
				} catch (SQLException e) {
					MSG.getInstance().getLogger().warning("There was an error adding a player to the database.");
					Bukkit.getPluginManager().disablePlugin(MSG.getInstance());
					return;
				}
			}
			else {
				FileUtils.addPlayerToFiles(event.getPlayer());
			}
			
			MSG.getIgnore().put(event.getPlayer().getUniqueId(), new ArrayList<UUID>());
			MSG.getDonotdisturb().put(event.getPlayer().getUniqueId(), false);
			MSG.getSocialspy().put(event.getPlayer().getUniqueId(), false);
			
		}
		
	}
	
}
