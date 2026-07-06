package com.floodworld.client;

import com.floodworld.config.FloodWorldConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClothConfigScreenFactory {

    private static final String WATERLOG_TOOLTIP_SUFFIX =
            " Ignored unless Replacement Block is set to minecraft:water.";

    public static Screen build(Screen parent) {
        FloodWorldConfig config = FloodWorldConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("FloodWorld Settings"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        List<String> blockIds = BuiltInRegistries.BLOCK.keySet().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .collect(Collectors.toList());

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
        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Advanced Cave Detection (Flood Fill)"), config.caveDetectionFloodFill)
                .setTooltip(Component.literal("When enabled, cave air is detected with a per-chunk flood fill instead of a per-column heightmap compare. More accurate under overhangs and floating terrain (mushroom caps, wide canopies), at the cost of more generation-time CPU/memory per chunk."))
                .setDefaultValue(false).setSaveConsumer(val -> config.caveDetectionFloodFill = val).build());
        general.addEntry(entryBuilder.startStringDropdownMenu(Component.literal("Replacement Block"), config.replacementBlock)
                .setSelections(blockIds)
                .setSuggestionMode(true)
                .setErrorSupplier(val -> {
                    ResourceLocation loc = ResourceLocation.tryParse(val);
                    if (loc == null || !BuiltInRegistries.BLOCK.containsKey(loc)) {
                        return Optional.of(Component.literal("Not a valid block ID"));
                    }
                    return Optional.empty();
                })
                .setTooltip(Component.literal("The block placed instead of air/cave air/water-breakable blocks during generation. Defaults to minecraft:water. Waterlog options only take effect when this is minecraft:water."))
                .setDefaultValue("minecraft:water")
                .setSaveConsumer(val -> config.replacementBlock = val)
                .build());

        ConfigCategory overworld = builder.getOrCreateCategory(Component.literal("Overworld"));
        overworld.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Flooding"), config.enableOverworld)
                .setTooltip(Component.literal("Enable flooding in the Overworld."))
                .setDefaultValue(true).setSaveConsumer(val -> config.enableOverworld = val).build());
        overworld.addEntry(entryBuilder.startIntField(Component.literal("Water Height"), config.overworldWaterHeight)
                .setMin(-64).setMax(320)
                .setTooltip(Component.literal("The maximum Y level that water will be placed up to in the Overworld. Default: 250."))
                .setDefaultValue(250).setSaveConsumer(val -> config.overworldWaterHeight = val).build());
        overworld.addEntry(entryBuilder.startBooleanToggle(Component.literal("Waterlog Blocks"), config.overworldWaterlog)
                .setTooltip(Component.literal("When enabled, waterloggable blocks (fences, slabs, stairs, trapdoors, etc.) will be waterlogged during generation in the Overworld." + WATERLOG_TOOLTIP_SUFFIX))
                .setDefaultValue(true).setSaveConsumer(val -> config.overworldWaterlog = val).build());

        ConfigCategory nether = builder.getOrCreateCategory(Component.literal("Nether"));
        nether.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Flooding"), config.enableNether)
                .setTooltip(Component.literal("Enable flooding in the Nether."))
                .setDefaultValue(false).setSaveConsumer(val -> config.enableNether = val).build());
        nether.addEntry(entryBuilder.startIntField(Component.literal("Water Height"), config.netherWaterHeight)
                .setMin(0).setMax(256)
                .setTooltip(Component.literal("The maximum Y level that water will be placed up to in the Nether. Default: 100."))
                .setDefaultValue(100).setSaveConsumer(val -> config.netherWaterHeight = val).build());
        nether.addEntry(entryBuilder.startBooleanToggle(Component.literal("Waterlog Blocks"), config.netherWaterlog)
                .setTooltip(Component.literal("When enabled, waterloggable blocks will be waterlogged during generation in the Nether." + WATERLOG_TOOLTIP_SUFFIX))
                .setDefaultValue(true).setSaveConsumer(val -> config.netherWaterlog = val).build());

        ConfigCategory end = builder.getOrCreateCategory(Component.literal("End"));
        end.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Flooding"), config.enableEnd)
                .setTooltip(Component.literal("Enable flooding in the End."))
                .setDefaultValue(false).setSaveConsumer(val -> config.enableEnd = val).build());
        end.addEntry(entryBuilder.startIntField(Component.literal("Water Height"), config.endWaterHeight)
                .setMin(0).setMax(256)
                .setTooltip(Component.literal("The maximum Y level that water will be placed up to in the End. Default: 64."))
                .setDefaultValue(64).setSaveConsumer(val -> config.endWaterHeight = val).build());
        end.addEntry(entryBuilder.startBooleanToggle(Component.literal("Waterlog Blocks"), config.endWaterlog)
                .setTooltip(Component.literal("When enabled, waterloggable blocks will be waterlogged during generation in the End." + WATERLOG_TOOLTIP_SUFFIX))
                .setDefaultValue(true).setSaveConsumer(val -> config.endWaterlog = val).build());

        builder.setSavingRunnable(() -> config.save());
        return builder.build();
    }
}
