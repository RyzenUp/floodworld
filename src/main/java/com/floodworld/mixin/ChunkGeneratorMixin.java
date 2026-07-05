package com.floodworld.mixin;

import com.floodworld.config.FloodWorldConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.tags.BlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void floodworld(WorldGenLevel world, ChunkAccess chunk,
                                              StructureManager structureManager, CallbackInfo ci) {
        FloodWorldConfig config = FloodWorldConfig.getInstance();
        if (!config.replaceAir && !config.replaceCaveAir && !config.replaceWaterBreakable && !config.overworldWaterlog) return;

        var dimension = world.getLevel().dimension();
        int maxY;
        boolean isOverworld;
        boolean waterlog;
        if (dimension.equals(Level.OVERWORLD)) {
            if (!config.enableOverworld) return;
            maxY = config.overworldWaterHeight;
            isOverworld = true;
            waterlog = config.overworldWaterlog;
        } else if (dimension.equals(Level.NETHER)) {
            if (!config.enableNether) return;
            maxY = config.netherWaterHeight;
            isOverworld = false;
            waterlog = config.netherWaterlog;
        } else if (dimension.equals(Level.END)) {
            if (!config.enableEnd) return;
            maxY = config.endWaterHeight;
            isOverworld = false;
            waterlog = config.endWaterlog;
        } else {
            return;
        }

        int minY = chunk.getMinBuildHeight();
        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();

        var waterState = Blocks.WATER.defaultBlockState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();

        boolean[] surfaceConnected = null;
        if (isOverworld && config.caveDetectionFloodFill && maxY > minY) {
            surfaceConnected = computeSurfaceConnected(world, chunk, minY, maxY - minY, startX, startZ);
        }
        boolean[] surfaceConnectedMask = surfaceConnected;

        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                int surfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                for (int y = minY; y < maxY; y++) {
                    mutablePos.set(x, y, z);
                    var state = world.getBlockState(mutablePos);

                    boolean isAirLike = state.isAir() || state.is(Blocks.VOID_AIR);

                    if (isAirLike) {
                        if (!isOverworld) {
                            world.setBlock(mutablePos, waterState, 2);
                            continue;
                        }

                        boolean isCave;
                        if (surfaceConnectedMask != null) {
                            int idx = ((x - startX) * 16 + (z - startZ)) * (maxY - minY) + (y - minY);
                            isCave = !surfaceConnectedMask[idx] && !isUnderVegetation(world, scanPos, x, y, z, surfaceY);
                        } else {
                            isCave = y < surfaceY && !isUnderVegetation(world, scanPos, x, y, z, surfaceY);
                        }

                        if (isCave && config.replaceCaveAir) {
                            world.setBlock(mutablePos, waterState, 2);
                        } else if (!isCave && config.replaceAir) {
                            world.setBlock(mutablePos, waterState, 2);
                        }
                    } else if (waterlog && state.hasProperty(BlockStateProperties.WATERLOGGED)
                            && !state.getValue(BlockStateProperties.WATERLOGGED)) {
                        world.setBlock(mutablePos, state.setValue(BlockStateProperties.WATERLOGGED, true), 2);
                    } else if (config.replaceWaterBreakable && isWaterBreakable(state)) {
                        world.setBlock(mutablePos, waterState, 2);
                    }
                }
            }
        }
    }

    private static boolean isWaterBreakable(BlockState state) {
        if (state.isAir() || state.liquid()) return false;
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) return false;

        return state.is(BlockTags.REPLACEABLE)
                || state.is(BlockTags.RAILS)
                || state.is(BlockTags.BUTTONS)
                || state.is(BlockTags.PRESSURE_PLATES)
                || state.is(BlockTags.WOOL_CARPETS)
                || state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)
                || state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)
                || state.is(Blocks.REDSTONE_TORCH) || state.is(Blocks.REDSTONE_WALL_TORCH)
                || state.is(Blocks.REDSTONE_WIRE)
                || state.is(Blocks.REPEATER)
                || state.is(Blocks.COMPARATOR)
                || state.is(Blocks.LEVER)
                || state.is(Blocks.COBWEB)
                || state.is(Blocks.VINE)
                || state.is(Blocks.HANGING_ROOTS)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.NETHER_SPROUTS)
                || state.is(Blocks.CRIMSON_ROOTS) || state.is(Blocks.WARPED_ROOTS)
                || state.is(Blocks.WEEPING_VINES) || state.is(Blocks.WEEPING_VINES_PLANT)
                || state.is(Blocks.TWISTING_VINES) || state.is(Blocks.TWISTING_VINES_PLANT);
    }

    /**
     * Flood-fills air blocks from every position known to be at or above its column's surface
     * height, through 6-connected air, to find every air block genuinely reachable from open
     * surface air. Anything not reached (a false in the returned array) is enclosed cave air,
     * unlike the plain heightmap compare, which follows the actual connected space instead of
     * trusting one column's height alone.
     * Doesn't by itself account for solid-but-non-terrain overhangs (tree trunks, mushroom stems,
     * etc. inflate a column's heightmap the same way a rock overhang would) -- callers still need
     * the isUnderVegetation exclusion on top of this mask, same as the plain heightmap path.
     * Bounded to the current chunk, so a cave mouth that only opens into a neighboring chunk is
     * still misclassified -- same blind spot the heightmap approach already has.
     */
    private static boolean[] computeSurfaceConnected(WorldGenLevel world, ChunkAccess chunk,
                                                      int minY, int height, int startX, int startZ) {
        int size = 16 * 16 * height;
        boolean[] airLike = new boolean[size];
        boolean[] open = new boolean[size];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = startX + lx;
                int z = startZ + lz;
                int colSurfaceY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                for (int ly = 0; ly < height; ly++) {
                    int y = minY + ly;
                    pos.set(x, y, z);
                    var state = world.getBlockState(pos);
                    boolean isAir = state.isAir() || state.is(Blocks.VOID_AIR);
                    int idx = (lx * 16 + lz) * height + ly;
                    airLike[idx] = isAir;
                    if (isAir && y >= colSurfaceY) {
                        open[idx] = true;
                        queue.add(idx);
                    }
                }
            }
        }

        while (!queue.isEmpty()) {
            int idx = queue.poll();
            int ly = idx % height;
            int col = idx / height;
            int lz = col % 16;
            int lx = col / 16;

            visitNeighbor(lx + 1, lz, ly, height, airLike, open, queue);
            visitNeighbor(lx - 1, lz, ly, height, airLike, open, queue);
            visitNeighbor(lx, lz + 1, ly, height, airLike, open, queue);
            visitNeighbor(lx, lz - 1, ly, height, airLike, open, queue);
            visitNeighbor(lx, lz, ly + 1, height, airLike, open, queue);
            visitNeighbor(lx, lz, ly - 1, height, airLike, open, queue);
        }

        return open;
    }

    private static void visitNeighbor(int lx, int lz, int ly, int height,
                                       boolean[] airLike, boolean[] open, ArrayDeque<Integer> queue) {
        if (lx < 0 || lx >= 16 || lz < 0 || lz >= 16 || ly < 0 || ly >= height) return;
        int idx = (lx * 16 + lz) * height + ly;
        if (!airLike[idx] || open[idx]) return;
        open[idx] = true;
        queue.add(idx);
    }

    private static boolean isUnderVegetation(WorldGenLevel world, BlockPos.MutableBlockPos scanPos,
                                              int x, int y, int z, int surfaceY) {
        for (int scanY = y + 1; scanY <= surfaceY + 20; scanY++) {
            scanPos.set(x, scanY, z);
            var above = world.getBlockState(scanPos);
            if (above.is(BlockTags.LOGS) || above.is(BlockTags.LEAVES)
                    || above.is(Blocks.MUSHROOM_STEM) || above.is(Blocks.RED_MUSHROOM_BLOCK)
                    || above.is(Blocks.BROWN_MUSHROOM_BLOCK) || above.is(Blocks.BAMBOO)
                    || above.is(Blocks.CHORUS_PLANT) || above.is(Blocks.CHORUS_FLOWER)
                    || above.is(Blocks.CACTUS) || above.is(Blocks.SUGAR_CANE)) {
                return true;
            }
            if (above.isSolidRender(world, scanPos)) return false;
        }
        return false;
    }
}
