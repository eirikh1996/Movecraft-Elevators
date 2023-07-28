package io.github.eirikh1996.movecraftelevators.sign;

import io.github.eirikh1996.movecraftelevators.config.Manager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.util.hitboxes.BitmapHitBox;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.countercraft.movecraft.util.hitboxes.MutableHitBox;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class PlatformSign implements Listener {

    private final String HEADER = ChatColor.DARK_PURPLE + "[Platform]";
    @EventHandler
    public void onCreate(SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase(ChatColor.stripColor(HEADER)))
            return;
        final String secLine = event.getLine(1);
        if (secLine == null || secLine.length() == 0) {
            event.getPlayer().sendMessage("Second line cannot be blank");
            return;
        }
        final CraftType foundType = CraftManager.getInstance().getCraftTypeFromString(secLine);
        if (foundType == null) {
            event.getPlayer().sendMessage(secLine + " is not a valid craft type");
            return;
        }
        if (!Manager.ElevatorCrafts.contains(foundType)) {
            event.getPlayer().sendMessage(foundType.getStringProperty(CraftType.NAME) + " is not a permitted elevator craft");
            return;
        }
        event.setLine(0, HEADER);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        final Block clicked = event.getClickedBlock();
        if (!Tag.WALL_SIGNS.isTagged(clicked.getType())) {
            return;
        }
        final Sign sign = (Sign) clicked.getState();
        if (!sign.getLine(0).equals(HEADER))
            return;
        final WallSign data = (WallSign) sign.getBlockData();
        final Vector direction = data.getFacing().getOppositeFace().getDirection().clone();
        direction.multiply(2);
        final World world = sign.getWorld();
        final Queue<MovecraftLocation> queue = new LinkedList<>();
        final MovecraftLocation signLoc = MathUtils.bukkit2MovecraftLoc(sign.getLocation());
        final MovecraftLocation testLoc = signLoc.translate(direction.getBlockX(), direction.getBlockY(), direction.getBlockZ());
        final Material testType = testLoc.toBukkit(world).getBlock().getType();
        if (testType.isAir() || Manager.allowedBlockOnElevatorCrafts(testType)) {
            queue.add(testLoc);
        } else {
            final Vector left = direction.clone();
            final Vector right = direction.clone();
            switch (data.getFacing()) {
                case NORTH -> {
                    left.add(BlockFace.EAST.getDirection());
                    right.add(BlockFace.WEST.getDirection());
                }
                case SOUTH -> {
                    right.add(BlockFace.EAST.getDirection());
                    left.add(BlockFace.WEST.getDirection());
                }
                case WEST -> {
                    left.add(BlockFace.NORTH.getDirection());
                    right.add(BlockFace.SOUTH.getDirection());
                }
                case EAST -> {
                    left.add(BlockFace.SOUTH.getDirection());
                    right.add(BlockFace.NORTH.getDirection());
                }
            }
            queue.add(signLoc.translate(left.getBlockX(), left.getBlockY(), left.getBlockZ()));
            queue.add(signLoc.translate(right.getBlockX(), right.getBlockY(), right.getBlockZ()));
        }

        final Queue<MovecraftLocation> startLocs = new LinkedList<>();
        while (!queue.isEmpty()) {
            final MovecraftLocation poll = queue.poll();
            var y = poll.getY();
            var increaseY = false;
            while (true) {
                final Block test = world.getBlockAt(poll.getX(), y, poll.getZ());
                if (
                        !test.getType().isAir() &&
                        !Manager.allowedBlockOnElevatorCrafts(test.getType()) &&
                        !Manager.elevatorCraftsCanPassThrough(test.getType())
                ) {
                    if (!increaseY) {
                        increaseY = true;
                        y = poll.getY() + 1;
                        continue;
                    }
                    break;
                }
                if (Manager.allowedBlockOnElevatorCrafts(test.getType())) {
                    startLocs.add(new MovecraftLocation(poll.getX(), y, poll.getZ()));
                }
                if (increaseY) {
                    y++;
                } else {
                    y--;
                }
            }

        }
        Set<MovecraftLocation> visited = new HashSet<>();
        Queue<MovecraftLocation> testLocs = new LinkedList<>();
        Set<HitBox> platforms = new HashSet<>();
        while (!startLocs.isEmpty()) {
            final MovecraftLocation poll = startLocs.poll();
            testLocs.add(poll);
            final MutableHitBox hitBox = new BitmapHitBox();
            hitBox.add(poll);
            while (!testLocs.isEmpty()) {
                final MovecraftLocation testPoll = testLocs.poll();
                for (var shift : SHIFTS) {
                    final MovecraftLocation test = testPoll.add(shift);
                    if (visited.contains(test) || !Manager.allowedBlockOnElevatorCrafts(testPoll.toBukkit(world).getBlock().getType())) {
                        continue;
                    }
                    testLocs.add(test);
                    hitBox.add(test);
                }
            }
            platforms.add(hitBox);
        }
    }

    private final static MovecraftLocation[] SHIFTS = {
            new MovecraftLocation(0, 1, 1),
            new MovecraftLocation(0, 0, 1),
            new MovecraftLocation(0, -1, 1),
            new MovecraftLocation(0, 1, 0),
            new MovecraftLocation(1, 1 ,0),
            new MovecraftLocation(1, 0 ,0),
            new MovecraftLocation(1, -1 ,0),
            new MovecraftLocation(0, 1, -1),
            new MovecraftLocation(0, 0, -1),
            new MovecraftLocation(0, -1, -1),
            new MovecraftLocation(0, -1, 0),
            new MovecraftLocation(-1, 1, 0),
            new MovecraftLocation(-1, 0, 0),
            new MovecraftLocation(-1, -1, 0)
    };
}
