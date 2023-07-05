package dev.gether.getsklep.cmd;

import dev.gether.getsklep.GetSklep;
import dev.gether.getsklep.file.TimeFile;
import dev.gether.getsklep.file.VaultFile;
import dev.gether.getsklep.utils.ColorFixer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class SklepCmd implements CommandExecutor, TabCompleter {

    private final GetSklep plugin;
    public SklepCmd(GetSklep plugin)
    {
        this.plugin = plugin;
        plugin.getCommand("getsklep").setExecutor(this);
        plugin.getCommand("getsklep").setTabCompleter(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        if(args.length==3)
        {
            if(player.hasPermission("getsklep.admin"))
            {
                if(args[0].equalsIgnoreCase("vault"))
                {
                    ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
                    if(itemStack.getType()==Material.AIR)
                    {
                        player.sendMessage(ColorFixer.addColors("&cMusisz cos trzymac w rece!"));
                        return false;
                    }
                    if(!isDouble(args[1]))
                    {
                        player.sendMessage(ColorFixer.addColors("&cMusisz podac liczbe double!"));
                        return false;
                    }
                    if(!isInt(args[2]))
                    {
                        player.sendMessage(ColorFixer.addColors("&cMusisz podac liczbe calkowita!"));
                        return false;
                    }
                    double cost = Double.parseDouble(args[1]);
                    int slot = Integer.parseInt(args[2]);
                    plugin.getShopManager().saveItem(VaultFile.getConfig(), itemStack, cost, slot);
                    player.sendMessage(ColorFixer.addColors("&aPomyslnie zapisano item.\n&aPamietaj o przeladowaniu pluginu!"));
                    return true;
                }
                if(args[0].equalsIgnoreCase("time"))
                {
                    ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
                    if(itemStack.getType()==Material.AIR)
                    {
                        player.sendMessage(ColorFixer.addColors("&cMusisz cos trzymac w rece!"));
                        return false;
                    }
                    if(!isInt(args[1]) || !isInt(args[2]))
                    {
                        player.sendMessage(ColorFixer.addColors("&cMusisz podac liczbe calko!"));
                        return false;
                    }
                    int cost = Integer.parseInt(args[1]);
                    int slot = Integer.parseInt(args[2]);
                    plugin.getShopManager().saveItem(TimeFile.getConfig(), itemStack, cost, slot);
                    player.sendMessage(ColorFixer.addColors("&aPomyslnie zapisano item.\n&aPamietaj o przeladowaniu pluginu!"));
                    return true;
                }
            }
        }

        if(!player.hasPermission("getsklep.use"))
            return false;

        player.openInventory(plugin.getShopManager().getMainShop());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length==1)
            if(sender.hasPermission("getsklep.admin"))
                return Arrays.asList("vault", "time");

        return null;
    }

    private boolean isInt(String input)
    {
        try {
            int a = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ignored) {}

        return false;
    }

    private boolean isDouble(String input)
    {
        try {
            double a = Double.parseDouble(input);
            return true;
        } catch (NumberFormatException ignored) {}

        return false;
    }
}
