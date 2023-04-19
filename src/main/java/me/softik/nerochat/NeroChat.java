package me.softik.nerochat;

import lombok.Getter;
import me.softik.nerochat.utils.*;
import me.softik.nerochat.api.NeroChatAPI;
import me.softik.nerochat.commands.MainCommand;
import me.softik.nerochat.commands.ignore.HardIgnoreCommand;
import me.softik.nerochat.commands.ignore.IgnoreListCommand;
import me.softik.nerochat.commands.toggle.ToggleChatCommand;
import me.softik.nerochat.commands.toggle.ToggleWhisperingCommand;
import me.softik.nerochat.commands.whisper.LastCommand;
import me.softik.nerochat.commands.whisper.ReplyCommand;
import me.softik.nerochat.commands.whisper.WhisperCommand;
import me.softik.nerochat.events.ChatEvent;
import me.softik.nerochat.events.QuitEvent;
import net.pistonmaster.pistonutils.logging.PistonLogger;
import net.pistonmaster.pistonutils.update.UpdateChecker;
import net.pistonmaster.pistonutils.update.UpdateParser;
import net.pistonmaster.pistonutils.update.UpdateType;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public final class NeroChat extends JavaPlugin implements Listener {

    private final TempDataTool tempDataTool = new TempDataTool();
    private final SoftIgnoreTool softignoreTool = new SoftIgnoreTool();
    private final CacheTool cacheTool = new CacheTool(this);
    private final IgnoreTool ignoreTool = new IgnoreTool(this);
    private static NeroChat instance;
    private static ConfigCache configCache;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static Logger logger;

    public static NeroChat getInstance()  {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        NeroChatAPI.setInstance(this);

        Logger log = getLogger();
        Server server = getServer();

        log.info("                                                             ");
        log.info("███╗░░██╗███████╗██████╗░░█████╗░░█████╗░██╗░░██╗░█████╗░████████╗");
        log.info("████╗░██║██╔════╝██╔══██╗██╔══██╗██╔══██╗██║░░██║██╔══██╗╚══██╔══╝");
        log.info("██╔██╗██║█████╗░░██████╔╝██║░░██║██║░░╚═╝███████║███████║░░░██║░░░");
        log.info("██║╚████║██╔══╝░░██╔══██╗██║░░██║██║░░██╗██╔══██║██╔══██║░░░██║░░░");
        log.info("██║░╚███║███████╗██║░░██║╚█████╔╝╚█████╔╝██║░░██║██║░░██║░░░██║░░░");
        log.info("╚═╝░░╚══╝╚══════╝╚═╝░░╚═╝░╚════╝░░╚════╝░╚═╝░░╚═╝╚═╝░░╚═╝░░░╚═╝░░░");
        log.info("                                                             ");

        log.info("Loading config");
        reloadNeroChat();
        log.info("Registering commands");
        PluginCommand ignore = server.getPluginCommand("ignore");
        PluginCommand whisper = server.getPluginCommand("whisper");
        PluginCommand reply = server.getPluginCommand("reply");
        PluginCommand last = server.getPluginCommand("last");
        PluginCommand ignorelist = server.getPluginCommand("ignorelist");
        PluginCommand toggleWhispering = server.getPluginCommand("togglewhispering");
        PluginCommand toggleChat = server.getPluginCommand("togglechat");
        PluginCommand main = server.getPluginCommand("nerochat");

        assert ignore != null;
        assert whisper != null;
        assert reply != null;
        assert last != null;
        assert ignorelist != null;
        assert toggleWhispering != null;
        assert toggleChat != null;
        assert main != null;

        ignore.setExecutor(new HardIgnoreCommand(this));
        ignore.setTabCompleter(new HardIgnoreCommand(this));

        whisper.setExecutor(new WhisperCommand(this));
        whisper.setTabCompleter(new WhisperCommand(this));

        reply.setExecutor(new ReplyCommand(this));
        reply.setTabCompleter(new ReplyCommand(this));

        last.setExecutor(new LastCommand(this));
        last.setTabCompleter(new LastCommand(this));

        ignorelist.setExecutor(new IgnoreListCommand(this));
        ignorelist.setTabCompleter(new IgnoreListCommand(this));

        toggleWhispering.setExecutor(new ToggleWhisperingCommand(this));
        toggleWhispering.setTabCompleter(new ToggleWhisperingCommand(this));

        toggleChat.setExecutor(new ToggleChatCommand(this));
        toggleChat.setTabCompleter(new ToggleChatCommand(this));

        main.setExecutor(new MainCommand(this));
        main.setTabCompleter(new MainCommand(this));

        log.info("Registering listeners");
        server.getPluginManager().registerEvents(new ChatEvent(this), this);
        server.getPluginManager().registerEvents(new QuitEvent(this), this);

        log.info("Checking for a newer version...");
        if (NeroChat.getPlugin(NeroChat.class).getConfig().getBoolean("notify-updates")) {
            new UpdateChecker(new PistonLogger(getLogger())).getVersion("https://raw.githubusercontent.com/ImNotSoftik/NeroChat/master/src/main/resources/version", version ->
                    new UpdateParser(getDescription().getVersion(), version).parseUpdate(updateType -> {
                        if (updateType == UpdateType.NONE || updateType == UpdateType.AHEAD) {
                            log.info("You're up to date!");
                        } else {
                            if (updateType == UpdateType.MAJOR) {
                                log.info("There is a MAJOR update available!");
                            } else if (updateType == UpdateType.MINOR) {
                                log.info("There is a MINOR update available!");
                            } else if (updateType == UpdateType.PATCH) {
                                log.info("There is a PATCH update available!");
                            }
                            log.warning("****************************************");
                            log.warning("The new NeroChat update was found, please update!");
                            log.warning("https://github.com/ImNotSoftik/NeroChat/releases");
                            log.warning("Current version: " + this.getDescription().getVersion() + " New version: " + version);
                            log.warning("****************************************");
                        }
                    }));
        } else {
            log.info("Checking for a newer version is disabled in the config. Skip it");
        }
        if (NeroChat.getPlugin(NeroChat.class).getConfig().getBoolean("bstats-metrics")) {
            log.info("Loading metrics");
            new Metrics(this, 18215);
        } else {
            log.info("Metrics are disabled in the config");
        }

        log.info("The plugin is ready to work!");
    }

    public void reloadNeroChat() {
        reloadLang();
        configCache = new ConfigCache();
        configCache.saveConfig();

        HandlerList.unregisterAll((Plugin) this);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.cancelTasks(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(instance.getDataFolder()+ "/lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                logger.info(String.format("Found language file for %s", localeString));
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            }
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if(!languageCacheMap.containsKey(localeString)) {
                        logger.info(String.format("Found language file for %s", localeString));
                        LanguageCache langCache = new LanguageCache(localeString);
                        languageCacheMap.put(localeString, langCache);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }
    private Set<String> getDefaultLanguageFiles(){
        Reflections reflections = new Reflections("lang", Scanners.Resources);
        return reflections.getResources(Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)"));
    }

    public static LanguageCache getLang(String lang) {
        lang = lang.replace("-", "_");
        if (configCache.auto_lang) {
            return languageCacheMap.getOrDefault(lang, languageCacheMap.get(configCache.default_lang));
        } else {
            return languageCacheMap.get(configCache.default_lang);
        }
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            return getLang(player.getLocale());
        } else {
            return getLang(configCache.default_lang);
        }
    }

    public static ConfigCache getConfiguration() {
        return configCache;
    }

}
