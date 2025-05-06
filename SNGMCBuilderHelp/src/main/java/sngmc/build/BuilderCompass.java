package sngmc.build;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BuilderCompass implements CommandExecutor {

    private final BuilderHelp plugin;

    public BuilderCompass(BuilderHelp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessagePrefix() + "Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        ItemStack builderItem = new ItemStack(Material.BOOK);
        ItemMeta meta = builderItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Меню строителя");
        builderItem.setItemMeta(meta);

        player.getInventory().addItem(builderItem);
        player.sendMessage(plugin.getMessagePrefix() + "Вы получили предмет строителя!");

        return true;
    }
}