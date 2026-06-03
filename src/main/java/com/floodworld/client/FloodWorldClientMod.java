package com.floodworld.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "floodworld", dist = Dist.CLIENT)
public class FloodWorldClientMod {

    public FloodWorldClientMod(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLLoader.getDist().isClient()) {
            try {
                Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
                modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                        (mc, parent) -> ClothConfigScreenFactory.build(parent));
            } catch (ClassNotFoundException ignored) {
            }
        }
    }
}
