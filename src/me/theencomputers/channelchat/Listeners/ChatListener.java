/*
 *   Date:           7/17/2024
 *   Description:    The until handles the SQL database that is used to store channel and player information.
 * */
package me.theencomputers.channelchat.Listeners;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import org.bukkit.event.Listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;
import utils.ConfigHandler;


public class ChatListener implements Listener{
	public static ArrayList<String> Muted= new ArrayList<String>();

        
    @EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
        //e.getPlayer().sendMessage("Used chat for message: " + e.getMessage());                  //DEBUG
        e.setCancelled(true);

        boolean anyoneListening = false;
        if (!Main.playerToMainChannel.containsKey(e.getPlayer()) || Main.playerToMainChannel.get(e.getPlayer()) == null || Main.playerToMainChannel.get(e.getPlayer()).name.equals("")) {
            e.getPlayer().sendMessage(ConfigHandler.CHAT_NO_CHANNELS);
        }

        else {
            if(Main.playerToMainChannel.containsKey(e.getPlayer()) && !e.getPlayer().hasPermission(Main.playerToMainChannel.get(e.getPlayer()).permission)){
                e.getPlayer().sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHAT_NO_PERMISSION, new String[]{Main.playerToMainChannel.get(e.getPlayer()).name}));
            }
            else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    boolean sendPlayer = false;
                    for (ChannelInfo c : Main.playerToChannelList.get(p)) {

                        if (c.name.equals(Main.playerToMainChannel.get(e.getPlayer()).name) && p.hasPermission(Main.playerToMainChannel.get(e.getPlayer()).permission)) {
                            //range check
                            //Bukkit.getConsoleSender().sendMessage(p.getName() + c.name + c.permission + c.format + c.radius + (c.radius < 0) + " " + (p.getLocation().getWorld().equals(e.getPlayer().getLocation().getWorld()) && p.getLocation().distance(e.getPlayer().getLocation()) <= c.radius));
                            if (c.radius < 0 || (p.getLocation().getWorld().equals(e.getPlayer().getLocation().getWorld()) && p.getLocation().distance(e.getPlayer().getLocation()) <= c.radius)) {
                                sendPlayer = true;
                                if (!p.equals(e.getPlayer()))
                                    anyoneListening = true;
                                break;
                            }
                        }
                    }
                    if (sendPlayer)
                        p.sendMessage(Main.playerToMainChannel.get(e.getPlayer()).format.replace("%MESSAGE%", e.getMessage())
                                .replace("%PLAYER%", e.getPlayer().getDisplayName())
                                .replace("&", "§"));
                }

                if (!anyoneListening)
                    e.getPlayer().sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHAT_NO_LISTENERS, new String[]{Main.playerToMainChannel.get(e.getPlayer()).name}));
            }
            Bukkit.getConsoleSender().sendMessage(Main.playerToMainChannel.get(e.getPlayer()).format.replace("%MESSAGE%", e.getMessage())
                    .replace("%PLAYER%", e.getPlayer().getDisplayName())
                    .replace("&", "§"));
        }
    }
}