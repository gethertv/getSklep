package dev.gether.getsklep.listeners;

import dev.gether.getsklep.GetSklep;
import dev.gether.getsklep.data.ButtonData;
import dev.gether.getsklep.data.ItemVault;
import dev.gether.getsklep.data.User;
import dev.gether.getsklep.manager.ShopManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

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
                    player.openInventory(shopManager.getTimeShop());
                    return;
                }
                if(event.getSlot()==shopManager.getSlotOpenVaultShop())
                {
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
                        if(event.getInventory().getItem(event.getSlot()).getType()== Material.AIR)
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
