package com.floodworld.client;

import com.floodworld.config.FloodWorldConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreenFactory {

    public static Screen build(Screen parent) {
        FloodWorldConfig config = FloodWorldConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("FloodWorld Settings"));

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startBooleanToggle(Component.literal("Replace Air"), config.replaceAir)
                .setTooltip(Component.literal("When enabled, regular air blocks will be replaced with water during chunk generation."))
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.replaceAir = val)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Component.literal("Replace Cave Air"), config.replaceCaveAir)
                .setTooltip(Component.literal("When enabled, cave air blocks will also be filled with water, flooding all underground caves."))
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.replaceCaveAir = val)
                .build());

        general.addEntry(entryBuilder
                .startIntSlider(Component.literal("Max Water Height"), config.maxWaterHeight, -64, 320)
                .setTooltip(Component.literal("The maximum Y level that water will be placed up to. Blocks above this height will remain as air. Default: 250."))
                .setDefaultValue(250)
                .setSaveConsumer(val -> config.maxWaterHeight = val)
                .build());

        builder.setSavingRunnable(() -> config.save());

        return builder.build();
    }
}

