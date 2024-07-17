/*
 *   Date:           7/17/2024
 *   Description:    Handles players joining and leaving. This is necessary as when a player joins their information needs to be retrieved from a database
 *                   and when they leave the memory can be freed.
 * */
package me.theencomputers.channelchat.Listeners;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import utils.SqlHandler;


public class JoinAndLeave implements Listener{

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        try {
            if(!SqlHandler.retrievePlayer(e.getPlayer())) {
                //if statement left for future additions

            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        if(Main.playerToMainChannel.containsKey(e.getPlayer()))
            Main.playerToMainChannel.remove(e.getPlayer());
        if(Main.playerToChannelList.containsKey(e.getPlayer()))
            Main.playerToChannelList.remove(e.getPlayer());
    }
}
