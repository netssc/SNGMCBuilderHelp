package sngmc.build;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BuilderGUI implements Listener {

    private final BuilderHelp plugin;
    private final String GUI_TITLE = ChatColor.BLUE + "Меню строителя";
    private final ItemStack builderBook;

    public BuilderGUI(BuilderHelp plugin) {
        this.plugin = plugin;
        this.builderBook = createBuilderBook();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getWorld().getGameRuleValue("doDaylightCycle").equals("true")) {
                    p.getWorld().setTime(6000);
                }
            }
        }, 20L, 100L);
    }

    private ItemStack createBuilderBook() {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Меню строителя");
        book.setItemMeta(meta);
        return book;
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.BOOK) return;

        if (!item.hasItemMeta() || !item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Меню строителя")) {
            return;
        }

        event.setCancelled(true);
        openBuilderGUI(event.getPlayer());
    }

    private void openBuilderGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);

        boolean daylightCycle = player.getWorld().getGameRuleValue("doDaylightCycle").equals("true");
        gui.setItem(0, createToggleItem(Material.WATCH, "Бесконечный день", !daylightCycle));

        boolean isClearWeather = !player.getWorld().hasStorm();
        gui.setItem(1, createToggleItem(Material.YELLOW_FLOWER, "Ясная погода", isClearWeather));

        boolean isPeaceful = player.getWorld().getDifficulty() == Difficulty.PEACEFUL;
        gui.setItem(2, createDifficultyItem(isPeaceful));

        boolean blockInfoEnabled = plugin.getConfig().getBoolean("blockInfo." + player.getUniqueId(), false);
        gui.setItem(3, createToggleItem(Material.STONE, "Информация о блоках", blockInfoEnabled));

        boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
        gui.setItem(4, createToggleItem(Material.DIAMOND_PICKAXE, "Режим игры", isCreative));

        gui.setItem(5, createActionItem(Material.BOOK, "Телепорт на спавн"));
        gui.setItem(6, createActionItem(Material.BARRIER, "Очистить инвентарь"));

        player.openInventory(gui);
    }

    @EventHandler
    public void onGUIClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        switch (event.getRawSlot()) {
            case 0:
                boolean currentDaylightCycle = player.getWorld().getGameRuleValue("doDaylightCycle").equals("true");
                player.getWorld().setGameRuleValue("doDaylightCycle", currentDaylightCycle ? "false" : "true");
                if (!currentDaylightCycle) player.getWorld().setTime(6000);
                player.sendMessage(plugin.getMessagePrefix() + "Бесконечный день " + (!currentDaylightCycle ? "выключен" : "включен"));
                break;

            case 1:
                if (player.getWorld().hasStorm()) {
                    player.getWorld().setStorm(false);
                    player.getWorld().setThundering(false);
                    player.sendMessage(plugin.getMessagePrefix() + "Ясная погода включена");
                } else {
                    player.getWorld().setStorm(true);
                    player.sendMessage(plugin.getMessagePrefix() + "Ясная погода выключена, пошел дождь");
                }
                break;

            case 2:
                Difficulty currentDifficulty = player.getWorld().getDifficulty();
                if (currentDifficulty == Difficulty.PEACEFUL) {
                    player.getWorld().setDifficulty(Difficulty.NORMAL);
                    player.sendMessage(plugin.getMessagePrefix() + "Сложность изменена на Обычную");
                } else {
                    player.getWorld().setDifficulty(Difficulty.PEACEFUL);
                    player.sendMessage(plugin.getMessagePrefix() + "Сложность изменена на Мирную");
                }
                break;

            case 3:
                boolean newState = !plugin.getConfig().getBoolean("blockInfo." + player.getUniqueId(), false);
                plugin.getConfig().set("blockInfo." + player.getUniqueId(), newState);
                plugin.saveConfig();
                player.sendMessage(plugin.getMessagePrefix() + (newState ? "Информация о блоках включена" : "Информация о блоках выключена"));
                break;

            case 4:
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(plugin.getMessagePrefix() + "Режим изменен на Выживание");
                } else {
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(plugin.getMessagePrefix() + "Режим изменен на Креатив");
                }
                break;

            case 5:
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage(plugin.getMessagePrefix() + "Телепорт на спавн выполнен");
                break;

            case 6:
                player.getInventory().clear();
                player.getInventory().setItemInMainHand(builderBook.clone());
                player.sendMessage(plugin.getMessagePrefix() + "Инвентарь очищен");
                break;
        }

        openBuilderGUI(player);
    }

    private ItemStack createToggleItem(Material material, String name, boolean enabled) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " +
                (enabled ? ChatColor.GREEN + "ВКЛ" : ChatColor.RED + "ВЫКЛ"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDifficultyItem(boolean isPeaceful) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 2);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Сложность: " +
                (isPeaceful ? ChatColor.GREEN + "Мирная" : ChatColor.RED + "Обычная"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createActionItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        item.setItemMeta(meta);
        return item;
    }
}