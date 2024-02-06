package me.softik.nerochat.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    public abstract String label();
    public abstract String description();
    public abstract String syntax();
    public abstract void perform(CommandSender sender, String[] args);
}
