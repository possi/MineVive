package de.jaschastarke.minecraft.vive.modules.vivecraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.jaschastarke.minecraft.vive.modules.vivecraft.packet.HelloVersion;
import de.jaschastarke.minecraft.vive.modules.vivecraft.packet.VivecraftPlayerProperties;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class Channel {
    public enum ChannelId {
        VERSION((byte) 0),
        ;

        final private byte id;
        ChannelId(byte id) {
            this.id = id;
        }
        public byte getId() {
            return id;
        }

        public static ChannelId fromByte(byte b) {
            for (ChannelId p : values())
                if (p.getId() == b)
                    return p;
            return null;
        }
    }

    private final VivecraftChannel mod;
    private final Player player;
    private final String tag;

    public Channel(VivecraftChannel vivecraftChannel, Player p, String tag) {
        this.mod = vivecraftChannel;
        this.player = p;
        this.tag = tag;
    }

    public void process(byte channel, byte[] data) {
        if (getMod().getPlugin().isDebug()) {
            if (data.length > 1)
                getMod().getLog().debug("RECV Tag/ChannelId/String " + tag + " #" + (int) channel + " -> " + ChannelId.fromByte(channel) + ": " + new String(data));
            else
                getMod().getLog().debug("SEND Tag/ChannelId/Byte " + tag + " #" + (int) channel + " -> " + ChannelId.fromByte(channel) + ": " + (int) data[0]);
        }

        Packet packet = createIncoming(ChannelId.fromByte(channel));
        if (packet != null) {
            packet.process(data);
        }
    }

    public Packet createIncoming(ChannelId type) {
        if (type == null)
            return null;

        switch (type) {
            case VERSION:
                return new HelloVersion(this);
            default:
                return null;
        }
    }

    private boolean _response(byte[] data) {
        try {
            PacketContainer responsePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
            responsePacket.getModifier().writeDefaults();
            responsePacket.getStrings().write(0, tag);
            responsePacket.getByteArrays().write(0, data);

            ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), responsePacket);
        } catch (InvocationTargetException e) {
            mod.getLog().warn("Failed to response To Vive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean _response(byte channel, byte[] data) {
        byte[] newdata = new byte[data.length + 1];
        newdata[0] = channel;
        System.arraycopy(data, 0, newdata, 1, data.length);
        return _response(newdata);
    }
    public boolean response(ChannelId channel, byte[] data) {
        if (getMod().getPlugin().isDebug()) {
            if (data.length > 1)
                getMod().getLog().debug("SEND Tag/ChannelId/String: " + tag + " #" + (int) channel.getId() + " -> " + channel + ": " + new String(data));
            else
                getMod().getLog().debug("SEND Tag/ChannelId/Byte: " + tag + " #" + (int) channel.getId() + " -> " + channel + ": " + (int) data[0]);
        }
        return _response(channel.getId(), data);
    }
    public boolean response(byte channel, byte[] data) {
        if (getMod().getPlugin().isDebug()) {
            if (data.length > 1)
                getMod().getLog().debug("SEND Tag/ChannelId/String: " + tag + " #" + (int) channel + ": " + new String(data));
            else
                getMod().getLog().debug("SEND Tag/ChannelId/Byte: " + tag + " #" + (int) channel + ": " + (int) data[0]);
        }
        return _response(channel, data);
    }
    public boolean response(byte[] data) {
        if (getMod().getPlugin().isDebug()) {
            if (data.length > 1)
                getMod().getLog().debug("SEND Tag/String: " + tag + ": " + new String(data));
            else
                getMod().getLog().debug("SEND Tag/Byte: " + tag + ": " + (int) data[0]);
        }
        return _response(data);
    }

    public Player getPlayer() {
        return player;
    }

    public VivecraftChannel getMod() {
        return mod;
    }

    public VivecraftPlayerProperties getPlayerProperties() {
        return getMod().getOrCreatePlayerViveProperties(getPlayer());
    }
    public void triggerUpdatePlayer() {
        getMod().updatePlayer(getPlayer());
    }

}
