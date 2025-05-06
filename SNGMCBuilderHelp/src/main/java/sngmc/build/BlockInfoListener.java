package sngmc.build;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInfoListener implements Listener {

    private final BuilderHelp plugin;

    public BlockInfoListener(BuilderHelp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockLook(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!plugin.getConfig().getBoolean("blockInfo." + player.getUniqueId(), false)) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        String message = ChatColor.GRAY + "Блок: " + ChatColor.WHITE + block.getType() +
                ChatColor.GRAY + " Координаты: " + ChatColor.WHITE +
                block.getX() + ", " + block.getY() + ", " + block.getZ();

        try {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
        } catch (Exception e) {
            player.sendTitle("", message, 0, 20, 0);
        }
    }
}