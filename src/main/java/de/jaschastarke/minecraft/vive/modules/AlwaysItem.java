package de.jaschastarke.minecraft.vive.modules;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AlwaysItem implements Listener {
    private static final int LAST_HOTBAR_SLOT = 8;
    private JavaPlugin plugin;
    private ItemStack itemStack;

    public AlwaysItem(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        String item = plugin.getConfig().getString("alwaysItem.item");
        if (item == null || item.length() == 0)
            return;
        Material itemType = Material.getMaterial(item);
        if (itemType == null) {
            plugin.getLogger().warning("Unknown Item-Type for alwaysItem.item: " + item);
            return;
        }

        itemStack = new ItemStack(itemType, 1, (short) 0, new Integer(plugin.getConfig().getInt("alwaysItem.data", 0)).byteValue());

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void onDisable() {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ensureItem(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (ItemStack is : event.getDrops()) {
            if (isTheItem(is)) {
                is.setType(Material.AIR);
            }
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ensureItem(event.getPlayer());
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        if (isTheItem(event.getItem().getItemStack())) {
            if (getItemStack(event.getPlayer()) != null)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (isTheItem(event.getItemDrop().getItemStack()))
            event.setCancelled(true);
    }

    protected void ensureItem(Player player) {
        if (null == getItemStack(player)) {
            ItemStack c = player.getInventory().getItem(LAST_HOTBAR_SLOT);
            if (c == null || c.getType().equals(Material.AIR)) {
                player.getInventory().setItem(LAST_HOTBAR_SLOT, itemStack.clone());
            } else {
                player.getInventory().addItem(itemStack.clone());
            }
        }
    }

    protected ItemStack getItemStack(Player player) {
        for (ItemStack is : player.getInventory().getContents()) {
            if (isTheItem(is))
                return is;
        }
        return null;
    }

    protected boolean isTheItem(ItemStack is) {
        return is != null && is.getType().equals(itemStack.getType()) && is.getData().equals(itemStack.getData());
    }
}
