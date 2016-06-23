package de.jaschastarke.minecraft.vive.modules.permissions;

import org.bukkit.entity.Player;

import java.util.Map;

public interface PermissionIntegration {
    void updatePlayerPermissionGroup(Player p, Map<String, Boolean> groups);
}
