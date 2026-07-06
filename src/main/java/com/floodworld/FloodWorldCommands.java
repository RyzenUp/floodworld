package com.floodworld;

import com.floodworld.config.FloodWorldConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FloodWorldCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("floodworld")
                .requires(source -> source.hasPermission(2))

                .then(Commands.literal("get")
                        .then(Commands.literal("replaceAir").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replaceAir = " + FloodWorldConfig.getInstance().replaceAir), false); return 1; }))
                        .then(Commands.literal("replaceCaveAir").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replaceCaveAir = " + FloodWorldConfig.getInstance().replaceCaveAir), false); return 1; }))
                        .then(Commands.literal("replaceWaterBreakable").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replaceWaterBreakable = " + FloodWorldConfig.getInstance().replaceWaterBreakable), false); return 1; }))
                        .then(Commands.literal("caveDetectionFloodFill").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] caveDetectionFloodFill = " + FloodWorldConfig.getInstance().caveDetectionFloodFill), false); return 1; }))
                        .then(Commands.literal("replacementBlock").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replacementBlock = " + FloodWorldConfig.getInstance().replacementBlock), false); return 1; }))
                        .then(Commands.literal("enableOverworld").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] enableOverworld = " + FloodWorldConfig.getInstance().enableOverworld), false); return 1; }))
                        .then(Commands.literal("overworldWaterHeight").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] overworldWaterHeight = " + FloodWorldConfig.getInstance().overworldWaterHeight), false); return 1; }))
                        .then(Commands.literal("overworldWaterlog").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] overworldWaterlog = " + FloodWorldConfig.getInstance().overworldWaterlog), false); return 1; }))
                        .then(Commands.literal("enableNether").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] enableNether = " + FloodWorldConfig.getInstance().enableNether), false); return 1; }))
                        .then(Commands.literal("netherWaterHeight").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] netherWaterHeight = " + FloodWorldConfig.getInstance().netherWaterHeight), false); return 1; }))
                        .then(Commands.literal("netherWaterlog").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] netherWaterlog = " + FloodWorldConfig.getInstance().netherWaterlog), false); return 1; }))
                        .then(Commands.literal("enableEnd").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] enableEnd = " + FloodWorldConfig.getInstance().enableEnd), false); return 1; }))
                        .then(Commands.literal("endWaterHeight").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] endWaterHeight = " + FloodWorldConfig.getInstance().endWaterHeight), false); return 1; }))
                        .then(Commands.literal("endWaterlog").executes(ctx -> { ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] endWaterlog = " + FloodWorldConfig.getInstance().endWaterlog), false); return 1; }))
                )

                .then(Commands.literal("set")
                        .then(Commands.literal("replaceAir").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().replaceAir = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replaceAir set to " + val), true); return 1; })))
                        .then(Commands.literal("replaceCaveAir").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().replaceCaveAir = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replaceCaveAir set to " + val), true); return 1; })))
                        .then(Commands.literal("replaceWaterBreakable").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().replaceWaterBreakable = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replaceWaterBreakable set to " + val), true); return 1; })))
                        .then(Commands.literal("caveDetectionFloodFill").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().caveDetectionFloodFill = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] caveDetectionFloodFill set to " + val), true); return 1; })))
                        .then(Commands.literal("replacementBlock")
                                .then(Commands.argument("value", ResourceLocationArgument.id())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.BLOCK.keySet(), builder))
                                        .executes(ctx -> {
                                            ResourceLocation id = ResourceLocationArgument.getId(ctx, "value");
                                            if (!BuiltInRegistries.BLOCK.containsKey(id)) {
                                                ctx.getSource().sendFailure(Component.literal("[FloodWorld] No such block: " + id));
                                                return 0;
                                            }
                                            FloodWorldConfig.getInstance().replacementBlock = id.toString();
                                            FloodWorldConfig.getInstance().save();
                                            ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] replacementBlock set to " + id
                                                    + (id.toString().equals("minecraft:water") ? "" : " (waterlog options are ignored unless replacementBlock is minecraft:water)")), true);
                                            return 1;
                                        })))
                        .then(Commands.literal("enableOverworld").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().enableOverworld = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] enableOverworld set to " + val), true); return 1; })))
                        .then(Commands.literal("overworldWaterHeight").then(Commands.argument("value", IntegerArgumentType.integer(-64, 320)).executes(ctx -> { int val = IntegerArgumentType.getInteger(ctx, "value"); FloodWorldConfig.getInstance().overworldWaterHeight = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] overworldWaterHeight set to " + val), true); return 1; })))
                        .then(Commands.literal("overworldWaterlog").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().overworldWaterlog = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] overworldWaterlog set to " + val), true); return 1; })))
                        .then(Commands.literal("enableNether").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().enableNether = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] enableNether set to " + val), true); return 1; })))
                        .then(Commands.literal("netherWaterHeight").then(Commands.argument("value", IntegerArgumentType.integer(0, 256)).executes(ctx -> { int val = IntegerArgumentType.getInteger(ctx, "value"); FloodWorldConfig.getInstance().netherWaterHeight = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] netherWaterHeight set to " + val), true); return 1; })))
                        .then(Commands.literal("netherWaterlog").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().netherWaterlog = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] netherWaterlog set to " + val), true); return 1; })))
                        .then(Commands.literal("enableEnd").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().enableEnd = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] enableEnd set to " + val), true); return 1; })))
                        .then(Commands.literal("endWaterHeight").then(Commands.argument("value", IntegerArgumentType.integer(0, 256)).executes(ctx -> { int val = IntegerArgumentType.getInteger(ctx, "value"); FloodWorldConfig.getInstance().endWaterHeight = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] endWaterHeight set to " + val), true); return 1; })))
                        .then(Commands.literal("endWaterlog").then(Commands.argument("value", BoolArgumentType.bool()).executes(ctx -> { boolean val = BoolArgumentType.getBool(ctx, "value"); FloodWorldConfig.getInstance().endWaterlog = val; FloodWorldConfig.getInstance().save(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] endWaterlog set to " + val), true); return 1; })))
                )

                .then(Commands.literal("reload").executes(ctx -> { FloodWorldConfig.load(); ctx.getSource().sendSuccess(() -> Component.literal("[FloodWorld] Config reloaded from disk."), true); return 1; }));
    }
}
