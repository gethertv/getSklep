package dev.gether.getsklep;

import dev.gether.getsklep.cmd.SklepCmd;
import dev.gether.getsklep.listeners.InventoryClickListener;
import dev.gether.getsklep.listeners.PlayerJoinListener;
import dev.gether.getsklep.manager.ShopManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class GetSklep extends JavaPlugin {

    private static GetSklep instance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private ShopManager shopManager;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        shopManager = new ShopManager(this);

        registerListeners();
        registerCommand();

    }
    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }


    private void registerCommand()
    {
        new SklepCmd(this);
    }
    private void registerListeners()
    {
        new InventoryClickListener(this);
        new PlayerJoinListener(this);
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    public static GetSklep getInstance() {
        return instance;
    }

    public static Economy getEcon() {
        return econ;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }
}