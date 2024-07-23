package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelLeave implements SubCommand{
    @Override
    public boolean verifyCommand(CommandSender sender, String[] args) {
        if(args.length == 2 && cm.doesChannelExist(args[1]) && sender instanceof Player){

            return true;
        }

        return false;
    }

    @Override
    public void executeSubCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(!cm.isPlayerInChannel(player, cm.getChannel(args[1]))){
            sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[]{args[1]}));
            return;
        }
        if(!sql.isSafe(args[1])){
            sql.stopInjection(sender, args[1]);
            return;
        }
        if(cm.doesChannelExist(cm.getChannel(args[1]))) {
            cm.removePlayerFromChannel(cm.getChannel(args[1]), player);
            player.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_YOU_LEFT, new String[]{args[1]}));
        }
        else
            sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[]{args[1]}));

    }
}
