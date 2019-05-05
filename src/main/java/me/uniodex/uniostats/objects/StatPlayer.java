package me.uniodex.uniostats.objects;

import lombok.Getter;
import lombok.Setter;
import me.uniodex.uniostats.UnioStats;
import me.uniodex.uniostats.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatPlayer {

    private String playerName;
    private UnioStats plugin;

    // Stats
    @Getter
    @Setter
    private int kills;
    @Getter
    @Setter
    private int deaths;
    @Getter
    @Setter
    private int mobKills;
    @Getter
    @Setter
    private int bossKills;
    @Getter
    @Setter
    private int playTime;
    @Getter
    @Setter
    private long lastPlayTime;
    @Getter
    @Setter
    private int gapplesEaten;
    @Getter
    @Setter
    private int armorsBroke; //Opponent's armor
    @Getter
    @Setter
    private int armorsBroken; // Player's armor

    public StatPlayer(String playerName, UnioStats plugin) {
        this.playerName = playerName;
        this.plugin = plugin;
        lastPlayTime = System.currentTimeMillis();
        loadData();
    }

    private void loadData() {
        if (!plugin.getStatManager().getDataLoadingPlayers().contains(playerName) && !plugin.getStatManager().getDataSavingPlayers().contains(playerName)) {
            plugin.getStatManager().getDataLoadingPlayers().add(playerName);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Map<String, Integer> stats = plugin.getSqlManager().getPlayerStats(playerName, true);
                if (stats == null) {
                    Player p = Bukkit.getPlayerExact(playerName);
                    if (p != null) {
                        Utils.kickSync(plugin, playerName, "İstatistikleriniz yüklenirken bir sorun oluştuğu için atıldınız. Lütfen tekrar giriş yapınız. (Sorun ID: 1)");
                    }
                    plugin.getStatManager().getDataLoadingPlayers().remove(playerName);
                    return;
                }
                kills = stats.get("kills");
                deaths = stats.get("deaths");
                mobKills = stats.get("mobKills");
                bossKills = stats.get("bossKills");
                playTime = stats.get("playTime");
                gapplesEaten = stats.get("gapplesEaten");
                armorsBroke = stats.get("armorsBroke");
                armorsBroken = stats.get("armorsBroken");
                plugin.getStatManager().getDataLoadingPlayers().remove(playerName);
            });
        } else {
            Player p = Bukkit.getPlayerExact(playerName);
            if (p != null) {
                Utils.kickSync(plugin, playerName, "İstatistikleriniz yüklenirken bir sorun oluştuğu için atıldınız. Lütfen tekrar giriş yapınız. (Sorun ID: 2,"+plugin.getStatManager().getDataLoadingPlayers().contains(playerName)+","+plugin.getStatManager().getDataSavingPlayers().contains(playerName)+")");
            }
        }
    }

    public void saveData(boolean sync) {
        if (!plugin.getStatManager().getDataSavingPlayers().contains(playerName) && !plugin.getStatManager().getDataLoadingPlayers().contains(playerName)) {
            plugin.getStatManager().getDataSavingPlayers().add(playerName);
            updatePlayTime();

            if (sync) {
                plugin.getSqlManager().savePlayerStats(playerName, kills, deaths, mobKills, bossKills, playTime, gapplesEaten, armorsBroke, armorsBroken);
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqlManager().savePlayerStats(playerName, kills, deaths, mobKills, bossKills, playTime, gapplesEaten, armorsBroke, armorsBroken));

            }
        }
    }

    public void updatePlayTime() {
        playTime += (System.currentTimeMillis() - lastPlayTime) / 1000;
        lastPlayTime = System.currentTimeMillis();
    }

}
