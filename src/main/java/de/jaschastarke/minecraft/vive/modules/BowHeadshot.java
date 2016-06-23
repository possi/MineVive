package de.jaschastarke.minecraft.vive.modules;

import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.PlayerProperties;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BowHeadshot implements Listener {
    private static final double DEFAULT_HEADSET_DISTANCE = 0.4;

    private MineVive plugin;

    public BowHeadshot(MineVive plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        if (plugin.getConfig().getString("bow.headshot", "").length() <= 0)
            return;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void onDisable() {}

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
                plugin.getLogger().info("DEBUG HEADSHOT FROM: "+arrow.getLocation().toString());
                plugin.getLogger().info("DEBUG HEADSHOT TO  : "+eyeloc.toString() + "   eh: " + target.getEyeHeight());
                //plugin.getLogger().info("DEBUG HEADSHOT DIST: "+distance + "  sqrt:" + eyeloc.distanceSquared(arrow.getLocation()) + " t:"+target.getType());
                if (doesPierceHead(target, arrow.getLocation())) {
                    plugin.getLogger().info("DEBUG HEADSHOT!!!");
                    final ItemStack helmet = target.getEquipment().getHelmet();
                    if (helmet != null && !helmet.getType().equals(Material.AIR)) {
                        plugin.getLogger().info("DEBUG HEADSHOT: HAS HELMET: " + helmet.getType());
                        String s = plugin.getConfig().getString("bow.helmet");
                        if (s != null && s.toUpperCase().equals("IGNORE")) {
                            return;
                        } else if (s != null && s.toUpperCase().equals("DROP")) {
                            plugin.getLogger().info("DEBUG HEADSHOT: HELMET DROP");
                            helmet.setDurability((short) (helmet.getDurability() - 1));
                            if (helmet.getDurability() > 0)
                                target.getWorld().dropItemNaturally(event.getEntity().getLocation(), helmet);
                            target.getEquipment().setHelmet(null);
                            return;
                        }
                    }

                    String s = plugin.getConfig().getString("bow.headshot");
                    if (s != null && s.toUpperCase().equals("CRITICAL")) {
                        plugin.getLogger().info("DEBUG HEADSHOT: CRITICAL");
                        arrow.setCritical(true);
                    } else if (s != null && s.toUpperCase().equals("KILL")) {
                        plugin.getLogger().info("DEBUG HEADSHOT: INSTANT KILL");
                        event.setDamage(Math.max(event.getDamage(), target.getHealth()));
                    } else {
                        Double d = plugin.getConfig().getDouble("bow.headshot");
                        plugin.getLogger().info("DEBUG HEADSHOT: DMG x" + d.toString() + "  -> " + event.getDamage() + " => " + (event.getDamage() * d));
                        if (d > 0) {
                            event.setDamage(event.getDamage() * d);
                        }
                    }
                }
            } else {
                plugin.getLogger().info("DEBUG: NOOO no eyeloc :(");
            }
        }
    }

    private boolean doesPierceHead(final LivingEntity target, final Location pierce) {
        final double size = getHeadSize(target);
        final Location eye = target.getEyeLocation();
        if (eye == null)
            return false;

        Vector direction = pierce.getDirection().normalize().multiply(0.1);
        plugin.getLogger().info("DEBUG HEADSHOT DIR: "+direction + "  l:"+direction.length());
        double distance = eye.distance(pierce);

        Location loc = pierce.clone();
        while (distance > size) {
        //for (int i = 0; i < 20; i++) {
            //if (distance <= size)
                //break;

            plugin.getLogger().info("DEBUG HEADSHOT DIST ADD: NOT YET "+distance + "  loc:" + loc);
            loc.add(direction);
            double newdist = eye.distance(loc);
            if (newdist > distance) {
                plugin.getLogger().info("DEBUG HEADSHOT DIST ADD: NOT YET " + newdist + " > " + distance);
                break;
            }
            distance = newdist;
        }

        loc = pierce.clone();
        while (distance > size) {
        //for (int i = 0; i < 20; i++) {
            //if (distance <= size)
                //break;

            plugin.getLogger().info("DEBUG HEADSHOT DIST SUBTRACT: NOT YET "+distance + "  loc:" + loc);
            loc.subtract(direction);
            double newdist = eye.distance(loc);
            if (newdist > distance) {
                plugin.getLogger().info("DEBUG HEADSHOT DIST SUBTRACT: NOT YET "+ newdist + " > " + distance);
                loc.subtract(direction);
                return false;
            }
            distance = newdist;
        }
        plugin.getLogger().info("DEBUG HEADSHOT DIST: "+ distance + "  loc:" + loc);
        return true;
    }

    private double getHeadSize(LivingEntity target) {
        return DEFAULT_HEADSET_DISTANCE;
    }
}
