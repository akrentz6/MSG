package me.scruffyboy13.MSG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ImmutableMap;

import me.scruffyboy13.MSG.commands.DoNotDisturbCommand;
import me.scruffyboy13.MSG.commands.IgnoreCommand;
import me.scruffyboy13.MSG.commands.MessageCommand;
import me.scruffyboy13.MSG.commands.ReplyCommand;
import me.scruffyboy13.MSG.commands.SocialSpyCommand;
import me.scruffyboy13.MSG.listeners.PlayerJoinListener;
import me.scruffyboy13.MSG.utils.FileUtils;
import me.scruffyboy13.MSG.utils.SQLUtils;
import me.scruffyboy13.MSG.utils.StringUtils;

public class MSG extends JavaPlugin {

	private static MSG instance;
	private static MySQL sql;
	private static DataManager data;
	private static Map<String, String> sqlColumns = new HashMap<>();
	private static Map<UUID, UUID> replies = new HashMap<UUID, UUID>();
	private static Map<UUID, List<UUID>> ignore = new HashMap<UUID, List<UUID>>();
	private static Map<UUID, Boolean> donotdisturb = new HashMap<UUID, Boolean>();
	private static Map<UUID, Boolean> socialspy = new HashMap<UUID, Boolean>();
	
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		
		sqlColumns.put("IgnoreList", "LONGTEXT NOT NULL");
		sqlColumns.put("DoNotDisturb", "BOOLEAN NOT NULL DEFAULT 0");
		sqlColumns.put("SocialSpy", "BOOLEAN NOT NULL DEFAULT 0");
		
		instance = this;
		
		this.getCommand("message").setExecutor(new MessageCommand());
		this.getCommand("reply").setExecutor(new ReplyCommand());
		this.getCommand("ignore").setExecutor(new IgnoreCommand());
		this.getCommand("donotdisturb").setExecutor(new DoNotDisturbCommand());
		this.getCommand("socialspy").setExecutor(new SocialSpyCommand());
		
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		
		if (getConfig().getBoolean("mysql.use-mysql")) {
			
			sql = new MySQL(
					getConfig().getString("mysql.host"), 
					getConfig().getInt("mysql.port"), 
					getConfig().getString("mysql.database"), 
					getConfig().getString("mysql.username"), 
					getConfig().getString("mysql.password"));
			
			connectToSQL();
			
			if (sql.isConnected()) {
				try {
					SQLUtils.createTable();
					ignore = SQLUtils.getIgnore();
					donotdisturb = SQLUtils.getDoNotDisturb();
					socialspy = SQLUtils.getSocialSpy();
				} catch (SQLException e) {
					this.getLogger().warning("There was an error with getting player settings from your mysql database.");
					Bukkit.getPluginManager().disablePlugin(this);
					return;
				}
				
			}
			else {
				return;
			}
			
		}
		else {
			
			Path dataDir = Paths.get(getDataFolderPath() + "/data/");
			if (!Files.exists(dataDir)) {
				try {
					Files.createDirectory(dataDir);
				} catch (IOException e) {
					this.getLogger().warning("There was an error creating the data directory.");
		            this.getServer().getPluginManager().disablePlugin(this);
		            return;
				}
			}
			
			data = new DataManager("data.yml", getDataFolder() + "/data/");
			if (!data.getConfig().isConfigurationSection("data")) {
				data.getConfig().createSection("data");
				data.saveConfig();
			}
			ignore = FileUtils.getIgnore();
			donotdisturb = FileUtils.getDoNotDisturb();
			socialspy = FileUtils.getSocialSpy();
			
		}
		
	}

	public String getDataFolderPath() {
		return this.getDataFolder().getAbsolutePath();
	}

	public void connectToSQL() {
		try {
			sql.connect();
            this.getLogger().info("Successfully connected to mysql database.");
        } 
        catch (SQLException e) {
			this.getLogger().warning("There was an error connecting to the database. " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        catch (ClassNotFoundException e) {
        	this.getLogger().warning("The MySQL driver class could not be found.");
        	Bukkit.getPluginManager().disablePlugin(this);
        	return;
        }
	}
	
	@Override
	public void onDisable() {

	}

	public static MSG getInstance() {
		return instance;
	}

	public static Map<UUID, UUID> getReplies() {
		return replies;
	}
	
	public static void setReplies(Map<UUID, UUID> replies) {
		MSG.replies = replies;
	}

	public static Map<UUID, List<UUID>> getIgnore() {
		return ignore;
	}

	public static void setIgnore(Map<UUID, List<UUID>> ignore) {
		MSG.ignore = ignore;
	}

	public static Map<UUID, Boolean> getDonotdisturb() {
		return donotdisturb;
	}

	public static void setDonotdisturb(Map<UUID, Boolean> donotdisturb) {
		MSG.donotdisturb = donotdisturb;
	}

	public static Map<UUID, Boolean> getSocialspy() {
		return socialspy;
	}

	public static void setSocialspy(Map<UUID, Boolean> socialspy) {
		MSG.socialspy = socialspy;
	}

	public static DataManager getData() {
		return data;
	}
	
	public static MySQL getSQL() {
		return sql;
	}

	public static Map<String, String> getSQLColumns() {
		return sqlColumns;
	}
	
	public static boolean isVanished(Player p) {
        for (MetadataValue meta : p.getMetadata("vanished")) {
            if (meta.asBoolean()) 
            	return true;
        }
        return false;
	}

	public static void message(Player sender, Player receiver, String message) {

		if (MSG.getDonotdisturb().get(receiver.getUniqueId()) == true || 
				MSG.getIgnore().get(receiver.getUniqueId()).contains(sender.getUniqueId()) ||
				sender.equals(receiver)) {
			StringUtils.sendConfigMessage(sender, "messages.cannotMessage");
			return;
		}
		
		MSG.getReplies().put(receiver.getUniqueId(), sender.getUniqueId());
		
		StringUtils.sendConfigMessage(sender, "messages.sent", ImmutableMap.of(
				"%receiver%", receiver.getName(), 
				"%message%", message));
		StringUtils.sendConfigMessage(receiver, "messages.received", ImmutableMap.of(
				"%sender%", sender.getName(), 
				"%message%", message));
		
		for (Map.Entry<UUID, Boolean> player : socialspy.entrySet()) {
			if (player.getValue() == true) {
				Player ss = Bukkit.getPlayer(player.getKey());
				if (ss != null && !ss.equals(sender) && !ss.equals(receiver)) {
					StringUtils.sendConfigMessage(ss, "messages.socialspyMessage", ImmutableMap.of(
							"%sender%", sender.getName(), 
							"%receiver%", receiver.getName(), 
							"%message%", message));
				}
			}
		}
		
	}
	
}
