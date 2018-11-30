package io.github.cnamira.SkyOres.Listeners;

import io.github.cnamira.SkyOres.SkyOres;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import java.security.SecureRandom;
import java.util.*;

public class GenerateOres implements Listener {
    private SkyOres addon;
    private SecureRandom random = new SecureRandom();
    private List<Ore> ores = new ArrayList<>();
    private List<String> worlds = new ArrayList<>();
    private int sum = 0;

    public GenerateOres(SkyOres addon) {
        this.addon = addon;

        ConfigurationSection section = addon.getConfig().getConfigurationSection("ores");
        Set<String> keys = section.getKeys(false);
        for (String key : keys) {
            Material material = Material.matchMaterial(key);
            int weight = section.getInt(key);
            Ore ore = new Ore(material, weight);
            sum += weight;
            ores.add(ore);
        }

        List<String> worlds = addon.getConfig().getStringList("worlds");
        for (String world : worlds) {
            if (addon.getServer().getWorld(world) != null) {
                this.worlds.add(world);
            } else {
                this.addon.getLogger().warning("World name " + world + " does NOT exist, ignoring.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGenerateCobblestone(BlockFormEvent event) {
        if (this.worlds.contains(event.getNewState().getWorld().getName())) {
            if (event.getNewState().getType().equals(Material.COBBLESTONE) || event.getNewState().getType().equals(Material.STONE)) {
                event.getNewState().setType(getRandomMaterial());
                return;
            }
        }
    }

    private Material getRandomMaterial() {
        int rand = random.nextInt(sum + 1);
        for (Ore ore : ores) {
            rand -= ore.getWeight();
            if (rand <= 0) {
                return ore.getType();
            }
        }
        return Material.COBBLESTONE;
    }

    private class Ore {
        private Material type;
        private int weight;

        Ore(Material type, int weight) {
            this.type = type;
            this.weight = weight;
        }

        int getWeight() {
            return weight;
        }

        Material getType() {
            return type;
        }
    }
}
