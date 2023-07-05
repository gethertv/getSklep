package dev.gether.getsklep.manager;

import dev.gether.getsklep.GetSklep;
import dev.gether.getsklep.data.ItemTime;
import dev.gether.getsklep.data.ItemVault;
import dev.gether.getsklep.data.User;
import dev.gether.getsklep.file.TimeFile;
import dev.gether.getsklep.file.VaultFile;
import dev.gether.getsklep.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ShopManager {

    private final GetSklep plugin;

    private Inventory mainShop = null;
    private Inventory vaultShop = null;
    private Inventory timeShop = null;

    private int slotOpenTimeShop = 0;
    private int slotOpenVaultShop = 0;

    private HashMap<Integer, ItemVault> vaultItems = new HashMap<>();
    private HashMap<Integer, ItemTime> timeItems = new HashMap<>();


    private HashMap<UUID, User> userBuyData = new HashMap<>();

    private ButtonManager buttonManager;
    public ShopManager(GetSklep plugin)
    {
        this.plugin = plugin;

        createInventory();
        fillBackground();
        implementMainShopIcon();
    
        addButtonBack();
        TimeFile.loadFile();
        VaultFile.loadFile();
        implementItems(TimeFile.getConfig(), timeShop, ShopType.TIME);
        implementItems(VaultFile.getConfig(), vaultShop, ShopType.VAULT);

        buttonManager = new ButtonManager(plugin, this);

    }

    private void addButtonBack() {
        ConfigurationSection sectionButton = plugin.getConfig().getConfigurationSection("back");
        ItemStack itemStack = itemFromConfig(sectionButton);
        int slot = sectionButton.getInt(".slot");
        vaultShop.setItem(slot, itemStack);
        timeShop.setItem(slot, itemStack);
    }

    private void implementMainShopIcon()
    {
        /*
            SECTION SHOP TIME
         */
        {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("shop.main.icon-time");
            slotOpenTimeShop  = section.getInt(".slot");
            ItemStack itemStack = itemFromConfig(section.getConfigurationSection(".item"));
            mainShop.setItem(slotOpenTimeShop, itemStack);
        }
        /*
            SECTION SHOP VAULT
         */
        {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("shop.main.icon-vault");
            slotOpenVaultShop = section.getInt(".slot");
            ItemStack itemStack = itemFromConfig(section.getConfigurationSection(".item"));
            mainShop.setItem(slotOpenVaultShop, itemStack);
        }
    }
    private void fillBackground() {
        for(String shop : plugin.getConfig().getConfigurationSection("shop").getKeys(false)) {
            ConfigurationSection sectionShop = plugin.getConfig().getConfigurationSection("shop."+shop);
            for (String keyItem : sectionShop.getConfigurationSection(".backgrounds").getKeys(false)) {
                ShopType shopType = ShopType.valueOf(shop.toUpperCase());

                ConfigurationSection sectionItem = sectionShop.getConfigurationSection(".backgrounds."+keyItem);

                ItemStack itemStack = itemFromConfig(sectionItem);

                List<Integer> slots = new ArrayList<>(sectionItem.getIntegerList(".slots"));

                if (shopType == ShopType.MAIN)
                    for (int slot : slots)
                        mainShop.setItem(slot, itemStack);

                if (shopType == ShopType.VAULT)
                    for (int slot : slots)
                        vaultShop.setItem(slot, itemStack);

                if (shopType == ShopType.TIME)
                    for (int slot : slots)
                        timeShop.setItem(slot, itemStack);

            }
        }
    }

    public ItemStack itemFromConfig(ConfigurationSection section)
    {
        ItemStack itemStack = new ItemStack(Material.valueOf(section.getString(".material").toUpperCase()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors(section.getString(".displayname")));
        List<String> lore = new ArrayList<>(section.getStringList(".lore"));
        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    private void createInventory() {
        mainShop = Bukkit.createInventory(
                null,
                plugin.getConfig().getInt("shop.main.size"),
                ColorFixer.addColors(plugin.getConfig().getString("shop.main.title"))
        );

        vaultShop = Bukkit.createInventory(
                null,
                plugin.getConfig().getInt("shop.vault.size"),
                ColorFixer.addColors(plugin.getConfig().getString("shop.vault.title"))
        );

        timeShop = Bukkit.createInventory(
                null,
                plugin.getConfig().getInt("shop.time.size"),
                ColorFixer.addColors(plugin.getConfig().getString("shop.time.title"))
        );
    }

    private Inventory implementItems(FileConfiguration config, Inventory inventory, ShopType shopType) {
        for(String key : config.getConfigurationSection("items").getKeys(false))
        {
            ConfigurationSection itemSection = config.getConfigurationSection("items." + key);
            ItemStack itemStack = itemSection.getItemStack(".item").clone();
            ItemStack itemBuy = itemStack.clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            int slot = itemSection.getInt(".slot");
            List<String> lore = new ArrayList<>();
            if(itemMeta!=null)
                lore.addAll(itemMeta.getLore());

            if(shopType==ShopType.VAULT)
            {
                double cost = itemSection.getDouble(".cost");
                vaultItems.put(slot, new ItemVault(itemBuy, cost));
                for(String line : plugin.getConfig().getStringList("extra-line.vault"))
                {
                    lore.add(line.replace("{price}", String.valueOf(cost)));
                }
            }
            if(shopType==ShopType.TIME)
            {
                int cost = itemSection.getInt(".cost");
                timeItems.put(slot, new ItemTime(itemBuy, cost));
                for(String line : plugin.getConfig().getStringList("extra-line.time"))
                {
                    lore.add(line.replace("{price}", String.valueOf(cost)));
                }
            }
            itemMeta.setLore(ColorFixer.addColors(lore));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(slot, itemStack);

        }
        return inventory;
    }

    public void saveItem(FileConfiguration config, ItemStack itemStack, double cost, int slot)
    {
        ItemStack item = itemStack.clone();
        item.setAmount(1);

        int index = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
        if(config.isSet("items."+index))
        {
            saveItem(config, itemStack, cost, slot);
            return;
        }
        config.set("items."+index+".item", item);
        config.set("items."+index+".cost", cost);
        config.set("items."+index+".slot", slot);
        VaultFile.save();
        TimeFile.save();
    }
    public Inventory getMainShop() {
        return mainShop;
    }

    public Inventory getTimeShop() {
        return timeShop;
    }

    public HashMap<Integer, ItemVault> getVaultItems() {
        return vaultItems;
    }

    public HashMap<Integer, ItemTime> getTimeItems() {
        return timeItems;
    }

    public HashMap<UUID, User> getUserBuyData() {
        return userBuyData;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public Inventory getVaultShop() {
        return vaultShop;
    }

    public int getSlotOpenTimeShop() {
        return slotOpenTimeShop;
    }

    public int getSlotOpenVaultShop() {
        return slotOpenVaultShop;
    }
}
