package dev.gether.getsklep.placeholder;

import dev.gether.getsklep.GetSklep;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimePointsHolder extends PlaceholderExpansion {

    private final GetSklep plugin;

    public TimePointsHolder(GetSklep plugin)
    {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "getsklep";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gethertv";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer.getPlayer() == null) return null;
        Player player = offlinePlayer.getPlayer();
        if(identifier.equalsIgnoreCase("points"))
        {

            return String.valueOf(plugin.getShopManager().getPlayerPoints(player));
        }


        return null;
    }
}
