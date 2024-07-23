package me.theencomputers.channelchat;

public class ChannelInfo {
    public String name, permission, format;
    public float radius;
    public ChannelInfo(String channelName, String perm, float channelRadius, String channelFormat){
        name = channelName;
        permission = perm;
        radius = channelRadius;
        format = channelFormat;
    }
}
