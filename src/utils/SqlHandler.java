/*
 *   Author:         Theencomputers
 *   Date:           7/17/2024
 *   Description:    This util handles the SQL database that is used to store channel and player information.
 * */

package utils;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Commands.Channel;
import me.theencomputers.channelchat.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlHandler {
    static String insertChannelSQL = "INSERT INTO channels (name, format, permission, radius) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE format = VALUES(format), permission = VALUES(permission), radius = VALUES(radius)";
    static String insertPlayerSQL = "INSERT INTO players (uuid, currentChannel, ChannelList) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE currentChannel = VALUES(currentChannel), ChannelList = VALUES(ChannelList)";

    static PreparedStatement insertChannelStatement = null;
    static PreparedStatement insertPlayerStatement = null;

    static Connection connection = null;
    static Statement statement = null;
    public static String sqlUrl, sqlUsername, sqlPassword;

    public static void initSql(String url, String username, String password) {
        sqlUrl = url;
        sqlPassword = password;
        sqlUsername = username;
        String createChannelTableSQL = "CREATE TABLE IF NOT EXISTS channels ("
                + "name VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "format VARCHAR(255), "
                + "permission VARCHAR(255), "
                + "radius FLOAT"
                + ")";
        String createPlayersTableSQL = "CREATE TABLE IF NOT EXISTS players ("
                + "uuid VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "currentChannel VARCHAR(255), "
                + "ChannelList TEXT(65535)"
                + ")";

        try {
            Bukkit.getConsoleSender().sendMessage("[Channel Chat] Attempting to connect to SQL...");

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);

            statement = connection.createStatement();

            statement.executeUpdate(createChannelTableSQL);
            statement.executeUpdate(createPlayersTableSQL);


            Bukkit.getConsoleSender().sendMessage("Table 'channels' has been created or already exists.");

        } catch (ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("[ChannelChat] Critical: Failed to load SQL please check credentials. Aborting...");
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("MySQL JDBC Driver not found. Include the MySQL Connector library in your classpath.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("[Channel Chat] Critical: Failed to load SQL please check credentials. Aborting...");
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private static final String[] SQL_INJECTION_PATTERNS = {
            "' OR '1'='1",
            "' OR '1'='1' --",
            "' OR '1'='1' /*",
            "' OR '1'='1' #",
            "--",
            "/*",
            "#",
            "';--",
            "';/*",
            "';#",
            "'; DROP",
            "'; SELECT",
            "'; INSERT",
            "'; UPDATE",
            "'; DELETE",
            "'; EXEC"
    };
    public static void stopInjection(CommandSender sender, String attempt){
        sender.sendMessage(ConfigHandler.SQL_INJECT_ATTEMPT);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + sender.getName() + " has just attempted an SQL Injection attack. You should probably ip-ban them they typed: " + attempt);
    }
    public static boolean isSafe(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        String lowerCaseInput = input.toLowerCase();

        for (String pattern : SQL_INJECTION_PATTERNS) {
            if (lowerCaseInput.contains(pattern.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
    public static void addChannel(ChannelInfo c) throws SQLException {
        connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
        insertChannelStatement = connection.prepareStatement(insertChannelSQL);
        insertChannelStatement.setString(1, c.name);
        insertChannelStatement.setString(2, c.format);
        insertChannelStatement.setString(3, c.permission);
        insertChannelStatement.setFloat(4, c.radius);
        insertChannelStatement.executeUpdate();
    }
    public static void addPlayer(Player p, ChannelInfo mainChannel, ArrayList<ChannelInfo> channelList) throws SQLException {
        connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
        insertPlayerStatement = connection.prepareStatement(insertPlayerSQL);
        insertPlayerStatement.setString(1, p.getUniqueId().toString());
        insertPlayerStatement.setString(2, mainChannel.name);
        String channelListStr = "";
        for(int i = 0; i < channelList.size(); i++) {
            if(channelList.get(i) != null)
                channelListStr += channelList.get(i).name + "|";
        }
        insertPlayerStatement.setString(3, channelListStr);
        insertPlayerStatement.executeUpdate();
    }
    public static boolean retrievePlayer(Player p) throws SQLException {

        connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
        String selectChannelSQL = "SELECT * FROM players WHERE uuid = ?";
        PreparedStatement selectChannelStatement = null;
        ResultSet resultSet = null;

        try {
            selectChannelStatement = connection.prepareStatement(selectChannelSQL);
            selectChannelStatement.setString(1, p.getUniqueId().toString());
            resultSet = selectChannelStatement.executeQuery();

            if (resultSet.next()) {

                String uuid = resultSet.getString("uuid");
                String currentChannel = resultSet.getString("currentChannel");
                String channelList = resultSet.getString("ChannelList");
                Main.playerToMainChannel.put(p, Channel.getChannel(currentChannel));
                ArrayList<ChannelInfo> infoList = new ArrayList<>();
                String[] infoStrList = channelList.split("\\|");

                for(int i = 0; i < infoStrList.length; i++){
                    infoList.add(Channel.getChannel(infoStrList[i]));
                }
                if(!Main.playerToChannelList.containsKey(p))
                    Main.playerToChannelList.put(p, infoList);
                else
                    Main.playerToChannelList.replace(p, infoList);
                if(!Main.playerToMainChannel.containsKey(p)){
                    if(!currentChannel.equals(""))
                        Main.playerToMainChannel.put(p, Channel.getChannel(currentChannel));
                    else{
                        ChannelInfo blankChannel = new ChannelInfo();
                        blankChannel.format = "";
                        blankChannel.permission = "";
                        blankChannel.name = "";
                        blankChannel.radius = 0;
                        ArrayList<ChannelInfo> blankChannelList = new ArrayList<>();
                        Main.playerToMainChannel.put(p, blankChannel);
                    }
                }
                else
                    Main.playerToMainChannel.replace(p, Channel.getChannel(currentChannel));
                return true;

            } else {
                ChannelInfo blankChannel = new ChannelInfo();
                blankChannel.format = "";
                blankChannel.permission = "";
                blankChannel.name = "";
                blankChannel.radius = 0;
                ArrayList<ChannelInfo> blankChannelList = new ArrayList<>();
                if(!Main.playerToChannelList.containsKey(p))
                    Main.playerToChannelList.put(p, blankChannelList);
                else
                    Main.playerToChannelList.replace(p, blankChannelList);
                if(!Main.playerToMainChannel.containsKey(p))
                    Main.playerToMainChannel.put(p, blankChannel);
                else
                    Main.playerToMainChannel.replace(p, blankChannel);
                return false;
            }
        } catch (SQLException e) {
        } finally {
            if (resultSet != null) resultSet.close();
            if (selectChannelStatement != null) selectChannelStatement.close();
        }
        return true;
    }
    public static boolean retrieveChannel(String channelName) throws SQLException {
        connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
        String selectChannelSQL = "SELECT * FROM channels WHERE name = ?";
        PreparedStatement selectChannelStatement = null;
        ResultSet resultSet = null;
        if(channelName.equals(""))
            return true;
        try {
            selectChannelStatement = connection.prepareStatement(selectChannelSQL);
            selectChannelStatement.setString(1, channelName);
            resultSet = selectChannelStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String format = resultSet.getString("format");
                String permission = resultSet.getString("permission");
                //Bukkit.getConsoleSender().sendMessage("Retrieved data: " + name);     //DEBUG
                ChannelInfo c = new ChannelInfo();
                c.name = name;
                c.format = format;
                c.permission = permission;
                Main.channelNameToInfo.put(name, c);


            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (resultSet != null) resultSet.close();
            if (selectChannelStatement != null) selectChannelStatement.close();
        }
        return true;
    }
    public static void removeChannel(String channelName) throws SQLException {
        String deleteChannelSQL = "DELETE FROM channels WHERE name = ?";
        PreparedStatement deleteChannelStatement = null;

        try {
            deleteChannelStatement = connection.prepareStatement(deleteChannelSQL);
            deleteChannelStatement.setString(1, channelName);
            int rowsAffected = deleteChannelStatement.executeUpdate();

            if (rowsAffected > 0) {
                Bukkit.getConsoleSender().sendMessage("Channel '" + channelName + "' has been removed.");
            } else {
                Bukkit.getConsoleSender().sendMessage("No channel found with the name: " + channelName);
            }
        } finally {
            if (deleteChannelStatement != null) deleteChannelStatement.close();
        }
    }

}
