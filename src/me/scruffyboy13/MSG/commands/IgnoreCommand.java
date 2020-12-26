package me.scruffyboy13.MSG.commands;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import me.scruffyboy13.MSG.MSG;
import me.scruffyboy13.MSG.utils.FileUtils;
import me.scruffyboy13.MSG.utils.SQLUtils;
import me.scruffyboy13.MSG.utils.StringUtils;

public class IgnoreCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			StringUtils.sendConfigMessage(sender, "messages.playersOnly");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("msg.ignore")) {
			StringUtils.sendConfigMessage(player, "messages.nopermission");
			return true;
		}
		
		if (args.length < 2) {
			StringUtils.sendConfigMessage(player, "messages.ignore.usage");
			return true;
		}

		OfflinePlayer ignore = Bukkit.getOfflinePlayer(args[1]);
		
		if (ignore == null) {
			StringUtils.sendConfigMessage(player, "messages.ignore.doesntExist");
			return true;
		}
		
		UUID playerUUID = player.getUniqueId();
		UUID ignoreUUID = ignore.getUniqueId();
		

		if (args[0].equalsIgnoreCase("add")) {
			if (!MSG.getIgnore().get(playerUUID).contains(ignoreUUID)) {
				MSG.getIgnore().get(playerUUID).add(ignoreUUID);
				StringUtils.sendConfigMessage(player, "messages.ignore.added", ImmutableMap.of(
						"%player%", ignore.getName()));
			}
			else {
				StringUtils.sendConfigMessage(player, "messages.ignore.alreadyIgnored");
				return true;				
			}
		}
		else if (args[0].equalsIgnoreCase("remove")) {
			if (MSG.getIgnore().get(playerUUID).contains(ignoreUUID)) {
				MSG.getIgnore().get(playerUUID).remove(ignore.getUniqueId());
				StringUtils.sendConfigMessage(player, "messages.ignore.removed", ImmutableMap.of(
						"%player%", ignore.getName()));
			}
			else {
				StringUtils.sendConfigMessage(player, "messages.ignore.notIgnored");
				return true;
			}
		}
		else {
			StringUtils.sendConfigMessage(player, "messages.ignore.usage");
			return true;
		}
		
		if (MSG.getInstance().getConfig().getBoolean("mysql.use-mysql")) {
			try {
				SQLUtils.updateIgnore(playerUUID, MSG.getIgnore().get(playerUUID));
			} catch (SQLException e) {
				MSG.getInstance().getLogger().warning("There was an error updating a player's settings to the database.");
				Bukkit.getPluginManager().disablePlugin(MSG.getInstance());
				return true;
			}
		}
		else {
			FileUtils.updateIgnore(playerUUID, MSG.getIgnore().get(playerUUID));
		}

		return true;
		
	}
	
}
