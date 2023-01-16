package io.github.eirikh1996.movecraftelevators;

import io.github.eirikh1996.movecraftelevators.config.Settings;
import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MovecraftElevators extends JavaPlugin {
    private static MovecraftElevators instance;

    private Movecraft movecraftPlugin;

    @Override
    public void onEnable() {
        final Plugin movecraft = getServer().getPluginManager().getPlugin("Movecraft");
        if (!(movecraft instanceof Movecraft) || !movecraft.isEnabled()) {
            getLogger().severe("Movecraft is required, but was not found or is disabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        movecraftPlugin = (Movecraft) movecraft;
        saveDefaultConfig();
        final List<String> elevatorCrafts = getConfig().getStringList("AllowedElevatorCrafts");
        if (elevatorCrafts.isEmpty())
            return;
        for (var entry : elevatorCrafts) {
            var foundType = CraftManager.getInstance().getCraftTypeFromString(entry);
            if (foundType == null) {
                getLogger().warning(entry + " is not a valid elevator craft");
                continue;
            }
            Settings.ElevatorCrafts.add(foundType);

        }
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    public Movecraft getMovecraftPlugin() {
        return movecraftPlugin;
    }

    public static MovecraftElevators getInstance() {
        return instance;
    }
}
