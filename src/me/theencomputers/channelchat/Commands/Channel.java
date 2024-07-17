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
import utils.ConfigHandler;
import utils.SqlHandler;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Float.parseFloat;

public class Channel implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if(args.length == 2 && args[0].equalsIgnoreCase("join") && sender instanceof Player){
                Player player = (Player) sender;
                if(!SqlHandler.isSafe(args[1])){
                    SqlHandler.stopInjection(sender, args[1]);
                    return true;
                }
                try {
                    if(Main.channelNameToInfo.containsKey(args[1]) || SqlHandler.retrieveChannel(args[1])){
                        ChannelInfo channel = Main.channelNameToInfo.get(args[1]);
                        if(!sender.hasPermission(channel.permission)){
                            sender.sendMessage(ConfigHandler.NO_PERMISSION);
                            return true;
                        }
                            if(addPlayerToChannel(player, channel)) {

                                pushPlayer(player);
                                sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_JOINED, new String[]{channel.name}));
                                return true;
                            }
                            else{
                                sender.sendMessage(ConfigHandler.NO_PERMISSION);
                                return true;
                            }
                        }
                        else{
                            sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[] {args[1]}));
                        }
                } catch (SQLException e) {
                }

            }

            else if(args.length >= 5 && args[0].equalsIgnoreCase("create")){
                if(!sender.hasPermission(ConfigHandler.CHANNEL_CREATE_PERMISSION)){
                    sender.sendMessage(ConfigHandler.NO_PERMISSION);
                    return  true;
                }
                if(!SqlHandler.isSafe(args[1])){
                    SqlHandler.stopInjection(sender, args[1]);
                    return true;
                }
                try{
                    if(Main.channelNameToInfo.containsKey(args[1]) || SqlHandler.retrieveChannel(args[1])){
                        sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_ALREADY_EXISTS, new String[]{args[1]}));
                        return true;
                    }
                }catch (SQLException ex){}
                ChannelInfo c = new ChannelInfo();
                c.name = args[1];
                c.permission = args[2];
                c.radius = parseFloat(args[3]);
                String formatString = "";
                for (int i = 4; i < args.length - 1; i++){
                    formatString += args[i] + " ";
                }
                formatString += args[args.length-1];
                c.format = formatString;
                addChannel(c);
                sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_CREATED, new String[]{c.name}));

        }
            else if(args.length >= 5 && args[0].equalsIgnoreCase("modify")){
                if(!sender.hasPermission(ConfigHandler.CHANNEL_MODIFY_PERMISSION)){
                    sender.sendMessage(ConfigHandler.NO_PERMISSION);
                    return  true;
                }
                if(!SqlHandler.isSafe(args[1])){
                    SqlHandler.stopInjection(sender, args[1]);
                    return true;
                }
                try{
                    if(!Main.channelNameToInfo.containsKey(args[1]) && !SqlHandler.retrieveChannel(args[1])){
                        sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[]{args[1]}));
                        return true;
                    }
                }catch (SQLException ex){}
                ChannelInfo c = new ChannelInfo();
                c.name = args[1];
                c.permission = args[2];
                c.radius = parseFloat(args[3]);
                String formatString = "";
                for (int i = 4; i < args.length - 1; i++){
                    formatString += args[i] + " ";
                }
                formatString += args[args.length-1];
                c.format = formatString;
                pushChannel(c);
                sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_MODIFIED, new String[]{c.name}));
            }
            else if(args.length == 2 && args[0].equalsIgnoreCase("leave") && sender instanceof Player){
                Player player = (Player) sender;
                if(!SqlHandler.isSafe(args[1])){
                    SqlHandler.stopInjection(sender, args[1]);
                    return true;
                }
                if(Main.channelNameToInfo.containsKey(args[1]) && Main.playerToChannelList.get(player).contains(Main.channelNameToInfo.get(args[1]))){
                        removePlayerFromChannel(player, Main.channelNameToInfo.get(args[1]));
                }
                else{
                    sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[]{args[1]}));
                }

            }
            else if(args.length == 2 && args[0].equalsIgnoreCase("remove")){
                if(!SqlHandler.isSafe(args[1])){
                    SqlHandler.stopInjection(sender, args[1]);
                    return true;
                }
                if(sender.hasPermission(ConfigHandler.CHANNEL_REMOVE_PERMISSION)){
                    try {
                        if(Main.channelNameToInfo.containsKey(args[1]) || SqlHandler.retrieveChannel(args[1])){
                            if(removeChannel(Main.channelNameToInfo.get(args[1])))
                                sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_DELETED, new String[]{args[1]}));
                            else
                                sender.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_NO_CHANNEL, new String[]{args[1]}));
                        }
                    } catch (SQLException e) {
                    }
                }
            }
            else{
                sender.sendMessage(ConfigHandler.CHANNEL_HELP);
            }
            return true;
    }
    public static void addChannel(ChannelInfo c){
        Main.channelNameToInfo.put(c.name, c);
        pushChannel(c);
    }
    public static boolean removeChannel(ChannelInfo c){
        if(Main.channelNameToInfo.containsKey(c))
            Main.channelNameToInfo.remove(c.name);
        for(Player p: Bukkit.getOnlinePlayers()) {
            if (Main.playerToChannelList.containsKey(p) && Main.playerToChannelList.get(p).contains(c)) {
                Main.playerToChannelList.get(p).remove(c);
            }
            if (Main.playerToMainChannel.containsKey(p) && Main.playerToMainChannel.get(p).equals(c)){
                p.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHANNEL_YOU_LEFT, new String[]{c.name}));
                ChannelInfo blankChannel = new ChannelInfo();
                blankChannel.format = "";
                blankChannel.permission = "";
                blankChannel.name = "";
                blankChannel.radius = 0;
                Main.playerToMainChannel.replace(p, blankChannel);
            }
        }
        try {
            SqlHandler.removeChannel(c.name);
        }catch (SQLException ex){
            return false;
        }
        return true;
    }
    public static boolean pushPlayer(Player p){
    try {
        SqlHandler.addPlayer(p, Main.playerToMainChannel.get(p), Main.playerToChannelList.get(p));
    }
    catch (SQLException ex){
        return false;
    }
    return true;
    }
    public static boolean pushChannel(ChannelInfo c) {
        try{
            SqlHandler.addChannel(c);
            return true;
        }
        catch(SQLException ex){
            return false;
        }
    }
    public static boolean addPlayerToChannel(Player p, ChannelInfo c){
        if(!p.hasPermission(c.permission))
            return false;
        if(Main.playerToMainChannel.containsKey(p))
            Main.playerToMainChannel.replace(p, c);
        else
            Main.playerToMainChannel.put(p, c);

        if(Main.playerToChannelList.containsKey(p)) {
            if (!Main.playerToChannelList.get(p).contains(c))
                Main.playerToChannelList.get(p).add(c);
            else {
                ArrayList<ChannelInfo> cList = new ArrayList<>();
                cList.add(c);
                Main.playerToChannelList.replace(p, cList);
            }
        }
        else{
            ArrayList<ChannelInfo> cList = new ArrayList<>();
            cList.add(c);
            Main.playerToChannelList.put(p, cList);
        }
        pushPlayer(p);
        return true;
    }
    public static void removePlayerFromChannel(Player p, ChannelInfo c){
        ChannelInfo blankChannel = new ChannelInfo();
        blankChannel.format = "";
        blankChannel.permission = "";
        blankChannel.name = "";
        blankChannel.radius = 0;
        if(Main.playerToMainChannel.containsKey(p) && Main.playerToMainChannel.get(p).equals(c))
            Main.playerToMainChannel.replace(p, blankChannel);
        if(Main.playerToChannelList.containsKey(p) && Main.playerToChannelList.get(p).contains(c))
            Main.playerToChannelList.get(p).remove(c);
        pushPlayer(p);
    }
    public static ChannelInfo getChannel(String channelName){
        if(Main.channelNameToInfo.containsKey(channelName)){
            return Main.channelNameToInfo.get(channelName);
        }
        else{
            try {
                SqlHandler.retrieveChannel(channelName);

            }
            catch (SQLException e){
                e.printStackTrace();
            }
            if(Main.channelNameToInfo.containsKey(channelName)){
                return Main.channelNameToInfo.get(channelName);
            }
            else{
                return null;
            }
        }
    }
}
