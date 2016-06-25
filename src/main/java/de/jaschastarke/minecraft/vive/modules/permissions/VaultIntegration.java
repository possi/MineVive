package de.jaschastarke.minecraft.vive.modules.permissions;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.minecraft.vive.MineVive;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Map;

public class VaultIntegration extends CoreModule<MineVive> implements PermissionIntegration {
    public VaultIntegration(MineVive plugin) {
        super(plugin);
    }

    @Override
    public void updatePlayerPermissionGroup(Player p, Map<String, Boolean> groups) {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        Permission perm = rsp.getProvider();
        if (perm != null) {
            for (Map.Entry<String, Boolean> entry : groups.entrySet()) {
                if (entry.getValue()) {
                    if (!perm.playerInGroup(p, entry.getKey()))
                        perm.playerAddGroup(p, entry.getKey());
                } else {
                    if (perm.playerInGroup(p, entry.getKey()))
                        perm.playerRemoveGroup(p, entry.getKey());
                }
            }
        }
    }
}
