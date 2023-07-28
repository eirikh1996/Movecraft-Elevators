package io.github.eirikh1996.movecraftelevators.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class GUI implements Listener {
    private final Inventory inventory;

    public GUI(final String title, final int pages) {
        this.inventory = Bukkit.createInventory(null, 54, title);
    }
}
