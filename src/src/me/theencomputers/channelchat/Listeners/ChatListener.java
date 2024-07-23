/*
 *   Date:           7/17/2024
 *   Description:    The until handles the SQL database that is used to store channel and player information.
 * */
package me.theencomputers.channelchat.Listeners;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ChannelManager;
import org.bukkit.event.Listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;
import me.theencomputers.channelchat.utils.ConfigHandler;


public class ChatListener implements Listener{
	public static ArrayList<String> Muted= new ArrayList<String>();
    ChannelManager cm = new ChannelManager();

        
    @EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
        //e.getPlayer().sendMessage("Used chat for message: " + e.getMessage());                  //DEBUG
        e.setCancelled(true);
        Player player = e.getPlayer();
        boolean anyoneListening = false;
        if (!cm.isPlayerInChannel(player)) {
            e.getPlayer().sendMessage(ConfigHandler.CHAT_NO_CHANNELS);
        }

        else {
            if(!player.hasPermission(cm.getMainChannel(player).permission)){
                player.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHAT_NO_PERMISSION, new String[]{cm.getMainChannel(player).name}));
            }
            else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    boolean sendPlayer = false;
                    for (ChannelInfo c : cm.getChannelList(p)) {

                        if (c!= null && c.name.equals(cm.getMainChannel(player).name) && p.hasPermission(cm.getMainChannel(player).permission)) {
                            //range check
                            //Bukkit.getConsoleSender().sendMessage(p.getName() + c.name + c.permission + c.format + c.radius + (c.radius < 0) + " " + (p.getLocation().getWorld().equals(e.getPlayer().getLocation().getWorld()) && p.getLocation().distance(e.getPlayer().getLocation()) <= c.radius));
                            if (c.radius < 0 || (p.getLocation().getWorld().equals(player.getLocation().getWorld()) && p.getLocation().distance(player.getLocation()) <= c.radius)) {
                                sendPlayer = true;
                                if (!p.equals(player))
                                    anyoneListening = true;
                                break;
                            }
                        }
                    }
                    if (sendPlayer)
                        p.sendMessage(cm.getMainChannel(p).format.replace("%MESSAGE%", e.getMessage())
                                .replace("%PLAYER%", player.getDisplayName())
                                .replace("&", "§"));
                }

                if (!anyoneListening)
                    player.sendMessage(ConfigHandler.applyPlaceholders(ConfigHandler.CHAT_NO_LISTENERS, new String[]{cm.getMainChannel(player).name}));
            }
            Bukkit.getConsoleSender().sendMessage(cm.getMainChannel(player).format.replace("%MESSAGE%", e.getMessage())
                    .replace("%PLAYER%",player.getDisplayName())
                    .replace("&", "§"));
        }
    }
}