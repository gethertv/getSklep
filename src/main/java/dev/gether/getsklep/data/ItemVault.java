package dev.gether.getsklep.data;

import org.bukkit.inventory.ItemStack;

public class ItemVault {
    private ItemStack itemStack;
    private double cost;

    public ItemVault(ItemStack itemStack, double cost) {
        this.itemStack = itemStack;
        this.cost = cost;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
