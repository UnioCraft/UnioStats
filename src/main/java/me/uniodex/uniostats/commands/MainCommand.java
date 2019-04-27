package me.uniodex.uniostats.commands;

import me.uniodex.uniostats.UnioStats;
import me.uniodex.uniostats.objects.StatPlayer;
import me.uniodex.uniostats.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class MainCommand implements CommandExecutor {

    private UnioStats plugin;

    public MainCommand(UnioStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage("Kullanım: /stats <oyuncu>");
            return false;
        }

        String playerName = sender.getName();
        Player player = Bukkit.getPlayer(playerName);
        String displayName = player.getDisplayName();
        StatPlayer statPlayer = plugin.getStatManager().getPlayers().get(sender.getName());

        if (args.length == 1) {
            playerName = String.valueOf(args[0]);
            displayName = playerName;
            if ((player = Bukkit.getPlayer(playerName)) != null) {
                displayName = player.getDisplayName();
                statPlayer = plugin.getStatManager().getPlayers().get(String.valueOf(args[0]));
                statPlayer.updatePlayTime();
            }
        }

        int kills;
        int deaths;
        int mobKills;
        int bossKills;
        String playTime;
        int gapplesEaten;
        int armorsBroke;
        int armorsBroken;

        if (player == null) {
            Map<String, Integer> stats = plugin.getSqlManager().getPlayerStats(playerName, false);
            if (stats != null) {
                kills = stats.get("kills");
                deaths = stats.get("deaths");
                mobKills = stats.get("mobKills");
                bossKills = stats.get("bossKills");
                playTime = Utils.getPlayTime(stats.get("playTime"));
                gapplesEaten = stats.get("gapplesEaten");
                armorsBroke = stats.get("armorsBroke");
                armorsBroken = stats.get("armorsBroken");
            } else {
                sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Belirttiğiniz kişiye ait istatistik bulunamadı!");
                return false;
            }
        } else {
            kills = statPlayer.getKills();
            deaths = statPlayer.getDeaths();
            mobKills = statPlayer.getMobKills();
            bossKills = statPlayer.getBossKills();
            playTime = Utils.getPlayTime(statPlayer.getPlayTime());
            gapplesEaten = statPlayer.getGapplesEaten();
            armorsBroke = statPlayer.getArmorsBroke();
            armorsBroken = statPlayer.getArmorsBroken();
        }

        double kdr = Utils.calculateKDR(kills, deaths);

        sender.sendMessage(UnioStats.prefix + displayName + ChatColor.RED + " isimli oyuncunun istatistikleri:");
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Öldürme: " + ChatColor.AQUA + kills);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Ölme: " + ChatColor.AQUA + deaths);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Öldürme/Ölme Oranı: " + ChatColor.AQUA + kdr);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Öldürülen Yaratık: " + ChatColor.AQUA + mobKills);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Öldürülen Boss: " + ChatColor.AQUA + bossKills);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Toplam Oynama Süresi: " + ChatColor.AQUA + playTime);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Yenilen Büyülü Altın Elma: " + ChatColor.AQUA + gapplesEaten);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Kırdığı Zırh Sayısı: " + ChatColor.AQUA + armorsBroke);
        sender.sendMessage(UnioStats.prefix + ChatColor.RED + "Kırılan Zırh Sayısı: " + ChatColor.AQUA + armorsBroken);

        return false;
    }
}
