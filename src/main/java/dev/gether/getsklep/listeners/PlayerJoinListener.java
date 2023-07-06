package dev.gether.getsklep.listeners;

import dev.gether.getsklep.GetSklep;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {

    private final GetSklep plugin;

    public PlayerJoinListener(GetSklep plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getSqLite().loadUser(event.getPlayer());
            }
        }.runTaskAsynchronously(plugin);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        plugin.getShopManager().getUserBuyData().remove(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getSqLite().updateUser(player);
                plugin.getUserSpentPoints().remove(player.getUniqueId());
            }
        }.runTaskAsynchronously(plugin);
    }
}
