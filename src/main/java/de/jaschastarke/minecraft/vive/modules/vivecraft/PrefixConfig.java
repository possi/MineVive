package de.jaschastarke.minecraft.vive.modules.vivecraft;

import de.jaschastarke.bukkit.lib.configuration.Configuration;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.configuration.IConfigurationSubGroup;
import de.jaschastarke.configuration.annotations.IsConfigurationNode;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;
import de.jaschastarke.minecraft.vive.Config;
import de.jaschastarke.utils.ClassDescriptorStorage;

/**
 * Sets Prefix to Player names (Doesn't work yet with Essentials Display Names or PermGroup Prefixes)
 */
@ArchiveDocComments
@PluginConfigurations(parent = Config.class)
public class PrefixConfig extends Configuration implements IConfigurationSubGroup {
    public PrefixConfig(ConfigurationContainer container) {
        super(container);
    }

    public PrefixConfig(ClassDescriptorStorage docCommentStorage) {
        super(docCommentStorage);
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public int getOrder() {
        return 300;
    }

    /**
     * Prefix for Players using Minecrift for Vive
     * Example: "&4[Vive]&r "
     */
    @IsConfigurationNode(order = 100)
    public String getVive() {
        return config.getString("vive", null);
    }

    /**
     * Prefix for all other Players (Classic users)
     */
    @IsConfigurationNode(order = 200)
    public String getVanilla() {
        return config.getString("vanilla", null);
    }
}
