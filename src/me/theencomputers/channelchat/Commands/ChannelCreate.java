package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

import static java.lang.Float.parseFloat;

public class ChannelCreate implements SubCommand{
    @Override
    public boolean verifyCommand(CommandSender sender, String[] args) {
        if(args.length >= 5)
            return true;
        return false;
    }

    @Override
    public void executeSubCommand(CommandSender sender, String[] args) {
        if(cm.doesChannelExist(args[1])) {
            sender.sendMessage(cfg.applyPlaceholders(ConfigHandler.CHANNEL_ALREADY_EXISTS, new String[]{args[1]}));
            return;
        }
        String formatString = "";
        for (int i = 4; i < args.length - 1; i++){
            formatString += args[i] + " ";
        }
        formatString += args[args.length-1];
        ChannelInfo c = cm.addChannel(args[1], args[2],parseFloat(args[3]), formatString, true);
        sender.sendMessage(cfg.applyPlaceholders(ConfigHandler.CHANNEL_CREATED, new String[]{c.name}));

    }
}
