package de.jaschastarke.minecraft.vive.modules.vivecraft.packet;

import de.jaschastarke.minecraft.vive.PlayerProperties;
import org.bukkit.entity.Player;

public class VivecraftPlayerProperties implements PlayerProperties {
    protected boolean vive = false;
    protected boolean forge = false;
    protected boolean teleporting = true;
    protected boolean keyboard = false;
    protected boolean longbow_shooting = false;

    private Player player;
    public VivecraftPlayerProperties(Player player) {
        this.player = player;
    }

    @Override
    public boolean isVive() {
        return vive;
    }

    @Override
    public boolean isForge() {
        return forge;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isTeleporting() {
        return teleporting;
    }

    @Override
    public boolean hasKeyboard() {
        return keyboard;
    }

    @Override
    public boolean isLongbowShooting() {
        return longbow_shooting;
    }
}
