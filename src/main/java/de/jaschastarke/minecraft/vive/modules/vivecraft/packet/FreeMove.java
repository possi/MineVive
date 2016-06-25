package de.jaschastarke.minecraft.vive.modules.vivecraft.packet;

public class FreeMove {
    public static void setTeleporting(VivecraftPlayerProperties pp, byte value) {
        if (pp != null)
            pp.teleporting = value == 0;
    }
}
