package io.github.eirikh1996.movecraftelevators.sign;

import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class FloorSign implements Listener {
    private final String HEADER = ChatColor.DARK_PURPLE + "[Floor]";

    @EventHandler
    public void onCreate(SignChangeEvent event) {
        if (event.getLine(0) == null || !event.getLine(0).equalsIgnoreCase(ChatColor.stripColor(HEADER)))
            return;
        event.setLine(0, HEADER);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        final Block clickedBlock = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!Tag.WALL_SIGNS.isTagged(clickedBlock.getType())) {
                return;
            }
            final Sign sign = (Sign) clickedBlock.getState();
            if (!sign.getLine(0).equals(HEADER)) {
                return;
            }
            final WallSign wallSign = (WallSign) clickedBlock.getBlockData();
            final Vector direction = wallSign.getFacing().getOppositeFace().getDirection().multiply(2);
            final Vector left;
            final Vector right;
            switch (wallSign.getFacing()) {
                case NORTH -> {
                    left = direction.clone().add(BlockFace.EAST.getDirection());
                    right = direction.clone().add(BlockFace.WEST.getDirection());
                    break;
                }
                case SOUTH -> {
                    left = direction.clone().add(BlockFace.WEST.getDirection());
                    right = direction.clone().add(BlockFace.EAST.getDirection());
                    break;
                }
                case WEST -> {
                    left = direction.clone().add(BlockFace.NORTH.getDirection());
                    right = direction.clone().add(BlockFace.SOUTH.getDirection());
                    break;
                }
                case EAST -> {
                    left = direction.clone().add(BlockFace.SOUTH.getDirection());
                    right = direction.clone().add(BlockFace.NORTH.getDirection());
                    break;
                }
                default -> {
                    return;
                }
            }

        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

        }
    }
}
