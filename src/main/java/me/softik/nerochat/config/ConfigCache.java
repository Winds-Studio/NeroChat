package me.softik.nerochat.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.softik.nerochat.NeroChat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.*;

public class ConfigCache {

    private final ConfigFile config;
    public final Set<ColoredPrefix> color_prefixes;
    public final String default_lang, chat_format, console_name, prefix;
    public final int ignore_list_size;
    public final boolean auto_lang, bstats_metrics, notify_updates, display_nickname_color;

    public ConfigCache() throws Exception {
        // Create plugin folder first if it does not exist yet
        File pluginFolder = NeroChat.getInstance().getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            NeroChat.getLog().severe("Failed to create plugin folder.");
        // Load config.yml with ConfigMaster
        this.config = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));

        createTitledSection("Language", "Language");
        this.default_lang = getString("Language.default-language", "en_us",
                "The default language to be used if auto-lang is off or no matching language file was found.").toLowerCase();
        this.auto_lang = getBoolean("Language.auto-language", true,
                "Enable / Disable locale based messages.");

        createTitledSection("General", "Main");
        this.bstats_metrics = getBoolean("Main.bstats-metrics", true,
                "Enable / Disable bstats metrics. Please don't turn it off, if it is not difficult.");
        this.notify_updates = getBoolean("Main.notify-updates", false,
                "Enable / Disable notification of a new version of the plugin. It is recommended to turn this on.");
        this.display_nickname_color = getBoolean("Main.display-nickname-color", true,
                "Enable/disable the display of the player's nickname color.");
        this.prefix = ChatColor.translateAlternateColorCodes('&', getString("Main.prefix", "[&2NeroChat&r] &6"));
        this.console_name = ChatColor.translateAlternateColorCodes('&', getString("Main.console-name", "[console]",
                "Defines the sender's name when sending messages from the server console."));
        this.chat_format = ChatColor.translateAlternateColorCodes('&', getString("Main.chat-format", "<%player%&r>",
                "Change the format of messages in public chat."));
        this.ignore_list_size = getInt("Main.ignore-list-size", 9,
                "The size of the ignore list in pages. It is not recommended to set more than 5.");

        createTitledSection("CapsFilter", "CapsFilter");
        createTitledSection("RegexFilter", "RegexFilter");

        createTitledSection("Prefixes", "Prefixes");
        config.addComment("Prefixes", "To use these you need to add the respective permission.\n" +
                "EXAMPLE: Prefixes.BLUE -> nerochat.BLUE");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("GREEN", ">");
        defaults.put("BOLD", "**");
        defaults.put("ITALIC", "*");
        ConfigSection prefix_section = getConfigSection("Prefixes", defaults);
        final List<String> keys = prefix_section.getKeys(false);
        this.color_prefixes = new HashSet<>(keys.size());
        for (String color : keys) {
            try {
                final String chatPrefix = Objects.requireNonNull(prefix_section.getString(color));
                final ChatColor chatColor = ChatColor.valueOf(color);
                final Permission permission = new Permission( // Constructing a permission also makes it visible in perm plugins
                        "nerochat.chatcolor." + color,
                        "Chat prefix '" + chatPrefix + "' will apply chatcolor '" + color + "' to a players messages.",
                        PermissionDefault.FALSE
                );
                this.color_prefixes.add(new ColoredPrefix(chatPrefix, permission, chatColor));
            } catch (NullPointerException e) {
                NeroChat.getLog().warning("Cant register color '" + color + "' because you did not specify a prefix");
            } catch (IllegalArgumentException e) {
                NeroChat.getLog().warning("Cant register color '" + color + "' because its not an enum of ChatColor " +
                        "(https://jd.papermc.io/paper/1.12/org/bukkit/ChatColor.html)");
            } catch (Throwable t) {
                NeroChat.getLog().warning("Cant register color '" + color + "' because something unexpected happened - "+t.getLocalizedMessage());
            }
        }
    }

    public static class ColoredPrefix {
        public final String chat_prefix;
        public final Permission permission;
        public final ChatColor chat_color;
        public ColoredPrefix(String chat_prefix, Permission permission, ChatColor chat_color) {
            this.chat_prefix = chat_prefix;
            this.permission = permission;
            this.chat_color = chat_color;
        }
    }

    public ConfigFile master() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (Exception e) {
            NeroChat.getLog().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public void createTitledSection(String title, String path) {
        config.addSection(title);
        config.addDefault(path, null);
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
}
