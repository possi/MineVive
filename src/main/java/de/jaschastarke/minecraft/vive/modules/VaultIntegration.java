package de.jaschastarke.minecraft.vive.modules;

import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.PermissionIntegration;
import de.jaschastarke.minecraft.vive.PlayerProperties;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;

public class VaultIntegration implements PermissionIntegration {
    private MineVive plugin;
    public VaultIntegration(MineVive mineVive) {
        plugin = mineVive;
    }

    @Override
    public void updatePlayerPermissionGroup(Player p) {
        PlayerProperties pp = plugin.getPlayerViveProperties(p);

        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        Permission perm = rsp.getProvider();
        if (perm != null) {
            Map<String, Boolean> groups = new HashMap<String, Boolean>();

            String g_vive = plugin.getConfig().getString("groups.vive", null);
            String g_classic = plugin.getConfig().getString("groups.vanilla", null);
            if (g_vive != null)
                groups.put(g_vive, pp != null && pp.isVive());
            if (g_classic != null)
                groups.put(g_classic, pp == null || !pp.isVive());

            if (pp != null) {
                String g_freemove = plugin.getConfig().getString("groups.freemove", null);
                if (g_freemove != null)
                    groups.put(g_freemove, !pp.isTeleporting());
            }

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
