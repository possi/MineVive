package de.jaschastarke.minecraft.vive;

import de.jaschastarke.minecraft.vive.modules.PermGroup;
import de.jaschastarke.modularize.ModuleEntry.ModuleState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * @Todo may be abstract to some per-module-definitions that are checked onEnabled and here automaticly
 */
public class DependencyListener implements Listener {
    private MineVive plugin;

    public DependencyListener(MineVive plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPluginLoaded(PluginEnableEvent event) {
    }
    
    @EventHandler
    public void onPluginUnloaded(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("Vault")) {
            PermGroup mod = plugin.getModule(PermGroup.class);
            if (mod != null && mod.getModuleEntry().getState() == ModuleState.ENABLED) {
                //mod.getLog().warn(plugin.getLocale().trans("gmperm.warning.vault_not_found", mod.getName()));
                mod.getModuleEntry().deactivateUsage();
            }
        }
    }
}
