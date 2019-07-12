package me.uniodex.uniostats.managers;

import lombok.Getter;
import me.uniodex.uniostats.UnioStats;
import me.uniodex.uniostats.objects.StatPlayer;
import me.uniodex.uniostats.utils.Utils;
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

            if (stat.equals(Stats.MOBKILLS)) {
                players.get(player).setMobKills(players.get(player).getMobKills() + amount);
            }

            if (stat.equals(Stats.BOSSKILLS)) {
                players.get(player).setBossKills(players.get(player).getBossKills() + amount);
            }

            if (stat.equals(Stats.PLAYTIME)) {
                players.get(player).setPlayTime(players.get(player).getPlayTime() + amount);
            }

            if (stat.equals(Stats.GAPPLESEATEN)) {
                players.get(player).setGapplesEaten(players.get(player).getGapplesEaten() + amount);
            }

            if (stat.equals(Stats.ARMORSBROKE)) {
                players.get(player).setArmorsBroke(players.get(player).getArmorsBroke() + amount);
            }

            if (stat.equals(Stats.ARMORSBROKEN)) {
                players.get(player).setArmorsBroken(players.get(player).getArmorsBroken() + amount);
            }
        }
    }

    public String getStat(String player, Stats stat) {
        if (stat.equals(Stats.KILLS)) {
            return String.valueOf(players.get(player).getKills());
        }

        if (stat.equals(Stats.DEATHS)) {
            return String.valueOf(players.get(player).getDeaths());
        }

        if (stat.equals(Stats.MOBKILLS)) {
            return String.valueOf(players.get(player).getMobKills());
        }

        if (stat.equals(Stats.BOSSKILLS)) {
            return String.valueOf(players.get(player).getBossKills());
        }

        if (stat.equals(Stats.PLAYTIME)) {
            return Utils.getPlayTime(players.get(player).getPlayTime());
        }

        if (stat.equals(Stats.GAPPLESEATEN)) {
            return String.valueOf(players.get(player).getGapplesEaten());
        }

        if (stat.equals(Stats.ARMORSBROKE)) {
            return String.valueOf(players.get(player).getArmorsBroke());
        }

        if (stat.equals(Stats.ARMORSBROKEN)) {
            return String.valueOf(players.get(player).getArmorsBroken());
        }
        return null;
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.get(p.getName()).saveData(true);
        }
    }

    public enum Stats {
        KILLS, DEATHS, MOBKILLS, BOSSKILLS, PLAYTIME, GAPPLESEATEN,
        ARMORSBROKEN, //Player's armor
        ARMORSBROKE //Opponent's armor
    }

}
