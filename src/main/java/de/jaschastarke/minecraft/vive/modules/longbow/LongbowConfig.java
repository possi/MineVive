package de.jaschastarke.minecraft.vive.modules.longbow;

import de.jaschastarke.bukkit.lib.CoreModule;
import de.jaschastarke.bukkit.lib.configuration.Configuration;
import de.jaschastarke.bukkit.lib.configuration.ConfigurationContainer;
import de.jaschastarke.bukkit.lib.configuration.IToGeneric;
import de.jaschastarke.configuration.IConfigurationNode;
import de.jaschastarke.configuration.IConfigurationSubGroup;
import de.jaschastarke.configuration.InvalidValueException;
import de.jaschastarke.configuration.annotations.IsConfigurationNode;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginConfigurations;
import de.jaschastarke.minecraft.vive.Config;
import de.jaschastarke.modularize.IModule;
import de.jaschastarke.modularize.ModuleEntry;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Longbow-Shooting is more fun
 */
@ArchiveDocComments
@PluginConfigurations(parent = Config.class)
public class LongbowConfig extends Configuration implements IConfigurationSubGroup {
    private ModuleEntry<IModule> entry = null;

    public enum HeadshotType implements IToGeneric {
        CRITICAL,
        KILL,
        MULTIPLICATOR;

        @Override
        public Object toGeneric() {
            return name().toUpperCase();
        }
    }
    public enum RewardType implements  IToGeneric {
        DROP,
        IGNORE,
        KEEP;

        @Override
        public Object toGeneric() {
            return name().toUpperCase();
        }
    }

    public LongbowConfig(ConfigurationContainer container) {
        super(container);
    }

    public LongbowConfig(CoreModule mod, ModuleEntry<IModule> modEntry) {
        super(mod.getPlugin().getDocCommentStorage());
        entry = modEntry;
    }

    @Override
    public String getName() {
        return "bow";
    }

    @Override
    public int getOrder() {
        return 700;
    }

    @Override
    public void setValue(IConfigurationNode node, Object pValue) throws InvalidValueException {
        super.setValue(node, pValue);
        if (node.getName().equals("headshot") && entry != null) {
            entry.setEnabled(getHeadshot() != null);
        }
    }
    @Override
    public void setValues(ConfigurationSection sect) {
        super.setValues(sect);
        entry.setDefaultEnabled(getHeadshot() != null);
    }

    /**
     * Allows you to reward headshots
     * either: CRITICAL or KILL or or MULTIPLICATOR
     */
    @IsConfigurationNode(order = 100)
    public HeadshotType getHeadshot() {
        return config.getString("headshot") != null ? HeadshotType.valueOf(config.getString("headshot")) : null;

    }

    /**
     * For headshot = MULTIPLICATOR only. Float-Value like: 2.0 for double damage
     */
    @IsConfigurationNode(order = 200)
    public Double getMultiplicator() {
        return (getHeadshot() != null && getHeadshot().equals(HeadshotType.MULTIPLICATOR)) ? config.getDouble("multiplicator", 1.0d) : null;
    }

    /**
     * Change headshot reward if target has a helmet
     * either: DROP (to remove helmet) or IGNORE (Minecraft default behavor) or KEEP (keep bonus as above)
     */
    @IsConfigurationNode(order = 300)
    public RewardType getHelmet() {
        return RewardType.valueOf(config.getString("helmet", "IGNORE").toUpperCase());
    }
}

