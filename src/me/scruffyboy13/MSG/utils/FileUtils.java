package me.scruffyboy13.MSG.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.scruffyboy13.MSG.MSG;

public class FileUtils {

	public static void addPlayerToFiles(Player player) {
		FileConfiguration config = MSG.getData().getConfig();
		UUID uuid = player.getUniqueId();
		config.createSection("data." + player.getUniqueId().toString());
		config.set("data." + uuid.toString() + ".Ignore", "");
		config.set("data." + uuid.toString() + ".DoNotDisturb", false);
		config.set("data." + uuid.toString() + ".SocialSpy", false);
		MSG.getData().saveConfig();
	}

	public static void updateIgnore(UUID uuid, List<UUID> ignore) {
		MSG.getData().getConfig().set("data." + uuid.toString() + ".Ignore", StringUtils.toString(ignore));
		MSG.getData().saveConfig();
	}

	public static void updateDoNotDisturb(UUID uuid, Boolean donotdisturb) {
		MSG.getData().getConfig().set("data." + uuid.toString() + ".DoNotDisturb", donotdisturb);
		MSG.getData().saveConfig();
	}

	public static void updateSocialSpy(UUID uuid, Boolean socialspy) {
		MSG.getData().getConfig().set("data." + uuid.toString() + ".SocialSpy", socialspy);
		MSG.getData().saveConfig();
	}

	public static Map<UUID, List<UUID>> getIgnore() {
		Map<UUID, List<UUID>> ignore = new HashMap<UUID, List<UUID>>();
		FileConfiguration config = MSG.getData().getConfig();
		for (String uuid : config.getConfigurationSection("data").getKeys(false)) {
			ignore.put(UUID.fromString(uuid), StringUtils.fromString(config.getString("data." + uuid + ".Ignore")));
		}
		return ignore;
	}

	public static Map<UUID, Boolean> getDoNotDisturb() {
		Map<UUID, Boolean> donotdisturb = new HashMap<UUID, Boolean>();
		FileConfiguration config = MSG.getData().getConfig();
		for (String uuid : config.getConfigurationSection("data").getKeys(false)) {
			donotdisturb.put(UUID.fromString(uuid), config.getBoolean("data." + uuid + ".DoNotDisturb"));
		}
		return donotdisturb;
	}

	public static Map<UUID, Boolean> getSocialSpy() {
		Map<UUID, Boolean> socialspy = new HashMap<UUID, Boolean>();
		FileConfiguration config = MSG.getData().getConfig();
		for (String uuid : config.getConfigurationSection("data").getKeys(false)) {
			socialspy.put(UUID.fromString(uuid), config.getBoolean("data." + uuid + ".SocialSpy"));
		}
		return socialspy;
	}

}
