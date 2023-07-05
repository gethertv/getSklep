package dev.gether.getsklep.listeners;

import dev.gether.getsklep.GetSklep;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final GetSklep plugin;

    public PlayerJoinListener(GetSklep plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        plugin.getShopManager().getUserBuyData().remove(player.getUniqueId());
    }
}
