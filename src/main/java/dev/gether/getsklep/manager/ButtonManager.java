package dev.gether.getsklep.manager;

import dev.gether.getsklep.GetSklep;
import dev.gether.getsklep.data.ButtonData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ButtonManager {

    public final GetSklep plugin;

    public List<ButtonData> buttonDataList;
    public ShopManager shopManager;
    public ButtonManager(GetSklep plugin, ShopManager shopManager)
    {
        this.plugin = plugin;
        this.shopManager = shopManager;
        buttonDataList = new ArrayList<>();

        implementButtons();

    }

    private void implementButtons() {
        {
            ConfigurationSection leftSection = plugin.getConfig().getConfigurationSection("menu-confirm.left");
            {
                ItemStack itemStack = shopManager.itemFromConfig(leftSection.getConfigurationSection(".set_1"));
                int slot = leftSection.getInt(".set_1.slot");
                buttonDataList.add(new ButtonData(itemStack, slot, ButtonType.SET_1));
            }
            {
                ItemStack itemStack = shopManager.itemFromConfig(leftSection.getConfigurationSection(".remove_16"));
                int slot = leftSection.getInt(".remove_16.slot");
                buttonDataList.add(new ButtonData(itemStack, slot, ButtonType.REMOVE_16));
            }
            {
                ItemStack itemStack = shopManager.itemFromConfig(leftSection.getConfigurationSection(".remove_1"));
                int slot = leftSection.getInt(".remove_1.slot");
                buttonDataList.add(new ButtonData(itemStack, slot, ButtonType.REMOVE_1));
            }
        }
        {
            ConfigurationSection rightSection = plugin.getConfig().getConfigurationSection("menu-confirm.right");
            {
                ItemStack itemStack = shopManager.itemFromConfig(rightSection.getConfigurationSection(".set_64"));
                int slot = rightSection.getInt(".set_64.slot");
                buttonDataList.add(new ButtonData(itemStack, slot, ButtonType.SET_64));
            }
            {
                ItemStack itemStack = shopManager.itemFromConfig(rightSection.getConfigurationSection(".add_1"));
                int slot = rightSection.getInt(".add_1.slot");
                buttonDataList.add(new ButtonData(itemStack, slot, ButtonType.ADD_1));
            }
            {
                ItemStack itemStack = shopManager.itemFromConfig(rightSection.getConfigurationSection(".add_16"));
                int slot = rightSection.getInt(".add_16.slot");
                buttonDataList.add(new ButtonData(itemStack, slot, ButtonType.ADD_16));
            }
        }
    }

    public List<ButtonData> getButtonDataList() {
        return buttonDataList;
    }
}
