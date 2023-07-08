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
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
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

    private int timePointsValue;
    private int timeConverter;

    private DecimalFormat decimalFormat = new DecimalFormat("##0.##");
    public ShopManager(GetSklep plugin)
    {
        this.plugin = plugin;

        // przelicznik co ile sekund ma sie otrzymywac points
        timeConverter = plugin.getConfig().getInt("points.time");
        timePointsValue = plugin.getConfig().getInt("points.value");
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
            if(itemMeta.getLore()!=null)
                lore.addAll(itemMeta.getLore());

            if(shopType==ShopType.VAULT)
            {
                double cost = itemSection.getDouble(".cost");
                vaultItems.put(slot, new ItemVault(itemBuy, cost));
                for(String line : plugin.getConfig().getStringList("extra-line.vault"))
                {
                    lore.add(line.replace("{price}", getFormat(cost)));
                }
            }
            if(shopType==ShopType.TIME)
            {
                int cost = itemSection.getInt(".cost");
                timeItems.put(slot, new ItemTime(itemBuy, cost));
                for(String line : plugin.getConfig().getStringList("extra-line.time"))
                {
                    lore.add(line.replace("{price}", getFormat(cost)));
                }
            }
            itemMeta.setLore(ColorFixer.addColors(lore));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(slot, itemStack);

        }
        return inventory;
    }

    public void removeItem(Player player, ItemStack fromItem, int amount)
    {
        int removeItem = amount;


        for(ItemStack itemStack : player.getInventory()) {
            if(itemStack==null || itemStack.getType()== Material.AIR)
                continue;

            if(itemStack.isSimilar(fromItem))
            {
                if(removeItem<=0)
                    break;

                if(itemStack.getAmount()<=removeItem)
                {
                    removeItem-=itemStack.getAmount();
                    itemStack.setAmount(0);
                } else {
                    itemStack.setAmount(itemStack.getAmount()-removeItem);
                    removeItem=0;
                }
            }
        }
    }

    public int calcItem(Player player, ItemStack fromItem)
    {
        int amount = 0;
        for(ItemStack itemStack : player.getInventory())
        {
            if(itemStack==null || itemStack.getType()== Material.AIR)
                continue;

            if(itemStack.isSimilar(fromItem))
                amount+=itemStack.getAmount();
        }

        return amount;
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

    public int getPlayerPoints(Player player)
    {
        Integer spentPoints = plugin.getUserSpentPoints().get(player.getUniqueId());
        if(spentPoints==null)
            return 0;


        int sec = player.getStatistic(Statistic.PLAY_ONE_MINUTE)/20;
        int amount = sec/plugin.getShopManager().getTimeConverter();
        int points = (amount*plugin.getShopManager().getTimePointsValue())-spentPoints;
        if(points<0)
        {
            plugin.getUserSpentPoints().put(player.getUniqueId(), 0);
            points = 0;
        }
        return points;
    }

    public boolean hasPoints(Player player, int amount)
    {
        int points = getPlayerPoints(player);
        if(points>=amount)
            return true;

        return false;
    }
    public String getFormat(double amount) {
        String format = plugin.getConfig().getString("price-format");
        String symbol = "";
        double calc = 1;

        if (amount >= 1E18) {
            amount /= 1E18;
            symbol =  plugin.getConfig().getString("symbol.try");
        } else if (amount >= 1E15) {
            amount /= 1E15;
            symbol =  plugin.getConfig().getString("symbol.biliard");
        } else if (amount >= 1E12) {
            amount /= 1E12;
            symbol =  plugin.getConfig().getString("symbol.bilion");
        } else if (amount >= 1E9) {
            amount /= 1E9;
            symbol =  plugin.getConfig().getString("symbol.mld");
        } else if (amount >= 1E6) {
            amount /= 1E6;
            symbol =  plugin.getConfig().getString("symbol.mln");
        }
        else if (amount >= 1000) {
            amount /= 1000;
            symbol =  plugin.getConfig().getString("symbol.tys");
        }
        format = format.replace("{value}", decimalFormat.format(amount))
                    .replace("{symbol}",symbol);

        return format;
    }
    public void takePoints(Player player, int amount)
    {
        Integer spentPoints = plugin.getUserSpentPoints().get(player.getUniqueId());
        if(spentPoints==null) return;

        plugin.getUserSpentPoints().put(player.getUniqueId(), spentPoints+amount);
    }
    public int getTimeConverter() {
        return timeConverter;
    }

    public int getTimePointsValue() {
        return timePointsValue;
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
