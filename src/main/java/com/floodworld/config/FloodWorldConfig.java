package com.floodworld.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.*;

public class FloodWorldConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("floodworld.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static FloodWorldConfig instance;

    // Config fields
    public boolean replaceAir = true;
    public boolean replaceCaveAir = true;
    public int maxWaterHeight = 250;
    public boolean nativeFlooding = true; // true = during generation, false = post-processing tick queue

    public static FloodWorldConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static FloodWorldConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                instance = GSON.fromJson(reader, FloodWorldConfig.class);
                return instance;
            } catch (IOException e) {
                System.err.println("[FloodWorld] Failed to load config: " + e.getMessage());
            }
        }
        instance = new FloodWorldConfig();
        instance.save();
        return instance;
    }

    public void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("[FloodWorld] Failed to save config: " + e.getMessage());
        }
    }
}
