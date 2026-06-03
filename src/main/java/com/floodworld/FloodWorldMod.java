package com.floodworld;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("floodworld")
public class FloodWorldMod {

    public static final String MOD_ID = "floodworld";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public FloodWorldMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("FloodWorld initialized.");
    }
}
