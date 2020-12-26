package me.scruffyboy13.MSG.utils;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.scruffyboy13.MSG.MSG;

public class SQLUtils {

	public static void createTable() throws SQLException {
		Statement statement = MSG.getSQL().getConnection().createStatement();
		DatabaseMetaData md = MSG.getSQL().getConnection().getMetaData();
		statement.execute("CREATE TABLE IF NOT EXISTS MSG (UUID VARCHAR(36) NOT NULL);");
		for (Map.Entry<String, String> column : MSG.getSQLColumns().entrySet()) {
			if (!md.getColumns(null, null, "MSG", column.getKey()).next()) {
				statement.execute("ALTER TABLE MSG ADD " + column.getKey() + " " + column.getValue() + ";");
			}
		}
		statement.close();
	}

	public static void addPlayerToDatabase(Player player) throws SQLException {
		PreparedStatement statement = MSG.getSQL().getConnection().prepareStatement("INSERT INTO MSG "
				+ "(UUID, IgnoreList, DoNotDisturb, SocialSpy) VALUES (?, ?, ?, ?);");
		statement.setString(1, player.getUniqueId().toString());
		statement.setString(2, "");
		statement.setBoolean(3, false);
		statement.setBoolean(4, false);
		statement.executeUpdate();
		statement.close();
	}
	
	public static void updateIgnore(UUID uuid, List<UUID> ignore) throws SQLException {
		PreparedStatement statement = MSG.getSQL().getConnection().prepareStatement("UPDATE MSG SET "
				+ "IgnoreList=? WHERE UUID=?;");
		statement.setString(1, StringUtils.toString(ignore));
		statement.setString(2, uuid.toString());
		statement.executeUpdate();
		statement.close();
	}
	
	public static void updateDoNotDisturb(UUID uuid, boolean donotdisturb) throws SQLException {
		PreparedStatement statement = MSG.getSQL().getConnection().prepareStatement("UPDATE MSG SET "
				+ "DoNotDisturb=? WHERE UUID=?;");
		statement.setBoolean(1, donotdisturb);
		statement.setString(2, uuid.toString());
		statement.executeUpdate();
		statement.close();
	}
	
	public static void updateSocialSpy(UUID uuid, boolean socialspy) throws SQLException {
		PreparedStatement statement = MSG.getSQL().getConnection().prepareStatement("UPDATE MSG SET "
				+ "SocialSpy=? WHERE UUID=?;");
		statement.setBoolean(1, socialspy);
		statement.setString(2, uuid.toString());
		statement.executeUpdate();
		statement.close();
	}

	public static Map<UUID, List<UUID>> getIgnore() throws SQLException {
		Map<UUID, List<UUID>> ignore = new HashMap<UUID, List<UUID>>();
		Statement statement = MSG.getSQL().getConnection().createStatement();
		ResultSet result = statement.executeQuery("SELECT * FROM MSG;");
		while (result.next()) {
			ignore.put(UUID.fromString(result.getString("UUID")), StringUtils.fromString(result.getString("IgnoreList")));
		}
		return ignore;
	}

	public static Map<UUID, Boolean> getDoNotDisturb() throws SQLException {
		Map<UUID, Boolean> donotdisturb = new HashMap<UUID, Boolean>();
		Statement statement = MSG.getSQL().getConnection().createStatement();
		ResultSet result = statement.executeQuery("SELECT * FROM MSG;");
		while (result.next()) {
			donotdisturb.put(UUID.fromString(result.getString("UUID")), result.getBoolean("DoNotDisturb"));
		}
		return donotdisturb;
	}

	public static Map<UUID, Boolean> getSocialSpy() throws SQLException {
		Map<UUID, Boolean> socialspy = new HashMap<UUID, Boolean>();
		Statement statement = MSG.getSQL().getConnection().createStatement();
		ResultSet result = statement.executeQuery("SELECT * FROM MSG;");
		while (result.next()) {
			socialspy.put(UUID.fromString(result.getString("UUID")), result.getBoolean("SocialSpy"));
		}
		return socialspy;
	}

}
