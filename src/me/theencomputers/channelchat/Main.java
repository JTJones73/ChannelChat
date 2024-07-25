/*
 *   Date:           7/17/2024
 *   Description:    This plugin creates a chat system that uses multiple channels that can be created each with their own permissions, block radius, name, and format.
 *                   This plugin synchronizes with an SQL database allowing for large and efficient storage.
 * */
package me.theencomputers.channelchat;

import me.theencomputers.channelchat.Commands.Channel;
import me.theencomputers.channelchat.Listeners.ChatListener;


import me.theencomputers.channelchat.Listeners.JoinAndLeave;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import me.theencomputers.channelchat.utils.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JavaPlugin implements Listener{
    static me.theencomputers.channelchat.Main plugin;
    FileConfiguration config = getConfig();
    SqlHandler sql = new SqlHandler();

    ConfigHandler cfg = new ConfigHandler();
    Channel c = new Channel();
    public void onEnable(){
        plugin = this;

            //Load config
            cfg.init(config);
            config.options().copyDefaults(true);
            saveConfig();
            sql.initSql(ConfigHandler.SQL_URL, ConfigHandler.SQL_USERNAME, ConfigHandler.SQL_PASSWORD);
            Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), plugin);
            Bukkit.getServer().getPluginManager().registerEvents(new JoinAndLeave(), plugin);
            c.init();
            getCommand("channel").setExecutor(c);



    }
    public void onDisable(){

    }
    public static me.theencomputers.channelchat.Main getInstance(){
        return plugin;
    }


}
