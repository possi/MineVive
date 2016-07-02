package de.jaschastarke.minecraft.vive.modules;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.PlayerProperties;
import de.jaschastarke.minecraft.vive.modules.longbow.LongbowConfig;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BowHeadshot extends CoreModule<MineVive> implements Listener {
    private static final double DEFAULT_HEADSET_DISTANCE = 0.3;
    private LongbowConfig config;

    public BowHeadshot(MineVive plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "BowHeadshot";
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getLog().info(plugin.getLang().trans("basic.loaded.module"));
    }

    @Override
    public void initialize(ModuleEntry<IModule> pEntry) {
        super.initialize(pEntry);
        config = plugin.getPluginConfig().registerSection(new LongbowConfig(this, entry));
        listeners.addListener(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getEntity() instanceof LivingEntity) {
            final Arrow arrow = (Arrow) event.getDamager();
            if (!arrow.getType().equals(EntityType.ARROW) || !(arrow.getShooter() instanceof Player))
                return;
            final PlayerProperties playerViveProperties = plugin.getPlayerViveProperties((Player) arrow.getShooter());
            if (playerViveProperties == null || !playerViveProperties.isLongbowShooting())
                return;

            final LivingEntity target = (LivingEntity) event.getEntity();
            final Location eyeloc = target.getEyeLocation();
            if (eyeloc != null) {
                //debugParticleLocation(eyeloc, Effect.FLAME);
                //debugParticleLocation(eyeloc, Effect.HEART);
                //debugParticleLocation(arrow.getLocation(), Effect.FLAME);
                //debugParticleLocation(arrow.getLocation(), Effect.LAVA_POP);
                getLog().debug("HEADSHOT FROM: "+arrow.getLocation().toString());
                getLog().debug("HEADSHOT TO  : "+eyeloc.toString() + "   eh: " + target.getEyeHeight());
                //getLog().debug("HEADSHOT DIST: "+distance + "  sqrt:" + eyeloc.distanceSquared(arrow.getLocation()) + " t:"+target.getType());
                if (doesPierceHead(target, arrow.getLocation(), arrow.getVelocity())) {
                    getLog().debug("HEADSHOT!!!");
                    final ItemStack helmet = target.getEquipment().getHelmet();
                    if (helmet != null && !helmet.getType().equals(Material.AIR)) {
                        getLog().debug("HEADSHOT: HAS HELMET: " + helmet.getType());
                        String s = plugin.getConfig().getString("bow.helmet");
                        if (config.getHelmet().equals(LongbowConfig.RewardType.IGNORE)) {
                            return;
                        } else if (config.getHelmet().equals(LongbowConfig.RewardType.DROP)) {
                            getLog().debug("HEADSHOT: HELMET DROP");
                            helmet.setDurability((short) (helmet.getDurability() - 1));
                            if (helmet.getDurability() > 0)
                                target.getWorld().dropItemNaturally(event.getEntity().getLocation(), helmet);
                            target.getEquipment().setHelmet(null);
                            return;
                        }
                    }

                    switch (config.getHeadshot()) {
                        case CRITICAL:
                            getLog().debug("HEADSHOT: CRITICAL");
                            arrow.setCritical(true);
                            break;
                        case KILL:
                            getLog().debug("HEADSHOT: INSTANT KILL");
                            event.setDamage(Math.max(event.getDamage(), target.getHealth()));
                            break;
                        case MULTIPLICATOR:
                            Double d = config.getMultiplicator();
                            getLog().debug("HEADSHOT: DMG x" + d.toString() + "  -> " + event.getDamage() + " => " + (event.getDamage() * d));
                            if (d > 0) {
                                event.setDamage(event.getDamage() * d);
                            }
                            break;
                    }
                }
            } else {
                getLog().debug("NOOO no eyeloc :(");
            }
        }
    }

    private boolean doesPierceHead(final LivingEntity target, final Location source, final Vector vec) {
        final double size = getHeadSize(target);
        // Raise head center, because I like it so
        final Location eye = target.getEyeLocation().clone().add(0, 0.2, 0);
        //debugSphereOuter(eye, size);

        Vector direction = vec.clone().normalize().multiply(0.1);
        getLog().debug("HEADSHOT DIR: "+direction + "  l:"+direction.length());
        double distance = eye.distance(source);

        Location loc = source.clone();
        while (distance > size) {
        //for (int i = 0; i < 20; i++) {
            if (distance <= size)
                break;

            getLog().debug("HEADSHOT DIST ADD: NOT YET "+distance + "  loc:" + loc);
            loc.add(direction);
            //debugParticleLocation(loc);
            double newdist = eye.distance(loc);
            if (newdist > distance) {
                getLog().debug("HEADSHOT DIST ADD: NOT YET " + newdist + " > " + distance);
                break;
            }
            distance = newdist;
        }

        loc = source.clone();
        while (distance > size) {
        //for (int i = 0; i < 20; i++) {
            if (distance <= size)
                break;

            getLog().debug("HEADSHOT DIST SUBTRACT: NOT YET "+distance + "  loc:" + loc);
            loc.subtract(direction);
            //debugParticleLocation(loc);
            double newdist = eye.distance(loc);
            if (newdist > distance) {
                getLog().debug("HEADSHOT DIST SUBTRACT: NOT YET "+ newdist + " > " + distance);
                loc.subtract(direction);
                return false;
            }
            distance = newdist;
        }
        getLog().debug("HEADSHOT DIST: "+ distance + "  loc:" + loc);
        return distance < size;
    }

    private double getHeadSize(LivingEntity target) {
        return DEFAULT_HEADSET_DISTANCE;
    }

    /*
    private void debugParticleLocation(final Location loc) {
        debugParticleLocation(loc, Effect.FIREWORKS_SPARK);
    }
    private void debugParticleLocation(final Location loc, final Effect e) {
        if (!isDebug())
            return;

        final Location l = loc.clone();
        for (int i = 0; i < 60; i++) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    for (Player p : loc.getWorld().getPlayers()) {
                        try {
                            PacketContainer responsePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.WORLD_PARTICLES);
                            responsePacket.getModifier().writeDefaults();
                            responsePacket.getStrings().write(0, e.getName());
                            responsePacket.getFloat().write(0, (float) l.getX());
                            responsePacket.getFloat().write(1, (float) l.getY());
                            responsePacket.getFloat().write(2, (float) l.getZ());
                            responsePacket.getFloat().write(3, 0f);
                            responsePacket.getFloat().write(4, 0f);
                            responsePacket.getFloat().write(5, 0f);
                            ProtocolLibrary.getProtocolManager().sendServerPacket(p, responsePacket);
                        } catch (InvocationTargetException e) {
                            getLog().debug("ERROR: Couldn't show particle for " + p.getName());
                        }
                    }
                }
            }, 5L * i);
        }
    }

    public void debugSphereOuter(final Location mid, final double r)
    {
        if (!isDebug())
            return;

        for (double x = -1.0; x <= 1.0; x += 0.1) {
            //double z = Math.sqrt(Math.pow(r, 2) - Math.pow(x, 2));
            for (double y = -1.0; y <= 1.0; y += 0.1) {
                //double y = Math.sqrt(Math.pow(r, 2) - Math.pow(x2, 2));
                double z =  Math.sqrt(Math.pow(r, 2) - Math.pow(x, 2) - Math.pow(y, 2));
                debugParticleLocation(new Location(mid.getWorld(), mid.getX() + x, mid.getY() + y, mid.getZ() + z), Effect.FLAME);
                debugParticleLocation(new Location(mid.getWorld(), mid.getX() + x, mid.getY() + y, mid.getZ() - z), Effect.FLAME);
                debugParticleLocation(new Location(mid.getWorld(), mid.getX() + x, mid.getY() - y, mid.getZ() + z), Effect.FLAME);
                debugParticleLocation(new Location(mid.getWorld(), mid.getX() + x, mid.getY() - y, mid.getZ() - z), Effect.FLAME);
            }
        }
    }

    @EventHandler
    public void onProjectileFire(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            final Arrow arrow = (Arrow) event.getEntity();
            debugParticleLocation(arrow.getLocation());
            debugParticleLocation(arrow.getLocation(), Effect.LAVA_POP);
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.isDead()) {
                        this.cancel();
                    }
                    debugParticleLocation(arrow.getLocation());
                }
            };
            task.runTaskTimer(plugin, 1L, 0L);
        }
    }*/
}
