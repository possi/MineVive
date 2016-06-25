package de.jaschastarke.minecraft.vive.modules.vivecraft;

abstract public class Packet {
    protected Channel channel;

    public Packet(Channel channel) {
        this.channel = channel;
    }

    abstract public void process(byte[] data);
}
