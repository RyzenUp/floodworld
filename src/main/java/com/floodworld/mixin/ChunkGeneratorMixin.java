package com.floodworld.mixin;

import com.floodworld.config.FloodWorldConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

    @Inject(method = "generateFeatures", at = @At("TAIL"))
    private void floodworld(StructureWorldAccess world, Chunk chunk,
                                              StructureAccessor structureAccessor, CallbackInfo ci) {
        FloodWorldConfig config = FloodWorldConfig.getInstance();

        // Skip if native flooding is disabled (post-processing mode handles it instead)
        if (!config.nativeFlooding) return;
        if (!config.replaceAir && !config.replaceCaveAir) return;

        int minY = chunk.getBottomY();
        int maxY = config.maxWaterHeight;
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    mutablePos.set(x, y, z);
                    var state = world.getBlockState(mutablePos);
                    if ((config.replaceAir && state.isOf(Blocks.AIR)) ||
                        (config.replaceCaveAir && state.isOf(Blocks.CAVE_AIR))) {
                        world.setBlockState(mutablePos, Blocks.WATER.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}
