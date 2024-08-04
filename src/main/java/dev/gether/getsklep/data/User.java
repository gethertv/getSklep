package dev.gether.getsklep.data;

import dev.gether.getsklep.GetSklep;
import dev.gether.getsklep.manager.ButtonManager;
import dev.gether.getsklep.manager.ButtonType;
import dev.gether.getsklep.manager.ShopManager;
import dev.gether.getsklep.utils.ColorFixer;
import dev.gether.getsklep.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {

    private Inventory inventory;
    private ItemVault itemVault;
    private ItemStack itemStack;
    private int amount;
    private List<String> extraLine;

    private HashMap<Integer, Integer> slotMultiply;

    public User(ItemVault itemVault) {
        ConfigurationSection confirmSection = GetSklep.getInstance().getConfig().getConfigurationSection("menu-confirm");
        this.inventory = Bukkit.createInventory(null, confirmSection.getInt(".size"), ColorFixer.addColors(confirmSection.getString(".title")));
        this.extraLine = new ArrayList<>(confirmSection.getStringList(".extra-line"));
        this.itemVault = itemVault;
        this.itemStack = itemVault.getItemStack().clone();
        this.amount = 1;
        fillButton(confirmSection);
        fillTopStack(confirmSection);
        fillLeftRightBtn();
        refreshItem();
    }

    public void buy(Player player)
    {
        GetSklep instance = GetSklep.getInstance();
        FileConfiguration config = instance.getConfig();

        double price = amount* itemVault.getCost();
        if(!GetSklep.getEcon().has(player, price))
        {
            double diff = price - GetSklep.getEcon().getBalance(player);
            player.sendMessage(ColorFixer.addColors(config.getString("lang.no-money")
                    .replace("{need-money}",instance.getShopManager().getFormat(diff))));
            return;
        }
        GetSklep.getEcon().withdrawPlayer(player, price);
        ItemStack item = itemVault.getItemStack().clone();

        int rest = amount % item.getMaxStackSize();
        int multiply = amount / item.getMaxStackSize();

        if (multiply <= 0) {
            item.setAmount(amount);
            PlayerUtil.giveItem(player, item);
        } else {
            ItemStack baseItem = itemVault.getItemStack().clone();
            for (int i = 0; i < multiply; i++) {
                ItemStack stack = baseItem.clone();
                stack.setAmount(stack.getMaxStackSize());
                PlayerUtil.giveItem(player, stack);
            }
            if (rest != 0) {
                ItemStack remainderItem = baseItem.clone();
                remainderItem.setAmount(rest);
                PlayerUtil.giveItem(player, remainderItem);
            }
        }


        player.sendMessage(ColorFixer.addColors(config.getString("lang.success-buy")
                .replace("{price}", instance.getShopManager().getFormat(price))));

        player.closeInventory();
    }

    public void clickType(ButtonType buttonType)
    {
        if(buttonType==ButtonType.ADD_1)
            amount++;

        if(buttonType==ButtonType.ADD_16)
            amount+=16;

        if(buttonType==ButtonType.SET_1)
            amount = 1;

        if(buttonType==ButtonType.SET_64)
            amount = 64;

        if(buttonType==ButtonType.REMOVE_1)
            amount--;

        if(buttonType==ButtonType.REMOVE_16)
            amount-=16;
    }

    public void refreshInv()
    {
        refreshItem();
        fillLeftRightBtn();
    }
    public void refreshItem()
    {
        ItemMeta itemMeta = itemVault.getItemStack().clone().getItemMeta();
        List<String> lore = new ArrayList<>();
        List<String> tempLore = itemMeta.getLore();
        if(tempLore!=null)
            lore.addAll(tempLore);

        double price = amount * itemVault.getCost();
        for(String line : extraLine)
        {
            lore.add(
                    line.replace("{amount}", String.valueOf(amount))
                        .replace("{price}", GetSklep.getInstance().getShopManager().getFormat(price))
            );
        }
        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);

        if(amount>64)
            itemStack.setAmount(64);
        else
            itemStack.setAmount(amount);

        inventory.setItem(GetSklep.getInstance().getConfig().getInt("menu-confirm.item-buy-slot"), itemStack);
    }
    private void fillLeftRightBtn() {
        ButtonManager buttonManager = GetSklep.getInstance().getShopManager().getButtonManager();
        for(ButtonData buttonData : buttonManager.getButtonDataList())
        {
            inventory.setItem(buttonData.getSlot(), null);
            if(buttonData.getButtonType()== ButtonType.ADD_1)
            {
                if(amount<64)
                    inventory.setItem(buttonData.getSlot(), buttonData.getItemStack());
            }
            if(buttonData.getButtonType()== ButtonType.ADD_16)
            {
                if(amount<=48)
                    inventory.setItem(buttonData.getSlot(), buttonData.getItemStack());
            }
            if(buttonData.getButtonType()== ButtonType.SET_64)
            {
                if(amount!=64)
                    inventory.setItem(buttonData.getSlot(), buttonData.getItemStack());
            }
            if(buttonData.getButtonType()== ButtonType.SET_1)
            {
                if(amount!=1)
                    inventory.setItem(buttonData.getSlot(), buttonData.getItemStack());
            }
            if(buttonData.getButtonType()== ButtonType.REMOVE_1)
            {
                if(amount>1 && amount<=64)
                    inventory.setItem(buttonData.getSlot(), buttonData.getItemStack());
            }
            if(buttonData.getButtonType()== ButtonType.REMOVE_16)
            {
                if(amount>16)
                    inventory.setItem(buttonData.getSlot(), buttonData.getItemStack());
            }
        }
    }

    private void fillTopStack(ConfigurationSection confirmSection) {
        slotMultiply = new HashMap<>();
        for(String multiplyStr : confirmSection.getConfigurationSection(".stack").getKeys(false))
        {
            ConfigurationSection stackSection = confirmSection.getConfigurationSection(".stack."+multiplyStr);
            int multiply = Integer.parseInt(multiplyStr);
            int slot = stackSection.getInt(".slot");
            slotMultiply.put(slot, multiply);
            ItemStack item = itemVault.getItemStack().clone();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors(stackSection.getString(".displayname")));
            List<String> lore = new ArrayList<>();
            List<String> tempLore = itemMeta.getLore();
            if(tempLore!=null)
                lore.addAll(tempLore);

            double cost = multiply * itemVault.getCost() * 64;
            for(String line : stackSection.getStringList(".lore"))
            {
                lore.add(line.replace("{price}", GetSklep.getInstance().getShopManager().getFormat(cost)));
            }
            itemMeta.setLore(ColorFixer.addColors(lore));
            item.setItemMeta(itemMeta);
            item.setAmount(multiply);

            inventory.setItem(slot, item);
        }

    }

    private void fillButton(ConfigurationSection confirmSection) {
        ShopManager shopManager = GetSklep.getInstance().getShopManager();
        ItemStack item = shopManager.itemFromConfig(confirmSection.getConfigurationSection(".confirm"));
        int slotItem = confirmSection.getInt(".confirm.slot");
        ItemStack backItem = shopManager.itemFromConfig(GetSklep.getInstance().getConfig().getConfigurationSection("back"));
        int slotBack = confirmSection.getInt(".back-slot");
        inventory.setItem(slotItem, item);
        inventory.setItem(slotBack, backItem);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getAmount() {
        return amount;
    }

    public HashMap<Integer, Integer> getSlotMultiply() {
        return slotMultiply;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
