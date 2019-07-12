package me.uniodex.uniostats.managers;

import lombok.Getter;
import me.uniodex.uniostats.UnioStats;
import me.uniodex.uniostats.objects.StatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StatManager {

    private UnioStats plugin;
    @Getter
    private List<String> dataSavingPlayers = new ArrayList<>();
    @Getter
    private List<String> dataLoadingPlayers = new ArrayList<>();
    @Getter
    private Map<String, StatPlayer> players = new HashMap<>();

    public StatManager(UnioStats plugin) {
        this.plugin = plugin;

        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayer(p.getName());
        }

        Bukkit.getScheduler().runTaskTimer(plugin, saveTask(), 6000, 6000);
    }

    private Runnable saveTask() {
        return () -> {
            Bukkit.getLogger().log(Level.INFO, "[UnioStats] UnioStats verileri kaydediliyor...");
            for (Player p : Bukkit.getOnlinePlayers()) {
                players.get(p.getName()).saveData(false);
            }
            Bukkit.getLogger().log(Level.INFO, "[UnioStats] UnioStats verileri kaydedildi.");
        };
    }

    public void addPlayer(String player) {
        players.put(player, new StatPlayer(player, plugin));
    }

    public void removePlayer(String player) {
        players.get(player).saveData(false);
        players.remove(player);
    }

    public StatPlayer getPlayer(String player) {
        return players.get(player);
    }

    public void giveStat(String player, Stats stat, int amount) {
        if (players.containsKey(player)) {
            if (stat.equals(Stats.KILLS)) {
                players.get(player).setKills(players.get(player).getKills() + amount);
            }

            if (stat.equals(Stats.DEATHS)) {
                players.get(player).setDeaths(players.get(player).getDeaths() + amount);
            }

            if (stat.equals(Stats.MOB_KILLS)) {
                players.get(player).setMobKills(players.get(player).getMobKills() + amount);
            }

            if (stat.equals(Stats.BOSS_KILLS)) {
                players.get(player).setBossKills(players.get(player).getBossKills() + amount);
            }

            if (stat.equals(Stats.PLAYTIME)) {
                players.get(player).setPlayTime(players.get(player).getPlayTime() + amount);
            }

            if (stat.equals(Stats.GAPPLES_EATEN)) {
                players.get(player).setGapplesEaten(players.get(player).getGapplesEaten() + amount);
            }

            if (stat.equals(Stats.ARMORS_BROKE)) {
                players.get(player).setArmorsBroke(players.get(player).getArmorsBroke() + amount);
            }

            if (stat.equals(Stats.ARMORS_BROKEN)) {
                players.get(player).setArmorsBroken(players.get(player).getArmorsBroken() + amount);
            }
        }
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.get(p.getName()).saveData(true);
        }
    }

    public enum Stats {
        KILLS, DEATHS, MOB_KILLS, BOSS_KILLS, PLAYTIME, GAPPLES_EATEN,
        ARMORS_BROKEN, //Player's armor
        ARMORS_BROKE //Opponent's armor
    }

}
