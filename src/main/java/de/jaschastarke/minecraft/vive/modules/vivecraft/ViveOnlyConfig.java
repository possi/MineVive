package de.jaschastarke.minecraft.vive.modules.vivecraft;

import de.jaschastarke.bukkit.lib.configuration.Configuration;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.configuration.IConfigurationNode;
import de.jaschastarke.configuration.IConfigurationSubGroup;
import de.jaschastarke.configuration.InvalidValueException;
import de.jaschastarke.configuration.annotations.IsConfigurationNode;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;
import de.jaschastarke.minecraft.vive.Config;
import de.jaschastarke.minecraft.vive.modules.ViveOnly;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.bukkit.configuration.ConfigurationSection;

@ArchiveDocComments
@PluginConfigurations(parent = Config.class)
public class ViveOnlyConfig extends Configuration implements IConfigurationSubGroup {
    private static final String KICK_MESSAGE_DEFAULT = "This Server requires you to use Minecrift for Vive: https://github.com/jrbudda/minecrift";

    private ModuleEntry<IModule> entry = null;

    public ViveOnlyConfig(ConfigurationContainer container) {
        super(container);
    }

    public ViveOnlyConfig(ViveOnly mod, ModuleEntry<IModule> modEntry) {
        super(mod.getPlugin().getDocCommentStorage());
        entry = modEntry;
    }

    @Override
    public String getName() {
        return "viveOnly";
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public void setValue(IConfigurationNode node, Object pValue) throws InvalidValueException {
        super.setValue(node, pValue);
        if (node.getName().equals("enabled") && entry != null) {
            entry.setEnabled(getEnabled());
        }
    }
    @Override
    public void setValues(ConfigurationSection sect) {
        super.setValues(sect);
        entry.setDefaultEnabled(getEnabled());
    }

    /**
     * Kick all Players not using Vive-Minecrift, so that only teleporting users are on the Server
     */
    @IsConfigurationNode(order = 100)
    public boolean getEnabled() {
        return config.getBoolean("enabled", false);
    }

    /**
     * Kick-Message for Non-Vivers
     */
    @IsConfigurationNode(order = 200)
    public String getMessage() {
        return config.getString("message", KICK_MESSAGE_DEFAULT);
    }

    /**
     * The timeout before a non-vive player gets kicked. You may increase thit, if you assume false-positiv kicks
     * In ticks: so 10 is half a second. 20 for one second, 40 for two seconds, and so on
     */
    @IsConfigurationNode(order = 300)
    public long getWaitTimeout() {
        return config.getLong("waitTimeout", 10L);
    }
}
