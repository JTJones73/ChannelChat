package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;
import org.bukkit.command.CommandSender;

import static java.lang.Float.parseFloat;

public class ChannelModify implements SubCommand{
    @Override
    public boolean verifyCommand(CommandSender sender, String[] args) {
        if(args.length < 5 || !args[3].matches("[-+]?[0-9]*\\.?[0-9]+") || !cm.doesChannelExist(args[1]))
            return false;
        return true;
    }

    @Override
    public void executeSubCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(ConfigHandler.CHANNEL_MODIFY_PERMISSION)){
            sender.sendMessage(ConfigHandler.NO_PERMISSION);
            return;
        }
        if(!sql.isSafe(args[1])){
            sql.stopInjection(sender, args[1]);
            return;
        }
        if(!cm.doesChannelExist(args[1])){
            sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[]{args[1]}));
            return;
        }
        String formatString = "";
        for (int i = 4; i < args.length - 1; i++){
            formatString += args[i] + " ";
        }
        formatString += args[args.length-1];
        ChannelInfo c = cm.getChannel(args[1]);
        cm.modifyChannel(c,args[2],parseFloat(args[3]), formatString);
        sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_MODIFIED, new String[]{args[1]}));
    }
}
