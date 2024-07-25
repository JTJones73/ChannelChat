package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.utils.ChannelManager;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommand {
    ChannelManager cm = new ChannelManager();
    SqlHandler sql = new SqlHandler();
    ConfigHandler cfg = new ConfigHandler();
    boolean verifyCommand(CommandSender sender, String [] args);
    void executeSubCommand(CommandSender sender, String [] args);
}
