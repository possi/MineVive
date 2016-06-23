package de.jaschastarke.minecraft.vive.modules;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.modules.alwaysitem.AlwaysItemConfig;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class AlwaysItem extends CoreModule<MineVive> implements Listener {
    private static final int LAST_HOTBAR_SLOT = 8;
    private ItemStack itemStack;
    private AlwaysItemConfig config;

    public AlwaysItem(MineVive plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "AlwayItem";
    }

    @Override
    public void initialize(ModuleEntry<IModule> pEntry) {
        super.initialize(pEntry);

        config = plugin.getPluginConfig().registerSection(new AlwaysItemConfig(this, entry));
        listeners.addListener(this);
    }

    @Override
    public void onEnable() {
        String item = config.getItem();
        if (item == null || item.length() == 0)
            return;
        Material itemType = Material.getMaterial(item);
        if (itemType == null) {
            plugin.getLogger().warning("Unknown Item-Type for alwaysItem.item: " + item);
            getModuleEntry().deactivateUsage();
            return;
        }

        itemStack = new ItemStack(itemType, 1, (short) 0, new Integer(config.getData()).byteValue());
    }

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
