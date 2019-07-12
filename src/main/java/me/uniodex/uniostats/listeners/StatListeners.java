package me.uniodex.uniostats.listeners;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import me.uniodex.uniostats.UnioStats;
import me.uniodex.uniostats.managers.StatManager;
import me.uniodex.uniostats.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class StatListeners implements Listener {

    private UnioStats plugin;

    public StatListeners(UnioStats plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs") && plugin.getConfig().getBoolean("stats.bossKills")) {
            Bukkit.getPluginManager().registerEvents(new MythicMobListener(), plugin);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getStatManager().addPlayer(event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getStatManager().removePlayer(event.getPlayer().getName());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!plugin.getConfig().getBoolean("stats.killsAndDeaths")) return;

        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            plugin.getStatManager().giveStat(killer.getName(), StatManager.Stats.KILLS, 1);
        }
        plugin.getStatManager().giveStat(victim.getName(), StatManager.Stats.DEATHS, 1);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.getConfig().getBoolean("stats.mobKills")) return;

        Player killer = event.getEntity().getKiller();
        if (event.getEntity() instanceof Player) {
            return;
        }

        if (killer != null) {
            plugin.getStatManager().giveStat(killer.getName(), StatManager.Stats.MOB_KILLS, 1);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!plugin.getConfig().getBoolean("stats.gapplesEaten")) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType().equals(Material.GOLDEN_APPLE) && item.getDurability() == 1) {
            plugin.getStatManager().giveStat(player.getName(), StatManager.Stats.GAPPLES_EATEN, 1);
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();

        if (Utils.isArmor(event.getBrokenItem()) && plugin.getConfig().getBoolean("stats.armorsBroken")) {
            plugin.getStatManager().giveStat(player.getName(), StatManager.Stats.ARMORS_BROKEN, 1);
        }

        if (!(player.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        if (!plugin.getConfig().getBoolean("stats.armorsBroke")) return;

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) player.getLastDamageCause();
        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            plugin.getStatManager().giveStat(damager.getName(), StatManager.Stats.ARMORS_BROKE, 1);
        } else if (e.getDamager() instanceof Arrow) {
            Arrow killerArrow = (Arrow) e.getDamager();

            if (killerArrow.getShooter() instanceof Player) {
                Player shooter = (Player) killerArrow.getShooter();

                plugin.getStatManager().giveStat(shooter.getName(), StatManager.Stats.ARMORS_BROKE, 1);
            }
        }
    }

    public class MythicMobListener implements Listener {

        @EventHandler
        public void onBossKill(MythicMobDeathEvent event) {
            LivingEntity killer = event.getKiller();

            if (event.getMobType().getInternalName().toLowerCase().contains("minyon")) {
                return;
            }

            if (killer != null) {
                if (killer instanceof Player) {
                    plugin.getStatManager().giveStat(killer.getName(), StatManager.Stats.BOSS_KILLS, 1);
                }
            }
        }

    }
}
