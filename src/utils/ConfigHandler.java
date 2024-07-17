/*
 *   Date:           7/17/2024
 *   Description:    This util handles the YML file configuration that allows this plugin to be easily modified.
 * */
package utils;

import me.theencomputers.channelchat.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class ConfigHandler {
    FileConfiguration config;
    public static String SQL_URL, SQL_USERNAME, SQL_PASSWORD, CHANNEL_HELP, CHANNEL_JOINED, CHANNEL_LEFT, CHANNEL_MADE, CHANNEL_DELETED,
        CHAT_NO_CHANNELS, CHAT_NO_PERMISSION, CHAT_NO_LISTENERS, NO_PERMISSION, SQL_INJECT_ATTEMPT, CHANNEL_CREATE_PERMISSION, CHANNEL_ALREADY_EXISTS,
        CHANNEL_REMOVE_PERMISSION, CHANNEL_NO_CHANNEL, CHANNEL_YOU_LEFT, CHANNEL_CREATED, CHANNEL_MODIFY_PERMISSION, CHANNEL_MODIFIED;


  public void init(FileConfiguration c){
      config = c;
      config.addDefaults(Map.of("SQL_URL", "jdbc:mysql://url.to.sql/s7955_dev"));
          config.addDefaults(Map.of("SQL_USERNAME", "USERNAME"));
      config.addDefaults(Map.of("SQL_PASSWORD", "PASSWORD"));
      config.addDefaults(Map.of("CHANNEL_HELP", "/channel join <channel> : Join target channel \n /channel create <channel name> <permission> <radius> <format> " +
       " : Create channel with specified name, permission, radius, and format (use %PLAYER% and %MESSAGE% as placeholders " +
              "set radius to -1 for infinite distance) \n /channel modify <channel name> <permission> <radius> <format>"));
      config.addDefaults(Map.of("CHANNEL_JOINED", "&aYou have joined channel %CHANNEL%"));
      config.addDefaults(Map.of("CHANNEL_CREATED", "&aCreated channel: %CHANNEL%"));
      config.addDefaults(Map.of("CHANNEL_NO_CHANNEL", "&c No channel exists named %CHANNEL%"));
      config.addDefaults(Map.of("CHANNEL_LEFT", "&cYou have left channel %CHANNEL%"));
      config.addDefaults(Map.of("CHANNEL_MADE", "&aYou have made channel %CHANNEL% with permission %PERMISSION%, radius %RADIUS%, and format %FORMAT%."));
      config.addDefaults(Map.of("CHANNEL_DELETED", "&cYou have deleted channel %CHANNEL%"));
      config.addDefaults(Map.of("CHANNEL_ALREADY_EXISTS", "&cChannel %CHANNEL% already exists. Use /channel remove %CHANNEL%"));
      config.addDefaults(Map.of("CHANNEL_MODIFIED", "&aModified %CHANNEL%"));
      config.addDefaults(Map.of("CHAT_NO_CHANNELS", "&cYou are not in any channels do /channel join to join a channel"));
      config.addDefaults(Map.of("CHAT_NO_PERMISSION", "&cYou do not have permission to speak in %CHANNEL%"));
      config.addDefaults(Map.of("CHAT_NO_LISTENERS", "&cNo one heard you in %CHANNEL% :("));
      config.addDefaults(Map.of("NO_PERMISSION", "&cYou do not have permission to use this command"));
      config.addDefaults(Map.of("SQL_INJECT_ATTEMPT", "&cFBI OPEN UP!"));
      config.addDefaults(Map.of("CHANNEL_CREATE_PERMISSION", "channelchat.create"));
      config.addDefaults(Map.of("CHANNEL_MODIFY_PERMISSION", "channelchat.modify"));
      config.addDefaults(Map.of("CHANNEL_REMOVE_PERMISSION", "channelchat.remove"));
      config.addDefaults(Map.of("CHANNEL_YOU_LEFT", "&cYou have left %CHANNEL%"));

      config.options().copyDefaults(true);
      Main.getInstance().saveConfig();

      SQL_URL = config.getString("SQL_URL");
      SQL_USERNAME = config.getString("SQL_USERNAME");
      SQL_PASSWORD = config.getString("SQL_PASSWORD");
      CHANNEL_HELP = config.getString("CHANNEL_HELP").replace("&", "§");
      CHANNEL_JOINED = config.getString("CHANNEL_JOINED").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHANNEL_LEFT = config.getString("CHANNEL_LEFT").replace("&", "§")
              .replace("%CHANNEL%", "%0%");

      CHANNEL_MADE = config.getString("CHANNEL_MADE").replace("&", "§")
              .replace("%CHANNEL%", "%0%")
              .replace("%PERMISSION%", "%1%")
              .replace("%CHANNEL%", "%2%")
              .replace("%RADIUS%", "%3%")
              .replace("%FORMAT%", "%4%");
      CHANNEL_CREATED = config.getString("CHANNEL_CREATED").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHANNEL_MODIFIED = config.getString("CHANNEL_MODIFIED").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHANNEL_ALREADY_EXISTS = config.getString("CHANNEL_ALREADY_EXISTS").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHANNEL_NO_CHANNEL = config.getString("CHANNEL_NO_CHANNEL").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHANNEL_YOU_LEFT = config.getString("CHANNEL_YOU_LEFT").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHANNEL_DELETED = config.getString("CHANNEL_DELETED").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHAT_NO_CHANNELS = config.getString("CHAT_NO_CHANNELS").replace("&", "§");
      CHAT_NO_PERMISSION = config.getString("CHAT_NO_PERMISSION").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      CHAT_NO_LISTENERS = config.getString("CHAT_NO_LISTENERS").replace("&", "§")
              .replace("%CHANNEL%", "%0%");
      NO_PERMISSION = config.getString("NO_PERMISSION").replace("&", "§");
      SQL_INJECT_ATTEMPT = config.getString("SQL_INJECT_ATTEMPT").replace("&", "§");
      CHANNEL_CREATE_PERMISSION = config.getString("CHANNEL_CREATE_PERMISSION");
      CHANNEL_REMOVE_PERMISSION = config.getString("CHANNEL_REMOVE_PERMISSION");
      CHANNEL_MODIFY_PERMISSION = config.getString("CHANNEL_MODIFY_PERMISSION");


  }
  public static String applyPlaceholders(String str, String [] values) {
      for(int i = 0; i < values.length; i++){
              str = str.replace("%" + i + "%", values[i]);
      }
      return str;
    }


}
