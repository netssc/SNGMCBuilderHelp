package sngmc.build;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class BuilderHelp extends JavaPlugin {
    private String messagePrefix;
    private String consolePrefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Загружаем префиксы из конфига
        messagePrefix = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("prefix", "&x&0&3&4&B&B&C&ls&x&1&1&5&C&C&4&lʏ&x&2&0&6&D&C&B&ls&x&2&E&7&E&D&3&lᴛ&x&3&D&9&0&D&B&lᴇ&x&4&B&A&1&E&3&lᴍ &x&6&8&C&3&F&2• &r&f"));
        consolePrefix = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("console-prefix", "&x&0&3&4&B&B&C&ls&x&1&1&5&C&C&4&lʏ&x&2&0&6&D&C&B&ls&x&2&E&7&E&D&3&lᴛ&x&3&D&9&0&D&B&lᴇ&x&4&B&A&1&E&3&lᴍ &x&6&8&C&3&F&2• &r&f"));

        getCommand("buildhelp").setExecutor(new BuilderCompass(this));
        Bukkit.getPluginManager().registerEvents(new BuilderGUI(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockInfoListener(this), this);

        // Сообщение в консоль с цветами
        Bukkit.getConsoleSender().sendMessage(consolePrefix + " BuilderHelp включен! Скачано с plugins.sngmc.ru");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(consolePrefix + " BuilderHelp выключен! Скачано с plugins.sngmc.ru");
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public String getConsolePrefix() {
        return consolePrefix;
    }
}