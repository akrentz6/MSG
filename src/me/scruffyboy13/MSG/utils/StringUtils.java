package me.scruffyboy13.MSG.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import me.scruffyboy13.MSG.MSG;

public class StringUtils {

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static void sendMessage(CommandSender sender, List<String> message) {
		for (String line : message) {
			sender.sendMessage(color(line));
		}
	}

	public static void sendMessage(Player player, List<String> message) {
		for (String line : message) {
			player.sendMessage(color(line));
		}
	}
	
	public static void sendMessage(Player player, List<String> message, ImmutableMap<String, String> placeholders) {
		for (String line : message) {
			for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
				line = line.replace(placeholder.getKey(), placeholder.getValue());
			}
			player.sendMessage(color(line));
		}
	}
	
	public static void sendMessage(CommandSender sender, List<String> message, ImmutableMap<String, String> placeholders) {
		for (String line : message) {
			for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
				line = line.replace(placeholder.getKey(), placeholder.getValue());
			}
			sender.sendMessage(color(line));
		}
	}
	
	public static void sendConfigMessage(CommandSender sender, String path) {
		for (String line : MSG.getInstance().getConfig().getStringList(path)) {
			sender.sendMessage(color(line));
		}
	}
	
	public static void sendConfigMessage(Player player, String path) {
		for (String line : MSG.getInstance().getConfig().getStringList(path)) {
			player.sendMessage(color(line));
		}
	}

	public static void sendConfigMessage(Player player, String path, ImmutableMap<String, String> placeholders) {
		for (String line : MSG.getInstance().getConfig().getStringList(path)) {
			for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
				line = line.replace(placeholder.getKey(), placeholder.getValue());
			}
			player.sendMessage(color(line));
		}
	}

	public static void sendConfigMessage(CommandSender sender, String path, ImmutableMap<String, String> placeholders) {
		for (String line : MSG.getInstance().getConfig().getStringList(path)) {
			for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
				line = line.replace(placeholder.getKey(), placeholder.getValue());
			}
			sender.sendMessage(color(line));
		}
	}

	public static String toString(List<UUID> uuids) {
		if (uuids.isEmpty()) {
			return "";
		}
		List<String> strings = new ArrayList<String>();
		for (UUID uuid : uuids) {
			strings.add(uuid.toString());
		}
		return String.join(";", strings);
	}

	public static List<UUID> fromString(String string) {
		if (string.equals("")) {
			return new ArrayList<UUID>();
		}
		List<UUID> uuids = new ArrayList<UUID>();
		for (String uuid : string.split(";")) {
			uuids.add(UUID.fromString(uuid));
		}
		return uuids;
	}

}
