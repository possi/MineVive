package de.jaschastarke.minecraft.vive.modules.vivecraft.packet;

import de.jaschastarke.minecraft.vive.modules.vivecraft.Channel;
import de.jaschastarke.minecraft.vive.modules.vivecraft.Packet;

public class HelloVersion extends Packet {
    public static Channel.ChannelId ID = Channel.ChannelId.VERSION;

    public HelloVersion(Channel channel) {
        super(channel);
    }

    public void process(byte[] data) {
        channel.response(ID, getVersion().getBytes());
        processVersion(data != null && data.length > 0 ? new String(data) : null);
        channel.triggerUpdatePlayer();
    }

    public VivecraftPlayerProperties processVersion(String version) {
        VivecraftPlayerProperties playerProperties = channel.getPlayerProperties();
        playerProperties.vive = true;

        if (version != null) {
            int pos = version.lastIndexOf("jrbudda");
            if (pos > -1) {
                try {
                    int v = Integer.parseInt(version.substring(pos + 7));
                    if (v >= 15) {
                        playerProperties.keyboard = true;
                        playerProperties.longbow_shooting = true;
                    }
                } catch (NumberFormatException nfe) {
                    // Version string not supported
                }
            }
        }
        return playerProperties;
    }

    public static void setForge(VivecraftPlayerProperties pp, boolean b) {
        if (pp != null)
            pp.forge = b;
    }

    public String getVersion() {
        return channel.getMod().getPlugin().getDescription().getName() + "-" +
                channel.getMod().getPlugin().getDescription().getVersion();
    }
}
