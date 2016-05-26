package de.jaschastarke.minecraft.vive;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class MineVive extends JavaPlugin implements Listener {
    private static final String REQUEST_PAYLOAD_TAG = "MC|Vive";
    private static final String RESPONSE_PAYLOAD_TAG = "MC|ViveOK";
    private static final String RESPONSE_PAYLOAD_DATA = "/u/jpossi";
    private final PacketAdapter adapter;
    private Set<Player> onlineVivers = new HashSet<Player>();

    public MineVive() {
        adapter = new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                /*
                 * We could use https://github.com/aadnk/PacketWrapper/blob/master/PacketWrapper/src/main/java/com/comphenix/packetwrapper/WrapperPlayClientCustomPayload.java
                 * but that would be a little overkill for just 2 lines:
                 */
                String customPayloadTag = event.getPacket().getStrings().readSafely(0);
                //String customPayloadData = new String(event.getPacket().getByteArrays().readSafely(0));
                if (customPayloadTag.equals(REQUEST_PAYLOAD_TAG)) {
                    PacketContainer responsePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
                    responsePacket.getModifier().writeDefaults();
                    responsePacket.getStrings().write(0, RESPONSE_PAYLOAD_TAG);
                    responsePacket.getByteArrays().write(0, RESPONSE_PAYLOAD_DATA.getBytes());
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), responsePacket);
                        onlineVivers.add(event.getPlayer());
                    } catch (InvocationTargetException e) {
                        Bukkit.getLogger().warning("Failed to response To Vive: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        };
    }
    public void onEnable() {
        this.saveDefaultConfig();
        ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
        getServer().getPluginManager().registerEvents(this, this);
    }
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (this.getConfig().getBoolean("viveOnly.enable")) {
            this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    if (p.isOnline() && !onlineVivers.contains(p)) {
                        p.kickPlayer(MineVive.this.getConfig().getString("viveOnly.message"));
                    }
                }
            }, 10L);
        }
    }
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        onlineVivers.remove(event.getPlayer());
    }
    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event) {
        onlineVivers.remove(event.getPlayer());
    }

    public boolean isVivePlayer(Player player) {
        return onlineVivers.contains(player);
    }
}
