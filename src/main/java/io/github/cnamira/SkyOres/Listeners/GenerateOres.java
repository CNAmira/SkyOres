package io.github.cnamira.SkyOres.Listeners;

import io.github.cnamira.SkyOres.SkyOres;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.*;

public class GenerateOres implements Listener {
    private SkyOres addon;
    private List<Ore> ores;
    private int sum = 0;

    public GenerateOres(SkyOres addon) {
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGenerateCobblestone(BlockFromToEvent event) {
        if ((event.getBlock().getType().equals(Material.WATER) || event.getBlock().getType().equals(Material.LAVA)) && isGeneratingCobblestone(event.getBlock(), event.getToBlock())) {
            for (String world : addon.getConfig().getStringList("worlds")) {
                if (event.getBlock().getWorld().getName().equals(world)) {
                    event.getToBlock().setType(getRandomMaterial());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private final BlockFace[] faces = new BlockFace[]{
            BlockFace.SELF,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    private boolean isGeneratingCobblestone(Block block, Block to) {
        Material m2 = block.getType().equals(Material.WATER) ? Material.LAVA : Material.WATER;
        for (BlockFace face : faces) {
            Block r = to.getRelative(face, 1);
            if (r.getType().equals(m2)) {
                return true;
            }
        }
        return false;
    }

    private Material getRandomMaterial() {
        if (ores == null) {
            ores = new ArrayList<>();
            ConfigurationSection section = addon.getConfig().getConfigurationSection("ores");
            Set<String> keys = section.getKeys(false);
            for (String key : keys) {
                Material material = Material.matchMaterial(key);
                int weight = section.getInt(key);
                Ore ore = new Ore(material, weight);
                sum += weight;
                ores.add(ore);
            }
        }

        Random random = new Random();
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
