package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ChannelManager;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelJoin implements SubCommand{
    @Override
    public boolean verifyCommand(CommandSender sender, String[] args) {
        if(args.length == 2 && sender instanceof Player)
            return true;
        return false;
    }

    @Override
    public void executeSubCommand(CommandSender sender, String[] args) {
        if(cm.doesChannelExist(args[1])){
            ChannelInfo channel = cm.getChannel(args[1]);
            if(!sender.hasPermission(channel.permission)){
                sender.sendMessage(ConfigHandler.NO_PERMISSION);
                return;
            }
                cm.addPlayerToChannel(channel,(Player) sender);
                sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_JOINED, new String[]{channel.name}));
                return;
        }
        else{
            sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[] {args[1]}));
        }
    }
}
