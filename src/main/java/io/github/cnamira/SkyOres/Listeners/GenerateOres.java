package io.github.cnamira.SkyOres.Listeners;

import io.github.cnamira.SkyOres.SkyOres;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.*;

public class GenerateOres implements Listener {
    private SkyOres addon;
    private List<Integer> weights;
    private List<Material> ores;

    public GenerateOres(SkyOres addon) {
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGenerateCobblestone(BlockFromToEvent event) {
        if (event.getToBlock().getType() == Material.WATER || event.getToBlock().getType() == Material.LAVA || event.getBlock().getType() == Material.DRAGON_EGG) {
            return;
        }
        for (String world : addon.getConfig().getStringList("worlds")) {
            if (event.getBlock().getWorld().getName().equals(world)) {
                event.getToBlock().setType(getRandomMaterial());
                event.setCancelled(true);
                return;
            }
        }
    }

    private Material getRandomMaterial() {
        if (weights == null || ores == null) {
            weights = new ArrayList<>();
            ores = new ArrayList<>();
            int i = 0;
            ConfigurationSection section = addon.getConfig().getConfigurationSection("ores");
            for (String key : section.getKeys(false)) {
                i += section.getInt(key);
                weights.add(i);
                ores.add(Material.matchMaterial(key));
            }
        }
        Random random = new Random();
        int rand = random.nextInt(weights.get(weights.size() - 1) + 1);
        for (int i = weights.size() - 1; i >= 0; i--) {
            if (weights.get(i) <= rand) {
                return ores.get(i);
            }
        }
        return Material.COBBLESTONE;
    }

}
