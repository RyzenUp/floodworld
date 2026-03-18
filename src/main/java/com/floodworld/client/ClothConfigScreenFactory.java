package com.floodworld.client;

import com.floodworld.config.FloodWorldConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfigScreenFactory {

    public static Screen build(Screen parent) {
        FloodWorldConfig config = FloodWorldConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("FloodWorld Settings"));

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Replace Air"), config.replaceAir)
                .setTooltip(Text.literal("When enabled, regular air blocks will be replaced with water during chunk generation."))
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.replaceAir = val)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Replace Cave Air"), config.replaceCaveAir)
                .setTooltip(Text.literal("When enabled, cave air blocks will also be filled with water, flooding all underground caves."))
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.replaceCaveAir = val)
                .build());

        general.addEntry(entryBuilder
                .startIntSlider(Text.literal("Max Water Height"), config.maxWaterHeight, -64, 320)
                .setTooltip(Text.literal("The maximum Y level that water will be placed up to. Blocks above this height will remain as air. Default: 250."))
                .setDefaultValue(250)
                .setSaveConsumer(val -> config.maxWaterHeight = val)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Native Flooding"), config.nativeFlooding)
                .setTooltip(Text.literal("ON: Water is placed directly during chunk generation (faster). OFF: Water is placed after generation via a tick queue (slower but more compatible). Changes apply to newly generated chunks only."))
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.nativeFlooding = val)
                .build());

        builder.setSavingRunnable(() -> config.save());

        return builder.build();
    }
}
