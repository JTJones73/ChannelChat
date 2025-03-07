/*
 *   Author:         Theencomputers
 *   Date:           7/17/2024
 *   Description:    This util handles the SQL database that is used to store channel and player information.
 * */

package me.theencomputers.channelchat.utils;

import me.theencomputers.channelchat.ChannelInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;

public class SqlHandler {
    String insertChannelSQL = "INSERT INTO channels (name, format, permission, radius) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE format = VALUES(format), permission = VALUES(permission), radius = VALUES(radius)";
    String insertPlayerSQL = "INSERT INTO players (uuid, currentChannel, ChannelList) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE currentChannel = VALUES(currentChannel), ChannelList = VALUES(ChannelList)";
    String selectPlayerSQL = "SELECT * FROM players WHERE uuid = ?";
    String selectChannelSQL = "SELECT * FROM channels WHERE name = ?";
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
    String deleteChannelSQL = "DELETE FROM channels WHERE name = ?";
    PreparedStatement deleteChannelStatement = null;
    PreparedStatement insertChannelStatement = null;
    PreparedStatement insertPlayerStatement = null;
    PreparedStatement selectPlayerStatement = null;
    PreparedStatement selectChannelStatement = null;

    private static Connection connection = null;
    private static Statement statement = null;
    private static String sqlUrl, sqlUsername, sqlPassword;


