/*
*   Author:         Theencomputers
*   Date:           7/17/2024
*   Description:    Handles the command for /channel
* */
package me.theencomputers.channelchat.Commands;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.theencomputers.channelchat.utils.ConfigHandler;
import me.theencomputers.channelchat.utils.SqlHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Float.parseFloat;

public class Channel implements CommandExecutor {
private static HashMap<String, SubCommand> subCmdExec = new HashMap<>();
    public void init(){
        subCmdExec.put("create", new ChannelCreate());
        subCmdExec.put("remove", new ChannelRemove());
        subCmdExec.put("leave", new ChannelLeave());
        subCmdExec.put("join", new ChannelJoin());
        subCmdExec.put("modify", new ChannelModify());
    }
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if(args.length > 0 && subCmdExec.containsKey(args[0].toLowerCase()) && subCmdExec.get(args[0].toLowerCase()).verifyCommand(sender, args)){
                subCmdExec.get(args[0].toLowerCase()).executeSubCommand(sender, args);
            }
            else {
                sender.sendMessage(ConfigHandler.CHANNEL_HELP);
            }
        return true;
    }

}
