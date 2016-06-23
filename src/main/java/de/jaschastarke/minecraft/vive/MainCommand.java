package de.jaschastarke.minecraft.vive;

import de.jaschastarke.LocaleString;
import de.jaschastarke.bukkit.lib.commands.*;
import de.jaschastarke.bukkit.lib.commands.annotations.Description;
import de.jaschastarke.bukkit.lib.commands.annotations.IsCommand;
import de.jaschastarke.bukkit.lib.commands.annotations.NeedsPermission;
import de.jaschastarke.bukkit.lib.commands.annotations.Usages;
import de.jaschastarke.bukkit.lib.commands.parser.TabCompletion;
import de.jaschastarke.maven.ArchiveDocComments;
import de.jaschastarke.maven.PluginCommand;
import de.jaschastarke.minecraft.lib.permissions.IAbstractPermission;
import de.jaschastarke.minecraft.lib.permissions.IPermission;

import java.util.List;

/**
 * Vivecraft Spigot Plugin: Limit to Vive-Players. Per Client Permissions, Prefixes and more.
 * @usage /<command> - displays Vivecraft Spigot-Help
 * @permission minevive.command
 */
@ArchiveDocComments
@PluginCommand
public class MainCommand extends BukkitCommand implements IHelpDescribed, IMethodCommandContainer {
    private MineVive plugin;
    
    public MainCommand() {
    }
    public MainCommand(MineVive plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "vive";
    }

    /**
     * @internal has no effect, as not tested by any command handler
     * @see IHelpDescribed
     */
    @Override
    public IAbstractPermission[] getRequiredPermissions() {
        return new IAbstractPermission[]{Permissions.COMMAND};
    }
    @Override
    public String[] getUsages() {
        return null;
    }
    @Override
    public CharSequence getDescription() {
        return new LocaleString("command.general");
    }
    @Override
    public String getPackageName() {
        return plugin.getName();
    }
    @Override
    public IPermission getPermission(String subPerm) {
        return Permissions.CONTAINER.getPermission(subPerm);
    }
    
    @IsCommand("reload")
    @Usages("")
    @Description(value = "command.config.reload", translate = true)
    @NeedsPermission(value={"config"})
    public boolean doReload(final CommandContext context) {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (plugin.isDebug())
                    plugin.getLog().debug("Scheduler: Synchronous Task run: Disable");
                plugin.onDisable();
                plugin.getPluginConfig().reload();
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.isDebug())
                            plugin.getLog().debug("Scheduler: Synchronous Task run: Enable");
                        plugin.onEnable();
                        context.response(context.getFormatter().getString("command.config.reload.success"));
                    }
                });
            }
        });
        return true;
    }
    
    @Override
    public List<TabCompletion> getTabCompleter(MethodCommand cmd) {
        return null;
    }
}
