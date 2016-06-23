package de.jaschastarke.minecraft.vive.modules.pvp;

import de.jaschastarke.bukkit.lib.configuration.Configuration;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.configuration.IConfigurationNode;
import de.jaschastarke.configuration.IConfigurationSubGroup;
import de.jaschastarke.configuration.InvalidValueException;
import de.jaschastarke.configuration.annotations.IsConfigurationNode;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;
import de.jaschastarke.minecraft.vive.Config;
import de.jaschastarke.minecraft.vive.modules.DisablePVP;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;

/**
 * Disable PVP between Non-Vive and Vive-Players
 */
@ArchiveDocComments
@PluginConfigurations(parent = Config.class)
public class DisablePVPConfig extends Configuration implements IConfigurationSubGroup {
    private ModuleEntry<IModule> entry = null;

    public DisablePVPConfig(ConfigurationContainer container) {
        super(container);
    }

    public DisablePVPConfig(DisablePVP mod, ModuleEntry<IModule> modEntry) {
        super(mod.getPlugin().getDocCommentStorage());
        entry = modEntry;
    }

    @Override
    public String getName() {
        return "pvp";
    }

    @Override
    public int getOrder() {
        return 500;
    }

    @Override
    public void setValue(IConfigurationNode node, Object pValue) throws InvalidValueException {
        if (node.getName().equals("sameTypeOnly") && entry != null) {
            entry.setEnabled(getSameTypeOnly());
        }
    }

    @IsConfigurationNode(order = 100)
    public boolean getSameTypeOnly() {
        return config.getBoolean("sameTypeOnly", false);
    }

    /**
     * Also Prevent Splash-Potions (Healing Potions are prevented also)
     */
    @IsConfigurationNode(order = 200)
    public Boolean getPreventPotion() {
        return config.getBoolean("preventPotion", true);
    }
}
