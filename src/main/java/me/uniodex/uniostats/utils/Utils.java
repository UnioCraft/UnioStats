package me.uniodex.uniostats.utils;

import me.uniodex.uniostats.UnioStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Utils {

    public static void addIfNotExist(Object object, List list) {
        if (!list.contains(object)) {
            list.add(object);
        }
    }

    public static boolean isHelmet(ItemStack item) {
        return item.getType().equals(Material.CHAINMAIL_HELMET) || item.getType().equals(Material.SKULL_ITEM) || item.getType().equals(Material.DIAMOND_HELMET) || item.getType().equals(Material.GOLD_HELMET) || item.getType().equals(Material.IRON_HELMET) || item.getType().equals(Material.LEATHER_HELMET);
    }

    public static boolean isChestplate(ItemStack item) {
        return item.getType().equals(Material.CHAINMAIL_CHESTPLATE) || item.getType().equals(Material.DIAMOND_CHESTPLATE) || item.getType().equals(Material.GOLD_CHESTPLATE) || item.getType().equals(Material.IRON_CHESTPLATE) || item.getType().equals(Material.LEATHER_CHESTPLATE);
    }

    public static boolean isLeggings(ItemStack item) {
        return item.getType().equals(Material.CHAINMAIL_LEGGINGS) || item.getType().equals(Material.DIAMOND_LEGGINGS) || item.getType().equals(Material.GOLD_LEGGINGS) || item.getType().equals(Material.IRON_LEGGINGS) || item.getType().equals(Material.LEATHER_LEGGINGS);
    }

    public static boolean isBoots(ItemStack item) {
        return item.getType().equals(Material.CHAINMAIL_BOOTS) || item.getType().equals(Material.DIAMOND_BOOTS) || item.getType().equals(Material.GOLD_BOOTS) || item.getType().equals(Material.IRON_BOOTS) || item.getType().equals(Material.LEATHER_BOOTS);
    }

    public static boolean isArmor(ItemStack item) {
        return (isHelmet(item) || isChestplate(item) || isLeggings(item) || isBoots(item));
    }

    public static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public static void kickSync(UnioStats plugin, String playerName, String reason) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                player.kickPlayer(reason);
            }
        });
    }

    public static double calculateKDR(int kills, int deaths) {
        return new BigDecimal(deaths > 1 ? (double) kills / deaths : kills).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static String getPlayTime(int playTime) {
        float actplayTime = playTime / 3600f;
        double playTimeDouble = Utils.round(actplayTime);
        return playTimeDouble + " saat";
    }
}
