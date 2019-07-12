package me.uniodex.uniostats.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.uniodex.uniostats.UnioStats;
import me.uniodex.uniostats.objects.StatPlayer;
import me.uniodex.uniostats.utils.Utils;
import org.bukkit.OfflinePlayer;

/**
 * This class will automatically register as a placeholder expansion
 * when a jar including this class is added to the directory
 * {@code /plugins/PlaceholderAPI/expansions} on your server.
 * <br>
 * <br>If you create such a class inside your own plugin, you have to
 * register it manually in your plugins {@code onEnable()} by using
 * {@code new YourExpansionClass().register();}
 */
public class PlaceholderManager extends PlaceholderExpansion {

    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return "UnioDex";
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "uniostats";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player     A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param identifier A String containing the identifier/value.
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (!player.isOnline()) return null;
        StatPlayer statPlayer = UnioStats.getInstance().getStatManager().getPlayer(player.getName());
        if (statPlayer == null) return null;

        for (StatManager.Stats stat : StatManager.Stats.values()) {
            if (identifier.equalsIgnoreCase(stat.toString().toLowerCase())) {
                return UnioStats.getInstance().getStatManager().getStat(player.getName(), stat);
            }
        }

        if (identifier.equalsIgnoreCase("kdr")) {
            return String.valueOf(Utils.calculateKDR(statPlayer.getKills(), statPlayer.getDeaths()));
        }

        return null;
    }
}