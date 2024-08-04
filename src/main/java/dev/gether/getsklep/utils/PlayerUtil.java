package dev.gether.getsklep.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil {

    public static void giveItem(Player player, ItemStack itemStack) {
        if(isInventoryFull(player)) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        } else {
            player.getInventory().addItem(itemStack);
        }
    }

    private static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

}
