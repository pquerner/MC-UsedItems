package de.paskl.useditems.listeners;

import de.paskl.useditems.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;


public class BlockPlaceEventListener implements Listener {
    private Main plugin;

    public BlockPlaceEventListener(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (this.plugin.commandEnabledForPlayers.contains(p.getName())) {
            Block b = event.getBlockPlaced();
            Material bt = b.getType();
            int typeId = b.getData();
            int itemId = bt.getId();

            if (this.plugin.personsBlockCount.containsKey(p.getName())
                    && this.plugin.personsBlockCount.get(p.getName()).containsKey(itemId)) {
                //Item exists already
                //Get item count
                String val = this.plugin.personsBlockCount.get(p.getName()).get(itemId);
                //val is now <count>_<type_id> (str)
                //explode this string, add +1 to count
                String[] parts = val.split("_");
                int count = Integer.parseInt(parts[0]);
                //Add +=1
                count += 1;
                //Save again
                String saveStr = String.format("%d_%d", count, typeId);
                this.plugin.personsBlockCount.get(p.getName()).put(itemId, saveStr);
            } else if (this.plugin.personsBlockCount.containsKey(p.getName())
                    && !this.plugin.personsBlockCount.get(p.getName()).containsKey(itemId)) {
                //Item does not exist yet
                String saveStr = String.format("%d_%d", 1, typeId);
                this.plugin.personsBlockCount.get(p.getName()).put(itemId, saveStr);
            } else if (!this.plugin.personsBlockCount.containsKey(p.getName())) {
                //very first item placed
                Map<Integer, String> itemsCount = new HashMap<>();
                String saveStr = String.format("%d_%d", 1, typeId);
                itemsCount.put(itemId, saveStr); //Add first placed item
                this.plugin.personsBlockCount.put(p.getName(), itemsCount);
            } else {
                //Something fucked up
                this.plugin.getLogger().warning("Something fucked up here" + this);
            }
        }
    }
}
