package com.floodworld.mixin;

import com.floodworld.config.FloodWorldConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.tags.BlockTags;
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
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();

        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                int surfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                for (int y = minY; y < maxY; y++) {
                    mutablePos.set(x, y, z);
                    var state = world.getBlockState(mutablePos);
                    if (!state.isAir()) continue;

                    boolean isCave = y < surfaceY && !isUnderTree(world, scanPos, x, y, z, surfaceY);

                    if (isCave && config.replaceCaveAir) {
                        world.setBlock(mutablePos, waterState, 2);
                    } else if (!isCave && config.replaceAir) {
                        world.setBlock(mutablePos, waterState, 2);
                    }
                }
            }
        }
    }

    private static boolean isUnderTree(WorldGenLevel world, BlockPos.MutableBlockPos scanPos,
                                        int x, int y, int z, int surfaceY) {
        for (int scanY = y + 1; scanY <= surfaceY + 20; scanY++) {
            scanPos.set(x, scanY, z);
            var above = world.getBlockState(scanPos);
            if (above.is(BlockTags.LOGS) || above.is(BlockTags.LEAVES)) {
                return true;
            }
            if (above.isSolidRender()) {
                return false;
            }
        }
        return false;
    }
}
