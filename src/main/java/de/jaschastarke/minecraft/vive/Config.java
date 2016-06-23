package de.jaschastarke.minecraft.vive;

import de.jaschastarke.bukkit.lib.Core;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.bukkit.lib.configuration.PluginConfiguration;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;

/**
 * Vivecraft Spigot Plugin - Configuration
 *
 * This configuration-file is automatically written when changed via ingame-commands. So any manual added comments are
 * removed.
 *
 * @see [https://github.com/possi/MineVive/blob/master/src/main/resources/config.yml]
 */
@ArchiveDocComments
@PluginConfigurations
public class Config extends PluginConfiguration{
    public Config(ConfigurationContainer container) {
        super(container);
    }

    public Config(Core plugin) {
        super(plugin);
    }
}
