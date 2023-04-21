package me.softik.nerochat.commands.ignore;

import com.google.common.math.IntMath;
import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.utils.CommonTool;
import me.softik.nerochat.utils.IgnoreTool;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class IgnoreListCommand implements CommandExecutor, TabExecutor {
    private final NeroChat plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            List<String> list = new ArrayList<>();

            for (OfflinePlayer offlinePlayer : plugin.getIgnoreTool().getIgnoredPlayers(player).keySet()) {
                list.add(offlinePlayer.getName());
            }

            if (list.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', NeroChat.getLang(sender).no_one_ignored));
            } else {
                if (args.length > 0) {
                    try {
                        int page = Integer.parseInt(args[0]);

                        if (page < plugin.getIgnoreTool().getIgnoredPlayers(player).size()) {
                            showList(page, player);
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', CommonTool.getPrefix() + NeroChat.getLang(player).page_doesent_exist));
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CommonTool.getPrefix() + NeroChat.getLang(player).error));
                    }
                } else {
                    showList(1, player);
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NeroChat.getLang(sender).player_only));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    private void showList(int page, Player player) {
        int maxValue = page * NeroChat.getConfiguration().getInt("Main.ignore-list-size", 9);
        int minValue = maxValue - NeroChat.getConfiguration().getInt("Main.ignore-list-size", 9);

        Map<OfflinePlayer, IgnoreTool.IgnoreType> map = plugin.getIgnoreTool().getIgnoredPlayers(player);

        int allPages = IntMath.divide(map.size(), NeroChat.getConfiguration().getInt("Main.ignore-list-size", 9), RoundingMode.CEILING);

        ComponentBuilder navigation = new ComponentBuilder("").color(ChatColor.GOLD);
        navigation.append("[<]").color(page > 1 ? ChatColor.AQUA : ChatColor.GRAY);

        if (page > 1) {
            navigation.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignorelist " + (page - 1)));
            navigation.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to go to the previous page!").color(ChatColor.GOLD).create()));
        }

        navigation.append(" " + page + "/" + allPages + " ").reset().color(ChatColor.GOLD);

        navigation.append("[>]").color(allPages > page ? ChatColor.AQUA : ChatColor.GRAY);

        if (allPages > page) {
            navigation.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignorelist " + (page + 1)));
            navigation.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to go to the next page!").color(ChatColor.GOLD).create()));
        }

        player.spigot().sendMessage(navigation.create());

        int i = 0;

        for (Map.Entry<OfflinePlayer, IgnoreTool.IgnoreType> entry : map.entrySet()) {
            if (i >= minValue && i < maxValue) {
                ComponentBuilder playerBuilder = new ComponentBuilder(entry.getKey().getName());

                playerBuilder.append(" ").reset();

                playerBuilder.append("[");

                playerBuilder.color(ChatColor.GRAY);

                if (entry.getValue() == IgnoreTool.IgnoreType.HARD) {
                    playerBuilder.append("Ｘ");

                    playerBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore " + ChatColor.stripColor(entry.getKey().getName())));
                }
                playerBuilder.color(ChatColor.RED);

                playerBuilder.append("]").reset();

                playerBuilder.color(ChatColor.GRAY);

                player.spigot().sendMessage(playerBuilder.create());
            }

            i++;
        }
    }
}
