package io.github.eirikh1996.movecraftelevators.config;

import net.countercraft.movecraft.craft.type.CraftType;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class Manager {
    public static Set<CraftType> ElevatorCrafts = new HashSet<>();


    public static boolean allowedBlockOnElevatorCrafts(final Material type) {
        return ElevatorCrafts.stream().anyMatch( t -> t.getMaterialSetProperty(CraftType.ALLOWED_BLOCKS).contains(type));
    }

    public static boolean elevatorCraftsCanPassThrough(final Material type) {
        return ElevatorCrafts.stream().anyMatch( t -> t.getMaterialSetProperty(CraftType.PASSTHROUGH_BLOCKS).contains(type));
    }
}
