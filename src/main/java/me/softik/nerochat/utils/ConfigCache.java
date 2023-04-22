package me.softik.nerochat.utils;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.softik.nerochat.NeroChat;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class ConfigCache {

    private final boolean bstats_metrics;
    private final boolean notify_updates;
    private final boolean display_nickname_color;
    private final String console_name;
    private final int ignore_list_size;
    private final String prefixes_green;
    private final String prefixes_red;
    private final String prefixes_aqua;
    private final String prefixes_gold;
    private final String prefixes_yellow;
    private final String prefixes_gray;
    private final String prefixes_black;
    private final String prefixes_dark_green;
    private final String prefixes_dark_red;
    private final String prefixes_dark_gray;
    private final String prefixes_dark_blue;
    private final String prefixes_dark_aqua;
    private final String prefixes_dark_purple;
    private final String prefixes_light_purple;
    private final String prefixes_italic;
    private final String prefixes_underline;
    private final String prefixes_bold;
    private final String prefixes_strikethrough;
    private final String prefixes_blue;
    private final String chat_format;
    private final boolean RegexFilter_PublicChat_Logs_enabled;
    private final boolean RegexFilter_PublicChat_Player_Notify;
    private final boolean RegexFilter_PublicChat_Silent_mode;
    private final boolean RegexFilter_PublicChat_Case_Insensitive;
    private final List<String> RegexFilter_Public_Banned_Regex;
    private final boolean RegexFilter_Whisper_Case_Insensitive;
    private final boolean RegexFilter_Whisper_Player_Notify;
    private final boolean RegexFilter_Whisper_Logs_enabled;
    private final boolean RegexFilter_Whisper_Silent_mode;
    private final String prefix;
    public boolean Readable_Formatting_Whisper_Auto_Dot;
    public String Readable_Formatting_Last_Char;
    public boolean Readable_Formatting_Enable;
    public boolean Readable_Formatting_Public_Chat_Auto_Dot;
    public boolean Readable_Formatting_Whisper_Auto_Caps;
    public boolean Readable_Formatting_Public_Chat_Auto_Caps;
    public boolean RegexFilter_Whisper_enabled;
    public List<String> RegexFilter_Whisper_Banned_Regex;
    public boolean RegexFilter_PublicChat_enabled;
    private ConfigFile config;
    private final File configFile;
    private final Logger logger;
    public final String default_lang;
    public final HashSet<String> directories_to_scan = new HashSet<>();
    public final boolean auto_lang;

    public ConfigCache() {
        NeroChat plugin = NeroChat.getInstance();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        logger = plugin.getLogger();
        createFiles();
        loadConfig();
        config.addSection("Language");
        config.addDefault("Language", null);
        this.default_lang = getString("Language.default-language", "en_us", "The default language to be used if auto-lang is off or no matching language file was found.").toLowerCase();
        this.auto_lang = getBoolean("Language.auto-language", true, "Enable / Disable locale based messages.");
        config.addSection("Main");
        config.addDefault("Main", null);
        this.bstats_metrics = getBoolean("Main.bstats-metrics", true, "Enable / Disable bstats metrics. Please don't turn it off, if it is not difficult.");
        this.notify_updates = getBoolean("Main.notify-updates", true, "Enable / Disable notification of a new version of the plugin. It is recommended to turn this on.");
        this.display_nickname_color = getBoolean("Main.display-nickname-color", true, "Enable/disable the display of the player's nickname color.");
        this.prefix = getString("Main.prefix", "[&2NeroChat&r] &6");
        this.console_name = getString("Main.console-name", "[console]", "Defines the sender's name when sending messages from the server console.");
        this.chat_format = getString("Main.chat-format", "<%player%&r>", "Change the format of messages in public chat.");
        this.ignore_list_size = getInt("Main.ignore-list-size", 9, "The size of the ignore list in pages. It is not recommended to set more than 5.");
        config.addSection("Prefixes");
        config.addDefault("Prefixes", null, "To use these prefixes you need additionally the nerochat.<COLORCODE>\n/ indicates disabled!");
        this.prefixes_green = getString("Prefixes.GREEN", ">");
        this.prefixes_blue = getString("Prefixes.BLUE", "/");
        this.prefixes_red = getString("Prefixes.RED", "/");
        this.prefixes_aqua = getString("Prefixes.AQUA", "/");
        this.prefixes_gold = getString("Prefixes.GOLD", "/");
        this.prefixes_yellow = getString("Prefixes.YELLOW", "/");
        this.prefixes_gray = getString("Prefixes.GRAY", "/");
        this.prefixes_black = getString("Prefixes.BLACK", "/");
        this.prefixes_dark_green = getString("Prefixes.DARK_GREEN", "/");
        this.prefixes_dark_red = getString("Prefixes.DARK_RED", "/");
        this.prefixes_dark_gray = getString("Prefixes.DARK_GRAY", "/");
        this.prefixes_dark_blue = getString("Prefixes.DARK_BLUE", "/");
        this.prefixes_dark_aqua = getString("Prefixes.DARK_AQUA", "/");
        this.prefixes_dark_purple = getString("Prefixes.DARK_PURPLE", "/");
        this.prefixes_light_purple = getString("Prefixes.LIGHT_PURPLE", "/");
        this.prefixes_italic = getString("Prefixes.ITALIC", "/");
        this.prefixes_underline = getString("Prefixes.UNDERLINE", "/");
        this.prefixes_bold = getString("Prefixes.BOLD", "/");
        this.prefixes_strikethrough = getString("Prefixes.STRIKETHROUGH", "/");
        config.addSection("RegexFilter");
        config.addDefault("RegexFilter", null, "Filtering chat messages using regular expressions.\nIf you don't know how to create them, you can use ChatGPT");
        this.RegexFilter_PublicChat_enabled = getBoolean("RegexFilter.PublicChat.Enabled", false);
        this.RegexFilter_PublicChat_Logs_enabled = getBoolean("RegexFilter.PublicChat.Logs-Enabled", true, "Outputs the player's name and regex when the message is canceled.");
        this.RegexFilter_PublicChat_Player_Notify = getBoolean("RegexFilter.PublicChat.Player-Notify", true, "Do I inform the player that his message has not been sent? Doesn't work with silent mode.");
        this.RegexFilter_PublicChat_Silent_mode = getBoolean("RegexFilter.PublicChat.Silent-Mode", false, "The player will think he is sending messages, but in fact no one will see his messages.");
        this.RegexFilter_PublicChat_Case_Insensitive = getBoolean("RegexFilter.PublicChat.Case-Insensitive", true, "The search for matches will be case insensitive. Eliminates many regex bypasses with capslocks.");
        this.RegexFilter_Public_Banned_Regex = getList("RegexFilter.PublicChat.Banned-Regex", Collections.singletonList("^This is a(.*)banned message"), "Prevents any message that starts with \"This is a\" and ends with \"banned message\"");
        this.RegexFilter_Whisper_enabled = getBoolean("RegexFilter.Whisper.Enabled", false);
        this.RegexFilter_Whisper_Logs_enabled = getBoolean("RegexFilter.Whisper.Logs-Enabled", true, "Outputs the player's name and regex when the message is canceled.");
        this.RegexFilter_Whisper_Player_Notify = getBoolean("RegexFilter.Whisper.Player-Notify", true, "Do I inform the player that his message has not been sent? Doesn't work with silent mode.");
        this.RegexFilter_Whisper_Silent_mode = getBoolean("RegexFilter.Whisper.Silent-Mode", false, "The player will think he is sending messages, but in fact no one will see his messages.");
        this.RegexFilter_Whisper_Case_Insensitive = getBoolean("RegexFilter.Whisper.Case-Insensitive", true, "The search for matches will be case insensitive. Eliminates many regex bypasses with capslocks.");
        this.RegexFilter_Whisper_Banned_Regex = getList("RegexFilter.Whisper.Banned-Regex", Collections.singletonList("^This is a(.*)banned message"), "Prevents any message that starts with \"This is a\" and ends with \"banned message\"");
        config.addSection("ReadableFormatting");
        config.addDefault("ReadableFormatting", null, "Automatically puts a period at the end of a sentence and a capital letter at the beginning of a sentence.");
        this.Readable_Formatting_Enable = getBoolean("ReadableFormatting.Enable", false);
        this.Readable_Formatting_Last_Char = getString("ReadableFormatting.End-Sentence-Chars", ".?!", "If there are these characters at the end of the sentence, the plugin will not automatically put a period.");
        this.Readable_Formatting_Public_Chat_Auto_Caps = getBoolean("ReadableFormatting.PublicChat.Auto-Caps", true);
        this.Readable_Formatting_Public_Chat_Auto_Dot = getBoolean("ReadableFormatting.PublicChat.Auto-Dot", true);
        this.Readable_Formatting_Whisper_Auto_Dot = getBoolean("ReadableFormatting.Whisper.Auto-Dot", true);
        this.Readable_Formatting_Whisper_Auto_Caps = getBoolean("ReadableFormatting.Whisper.Auto-Caps", true);
    }

    private void createFiles() {
        try {
            File parent = new File(configFile.getParent());
            if (!parent.exists()) {
                if (!parent.mkdir())
                    logger.severe("Unable to create plugin directory.");
            }
            if (!configFile.exists()) {
                if (!configFile.createNewFile())
                    logger.severe("Unable to create config file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            config = ConfigFile.loadConfig(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (IOException e) {
            logger.severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
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

    public double getDouble(String path, Double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(String path, Double def) {
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

    public ConfigSection getConfigurationSection(String path) {
        return config.getConfigSection(path);
    }

    public void reloadConfig(Plugin plugin, String configFile) {
        try {
            config.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
