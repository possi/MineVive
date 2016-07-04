package de.jaschastarke.minecraft.vive;

import de.jaschastarke.bukkit.lib.Core;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.bukkit.lib.configuration.PluginConfiguration;
import de.jaschastarke.configuration.annotations.IsConfigurationNode;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;

/**
 * Vivecraft Spigot Plugin - Configuration
 *
 * This configuration-file is automatically written when changed via ingame-commands. So any manual added comments are
 * removed.
 *
 * See: http://ci.ja-s.de:8080/job/MineVive/lastSuccessfulBuild/artifact/target/default_config.yml/*view*
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

    /**
     * Debug
     *
     * The debug modus spams much details about the plugin to the server-log (console) which can help to solve issues.
     *
     * default: false
     */
    @IsConfigurationNode(order = 9999)
    public boolean getDebug() {
        return config.getBoolean("debug", false);
    }
}
