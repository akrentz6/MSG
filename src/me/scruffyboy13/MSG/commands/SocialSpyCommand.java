package me.scruffyboy13.MSG.commands;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.scruffyboy13.MSG.MSG;
import me.scruffyboy13.MSG.utils.FileUtils;
import me.scruffyboy13.MSG.utils.SQLUtils;
import me.scruffyboy13.MSG.utils.StringUtils;

public class SocialSpyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			StringUtils.sendConfigMessage(sender, "messages.playersOnly");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("msg.socialspy")) {
			StringUtils.sendConfigMessage(player, "messages.nopermission");
			return true;
		}
		
		UUID playerUUID = player.getUniqueId();
		
		if (MSG.getSocialspy().get(playerUUID)) {
			MSG.getSocialspy().put(playerUUID, false);
			StringUtils.sendConfigMessage(player, "messages.socialspy.disabled");
		}
		else {
			MSG.getSocialspy().put(playerUUID, true);
			StringUtils.sendConfigMessage(player, "messages.socialspy.enabled");
		}
		
		if (MSG.getInstance().getConfig().getBoolean("mysql.use-mysql")) {
			try {
				SQLUtils.updateSocialSpy(playerUUID, MSG.getSocialspy().get(playerUUID));
			} catch (SQLException e) {
				MSG.getInstance().getLogger().warning("There was an error updating a player's settings to the database.");
				Bukkit.getPluginManager().disablePlugin(MSG.getInstance());
				return true;
			}
		}
		else {
			FileUtils.updateSocialSpy(playerUUID, MSG.getSocialspy().get(playerUUID));
		}
		
		return true;
		
	}
	
}