    ChannelManager cm = new ChannelManager();
    public void initSql(String url, String username, String password) {
        sqlUrl = url;
        sqlPassword = password;
        sqlUsername = username;

        try {
            Bukkit.getConsoleSender().sendMessage("[Channel Chat] Attempting to connect to SQL...");

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);

            statement = connection.createStatement();

            statement.executeUpdate(createChannelTableSQL);
            statement.executeUpdate(createPlayersTableSQL);


            Bukkit.getConsoleSender().sendMessage("SQL connectivity has been established");
            selectChannelStatement = connection.prepareStatement(selectChannelSQL);



        } catch (ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("[ChannelChat] Critical: Failed to load SQL please check credentials. Aborting...");
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("MySQL JDBC Driver not found. Include the MySQL Connector library in your classpath.");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("[Channel Chat] Critical: Failed to load SQL please check credentials. Aborting...");
            throw new RuntimeException(e);
        } finally {

            try {
                connection.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private final String[] SQL_INJECTION_PATTERNS = {
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
    public void stopInjection(CommandSender sender, String attempt){
        sender.sendMessage(ConfigHandler.SQL_INJECT_ATTEMPT);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + sender.getName() + " has just attempted an SQL Injection attack. You should probably ip-ban them they typed: " + attempt);
    }
    public boolean isSafe(String input) {
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


    public void addChannel(ChannelInfo c){
        new Thread(new Runnable() {
            @Override
            public void run() {

                    try {
                        connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
                        insertChannelStatement = connection.prepareStatement(insertChannelSQL);
                        insertChannelStatement.setString(1, c.name);
                        insertChannelStatement.setString(2, c.format);
                        insertChannelStatement.setString(3, c.permission);
                        insertChannelStatement.setFloat(4, c.radius);
                        insertChannelStatement.executeUpdate();
                    } catch (SQLException ec) {
                    }
                    finally {
                        if(connection != null) {
                            try {
                                connection.close();
                            } catch (SQLException e) {
                            }
                        }
                    }
            }
        }).start();

    }
    public void addPlayer(Player p, ChannelInfo mainChannel, ArrayList<ChannelInfo> channelList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
                    insertPlayerStatement = connection.prepareStatement(insertPlayerSQL);

                    insertPlayerStatement.setString(1, p.getUniqueId().toString());
                    insertPlayerStatement.setString(2,  mainChannel == null  || mainChannel.name.equals("") ?  " ": mainChannel.name);
                    String channelListStr = "";
                    for (int i = 0; i < channelList.size(); i++) {
                        if (channelList.get(i) != null)
                            channelListStr += channelList.get(i).name + "|";
                    }
                    insertPlayerStatement.setString(3, channelListStr.equals("")  ? " ": channelListStr);
                    insertPlayerStatement.executeUpdate();
                } catch (SQLException e) {
                }
                finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
        }).start();
    }
    public boolean retrievePlayer(Player p){
        final boolean[] returnVal = {false};
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet resultSet = null;
                try {
                    connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
                    resultSet = null;
                    selectPlayerStatement = connection.prepareStatement(selectPlayerSQL);
                    selectPlayerStatement.setString(1, p.getUniqueId().toString());
                    resultSet = selectPlayerStatement.executeQuery();

                    if (resultSet.next()) {

                        String uuid = resultSet.getString("uuid");
                        String currentChannel = resultSet.getString("currentChannel");
                        String channelList = resultSet.getString("ChannelList");
                        ArrayList<ChannelInfo> infoList = new ArrayList<>();
                        String[] infoStrList = channelList.split("\\|");

                        for (int i = 0; i < infoStrList.length; i++) {
                            if(infoStrList[i].equals(" "))
                                continue;
                            if(cm.doesChannelExist(infoStrList[i]))
                                _retrieveChannel(infoStrList[i]);
                            infoList.add(cm.getChannel(infoStrList[i]));
                        }
                        if(!currentChannel.equals("") && !currentChannel.equals(" ")) {
                            _retrieveChannel(currentChannel);
                        }
                        cm.addPlayerChannelList(p, infoList);
                        if (!currentChannel.equals("")) {
                            cm.addPlayerToChannel(cm.getChannel(currentChannel), p,  false);
                        }
                        else {
                            ChannelInfo blankChannel = new ChannelInfo("", "", 0, "");
                            cm.addPlayerMainChannel(p, blankChannel, false);
                        }
                        returnVal[0] = true;

                    } else {
                        ChannelInfo blankChannel = new ChannelInfo("", "", 0, "");
                        ArrayList<ChannelInfo> blankChannelList = new ArrayList<>();
                        cm.addPlayerChannelList(p, blankChannelList);
                        cm.addPlayerMainChannel(p, blankChannel, false);
                    }
                } catch (SQLException e) {
                } finally {
                    if (resultSet != null) {
                        try {
                            connection.close();
                            resultSet.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (selectChannelStatement != null) {
                        try {
                            connection.close();
                            selectChannelStatement.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
            }).start();
        return returnVal[0];
    }
    public boolean retrieveChannel(String channelName){
        if(channelName.equals(""))
            return true;
        final boolean[] returnVal = {false};
        new Thread(new Runnable() {
            @Override
            public void run() {
                returnVal[0] = _retrieveChannel(channelName);
            }
        }).start();
        return returnVal[0];
    }
    public void removeChannel(String channelName){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
                    deleteChannelStatement = connection.prepareStatement(deleteChannelSQL);
                    deleteChannelStatement.setString(1, channelName);
                    int rowsAffected = deleteChannelStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        Bukkit.getConsoleSender().sendMessage("Channel '" + channelName + "' has been removed.");
                    } else {
                        Bukkit.getConsoleSender().sendMessage("No channel found with the name: " + channelName);
                    }
                } catch (SQLException e) {
                } finally {
                    if (deleteChannelStatement != null) {
                        try {
                            connection.close();
                            deleteChannelStatement.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
    }).start();
    }
    private boolean _retrieveChannel(String channelName){
        if(channelName.equals(""))
            return true;
        boolean returnVal = false;

                ResultSet resultSet = null;
                try {
                    connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
                    selectChannelStatement = connection.prepareStatement(selectChannelSQL);
                    resultSet = null;
                    selectPlayerStatement = connection.prepareStatement(selectChannelSQL);
                    selectPlayerStatement.setString(1, channelName);
                    resultSet = selectPlayerStatement.executeQuery();
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String format = resultSet.getString("format");
                        String permission = resultSet.getString("permission");
                        float radius = resultSet.getFloat("radius");
                        cm.addChannel(name, permission, radius, format,false);

                    } else {
                        returnVal = false;
                    }
                } catch (SQLException e) {
                } finally {
                    if (resultSet != null) {
                        try {
                            connection.close();
                            resultSet.close();
                            if (selectChannelStatement != null) selectChannelStatement.close();

                        } catch (SQLException e) {
                        }
                    }
                }
        return returnVal;
    }

}
