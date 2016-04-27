package de.paskl.useditems;

import de.paskl.useditems.listeners.BlockPlaceEventListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin {

    private PluginManager manager;
    public ArrayList<String> commandDisabledForPlayers = new ArrayList<>();
    public ArrayList<String> commandEnabledForPlayers = new ArrayList<>();
    final public Map<String, Map<Integer, String>> personsBlockCount = new HashMap<>();

    @Override
    public void onEnable() {
        this.manager = this.getServer().getPluginManager();
        this.registerListener();
        getLogger().info(String.format("[%s] v%s loaded.", getDescription().getName(), getDescription().getVersion()));
    }

    private void registerListener() {
        new BlockPlaceEventListener(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            //Cast to Player object
            Player player = (Player) sender;
            String playerName = player.getName();

            //Generate TreeSet of valid commands for this plugin
            TreeSet<String> cui = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            cui.add("cui");
            cui.add("count-used-items");

            if (cui.contains(cmd.getName())) {
                if (args.length == 0 || args.length >= 2) {
                    return false;
                }
                if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("off")) {
                    player.sendMessage(ChatColor.RED + String.format("[%s] is now disabled for you.", getDescription().getName()));
                    if (!this.commandDisabledForPlayers.contains(playerName)) {
                        this.commandDisabledForPlayers.add(playerName);
                        this.commandEnabledForPlayers.remove(playerName);
                    }
                } else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("on")) {
                    player.sendMessage(ChatColor.GREEN + String.format("[%s] is now enabled for you.", getDescription().getName()));
                    if (!this.commandEnabledForPlayers.contains(playerName)) {
                        this.commandEnabledForPlayers.add(playerName);
                        this.commandDisabledForPlayers.remove(playerName);
                    }
                } else if (args[0].equalsIgnoreCase("reset")) {
                    player.sendMessage("Reset items!");
                    this.personsBlockCount.remove(player.getName());
                } else if (args[0].equalsIgnoreCase("show")) {
                    if (this.personsBlockCount.containsKey(player.getName()) && !this.personsBlockCount.get(player.getName()).isEmpty()) {
                        Iterator<Map.Entry<Integer, String>> entries = this.personsBlockCount.get(playerName).entrySet().iterator();
                        player.sendMessage("\n================\nItems:\n");
                        while (entries.hasNext()) {
                            Map.Entry<Integer, String> entry = entries.next();

                            //getValue is <count>_<type_id>
                            String[] parts = entry.getValue().split("_");
                            int count = Integer.parseInt(parts[0]);

                            ItemStack is = new ItemStack(Material.getMaterial(entry.getKey()), 1, (short) 0, (byte) Integer.parseInt(parts[1]));

                            String times = count == 1 ? "time" : "times";

                            String str = String.format("Item %s used %d %s", is.getData().toString(), count, times);
                            player.sendMessage(str);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + String.format("You did not build anything yet."));
                    }
                }

                return true;

            }

        } else {
            sender.sendMessage("Only players can issue the plugin commands.");
        }

        return false;
    }
}
