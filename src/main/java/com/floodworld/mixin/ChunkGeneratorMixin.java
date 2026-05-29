package com.floodworld.mixin;

import com.floodworld.config.FloodWorldConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void floodworld(WorldGenLevel world, ChunkAccess chunk,
                                              StructureManager structureManager, CallbackInfo ci) {
        FloodWorldConfig config = FloodWorldConfig.getInstance();
        if (!config.replaceAir && !config.replaceCaveAir) return;

        int minY = chunk.getMinY();
        int maxY = config.maxWaterHeight;
        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();

        var waterState = Blocks.WATER.defaultBlockState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    mutablePos.set(x, y, z);
                    var state = world.getBlockState(mutablePos);
                    if ((config.replaceAir && state.is(Blocks.AIR)) ||
                        (config.replaceCaveAir && state.is(Blocks.CAVE_AIR))) {
                        world.setBlock(mutablePos, waterState, 2);
                    }
                }
            }
        }
    }
}

