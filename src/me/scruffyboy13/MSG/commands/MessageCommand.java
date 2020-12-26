package me.scruffyboy13.MSG.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.scruffyboy13.MSG.MSG;
import me.scruffyboy13.MSG.utils.StringUtils;

public class MessageCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			StringUtils.sendConfigMessage(sender, "messages.playersOnly");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("msg.message")) {
			StringUtils.sendConfigMessage(player, "messages.nopermission");
			return true;
		}
		
		if (args.length < 2) {
			StringUtils.sendConfigMessage(player, "messages.message.usage");
			return true;
		}
		
		Player receiver = Bukkit.getPlayer(args[0]);
		
		if (receiver == null || MSG.isVanished(receiver)) {
			StringUtils.sendConfigMessage(player, "messages.message.offline");
			return true;
		}

		String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		MSG.message(player, receiver, message);

		return true;
		
	}

}
