package de.jaschastarke.minecraft.vive;

import de.jaschastarke.bukkit.lib.Core;
import de.jaschastarke.bukkit.lib.PluginLang;
import de.jaschastarke.bukkit.lib.configuration.command.ConfigCommand;
import de.jaschastarke.minecraft.vive.modules.*;
import de.jaschastarke.utils.ClassDescriptorStorage;
import org.bukkit.entity.Player;

public class MineVive extends Core {
    protected Config config = null;
    private MainCommand command;

    @Override
    public void onInitialize() {
        super.onInitialize();
        config = new Config(this);

        setLang(new PluginLang("lang/messages", this));

        command = new MainCommand(this);
        ConfigCommand cc = new ConfigCommand(config, Permissions.CONFIG);
        cc.setPackageName(this.getName() + " - " + cc.getPackageName());
        command.registerCommand(cc);
        commands.registerCommand(command);

        addModule(new VivecraftChannel(this));
        addModule(new ViveOnly(this));
        addModule(new AlwaysItem(this));
        addModule(new PermGroup(this));
        addModule(new DisablePVP(this));
        listeners.addListener(new DependencyListener(this));

        config.saveDefault();
    }
    @Override
    public void onEnable() {
        super.onEnable();

        this.saveDefaultConfig();
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

    @Override
    public ClassDescriptorStorage getDocCommentStorage() {
        if (cds == null) {
            cds = new ClassDescriptorStorage();
            cds.getResourceBundle().addResourceBundle("lang.doccomments");
        }
        return cds;
    }
}
