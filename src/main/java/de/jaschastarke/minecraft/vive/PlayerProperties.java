package de.jaschastarke.minecraft.vive;

import org.bukkit.entity.Player;

public interface PlayerProperties {
    boolean isVive();
    boolean isForge();
    Player getPlayer();
    boolean isTeleporting();
    boolean hasKeyboard();
    boolean isLongbowShooting();
}
