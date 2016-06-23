package de.jaschastarke.minecraft.vive.modules;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.modules.permissions.PermGroupConfig;
import de.jaschastarke.minecraft.vive.modules.permissions.PermissionIntegration;
import de.jaschastarke.minecraft.vive.modules.vivecraft.VivePropertyChangeEvent;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class PermGroup extends CoreModule<MineVive> implements Listener {
    private PermissionIntegration vaultIntegraiont;
    private PermGroupConfig config;

    public PermGroup(MineVive plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "PermGroup";
    }

    @Override
    public void initialize(ModuleEntry<IModule> pEntry) {
        super.initialize(pEntry);

        config = plugin.getPluginConfig().registerSection(new PermGroupConfig(this));

        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            //if (config.getVive() != null)
                //getLog().warn(plugin.getLocale().trans("gmperm.warning.vault_not_found", getName()));
            entry.deactivateUsage();
            return;
        }
        vaultIntegraiont = new de.jaschastarke.minecraft.vive.modules.permissions.VaultIntegration(plugin);
    }

    @EventHandler
    public void onVivePropertyChange(VivePropertyChangeEvent event) {
        PlayerProperties pp = event.getPlayerProperties();

        Map<String, Boolean> groups = new HashMap<String, Boolean>();

        String g_vive = config.getVive();
        String g_classic = config.getVanilla();
        if (g_vive != null)
            groups.put(g_vive, pp != null && pp.isVive());
        if (g_classic != null)
            groups.put(g_classic, pp == null || !pp.isVive());

        if (pp != null) {
            String g_freemove = config.getFreemove();
            if (g_freemove != null)
                groups.put(g_freemove, !pp.isTeleporting());
        }

        vaultIntegraiont.updatePlayerPermissionGroup(event.getPlayer(), groups);
    }
}
