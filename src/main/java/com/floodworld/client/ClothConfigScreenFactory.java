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

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Replace Air"), config.replaceAir)
                .setTooltip(Component.literal("When enabled, regular air blocks will be replaced with water during chunk generation."))
                .setDefaultValue(true).setSaveConsumer(val -> config.replaceAir = val).build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Replace Cave Air"), config.replaceCaveAir)
                .setTooltip(Component.literal("When enabled, underground cave air will also be filled with water."))
                .setDefaultValue(true).setSaveConsumer(val -> config.replaceCaveAir = val).build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Replace Water-Breakable Blocks"), config.replaceWaterBreakable)
                .setTooltip(Component.literal("When enabled, blocks that would be destroyed by water (grass, flowers, torches, rails, redstone, carpets, snow layers, vines, etc.) are replaced with water during chunk generation."))
                .setDefaultValue(true).setSaveConsumer(val -> config.replaceWaterBreakable = val).build());

        ConfigCategory overworld = builder.getOrCreateCategory(Component.literal("Overworld"));
        overworld.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Flooding"), config.enableOverworld)
                .setTooltip(Component.literal("Enable flooding in the Overworld."))
                .setDefaultValue(true).setSaveConsumer(val -> config.enableOverworld = val).build());
        overworld.addEntry(entryBuilder.startIntSlider(Component.literal("Water Height"), config.overworldWaterHeight, -64, 320)
                .setTooltip(Component.literal("The maximum Y level that water will be placed up to in the Overworld. Default: 250."))
                .setDefaultValue(250).setSaveConsumer(val -> config.overworldWaterHeight = val).build());
        overworld.addEntry(entryBuilder.startBooleanToggle(Component.literal("Waterlog Blocks"), config.overworldWaterlog)
                .setTooltip(Component.literal("When enabled, waterloggable blocks (fences, slabs, stairs, trapdoors, etc.) will be waterlogged during generation in the Overworld."))
                .setDefaultValue(true).setSaveConsumer(val -> config.overworldWaterlog = val).build());

        ConfigCategory nether = builder.getOrCreateCategory(Component.literal("Nether"));
        nether.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Flooding"), config.enableNether)
                .setTooltip(Component.literal("Enable flooding in the Nether."))
                .setDefaultValue(false).setSaveConsumer(val -> config.enableNether = val).build());
        nether.addEntry(entryBuilder.startIntSlider(Component.literal("Water Height"), config.netherWaterHeight, 0, 256)
                .setTooltip(Component.literal("The maximum Y level that water will be placed up to in the Nether. Default: 100."))
                .setDefaultValue(100).setSaveConsumer(val -> config.netherWaterHeight = val).build());
        nether.addEntry(entryBuilder.startBooleanToggle(Component.literal("Waterlog Blocks"), config.netherWaterlog)
                .setTooltip(Component.literal("When enabled, waterloggable blocks will be waterlogged during generation in the Nether."))
                .setDefaultValue(true).setSaveConsumer(val -> config.netherWaterlog = val).build());

        ConfigCategory end = builder.getOrCreateCategory(Component.literal("End"));
        end.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Flooding"), config.enableEnd)
                .setTooltip(Component.literal("Enable flooding in the End."))
                .setDefaultValue(false).setSaveConsumer(val -> config.enableEnd = val).build());
        end.addEntry(entryBuilder.startIntSlider(Component.literal("Water Height"), config.endWaterHeight, 0, 256)
                .setTooltip(Component.literal("The maximum Y level that water will be placed up to in the End. Default: 64."))
                .setDefaultValue(64).setSaveConsumer(val -> config.endWaterHeight = val).build());
        end.addEntry(entryBuilder.startBooleanToggle(Component.literal("Waterlog Blocks"), config.endWaterlog)
                .setTooltip(Component.literal("When enabled, waterloggable blocks will be waterlogged during generation in the End."))
                .setDefaultValue(true).setSaveConsumer(val -> config.endWaterlog = val).build());

        builder.setSavingRunnable(() -> config.save());
        return builder.build();
    }
}
