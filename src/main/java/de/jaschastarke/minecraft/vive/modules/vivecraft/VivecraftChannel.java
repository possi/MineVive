package de.jaschastarke.minecraft.vive.modules.vivecraft;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.minecraft.vive.MineVive;
import de.jaschastarke.minecraft.vive.PlayerProperties;
import de.jaschastarke.minecraft.vive.modules.ViveOnly;
import de.jaschastarke.minecraft.vive.modules.vivecraft.packet.FreeMove;
import de.jaschastarke.minecraft.vive.modules.vivecraft.packet.HelloVersion;
import de.jaschastarke.minecraft.vive.modules.vivecraft.packet.VivecraftPlayerProperties;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class VivecraftChannel extends CoreModule<MineVive> implements Listener {
    private static final String REQUEST_PAYLOAD_TAG = "MC|Vive";
    private static final String REQUEST_BRAND_TAG = "MC|Brand";
    private static final String REQUEST_VERSION_PAYLOAD_TAG = "MC|Vive|Version";
    private static final String REQUEST_FREEMOVE_PAYLOAD_TAG = "MC|Vive|FreeMove";
    private static final String REQUEST_CHANNEL_TAG = "Vivecraft";
    private static final String RESPONSE_PAYLOAD_TAG = "MC|ViveOK";
    private PacketAdapter adapter;
    private Set<VivecraftPlayerProperties> onlineVivers = new HashSet<VivecraftPlayerProperties>();
    private Map<Player, Channel> vivecraftChannels = new WeakHashMap<Player, Channel>();
    private PrefixConfig prefixConfig;

    public VivecraftChannel(MineVive plugin) {
        super(plugin);
    }

    @Override
    public void initialize(ModuleEntry<IModule> pEntry) {
        super.initialize(pEntry);
        prefixConfig = plugin.getPluginConfig().registerSection(new PrefixConfig(plugin.getDocCommentStorage()));

        adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                /*
                 * We could use https://github.com/aadnk/PacketWrapper/blob/master/PacketWrapper/src/main/java/com/comphenix/packetwrapper/WrapperPlayClientCustomPayload.java
                 * but that would be a little overkill for just 2 lines:
                 */
                String customPayloadTag = event.getPacket().getStrings().readSafely(0);

                // BEGIN DEBUG:
                if (VivecraftChannel.this.plugin.isDebug()) {
                    byte[] ddata = event.getPacket().getByteArrays().readSafely(0);
                    if (ddata.length > 1)
                        getLog().debug("RECV Tag/String: " + customPayloadTag + ": " + new String(ddata));
                    else
                        getLog().debug("RECV Tag/Byte: " + customPayloadTag + ": " + (int) ddata[0]);
                }
                // END DEBUG;

                if (customPayloadTag.equals(REQUEST_BRAND_TAG)) {
                    String data = new String(event.getPacket().getByteArrays().readSafely(0));
                    if (data.contains("forge")) {
                        VivecraftPlayerProperties pp = getOrCreatePlayerViveProperties(event.getPlayer());
                        HelloVersion.setForge(pp, true);
                        //updatePlayer(event.getPlayer());
                    }
                } else if (customPayloadTag.equals(REQUEST_PAYLOAD_TAG) || customPayloadTag.equals(REQUEST_VERSION_PAYLOAD_TAG)) {
                    Channel packetHandler = new Channel(VivecraftChannel.this, event.getPlayer(), RESPONSE_PAYLOAD_TAG);
                    HelloVersion helloVersion = new HelloVersion(packetHandler);
                    if (customPayloadTag.equals(REQUEST_VERSION_PAYLOAD_TAG)) {
                        helloVersion.processVersion(new String(event.getPacket().getByteArrays().readSafely(0)));
                    } else {
                        helloVersion.processVersion(null);
                    }
                    packetHandler.response(helloVersion.getVersion().getBytes());
                } else if (customPayloadTag.equals(REQUEST_CHANNEL_TAG)) {
                    byte[] data = event.getPacket().getByteArrays().readSafely(0);
                    if (data.length > 0) {
                        byte channel = data[0];
                        data = Arrays.copyOfRange(data, 1, data.length);
                        getPacketHandler(event.getPlayer()).process(channel, data);
                    }
                } else if (customPayloadTag.equals(REQUEST_FREEMOVE_PAYLOAD_TAG)) {
                    VivecraftPlayerProperties pp = getPlayerViveProperties(event.getPlayer());
                    FreeMove.setTeleporting(pp, event.getPacket().getByteArrays().readSafely(0)[0]);
                    updatePlayer(event.getPlayer());
                }
            }
        };

        listeners.addListener(this);
    }

    protected Channel getPacketHandler(Player p) {
        Channel packetHandler = vivecraftChannels.get(p);
        if (packetHandler == null || packetHandler.getPlayer() == null) {
            packetHandler = new Channel(this, p, REQUEST_CHANNEL_TAG);
            vivecraftChannels.put(p, packetHandler);
        }
        return packetHandler;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        final Player p = event.getPlayer();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (p.isOnline() && !plugin.isVivePlayer(p)) {
                    updatePlayer(p);
                }
            }
        }, plugin.getModule(ViveOnly.class).getConfig().getWaitTimeout());
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
        for (Iterator<VivecraftPlayerProperties> pp = onlineVivers.iterator(); pp.hasNext(); ) {
            if (pp.next().getPlayer().equals(player))
                pp.remove();
        }
    }

    public VivecraftPlayerProperties getPlayerViveProperties(Player player) {
        for (VivecraftPlayerProperties pp : onlineVivers) {
            if (pp.getPlayer().equals(player))
                return pp;
        }
        return null;
    }
    public VivecraftPlayerProperties getOrCreatePlayerViveProperties(Player player) {
        VivecraftPlayerProperties pp = getPlayerViveProperties(player);
        if (pp == null) {
            pp = new VivecraftPlayerProperties(player);
            storePlayerViveProperties(pp);
        }
        return pp;
    }
    protected void storePlayerViveProperties(VivecraftPlayerProperties pp) {
        Validate.notNull(pp);
        onlineVivers.add(pp);
    }

    protected void updatePlayer(Player p) {
        VivePropertyChangeEvent event = new VivePropertyChangeEvent(p, getPlayerViveProperties(p));
        plugin.getServer().getPluginManager().callEvent(event);
        updatePlayerPrefix(p);
    }
    protected void updatePlayerPrefix(Player p) {
        String prefix = null;
        VivecraftPlayerProperties pp = getPlayerViveProperties(p);
        if (pp != null) {
            if (pp.isVive() && prefixConfig.getVive() != null)
                prefix = prefixConfig.getVive();
        } else if (prefixConfig.getVanilla() != null) {
            prefix = prefixConfig.getVanilla();
        }

        if (prefix != null) {
            p.setCustomName(prefix.replace("&", "§") + p.getName());
            p.setDisplayName(prefix.replace("&", "§") + p.getName());
        }
    }
}
