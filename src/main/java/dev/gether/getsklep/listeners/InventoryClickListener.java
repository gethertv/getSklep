package dev.gether.getsklep.listeners;

import dev.gether.getsklep.GetSklep;
import dev.gether.getsklep.data.ButtonData;
import dev.gether.getsklep.data.ItemTime;
import dev.gether.getsklep.data.ItemVault;
import dev.gether.getsklep.data.User;
import dev.gether.getsklep.manager.ShopManager;
import dev.gether.getsklep.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final GetSklep plugin;

    public InventoryClickListener(GetSklep plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event)
    {
        if(event.getClickedInventory()==null)
            return;

        Player player = (Player) event.getWhoClicked();
        ShopManager shopManager = plugin.getShopManager();
        if(event.getInventory().equals(shopManager.getMainShop()))
        {
            event.setCancelled(true);
            if(event.getClickedInventory().equals(shopManager.getMainShop()))
            {
                if(event.getSlot()==shopManager.getSlotOpenTimeShop())
                {
                    if(!shopManager.isEnableShopTimer())
                        return;

                    player.openInventory(shopManager.getTimeShop());
                    return;
                }
                if(event.getSlot()==shopManager.getSlotOpenVaultShop())
                {
                    if(!shopManager.isEnableShopVault())
                        return;

                    player.openInventory(shopManager.getVaultShop());
                    return;
                }
            }
            return;
        }
        if(event.getInventory().equals(shopManager.getTimeShop()))
        {
            event.setCancelled(true);
            if(event.getClickedInventory().equals(shopManager.getTimeShop()))
            {
                if(event.getSlot()==plugin.getConfig().getInt("back.slot"))
                {
                    player.openInventory(shopManager.getMainShop());
                    return;
                }
                ItemTime timeItem = plugin.getShopManager().getTimeItems().get(event.getSlot());
                if(timeItem==null)
                    return;

                Integer integer = plugin.getUserSpentPoints().get(player.getUniqueId());
                if(integer==null || !plugin.getShopManager().hasPoints(player, timeItem.getCost()))
                {
                    player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.no-points")));
                    return;
                }
                plugin.getShopManager().takePoints(player, timeItem.getCost());
                player.getInventory().addItem(timeItem.getItemStack());
                player.sendMessage(
                        ColorFixer.addColors(
                            plugin.getConfig().getString("lang.success-buy")
                                    .replace("{price}", String.valueOf(timeItem.getCost()))
                        )
                );
                return;
            }
            return;
        }
        if(event.getInventory().equals(shopManager.getVaultShop()))
        {
            event.setCancelled(true);
            if(event.getClickedInventory().equals(shopManager.getVaultShop()))
            {
                if(event.getSlot()==plugin.getConfig().getInt("back.slot"))
                {
                    player.openInventory(shopManager.getMainShop());
                    return;
                }
                ItemVault itemVault = plugin.getShopManager().getVaultItems().get(event.getSlot());
                if(itemVault==null)
                    return;

                User user = new User(itemVault);
                if(event.getClick()== ClickType.LEFT) {
                    plugin.getShopManager().getUserBuyData().put(player.getUniqueId(), user);
                    player.openInventory(user.getInventory());
                    return;
                }
                if(event.getClick()==ClickType.RIGHT)
                {
                    int amount = plugin.getShopManager().calcItem(player, itemVault.getItemStack());
                    if(amount<=0)
                    {
                        player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.no-item")
                                    .replace("{item}", (itemVault.getItemStack().getItemMeta().getDisplayName()!=null) ?
                                            itemVault.getItemStack().getItemMeta().getDisplayName() :
                                            itemVault.getItemStack().getType().name())
                        ));
                        return;
                    }
                    plugin.getShopManager().removeItem(player, itemVault.getItemStack().clone(), 1);
                    plugin.getEcon().depositPlayer(player, 1*itemVault.getCost());
                    player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.success-sell")
                            .replace("{money}", plugin.getShopManager().getFormat(itemVault.getCost()))));
                    return;
                }
                if(event.getClick()==ClickType.SHIFT_RIGHT)
                {
                    int amount = plugin.getShopManager().calcItem(player, itemVault.getItemStack());
                    if(amount<=0)
                    {
                        player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.no-item")
                                            .replace("{item}", (itemVault.getItemStack().getItemMeta().getDisplayName()!=null) ?
                                                    itemVault.getItemStack().getItemMeta().getDisplayName() :
                                                    itemVault.getItemStack().getType().name())
                        ));
                        return;
                    }
                    plugin.getShopManager().removeItem(player, itemVault.getItemStack().clone(), amount);
                    plugin.getEcon().depositPlayer(player, amount*itemVault.getCost());
                    player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.success-sell-all")
                            .replace("{money}", plugin.getShopManager().getFormat(amount*itemVault.getCost()))));
                    return;
                }
            }
            return;
        }
        User user = plugin.getShopManager().getUserBuyData().get(player.getUniqueId());
        if(user==null)
            return;

        if(event.getInventory().equals(user.getInventory()))
        {
            event.setCancelled(true);
            if(event.getClickedInventory().equals(user.getInventory()))
            {
                if(event.getSlot()==plugin.getConfig().getInt("menu-confirm.back-slot"))
                {
                    player.openInventory(shopManager.getVaultShop());
                    return;
                }
                if(event.getSlot()==plugin.getConfig().getInt("menu-confirm.confirm.slot"))
                {
                    user.buy(player);
                    return;
                }
                Integer integer = user.getSlotMultiply().get(event.getSlot());
                if(integer!=null)
                {
                    user.setAmount(64*integer);
                    user.refreshInv();
                    return;
                }
                for (ButtonData buttonData : plugin.getShopManager().getButtonManager().getButtonDataList()) {
                    if(event.getSlot()==buttonData.getSlot())
                    {
                        ItemStack item = event.getInventory().getItem(event.getSlot());
                        if(item==null || item.getType()==Material.AIR)
                            continue;

                        user.clickType(buttonData.getButtonType());
                        user.refreshInv();
                        return;
                    }
                }
            }
        }
    }

}
