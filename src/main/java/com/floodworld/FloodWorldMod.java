package com.floodworld;

import com.floodworld.config.FloodWorldConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FloodWorldMod implements ModInitializer {

    public static final String MOD_ID = "floodworld";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConcurrentLinkedQueue<ChunkPos> pendingChunks = new ConcurrentLinkedQueue<>();

    @Override
    public void onInitialize() {
        LOGGER.info("FloodWorld initialized.");

        // Post-processing mode: enqueue newly generated chunks
        ServerChunkEvents.CHUNK_LOAD.register((ServerWorld world, WorldChunk chunk) -> {
            FloodWorldConfig config = FloodWorldConfig.getInstance();
            if (config.nativeFlooding) return;
            if (!config.replaceAir && !config.replaceCaveAir) return;
            if (chunk.getInhabitedTime() != 0) return;
            pendingChunks.add(chunk.getPos());
        });

        // Post-processing mode: flood chunks from queue each tick
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            FloodWorldConfig config = FloodWorldConfig.getInstance();
            if (config.nativeFlooding) return;

            ServerWorld world = server.getOverworld();
            int processed = 0;
            while (!pendingChunks.isEmpty() && processed < 10) {
                ChunkPos pos = pendingChunks.poll();
                if (pos == null) break;
                if (!world.isChunkLoaded(pos.x, pos.z)) continue;

                WorldChunk chunk = world.getChunk(pos.x, pos.z);
                int minY = world.getBottomY();
                int maxY = config.maxWaterHeight;
                int startX = pos.getStartX();
                int startZ = pos.getStartZ();
                BlockPos.Mutable mutablePos = new BlockPos.Mutable();

                for (int x = startX; x < startX + 16; x++) {
                    for (int z = startZ; z < startZ + 16; z++) {
                        for (int y = minY; y < maxY; y++) {
                            mutablePos.set(x, y, z);
                            var state = chunk.getBlockState(mutablePos);
                            if ((config.replaceAir && state.isOf(Blocks.AIR)) ||
                                (config.replaceCaveAir && state.isOf(Blocks.CAVE_AIR))) {
                                world.setBlockState(mutablePos, Blocks.WATER.getDefaultState(), 3);
                            }
                        }
                    }
                }
                processed++;
            }
        });
    }
}
