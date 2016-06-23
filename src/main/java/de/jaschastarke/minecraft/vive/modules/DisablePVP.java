package de.jaschastarke.minecraft.vive.modules;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.modules.pvp.DisablePVPConfig;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.WeakHashMap;

public class DisablePVP extends CoreModule<MineVive> implements Listener {
    private static final String MSG_NO_PVP_VIVE = "You can only PVP with other VR Players";
    private static final String MSG_NO_PVP_CLASSIC = "You can't PVP with VR Players";
    private static final int MSG_TIMEOUT = 180;
    private Map<Player, Long> timeout = new WeakHashMap<Player, Long>();
    private DisablePVPConfig config;


    public DisablePVP(MineVive plugin) {
        super(plugin);
    }

    @Override
    public void initialize(ModuleEntry<IModule> pEntry) {
        super.initialize(pEntry);
        config = plugin.getPluginConfig().registerSection(new DisablePVPConfig(this, entry));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageEvent rawevent) {
        if (rawevent instanceof EntityDamageByEntityEvent && !rawevent.isCancelled()) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) rawevent;

            Entity source = event.getDamager();
            if (source instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) source).getShooter();
                if (shooter != null)
                    source = (Entity) shooter;
            }

            if (source instanceof Player && event.getEntity() instanceof Player) {
                Player player = (Player) source;
                Player target = (Player) event.getEntity();
                if (plugin.isVivePlayer(player) != plugin.isVivePlayer(target)) {
                    event.setCancelled(true);
                    msgPlayer(player, plugin.isVivePlayer(player) ? MSG_NO_PVP_VIVE : MSG_NO_PVP_CLASSIC);
                    msgPlayer(target, plugin.isVivePlayer(target) ? MSG_NO_PVP_VIVE : MSG_NO_PVP_CLASSIC);
                }
            }
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(PotionSplashEvent event) {
        if (config.getPreventPotion() && event.getAffectedEntities().size() > 0) {
            if (event.getPotion().getShooter() instanceof Player) {
                Player player = (Player) event.getPotion().getShooter();
                for (LivingEntity entity : event.getAffectedEntities()) {
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        if (plugin.isVivePlayer(player) != plugin.isVivePlayer(target)) {
                            event.setIntensity(entity, 0);
                            msgPlayer(player, plugin.isVivePlayer(player) ? MSG_NO_PVP_VIVE : MSG_NO_PVP_CLASSIC);
                            msgPlayer(target, plugin.isVivePlayer(target) ? MSG_NO_PVP_VIVE : MSG_NO_PVP_CLASSIC);
                        }
                    }
                }
                if (event.getAffectedEntities().size() == 0)
                    event.setCancelled(true);
            }
        }
    }

    private void msgPlayer(Player target, String s) {
        if (!timeout.containsKey(target) || (timeout.get(target) < System.currentTimeMillis() - (MSG_TIMEOUT * 1000L))) {
            target.sendMessage(s);
            timeout.put(target, System.currentTimeMillis());
        }
    }

}
