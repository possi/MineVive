package de.jaschastarke.minecraft.vive.modules;

import org.bukkit.entity.Player;

public class PlayerProperties {
    protected boolean teleporting = true;
    protected boolean keyboard = false;
    protected boolean longbow_shooting = false;

    private Player player;
    public PlayerProperties(Player player) {
        this.player = player;
    }

    public boolean isVive() {
        return true;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isTeleporting() {
        return teleporting;
    }

    public boolean hasKeyboard() {
        return keyboard;
    }

    public boolean isLongbowShooting() {
        return longbow_shooting;
    }
}
