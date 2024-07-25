package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class ChannelRemove implements SubCommand{

    @Override
    public boolean verifyCommand(CommandSender sender, String[] args) {
        if(args.length == 2)
            return true;
        return false;
    }

    @Override
    public void executeSubCommand(CommandSender sender, String[] args) {
        if(!sql.isSafe(args[1])){
            sql.stopInjection(sender, args[1]);
            return;
        }
        if(sender.hasPermission(ConfigHandler.CHANNEL_REMOVE_PERMISSION)){
                if(cm.doesChannelExist(args[1])){
                        cm.removeChannel(cm.getChannel(args[1]));
                        sender.sendMessage(cfg.applyPlaceholders(ConfigHandler.CHANNEL_DELETED, new String[]{args[1]}));
                    }
                    else
                        sender.sendMessage(cfg.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[]{args[1]}));
                }
        }
    }

