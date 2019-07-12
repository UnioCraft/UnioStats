package me.uniodex.uniostats.managers;

import me.uniodex.uniostats.UnioStats;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLManager {

    private ConnectionPoolManager pool;
    private String database;
    private String table;
    private UnioStats plugin;

    public SQLManager(UnioStats plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin, "UnioStatsPool");
        database = plugin.getConfig().getString("database.dbname");
        table = plugin.getConfig().getString("database.table");
        setupTable();
    }

    private void setupTable() {
        if (!tableExists(table)) {
            updateSQL("CREATE TABLE `" + table + "`(`id` int(11) NOT NULL, `player` varchar(20) NOT NULL, `kills` int(11) NOT NULL DEFAULT '0', `deaths` int(11) NOT NULL DEFAULT '0', `mobKills` int(11) NOT NULL DEFAULT '0', `bossKills` int(11) NOT NULL DEFAULT '0', `playTime` int(11) NOT NULL DEFAULT '0' COMMENT 'in seconds', `gapplesEaten` int(11) NOT NULL DEFAULT '0', `armorsBroke` int(11) NOT NULL DEFAULT '0', `armorsBroken` int(11) NOT NULL DEFAULT '0') ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            updateSQL("ALTER TABLE `" + table + "` ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `player` (`player`);");
            updateSQL("ALTER TABLE `" + table + "` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;");
        }
    }

    private boolean tableExists(String tableName) {
        try (Connection connection = pool.getConnection()) {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            return tables.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateSQL(String QUERY) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            int count = statement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateSQLAsync(String QUERY, Long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            try (Connection connection = pool.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(QUERY);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, delay);
    }

    private boolean playerExists(String player) {
        String QUERY = "SELECT * FROM `" + database + "`.`" + table + "` WHERE `player` = '" + player + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return res.getString("player") != null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createPlayer(String player) {
        if (!playerExists(player)) {
            return updateSQL("INSERT INTO `" + database + "`.`" + table + "` (`player`) VALUES ('" + player + "');");
        }
        return true;
    }

    public Map<String, Integer> getPlayerStats(String player, boolean createPlayer) {
        if (createPlayer) {
            if (!createPlayer(player)) {
                return null;
            }
        }

        String statsQuery = "SELECT * FROM `" + database + "`.`" + table + "` WHERE `player` = '" + player + "';";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(statsQuery);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                Map<String, Integer> stats = new HashMap<>();
                stats.put("kills", res.getInt("kills"));
                stats.put("deaths", res.getInt("deaths"));
                stats.put("mobKills", res.getInt("mobKills"));
                stats.put("bossKills", res.getInt("bossKills"));
                stats.put("playTime", res.getInt("playTime"));
                stats.put("gapplesEaten", res.getInt("gapplesEaten"));
                stats.put("armorsBroke", res.getInt("armorsBroke"));
                stats.put("armorsBroken", res.getInt("armorsBroken"));
                return stats;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePlayerStats(String player, int kills, int deaths, int mobKills, int bossKills, int playTime, int gapplesEaten, int armorsBroke, int armorsBroken, boolean sync) {
        if (!playerExists(player)) {
            updateSQL("INSERT INTO `" + database + "`.`" + table + "` (`player`, `kills`, `deaths`, `mobKills`, `bossKills`, `playTime`, `gapplesEaten`, `armorsBroke`, `armorsBroken`') " +
                    "VALUES ('" + player + "', '" + kills + "', '" + deaths + "', '" + mobKills + "', '" + bossKills + "', '" + playTime + "', '" + gapplesEaten + "', '" + armorsBroke + "', '" + armorsBroken + "');");
        } else {
            updateSQL("UPDATE `" + database + "`.`" + table + "` SET `kills` = '" + kills + "', `deaths` = '" + deaths + "', `mobKills` = '" + mobKills + "', `bossKills` = '" + bossKills + "', `playTime` = '" + playTime + "', `gapplesEaten` = '" + gapplesEaten + "', `armorsBroke` = '" + armorsBroke + "', `armorsBroken` = '" + armorsBroken + "' WHERE `" + table + "`.`player` = '" + player + "';");
        }

        if (!sync) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                while (plugin.getStatManager().getDataSavingPlayers().contains(player)) {
                    plugin.getStatManager().getDataSavingPlayers().remove(player);
                }
            });
        } else {
            while (plugin.getStatManager().getDataSavingPlayers().contains(player)) {
                plugin.getStatManager().getDataSavingPlayers().remove(player);
            }
        }
    }

    public void onDisable() {
        pool.closePool();
    }

}