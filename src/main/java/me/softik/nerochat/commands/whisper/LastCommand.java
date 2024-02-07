package me.softik.nerochat.commands.whisper;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.NeroChatCommand;
import me.softik.nerochat.tools.CommonTool;
import me.softik.nerochat.tools.IgnoreTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class LastCommand implements NeroChatCommand {

    private final IgnoreTool ignoreTool;

    public LastCommand() {
        this.ignoreTool = NeroChat.getIgnoreTool();
    }

    @Override
    public String label() {
        return "last";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return NO_COMPLETIONS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(NeroChat.getLang(sender).usage + " /"+label()+" " + NeroChat.getLang(sender).message_argument);
            return false;
        }

        Optional<CommandSender> lastSentTo = NeroChat.getCacheTool().getLastSentTo(sender);

        if (lastSentTo.isPresent()) {
            if (ignoreTool.isIgnored(sender, lastSentTo.get())) {
                sender.sendMessage(CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_me);
            } else if (ignoreTool.isIgnored(lastSentTo.get(), sender)) {
                sender.sendMessage(CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_you);
            } else {
                CommonTool.sendWhisperTo(sender, CommonTool.mergeArgs(args, 0), lastSentTo.get());
            }
            return true;
        }

        Optional<CommandSender> lastMessagedOf = NeroChat.getCacheTool().getLastMessagedOf(sender);

        if (lastMessagedOf.isPresent()) {
            if (ignoreTool.isIgnored(sender, lastMessagedOf.get())) {
                sender.sendMessage(CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_me);
            } else if (ignoreTool.isIgnored(lastMessagedOf.get(), sender)) {
                sender.sendMessage(CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_you);
            } else {
                CommonTool.sendWhisperTo(sender, CommonTool.mergeArgs(args, 0), lastMessagedOf.get());
            }
            return true;
        }

        sender.sendMessage(NeroChat.getLang(sender).not_online);
        return true;
    }
}