package io.github.eirikh1996.movecraftelevators.sign;

import io.github.eirikh1996.movecraftelevators.config.Settings;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
        if (!Settings.ElevatorCrafts.contains(foundType)) {
            event.getPlayer().sendMessage(foundType.getStringProperty(CraftType.NAME) + " is not a permitted elevator craft");
            return;
        }
        event.setLine(0, HEADER);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {

    }
}
