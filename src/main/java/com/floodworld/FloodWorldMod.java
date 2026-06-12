package com.floodworld;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("floodworld")
public class FloodWorldMod {

    public static final String MOD_ID = "floodworld";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public FloodWorldMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("FloodWorld initialized.");
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(FloodWorldCommands.register());
    }
}
