package me.softik.nerochat.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.softik.nerochat.NeroChat;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCache {

    private final ConfigFile lang;
    public final String hover_text, page_does_not_exist, error, chat_on, chat_off, pm_on, pm_off, chat_is_off, no_one_ignored,
            ignore, un_ignore, ignore_yourself, player_pm_off, whisper_to, whisper_from, player_only, not_online,
            pm_yourself, ignore_me, no_permissions, player_notify, usage, player_argument, message_argument, ignore_you,
            slowmode_notification, too_many_messages, too_many_similar_messages, too_many_violations, blocked_unicode;

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
        lang = ConfigFile.loadConfig(langYML);

        no_permissions = getTranslation("no-permission", "You don't have permission to use this command.");
        player_notify = getTranslation("player-notify", "Illegal characters were found in your message.");
        ignore_me = getTranslation("ignore-me", "This player ignores you.");
        ignore_you = getTranslation("ignore-you", "You are ignoring this player.");
        not_online = getTranslation("not-online", "This player is not online.");
        player_only = getTranslation("player-only", "You need to be a player to do ");
        whisper_from = getTranslation("Whisper.from", "&d%player%&d whispers: %message%");
        whisper_to = getTranslation("Whisper.to", "&dYou whisper to %player%&d: %message%");
        ignore_yourself = getTranslation("ignore-yourself", "You can't ignore yourself.");
        pm_yourself = getTranslation("pm-yourself", "You cannot write private messages to yourself.");
        ignore = getTranslation("ignore", "&6You ignore a player &3%player%");
        un_ignore = getTranslation("un-ignore", "&6You are no longer ignoring a player &3%player%");
        no_one_ignored = getTranslation("no-one-ignored", "You are not ignoring anyone.");
        page_does_not_exist = getTranslation("page-doesent-exist", "This page doesn't exist.");
        error = getTranslation("error", "There's been a mistake! Please contact the administrator.");
        chat_on = getTranslation("chat-on", "You have enabled public chat.");
        chat_off = getTranslation("chat-off", "You turned off public chat.");
        pm_on = getTranslation("pm-on", "You have enabled personal messages.");
        pm_off = getTranslation("pm-off", "You have turned off personal messages.");
        player_pm_off = getTranslation("player-pm-off", "This player has turned off personal messages.");
        chat_is_off = getTranslation("chat-is-off", "You have disabled public chat. Use /togglechat to turn it back on.");
        usage = getTranslation("usage", "Usage:");
        player_argument = getTranslation("player-argument", "<player>");
        message_argument = getTranslation("message-argument", "<message>");
        hover_text = getTranslation("hover-text", "&6Message &3%player%");
        slowmode_notification = getTranslation("slowmode-notification", "&cYou need to wait a bit before sending another message.");
        too_many_messages = getTranslation("too-many-messages-notification", "&cYou're sending too many messages at a time.");
        too_many_similar_messages = getTranslation("similar-message-notification", "&cYou're sending too many similar messages at a time.");
        too_many_violations = getTranslation("too-many-violations", "&cYou're blocked from spamming.");
        blocked_unicode = getTranslation("blocked-unicode", "&cYour message includes spammy characters, please change it.");

        try {
            lang.save();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save language file: "+ langYML.getName() +" - " + e.getLocalizedMessage());
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
        return lang.getStringList(path)
                .stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }

    public List<String> getListTranslation(String path, List<String> defaultTranslation, String comment) {
        lang.addDefault(path, defaultTranslation, comment);
        return lang.getStringList(path)
                .stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }
}