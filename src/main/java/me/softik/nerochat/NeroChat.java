package me.softik.nerochat;

import lombok.Getter;
import me.softik.nerochat.utils.*;
import net.md_5.bungee.api.ChatColor;
import me.softik.nerochat.api.NeroChatAPI;
import me.softik.nerochat.commands.MainCommand;
import me.softik.nerochat.commands.ignore.HardIgnoreCommand;
import me.softik.nerochat.commands.ignore.IgnoreListCommand;
import me.softik.nerochat.commands.ignore.SoftIgnoreCommand;
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
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

@Getter
public final class NeroChat extends JavaPlugin {
    private final ConfigManager config = new ConfigManager(this, "config.yml");
    private final ConfigManager language = new ConfigManager(this, "language.yml");
    private final TempDataTool tempDataTool = new TempDataTool();
    private final SoftIgnoreTool softignoreTool = new SoftIgnoreTool();
    private final CacheTool cacheTool = new CacheTool(this);
    private final IgnoreTool ignoreTool = new IgnoreTool(this);
    private final ConfigTool configTool = new ConfigTool(this);

    @Override
    public void onEnable() {
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
        try {
            config.create();
            language.create();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        log.info("Registering commands");
        PluginCommand ignorehard = server.getPluginCommand("ignorehard");
        PluginCommand ignore = server.getPluginCommand("ignore");
        PluginCommand whisper = server.getPluginCommand("whisper");
        PluginCommand reply = server.getPluginCommand("reply");
        PluginCommand last = server.getPluginCommand("last");
        PluginCommand ignorelist = server.getPluginCommand("ignorelist");
        PluginCommand toggleWhispering = server.getPluginCommand("togglewhispering");
        PluginCommand toggleChat = server.getPluginCommand("togglechat");
        PluginCommand main = server.getPluginCommand("nerochat");

        assert ignorehard != null;
        assert ignore != null;
        assert whisper != null;
        assert reply != null;
        assert last != null;
        assert ignorelist != null;
        assert toggleWhispering != null;
        assert toggleChat != null;
        assert main != null;

        ignorehard.setExecutor(new HardIgnoreCommand(this));
        ignorehard.setTabCompleter(new HardIgnoreCommand(this));

        ignore.setExecutor(new SoftIgnoreCommand(this));
        ignore.setTabCompleter(new SoftIgnoreCommand(this));

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

        log.info("Checking for a newer version");
        new UpdateChecker(new PistonLogger(getLogger())).getVersion("https://www.neromaster.net/NeroChat/VERSION.txt", version ->
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

                        log.info("Current version: " + this.getDescription().getVersion() + " New version: " + version);
                        log.info("Download it at: https://github.com/AlexProgrammerDE/NeroChat/releases");
                    }
                }));
        if (NeroChat.getPlugin(NeroChat.class).getConfig().getBoolean("bstats-metrics")) {
            log.info("Loading metrics");
            new Metrics(this, 18215);
        } else {
            log.info("Metrics are disabled in the config");
        }

        log.info("The plugin is ready to work!");
    }

    @Override
    public FileConfiguration getConfig() {
        return config.get();
    }

    public FileConfiguration getLanguage() {
        return language.get();
    }
}
