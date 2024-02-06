package me.softik.nerochat.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.models.ColoredPrefix;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.*;

public class Config {

    private final NeroChat plugin;
    private final ConfigFile config;

    public final Set<ColoredPrefix> color_prefixes;
    public final String default_lang, chat_format, console_name, prefix;
    public final int ignore_list_size;
    public final boolean auto_lang, bstats_metrics, notify_updates, display_nickname_color;

    public Config() throws Exception {
        this.plugin = NeroChat.getInstance();
        // Create plugin folder first if it does not exist yet
        File pluginFolder = this.plugin.getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            this.plugin.getLogger().severe("Failed to create plugin folder.");
        // Load this.config.yml with ConfigMaster
        this.config = ConfigFile.loadConfig(new File(pluginFolder, "this.config.yml"));

        // Create sections and headers first so we can force order
        this.preStructure();

        // Language
        this.default_lang = getString("Language.default-language", "en_us",
                "The default language to be used if auto-lang is off or no matching language file was found.").toLowerCase();
        this.auto_lang = getBoolean("Language.auto-language", true,
                "Enable / Disable locale based messages.");

        // General
        this.bstats_metrics = getBoolean("Main.bstats-metrics", true,
                "Enable / Disable bstats metrics. Please don't turn it off, if it is not difficult.");
        this.notify_updates = getBoolean("Main.notify-updates", false,
                "Enable / Disable notification of a new version of the this.plugin. It is recommended to turn this on.");
        this.display_nickname_color = getBoolean("Main.display-nickname-color", true,
                "Enable/disable the display of the player's nickname color.");
        this.prefix = ChatColor.translateAlternateColorCodes('&', getString("Main.prefix", "[&2NeroChat&r] &6"));
        this.console_name = ChatColor.translateAlternateColorCodes('&', getString("Main.console-name", "[console]",
                "Defines the sender's name when sending messages from the server console."));
        this.chat_format = ChatColor.translateAlternateColorCodes('&', getString("Main.chat-format", "<%player%&r>",
                "Change the format of messages in public chat."));
        this.ignore_list_size = getInt("Main.ignore-list-size", 9,
                "The size of the ignore list in pages. It is not recommended to set more than 5.");

        // Prefixes
        this.config.addComment("Prefixes", "To use these you need to add the respective permission.\n" +
                "EXAMPLE: Prefixes.BLUE -> nerochat.BLUE");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("GREEN", ">");
        defaults.put("BOLD", "**");
        defaults.put("ITALIC", "*");
        ConfigSection prefix_section = getConfigSection("Prefixes", defaults);
        final List<String> keys = prefix_section.getKeys(false);
        this.color_prefixes = new HashSet<>(keys.size());
        for (final String configuredColor : keys) {
            try {
                final String chatPrefix = Objects.requireNonNull(prefix_section.getString(configuredColor));
                final ChatColor chatColor = ChatColor.valueOf(configuredColor);
                final Permission permission = new Permission( // Constructing a permission also makes it visible in perm plugins
                        "nerochat.chatcolor." + configuredColor,
                        "Chat prefix '" + chatPrefix + "' will apply chatcolor '" + configuredColor + "' to a players messages.",
                        PermissionDefault.FALSE);
                this.color_prefixes.add(new ColoredPrefix(chatPrefix, permission, chatColor));
            } catch (NullPointerException e) {
                this.plugin.getLogger().warning("Cant register color '" + configuredColor + "' because you did not specify a prefix");
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().warning("Cant register color '" + configuredColor + "' because its not an enum of ChatColor " +
                        "(https://jd.papermc.io/paper/1.12/org/bukkit/ChatColor.html)");
            } catch (Throwable t) {
                this.plugin.getLogger().warning("Cant register color '" + configuredColor + "' because something unexpected happened - "+t.getLocalizedMessage());
            }
        }
    }

    public void saveConfig() {
        try {
            this.config.save();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public void preStructure() {
        this.createTitledSection("Language", "Language");
        this.createTitledSection("General", "Main");
        this.createTitledSection("Prefixes", "Prefixes");
        this.createTitledSection("CapsFilter", "CapsFilter");
        this.createTitledSection("RegexFilter", "RegexFilter");
        this.createTitledSection("AntiSpam", "anti-spam");
    }

    public void createTitledSection(String title, String path) {
        this.config.addSection(title);
        this.config.addDefault(path, null);
    }

    public ConfigFile getMaster() {
        return this.config;
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, def);
    }

    public String getString(String path, String def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getString(path, def);
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, def);
    }

    public double getDouble(String path, double def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getDouble(path, def);
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, def);
    }

    public int getInt(String path, int def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return this.config.getInteger(path, def);
    }

    public long getLong(String path, long def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getLong(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        this.config.addDefault(path, def);
        return this.config.getStringList(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue) {
        this.config.addDefault(path, null);
        this.config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> this.config.addExample(path+"."+string, object));
        return this.config.getConfigSection(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue, String comment) {
        this.config.addDefault(path, null, comment);
        this.config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> this.config.addExample(path+"."+string, object));
        return this.config.getConfigSection(path);
    }

    public List<String> getListFile(String fileName, String path, List<String> def) {
        try {
            ConfigFile list = ConfigFile.loadConfig(new File(this.plugin.getDataFolder(), fileName));
            list.addDefault(path, def);
            list.save();
            return list.getStringList(path);
        } catch (Exception e) {
            this.plugin.getLogger().severe("Error handling list file: "+fileName+"! - " + e.getLocalizedMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> getListFile(String fileName, String path, List<String> def, String comment) {
        try {
            ConfigFile list = ConfigFile.loadConfig(new File(this.plugin.getDataFolder(), fileName));
            list.addDefault(path, def, comment);
            list.save();
            return list.getStringList(path);
        } catch (Exception e) {
            this.plugin.getLogger().severe("Error handling list file: "+fileName+"! - " + e.getLocalizedMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
