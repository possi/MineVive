package de.jaschastarke.minecraft.vive.modules;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.modules.vivecraft.ViveOnlyConfig;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ViveOnly extends CoreModule<MineVive> implements Listener {
    private ViveOnlyConfig config;

    public ViveOnly(MineVive plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ViveOnly";
    }

    @Override
    public void initialize(ModuleEntry<IModule> pEntry) {
        super.initialize(pEntry);
        listeners.addListener(this);
        config = plugin.getPluginConfig().registerSection(new ViveOnlyConfig(this, entry));
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        final Player p = event.getPlayer();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (p.isOnline() && !plugin.isVivePlayer(p)) {
                    p.kickPlayer(config.getMessage());
                }
            }
        }, config.getWaitTimeout());
    }

}
