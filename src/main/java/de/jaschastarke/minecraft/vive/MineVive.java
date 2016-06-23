package de.jaschastarke.minecraft.vive;

import com.comphenix.protocol.ProtocolLibrary;
import de.jaschastarke.bukkit.lib.Core;
import de.jaschastarke.bukkit.lib.configuration.Configuration;
import de.jaschastarke.bukkit.lib.configuration.command.ConfigCommand;
import de.jaschastarke.minecraft.vive.modules.AlwaysItem;
import de.jaschastarke.minecraft.vive.modules.DisablePVP;
import de.jaschastarke.minecraft.vive.modules.PlayerProperties;
import de.jaschastarke.minecraft.vive.modules.VivecraftChannel;
import de.jaschastarke.minecraft.vive.modules.permissions.VaultIntegration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class MineVive extends Core {
    protected Config config = null;
    private MainCommand command;

    @Override
    public void onInitialize() {
        super.onInitialize();
        config = new Config(this);

        command = new MainCommand(this);
        ConfigCommand cc = new ConfigCommand(config, Permissions.CONFIG);
        cc.setPackageName(this.getName() + " - " + cc.getPackageName());
        commands.registerCommand(cc);
        commands.registerCommand(command);

        addModule(new VivecraftChannel(this));
        addModule(new AlwaysItem(this));
        addModule(new DisablePVP(this));
        listeners.addListener(new DependencyListener(this));

        config.saveDefault();
    }
    public void onEnable() {
        this.saveDefaultConfig();
    }
    public void onDisable() {
    }

    public PlayerProperties getPlayerViveProperties(Player player) {
        return getModule(VivecraftChannel.class).getPlayerViveProperties(player);
    }
    public boolean isVivePlayer(Player player) {
        return getPlayerViveProperties(player) != null;
    }

    public Config getPluginConfig() {
        return config;
    }
    public MainCommand getMainCommand() {
        return command;
    }
}
