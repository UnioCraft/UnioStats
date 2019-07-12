package me.uniodex.uniostats;

import lombok.Getter;
import me.uniodex.uniostats.commands.MainCommand;
import me.uniodex.uniostats.listeners.StatListeners;
import me.uniodex.uniostats.managers.PlaceholderManager;
import me.uniodex.uniostats.managers.SQLManager;
import me.uniodex.uniostats.managers.StatManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class UnioStats extends JavaPlugin {

    @Getter
    private StatManager statManager;
    @Getter
    private SQLManager sqlManager;
    @Getter
    private boolean isStopping;
    @Getter
    private static UnioStats instance;

    public static String prefix = ChatColor.AQUA + "" + ChatColor.BOLD + "UNIOCRAFT " + ChatColor.DARK_GREEN + "->" + ChatColor.RED;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
        this.sqlManager = new SQLManager(this);
        this.statManager = new StatManager(this);
        new StatListeners(this);
        getCommand("stats").setExecutor(new MainCommand(this));
        new PlaceholderManager().register();
    }

    public void onDisable() {
        this.isStopping = true;
        this.statManager.onDisable();
        this.sqlManager.onDisable();
    }

    public String getMessage(String configSection) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(configSection).replaceAll("%prefix%", prefix));
    }
}
