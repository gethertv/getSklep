package dev.gether.getsklep;

import dev.gether.getsklep.cmd.SklepCmd;
import dev.gether.getsklep.data.User;
import dev.gether.getsklep.database.SQLite;
import dev.gether.getsklep.file.TimeFile;
import dev.gether.getsklep.file.VaultFile;
import dev.gether.getsklep.listeners.InventoryClickListener;
import dev.gether.getsklep.listeners.PlayerJoinListener;
import dev.gether.getsklep.manager.ShopManager;
import dev.gether.getsklep.placeholder.TimePointsHolder;
import dev.gether.getsklep.task.AutoSave;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetSklep extends JavaPlugin {

    private static GetSklep instance;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private ShopManager shopManager;

    private SQLite sqLite;
    private HashMap<UUID, Integer> userSpentPoints = new HashMap<>();
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        sqLite = new SQLite("getsklep");
        if(!sqLite.isConnected())
        {
            getLogger().log (Level.WARNING, "Nie można połączyć sie z baza danych!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new TimePointsHolder(this)).register();


        shopManager = new ShopManager(this);

        registerListeners();
        registerCommand();

        new AutoSave(this).runTaskTimer(this, 20L*300, 20L*300);

        loadOnlineUser();

    }

    public void reloadPlugin()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            User user = getShopManager().getUserBuyData().get(player.getUniqueId());
            if(player.getOpenInventory().equals(getShopManager().getMainShop()) ||
                    player.getOpenInventory().equals(getShopManager().getTimeShop())
            )
                player.closeInventory();

            if(user!=null)
            {
                if(player.getOpenInventory().equals(user.getInventory()))
                    player.closeInventory();
            }
        }
        reloadConfig();
        TimeFile.loadFile();
        VaultFile.loadFile();

        shopManager = new ShopManager(this);
    }
    private void loadOnlineUser() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                    sqLite.loadUser(player);

            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
        if(sqLite.isConnected()) {
            saveUser();
            sqLite.disconnect();
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new TimePointsHolder(this)).unregister();

        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    private void saveUser()
    {
        for(Player player : Bukkit.getOnlinePlayers())
            sqLite.updateUser(player);
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

    public HashMap<UUID, Integer> getUserSpentPoints() {
        return userSpentPoints;
    }

    public SQLite getSqLite() {
        return sqLite;
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