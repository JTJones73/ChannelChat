/*
 *   Date:           7/17/2024
 *   Description:    Handles players joining and leaving. This is necessary as when a player joins their information needs to be retrieved from a database
 *                   and when they leave the memory can be freed.
 * */
package me.theencomputers.channelchat.Listeners;

import me.theencomputers.channelchat.Main;
import me.theencomputers.channelchat.utils.ChannelManager;
import org.bukkit.event.Listener;

import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import me.theencomputers.channelchat.utils.SqlHandler;


public class JoinAndLeave implements Listener{
    SqlHandler sql = new SqlHandler();
    ChannelManager cm = new ChannelManager();
    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        sql.retrievePlayer(e.getPlayer());
    }


    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        cm.removePlayer(e.getPlayer());
    }
}
