package me.scruffyboy13.MSG.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.scruffyboy13.MSG.MSG;
import me.scruffyboy13.MSG.utils.StringUtils;

public class ReplyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			StringUtils.sendConfigMessage(sender, "messages.playersOnly");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("msg.reply")) {
			StringUtils.sendConfigMessage(player, "messages.nopermission");
			return true;
		}
		
		if (args.length < 1) {
			StringUtils.sendConfigMessage(player, "messages.reply.usage");
			return true;
		}
		
		if (!MSG.getReplies().containsKey(player.getUniqueId())) {
			StringUtils.sendConfigMessage(player, "messages.reply.noreplies");
			return true;
		}
		
		Player receiver = Bukkit.getPlayer(MSG.getReplies().get(player.getUniqueId()));
		
		if (receiver == null || MSG.isVanished(receiver)) {
			StringUtils.sendConfigMessage(player, "messages.reply.offline");
			return true;
		}
		
		String message = String.join(" ", args);
		MSG.message(player, receiver, message);
		
		return true;
		
	}
	
}
