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
import java.util.function.BiConsumer;

public class GenerateOres implements Listener {
    private SkyOres addon;
    private SecureRandom random = new SecureRandom();
    private List<Ore> ores = new ArrayList<>();
    private List<String> worlds = new ArrayList<>();
    private int sum = 0;

    public GenerateOres(SkyOres addon) {
        this.addon = addon;

        addon.getConfig().getConfigurationSection("ores").getValues(false).forEach((key, _weight) -> {
            Material material = Material.matchMaterial(key);
            int weight = (Integer) _weight;
            Ore ore = new Ore(material, weight);
            ores.add(ore);
            sum += weight;
        });

        addon.getConfig().getStringList("worlds").forEach(world -> {
            if (addon.getServer().getWorld(world) != null) {
                worlds.add(world);
            } else {
                this.addon.getLogger().warning("World name " + world + " does NOT exist, ignoring.");
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGenerateCobblestone(BlockFormEvent event) {
        if (this.worlds.contains(event.getNewState().getWorld().getName())) {
            if (event.getNewState().getType().equals(Material.COBBLESTONE) || event.getNewState().getType().equals(Material.STONE)) {
                event.getNewState().setType(getRandomMaterial());
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
