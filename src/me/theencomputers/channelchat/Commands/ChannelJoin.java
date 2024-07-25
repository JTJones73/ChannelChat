package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Listeners.ChatListener;
import me.theencomputers.channelchat.Listeners.JoinAndLeave;
import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ChannelManager;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChannelJoin implements SubCommand{
    @Override
    public boolean verifyCommand(CommandSender sender, String[] args) {
        if(args.length == 2 && sender instanceof Player)
            return true;
        return false;
    }

    @Override
    public void executeSubCommand(CommandSender sender, String[] args) {
        sql.retrieveChannel(args[1]);
            //if 20 ticks isn't enough then it's time to give up
            new BukkitRunnable() {
                @Override
                public void run () {
                    ChannelInfo channel = cm.getChannel(args[1]);
                    if(channel == null){
                        sender.sendMessage(cfg.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[] {args[1]}));
                        return;
                    }
                    if(!sender.hasPermission(channel.permission)){
                        sender.sendMessage(ConfigHandler.NO_PERMISSION);
                        return;
                    }
                    cm.addPlayerToChannel(channel,(Player) sender, true);
                    sender.sendMessage(cfg.applyPlaceholders(ConfigHandler.CHANNEL_JOINED, new String[]{channel.name}));
                    return;

                }
            }.runTaskLater(Main.getInstance(), 20);


    }
}
