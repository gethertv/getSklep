package dev.gether.getsklep.data;

import org.bukkit.inventory.ItemStack;

public class ItemTime {

    private ItemStack itemStack;
    private int cost;

    public ItemTime(ItemStack itemStack, int cost) {
        this.itemStack = itemStack;
        this.cost = cost;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
