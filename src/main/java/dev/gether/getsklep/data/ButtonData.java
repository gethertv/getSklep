package dev.gether.getsklep.data;

import dev.gether.getsklep.manager.ButtonType;
import org.bukkit.inventory.ItemStack;

public class ButtonData {

    private ItemStack itemStack;
    private int slot;
    private ButtonType buttonType;

    public ButtonData(ItemStack itemStack, int slot, ButtonType buttonType) {
        this.itemStack = itemStack;
        this.slot = slot;
        this.buttonType = buttonType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public ButtonType getButtonType() {
        return buttonType;
    }
}
