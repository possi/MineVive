package de.jaschastarke.minecraft.vive;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jaschastarke.minecraft.vive.modules.AlwaysItem;
import de.jaschastarke.minecraft.vive.modules.DisablePVP;
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
import java.util.Iterator;
import java.util.Set;

public class MineVive extends JavaPlugin implements Listener {
    private static final String REQUEST_PAYLOAD_TAG = "MC|Vive";
    private static final String REQUEST_VERSION_PAYLOAD_TAG = "MC|Vive|Version";
    private static final String REQUEST_FREEMOVE_PAYLOAD_TAG = "MC|Vive|FreeMove";
    private static final String RESPONSE_PAYLOAD_TAG = "MC|ViveOK";
    private static final String RESPONSE_PAYLOAD_DATA = "/u/jpossi";
    private final PacketAdapter adapter;
    private final AlwaysItem alwaysItem;
    private final DisablePVP disablePvp;
    private Set<PlayerProperties> onlineVivers = new HashSet<PlayerProperties>();
    private PermissionIntegration vaultIntegration;

    public MineVive() {
        this.alwaysItem = new AlwaysItem(this);
        this.disablePvp = new DisablePVP(this);

        adapter = new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                /*
                 * We could use https://github.com/aadnk/PacketWrapper/blob/master/PacketWrapper/src/main/java/com/comphenix/packetwrapper/WrapperPlayClientCustomPayload.java
                 * but that would be a little overkill for just 2 lines:
                 */
                String customPayloadTag = event.getPacket().getStrings().readSafely(0);
                if (customPayloadTag.equals(REQUEST_PAYLOAD_TAG) || customPayloadTag.equals(REQUEST_VERSION_PAYLOAD_TAG)) {
                    PacketContainer responsePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
                    responsePacket.getModifier().writeDefaults();
                    responsePacket.getStrings().write(0, RESPONSE_PAYLOAD_TAG);
                    responsePacket.getByteArrays().write(0, RESPONSE_PAYLOAD_DATA.getBytes());
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), responsePacket);
                        PlayerProperties pp = new PlayerProperties(event.getPlayer());
                        if (customPayloadTag.equals(REQUEST_VERSION_PAYLOAD_TAG)) {
                            String customPayloadData = new String(event.getPacket().getByteArrays().readSafely(0));
                            int pos = customPayloadData.lastIndexOf("jrbudda");
                            if (pos > -1) {
                                try {
                                    int v = Integer.parseInt(customPayloadData.substring(pos + 7));
                                    if (v >= 15) {
                                        pp.keyboard = true;
                                        pp.longbow_shooting = true;
                                    }
                                } catch (NumberFormatException nfe) {
                                    // Version string not supported
                                }
                            }
                        }
                        onlineVivers.add(pp);
                        updatePlayer(event.getPlayer());
                    } catch (InvocationTargetException e) {
                        Bukkit.getLogger().warning("Failed to response To Vive: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (customPayloadTag.equals(REQUEST_FREEMOVE_PAYLOAD_TAG)) {
                    PlayerProperties pp = getPlayerViveProperties(event.getPlayer());
                    if (pp != null) {
                        pp.teleporting = event.getPacket().getByteArrays().readSafely(0)[0] == 0;
                        updatePlayer(event.getPlayer());
                    }
                }
            }
        };
    }
    public void onEnable() {
        this.saveDefaultConfig();
        ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
        getServer().getPluginManager().registerEvents(this, this);
        alwaysItem.onEnable();
        disablePvp.onEnable();
        if (getServer().getPluginManager().isPluginEnabled("Vault"))
            this.vaultIntegration = new de.jaschastarke.minecraft.vive.modules.VaultIntegration(this);
    }
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
        alwaysItem.onDisable();
        disablePvp.onDisable();
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        final Player p = event.getPlayer();

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (p.isOnline()) {
                    if (MineVive.this.getConfig().getBoolean("viveOnly.enable") && !isVivePlayer(p)) {
                        p.kickPlayer(MineVive.this.getConfig().getString("viveOnly.message"));
                    } else {
                        updatePlayer(p);
                    }
                }
            }
        }, 10L);
    }
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        removePlayerProperties(event.getPlayer());
    }
    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event) {
        removePlayerProperties(event.getPlayer());
    }
    protected void removePlayerProperties(Player player) {
        for (Iterator<PlayerProperties> pp = onlineVivers.iterator(); pp.hasNext(); ) {
            if (pp.next().getPlayer().equals(player))
                pp.remove();
        }
    }

    public PlayerProperties getPlayerViveProperties(Player player) {
        for (PlayerProperties pp : onlineVivers) {
            if (pp.getPlayer().equals(player))
                return pp;
        }
        return null;
    }
    public boolean isVivePlayer(Player player) {
        return getPlayerViveProperties(player) != null;
    }

    protected void updatePlayer(Player p) {
        updatePlayerPrefix(p);
        if (this.vaultIntegration != null)
            this.vaultIntegration.updatePlayerPermissionGroup(p);
    }
    protected void updatePlayerPrefix(Player p) {
        String prefix = null;
        PlayerProperties pp = getPlayerViveProperties(p);
        if (pp != null) {
            if (pp.isVive() && getConfig().getString("prefix.vive", null) != null)
                prefix = getConfig().getString("prefix.vive");
        } else if (getConfig().getString("prefix.vanilla", null) != null) {
            prefix = getConfig().getString("prefix.vanilla");
        }

        if (prefix != null) {
            p.setCustomName(prefix.replace("&", "§") + p.getName());
            p.setDisplayName(prefix.replace("&", "§") + p.getName());
        }
    }
}
