package de.jaschastarke.minecraft.vive.modules.alwaysitem;

import de.jaschastarke.bukkit.lib.configuration.Configuration;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.configuration.IConfigurationNode;
import de.jaschastarke.configuration.IConfigurationSubGroup;
import de.jaschastarke.configuration.InvalidValueException;
import de.jaschastarke.configuration.annotations.IsConfigurationNode;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;
import de.jaschastarke.minecraft.vive.Config;
import de.jaschastarke.minecraft.vive.modules.AlwaysItem;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;

/**
 * Give player this Items always (but only one time)
 */
@ArchiveDocComments
@PluginConfigurations(parent = Config.class)
public class AlwaysItemConfig extends Configuration implements IConfigurationSubGroup {
    private ModuleEntry<IModule> entry = null;

    public AlwaysItemConfig(ConfigurationContainer container) {
        super(container);
    }

    public AlwaysItemConfig(AlwaysItem mod, ModuleEntry<IModule> modEntry) {
        super(mod.getPlugin().getDocCommentStorage());
        entry = modEntry;
    }

    @Override
    public String getName() {
        return "alwaysItem";
    }

    @Override
    public int getOrder() {
        return 200;
    }

    @Override
    public void setValue(IConfigurationNode node, Object pValue) throws InvalidValueException {
        if (node.getName().equals("item") && entry != null) {
            entry.setEnabled(getItem() != null && getItem().length() > 0);
        }
    }

    /**
     * e.g. "MAP" for Map (@see https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Material.java)
     */
    @IsConfigurationNode(order = 100)
    public String getItem() {
        return config.getString("item", null);
    }

    /**
     * e.G. Map-Id
     */
    @IsConfigurationNode(order = 200)
    public int getData() {
        return config.getInt("data", 0);
    }
}
