package io.github.cnamira.SkyOres;

import io.github.cnamira.SkyOres.Listeners.GenerateOres;
import world.bentobox.bentobox.api.addons.Addon;

public class SkyOres extends Addon {

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new GenerateOres(this), this.getPlugin());
    }

    @Override
    public void onDisable() {
    }
}
