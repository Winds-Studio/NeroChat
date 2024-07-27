package me.softik.nerochat.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.models.ColoredPrefix;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Config {

    private final NeroChat plugin;
    private final ConfigFile config;

    public final Set<ColoredPrefix> color_prefixes;
    public final String default_lang, chat_format, console_name, plugin_prefix;
    public final int ignore_list_size;
    public final boolean auto_lang, bstats_metrics, display_nickname_color;

    public Config() throws Exception {
        plugin = NeroChat.getInstance();
        // Create plugin folder first if it does not exist yet
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            plugin.getLogger().severe("Failed to create plugin folder.");
        // Load config.yml with ConfigMaster
        config = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));

        // Create sections and headers first so we can force order
        preStructure();

        // Language
        default_lang = getString("language.default-language", "en_us",
                "The default language to be used if auto-lang is off or no matching language file was found.").toLowerCase();
        auto_lang = getBoolean("language.auto-language", true,
                "Enable / Disable locale based messages.");

        // General
        bstats_metrics = getBoolean("general.bstats-metrics", true,
                "Enable / Disable bstats metrics. Please don't turn it off, if it is not difficult.");
        display_nickname_color = getBoolean("general.display-nickname-color", true,
                "Enable / disable the display of the player's nickname color.");
        plugin_prefix = ChatColor.translateAlternateColorCodes('&', getString("general.plugin-prefix", "[&2NeroChat&r] &6"));
        console_name = ChatColor.translateAlternateColorCodes('&', getString("general.console-name", "[console]",
                "Defines the sender's name when sending messages from the server console."));
        chat_format = ChatColor.translateAlternateColorCodes('&', getString("general.chat-format", "<%player%&r>",
                "Change the format of messages in public chat."));
        ignore_list_size = getInt("general.ignore-list-size", 9,
                "The size of the ignore list in pages. It is not recommended to set more than 5.");

        // Prefixes
        config.addComment("prefixes", "To use these you need to add the respective permission.\n" +
                "EXAMPLE: Prefixes.BLUE -> nerochat.chatcolor.BLUE");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("GREEN", ">");
        defaults.put("BOLD", "**");
        defaults.put("ITALIC", "*");
        ConfigSection prefix_section = getConfigSection("prefixes", defaults);
        final List<String> keys = prefix_section.getKeys(false);
        color_prefixes = new HashSet<>(keys.size());
        for (final String configuredColor : keys) {
            try {
                final String chatPrefix = Objects.requireNonNull(prefix_section.getString(configuredColor));
                final ChatColor chatColor = ChatColor.valueOf(configuredColor);
                final Permission permission = new Permission( // Constructing a permission also makes it visible in perm plugins
                        "nerochat.chatcolor." + configuredColor,
                        "Chat prefix '" + chatPrefix + "' will apply chatcolor '" + configuredColor + "' to a players messages.",
                        PermissionDefault.FALSE);
                color_prefixes.add(new ColoredPrefix(chatPrefix, permission, chatColor));
            } catch (NullPointerException e) {
                plugin.getLogger().warning("Cant register color '" + configuredColor + "' because you did not specify a prefix");
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Cant register color '" + configuredColor + "' because its not an enum of ChatColor " +
                        "(https://jd.papermc.io/paper/1.12/org/bukkit/ChatColor.html)");
            } catch (Throwable t) {
                plugin.getLogger().warning("Cant register color '" + configuredColor + "' because something unexpected happened - "+t.getLocalizedMessage());
            }
        }
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public void preStructure() {
        createTitledSection("Language", "language");
        createTitledSection("General", "general");
        createTitledSection("Prefixes", "prefixes");
        createTitledSection("Audit", "audit");
        createTitledSection("AntiSpam", "anti-spam");
    }

    public void createTitledSection(String title, String path) {
        config.addSection(title);
        config.addDefault(path, null);
    }

    public ConfigFile getMaster() {
        return config;
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        config.addDefault(path, def, comment);
        return config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    public String getString(String path, String def, String comment) {
        config.addDefault(path, def, comment);
        return config.getString(path, def);
    }

    public String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    public double getDouble(String path, double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public int getInt(String path, int def, String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }

    public long getLong(String path, long def, String comment) {
        config.addDefault(path, def, comment);
        return config.getLong(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue) {
        config.addDefault(path, null);
        config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> config.addExample(path+"."+string, object));
        return config.getConfigSection(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue, String comment) {
        config.addDefault(path, null, comment);
        config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> config.addExample(path+"."+string, object));
        return config.getConfigSection(path);
    }

    public List<String> getListFile(String fileName, String path, List<String> def) {
        try {
            ConfigFile list = ConfigFile.loadConfig(new File(plugin.getDataFolder(), fileName));
            list.addDefault(path, def);
            list.save();
            return list.getStringList(path);
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling list file: "+fileName+"! - " + e.getLocalizedMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> getListFile(String fileName, String path, List<String> def, String comment) {
        try {
            ConfigFile list = ConfigFile.loadConfig(new File(plugin.getDataFolder(), fileName));
            list.addDefault(path, def, comment);
            list.save();
            return list.getStringList(path);
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling list file: "+fileName+"! - " + e.getLocalizedMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
