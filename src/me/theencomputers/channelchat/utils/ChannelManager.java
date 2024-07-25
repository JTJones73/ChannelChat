package me.theencomputers.channelchat.utils;

import me.theencomputers.channelchat.ChannelInfo;
import me.theencomputers.channelchat.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Float.parseFloat;

public class ChannelManager {
    private static HashMap<Player, ChannelInfo> playerToMainChannel = new HashMap<>();
    private static HashMap<Player, ArrayList<ChannelInfo>> playerToChannelList = new HashMap<>();
    private static HashMap<String, ChannelInfo> channelNameToInfo = new HashMap<>();
    private static SqlHandler sql = new SqlHandler();
    ConfigHandler cfg = new ConfigHandler();

    public boolean doesChannelExist(ChannelInfo c){
        if(channelNameToInfo.containsValue(c) || sql.retrieveChannel(c.name))
            return true;
        return  false;
    }
    public boolean doesChannelExist(String c){
        if(channelNameToInfo.containsKey(c)|| sql.retrieveChannel(c))
            return true;
        return  false;
    }
    public boolean isPlayerInChannel(Player p, ChannelInfo c){
        if(playerToChannelList.containsKey(p) && playerToChannelList.get(p).contains(c))
            return true;
        return false;
    }
    public boolean isPlayerInChannel(Player p){
        if(playerToMainChannel.containsKey(p) && playerToMainChannel.get(p) != null && !playerToMainChannel.get(p).name.equals(""))
            return true;
        return false;
    }
    public ChannelInfo getMainChannel(Player p){
        return playerToMainChannel.getOrDefault(p, null);
    }
    public ArrayList<ChannelInfo> getChannelList(Player p){
        return playerToChannelList.getOrDefault(p, null);
    }
    public void addPlayerToChannel(ChannelInfo c, Player p, Boolean pushSql){
        if(!playerToChannelList.containsKey(p)){
            ArrayList<ChannelInfo> cInf = new ArrayList<ChannelInfo>();
            playerToChannelList.put(p, cInf);
        }
        if(!playerToChannelList.get(p).contains(c)) {
            playerToChannelList.get(p).add(c);
        }
        if(playerToMainChannel.containsKey(p))
            playerToMainChannel.replace(p, c);
        else
            playerToMainChannel.put(p, c);
        if(pushSql)
            pushPlayer(p);
    }
    public boolean removePlayerFromChannel(ChannelInfo c, Player p){
        if(!playerToChannelList.get(p).contains(c))
            return false;
        else{
            playerToChannelList.get(p).remove(c);
            if(playerToMainChannel.containsKey(p) && playerToMainChannel.get(p).equals(c))
                playerToMainChannel.remove(p);
        }
        ChannelInfo blankChannel = new ChannelInfo("","",0,"");
        if(playerToMainChannel.containsKey(p) && playerToMainChannel.get(p).equals(c))
            playerToMainChannel.replace(p, blankChannel);
        if(playerToChannelList.containsKey(p) && playerToChannelList.get(p).contains(c))
            playerToChannelList.get(p).remove(c);
        pushPlayer(p);
        return true;
    }
    public ChannelInfo addChannel(String name, String perm, float radius, String format, boolean pushSql){
        if(channelNameToInfo.containsKey(name))
            return channelNameToInfo.get(name);
        ChannelInfo c = new ChannelInfo(name, perm, radius, format);
        channelNameToInfo.put(name, c);
        if(pushSql)
            pushChannel(c);
        return c;
    }
    public void modifyChannel(ChannelInfo channel, String perm, float radius, String format) {
        ChannelInfo c = new ChannelInfo(channel.name, perm, radius, format);
        channelNameToInfo.replace(c.name, c);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (playerToMainChannel.containsKey(p) && playerToMainChannel.get(p) != null && playerToMainChannel.get(p).name.equals(c.name)) {
                playerToMainChannel.replace(p, c);
            }
            boolean remove = false;
            ChannelInfo chan = null;
            if (playerToChannelList.containsKey(p) && playerToChannelList.get(p) != null) {
                for (ChannelInfo cInf : playerToChannelList.get(p)) {
                    if (cInf != null && cInf.name.equals(c.name)) {
                        remove = true;
                        chan = cInf;
                    }
                }
                if (remove) {
                    playerToChannelList.get(p).remove(chan);
                    playerToChannelList.get(p).add(c);
                }

            }
        }
        pushChannel(c);
    }
    public void addPlayerChannelList(Player p, ArrayList<ChannelInfo> cList){
        if(playerToChannelList.containsKey(p))
            playerToChannelList.replace(p, cList);
        else
            playerToChannelList.put(p, cList);
        pushPlayer(p);
    }
    public void addPlayerMainChannel(Player p, ChannelInfo c, boolean pushSql){
        if(playerToMainChannel.containsKey(p))
            playerToMainChannel.replace(p, c);
        else
            playerToMainChannel.put(p, c);
        if(pushSql)
            pushPlayer(p);
    }
    public void addPlayer(Player p){
        playerToMainChannel.put(p, null);
        playerToChannelList.put(p, null);
    }
    public ChannelInfo getChannel(String channelStr){
        if(channelNameToInfo.containsKey(channelStr))
            return channelNameToInfo.get(channelStr);
        return null;
    }


    public void removeChannel(ChannelInfo c){
            channelNameToInfo.remove(c.name);
        for(Player p: Bukkit.getOnlinePlayers()) {
            if (playerToChannelList.containsKey(p) && playerToChannelList.get(p).contains(c)) {
                playerToChannelList.get(p).remove(c);
            }
            if (playerToMainChannel.containsKey(p) && playerToMainChannel.get(p) != null && playerToMainChannel.get(p).equals(c)){
                p.sendMessage(cfg.applyPlaceholders(ConfigHandler.CHANNEL_YOU_LEFT, new String[]{c.name}));
                ChannelInfo blankChannel = new ChannelInfo("","",0,"");
                playerToMainChannel.replace(p, blankChannel);
            }
        }
            sql.removeChannel(c.name);
    }
public void removePlayer(Player p){
    if(playerToMainChannel.containsKey(p))
        playerToMainChannel.remove(p);
    if(playerToChannelList.containsKey(p))
        playerToChannelList.remove(p);
}
    private boolean pushChannel(ChannelInfo c) {
            sql.addChannel(c);
            return true;
    }
    private void pushPlayer(Player p){
        sql.addPlayer(p, playerToMainChannel.get(p), playerToChannelList.get(p));
    }
}
