package de.jaschastarke.minecraft.vive.modules.permissions;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.bukkit.lib.configuration.Configuration;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.configuration.IConfigurationSubGroup;
import de.jaschastarke.configuration.annotations.IsConfigurationNode;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;
import de.jaschastarke.minecraft.vive.Config;

/**
 * Groups a Player will be added to (additional to groups he already has)
 * This requires Vault and a supported Permission-Plugin with Multi-Group-Support (e.g. PermissionEx)
 */
@ArchiveDocComments
@PluginConfigurations(parent = Config.class)
public class PermGroupConfig extends Configuration implements IConfigurationSubGroup {
    public PermGroupConfig(ConfigurationContainer container) {
        super(container);
    }

    public PermGroupConfig(CoreModule<?> mod) {
        super(mod.getPlugin().getDocCommentStorage());
    }

    @Override
    public String getName() {
        return "groups";
    }

    @Override
    public int getOrder() {
        return 400;
    }

    /**
     * Group for Players using Minecrift for Vive
     */
    @IsConfigurationNode(order = 100)
    public String getVive() {
        return config.getString("vive", null);
    }

    /**
     * Group for Non-Vive-Players
     */
    @IsConfigurationNode(order = 200)
    public String getVanilla() {
        return config.getString("vanilla", null);
    }

    /**
     * Group for Non-Teleporting-Movement (in Addition to vive-Group)
     * Assumen that all Players are using teleportation by default, because of that there is no Teleporting-Group
     */
    @IsConfigurationNode(order = 300)
    public String getFreemove() {
        return config.getString("freemove", null);
    }
}
