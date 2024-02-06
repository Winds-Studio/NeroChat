package me.softik.nerochat.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.softik.nerochat.NeroChat;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCache {
    private final ConfigFile lang;

    public String hover_text, page_doesent_exist, error, chat_on, chat_off, pm_on, pm_off, chat_is_off, no_one_ignored,
            ignore, un_ignore, ignore_yourself, player_pm_off, whisper_to, whisper_from, player_only, not_online,
            pm_yourself, ignore_me, no_permissions, player_notify, usage, player_argument, message_argument, ignore_you;

    public LanguageCache(String locale) throws Exception {
        NeroChat plugin = NeroChat.getInstance();
        File langYML = new File(plugin.getDataFolder() + File.separator + "lang", locale + ".yml");
        // Check if the lang folder has already been created
        File parent = langYML.getParentFile();
        if (!parent.exists() && !parent.mkdir())
            NeroChat.getLog().severe("Unable to create lang directory.");
        // Check if the file already exists and save the one from the plugins resources folder if it does not
        if (!langYML.exists())
            plugin.saveResource("lang" + File.separator + locale + ".yml", false);
        // Finally, load the lang file with configmaster
        this.lang = ConfigFile.loadConfig(langYML);

        this.no_permissions = getTranslation("no-permission", "You don't have permission to use this command.");
        this.player_notify = getTranslation("player-notify", "Illegal characters were found in your message.");
        this.ignore_me = getTranslation("ignore-me", "This player ignores you.");
        this.ignore_you = getTranslation("ignore-you", "You are ignoring this player.");
        this.not_online = getTranslation("not-online", "This player is not online.");
        this.player_only = getTranslation("player-only", "You need to be a player to do this.");
        this.whisper_from = getTranslation("Whisper.from", "&d%player%&d whispers: %message%");
        this.whisper_to = getTranslation("Whisper.to", "&dYou whisper to %player%&d: %message%");
        this.ignore_yourself = getTranslation("ignore-yourself", "You can't ignore yourself.");
        this.pm_yourself = getTranslation("pm-yourself", "You cannot write private messages to yourself.");
        this.ignore = getTranslation("ignore", "&6You ignore a player &3%player%");
        this.un_ignore = getTranslation("un-ignore", "&6You are no longer ignoring a player &3%player%");
        this.no_one_ignored = getTranslation("no-one-ignored", "You are not ignoring anyone.");
        this.page_doesent_exist = getTranslation("page-doesent-exist", "This page doesn't exist.");
        this.error = getTranslation("error", "There's been a mistake! Please contact the administrator.");
        this.chat_on = getTranslation("chat-on", "You have enabled public chat.");
        this.chat_off = getTranslation("chat-off", "You turned off public chat.");
        this.pm_on = getTranslation("pm-on", "You have enabled personal messages.");
        this.pm_off = getTranslation("pm-off", "You have turned off personal messages.");
        this.player_pm_off = getTranslation("player-pm-off", "This player has turned off personal messages.");
        this.chat_is_off = getTranslation("chat-is-off", "You have disabled public chat. Use /togglechat to turn it back on.");
        this.usage = getTranslation("usage", "Usage:");
        this.player_argument = getTranslation("player-argument", "<player>");
        this.message_argument = getTranslation("message-argument", "<message>");
        this.hover_text = getTranslation("hover-text", "&6Message &3%player%");

        try {
            lang.save();
        } catch (Exception e) {
            NeroChat.getLog().severe("Failed to save language file: "+ langYML.getName() +" - " + e.getLocalizedMessage());
        }
    }

    public String getTranslation(String path, String defaultTranslation) {
        lang.addDefault(path, defaultTranslation);
        return ChatColor.translateAlternateColorCodes('&', lang.getString(path, defaultTranslation));
    }

    public String getTranslation(String path, String defaultTranslation, String comment) {
        lang.addDefault(path, defaultTranslation, comment);
        return ChatColor.translateAlternateColorCodes('&', lang.getString(path, defaultTranslation));
    }

    public List<String> getListTranslation(String path, List<String> defaultTranslation) {
        lang.addDefault(path, defaultTranslation);
        return lang.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }

    public List<String> getListTranslation(String path, List<String> defaultTranslation, String comment) {
        lang.addDefault(path, defaultTranslation, comment);
        return lang.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }
}

