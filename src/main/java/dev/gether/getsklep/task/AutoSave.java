package dev.gether.getsklep.task;

import dev.gether.getsklep.GetSklep;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSave extends BukkitRunnable {
    private final GetSklep plugin;

    public AutoSave(GetSklep plugin)
    {
        this.plugin = plugin;

    }
    @Override
    public void run() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    plugin.getSqLite().updateUser(player);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
