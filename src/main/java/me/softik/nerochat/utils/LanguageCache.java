package me.softik.nerochat.utils;

import me.softik.nerochat.NeroChat;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LanguageCache {
    private final FileConfiguration fileConfiguration;
    public String page_doesent_exist;
    public String error;
    public String chat_on;
    public String chat_off;
    public String pm_on;
    public String pm_off;
    public String chat_is_off;
    public String no_one_ignored;
    public String ignore;
    public String un_ignore;
    public String ignore_yourself;
    public String player_pm_off;
    private String whisper_to;
    private String whisper_from;
    public String player_only;
    public String not_online;
    public String pm_yourself;
    public String ignore_me;
    boolean addedMissing = false;
    public String no_permissions;
    public String player_notify;
    public String ignore_you;
    public List<String> world_stats_message;

    public LanguageCache(String lang) {
        NeroChat plugin = NeroChat.getInstance();
        File langFile = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        }
        try {
            fileConfiguration.load(langFile);
            this.no_permissions = getStringTranslation("no-permission", "You don't have permission to use this command.");
            this.player_notify = getStringTranslation("player-notify", "Illegal characters were found in your message.");
            this.ignore_me = getStringTranslation("ignore-me", "This player ignores you.");
            this.ignore_you = getStringTranslation("ignore-you", "You are ignoring this player.");
            this.not_online = getStringTranslation("not-online", "This player is not online.");
            this.player_only = getStringTranslation("player-only", "You need to be a player to do this.");
            this.whisper_from = getStringTranslation("Whisper.from", "&d%player%&d whispers: %message%");
            this.whisper_to = getStringTranslation("Whisper.to", "&dYou whisper to %player%&d: %message%");
            this.ignore_yourself = getStringTranslation("ignore-yourself", "You can't ignore yourself.");
            this.pm_yourself = getStringTranslation("pm-yourself", "You cannot write private messages to yourself.");
            this.ignore = getStringTranslation("ignore", "&6You ignore a player &3%player%.");
            this.un_ignore = getStringTranslation("un-ignore", "&6You are no longer ignoring a player &3%player%.");
            this.no_one_ignored = getStringTranslation("no-one-ignored", "You are not ignoring anyone.");
            this.page_doesent_exist = getStringTranslation("page-doesent-exist", "This page doesn't exist.");
            this.error = getStringTranslation("error", "There's been a mistake! Please contact the administrator.");
            this.chat_on = getStringTranslation("chat-on", "You have enabled public chat.");
            this.chat_off = getStringTranslation("chat-off", "You turned off public chat.");
            this.pm_on = getStringTranslation("pm-on", "You have enabled personal messages.");
            this.pm_off = getStringTranslation("pm-off", "You have turned off personal messages.");
            this.player_pm_off = getStringTranslation("player-pm-off", "This player has turned off personal messages.");
            this.chat_is_off = getStringTranslation("chat-is-off", "You have disabled public chat. Use /togglechat to turn it back on.");
            if (addedMissing) fileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            NeroChat.getInstance().getLogger().warning("Translation file " + langFile + " is not formatted properly. Skipping it.");
        }
    }

    public List<String> getListTranslation(String path, List<String> defaultTranslation) {
        List<String> translation = fileConfiguration.getStringList(path);
        if (translation.isEmpty()) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }

    public String getStringTranslation(String path, String defaultTranslation) {
        String translation = fileConfiguration.getString(path);
        if (translation == null) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }
}

