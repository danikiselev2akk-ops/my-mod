package com.customcrosshair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrosshairConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("customcrosshair.json");

    public static CrosshairSettings currentSettings = new CrosshairSettings();
    public static List<CrosshairPreset> presets = new ArrayList<>();

    static {
        initializeDefaultPresets();
    }

    private static void initializeDefaultPresets() {
        // Пресет 1: Классический
        CrosshairSettings classic = new CrosshairSettings();
        classic.crosshairType = CrosshairType.CROSS;
        classic.color = 0xFFFFFF;
        classic.thickness = 2;
        classic.gap = 3;
        classic.length = 6;
        classic.outline = true;
        classic.outlineColor = 0x000000;
        classic.dot = false;
        presets.add(new CrosshairPreset("Классический", classic));

        // Пресет 2: Точка
        CrosshairSettings dot = new CrosshairSettings();
        dot.crosshairType = CrosshairType.DOT;
        dot.color = 0x00FF00;
        dot.thickness = 3;
        dot.outline = true;
        dot.outlineColor = 0x000000;
        dot.dot = true;
        dot.dotSize = 2;
        presets.add(new CrosshairPreset("Точка", dot));

        // Пресет 3: Круг
        CrosshairSettings circle = new CrosshairSettings();
        circle.crosshairType = CrosshairType.CIRCLE;
        circle.color = 0xFF0000;
        circle.thickness = 2;
        circle.gap = 5;
        circle.outline = true;
        circle.outlineColor = 0x000000;
        presets.add(new CrosshairPreset("Круг", circle));

        // Пресет 4: T-образный
        CrosshairSettings tShape = new CrosshairSettings();
        tShape.crosshairType = CrosshairType.T_SHAPE;
        tShape.color = 0x00FFFF;
        tShape.thickness = 2;
        tShape.gap = 2;
        tShape.length = 8;
        tShape.outline = true;
        tShape.outlineColor = 0x000000;
        presets.add(new CrosshairPreset("T-образный", tShape));

        // Пресет 5: Квадрат
        CrosshairSettings square = new CrosshairSettings();
        square.crosshairType = CrosshairType.SQUARE;
        square.color = 0xFFFF00;
        square.thickness = 2;
        square.gap = 4;
        square.length = 5;
        square.outline = true;
        square.outlineColor = 0x000000;
        presets.add(new CrosshairPreset("Квадрат", square));

        // Пресет 6: Динамический
        CrosshairSettings dynamic = new CrosshairSettings();
        dynamic.crosshairType = CrosshairType.CROSS;
        dynamic.color = 0xFF00FF;
        dynamic.thickness = 2;
        dynamic.gap = 3;
        dynamic.length = 7;
        dynamic.outline = true;
        dynamic.outlineColor = 0x000000;
        dynamic.dynamicCrosshair = true;
        dynamic.dot = true;
        dynamic.dotSize = 1;
        presets.add(new CrosshairPreset("Динамический", dynamic));
    }

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    ConfigData data = GSON.fromJson(reader, ConfigData.class);
                    if (data != null) {
                        currentSettings = data.currentSettings;
                        if (data.customPresets != null && !data.customPresets.isEmpty()) {
                            presets.addAll(data.customPresets);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            ConfigData data = new ConfigData();
            data.currentSettings = currentSettings;
            // Сохраняем только пользовательские пресеты (после первых 6)
            data.customPresets = presets.size() > 6 ? 
                presets.subList(6, presets.size()) : new ArrayList<>();
            
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyPreset(CrosshairPreset preset) {
        currentSettings = preset.settings.copy();
        saveConfig();
    }

    public static void saveAsPreset(String name) {
        presets.add(new CrosshairPreset(name, currentSettings.copy()));
        saveConfig();
    }

    private static class ConfigData {
        CrosshairSettings currentSettings;
        List<CrosshairPreset> customPresets;
    }

    public static class CrosshairPreset {
        public String name;
        public CrosshairSettings settings;

        public CrosshairPreset(String name, CrosshairSettings settings) {
            this.name = name;
            this.settings = settings;
        }
    }

    public static class CrosshairSettings {
        public CrosshairType crosshairType = CrosshairType.CROSS;
        public int color = 0xFFFFFF;
        public int thickness = 2;
        public int gap = 3;
        public int length = 6;
        public boolean outline = true;
        public int outlineColor = 0x000000;
        public int outlineThickness = 1;
        public boolean dot = false;
        public int dotSize = 2;
        public int dotColor = 0xFFFFFF;
        public boolean dynamicCrosshair = false;
        public float opacity = 1.0f;

        public CrosshairSettings copy() {
            CrosshairSettings copy = new CrosshairSettings();
            copy.crosshairType = this.crosshairType;
            copy.color = this.color;
            copy.thickness = this.thickness;
            copy.gap = this.gap;
            copy.length = this.length;
            copy.outline = this.outline;
            copy.outlineColor = this.outlineColor;
            copy.outlineThickness = this.outlineThickness;
            copy.dot = this.dot;
            copy.dotSize = this.dotSize;
            copy.dotColor = this.dotColor;
            copy.dynamicCrosshair = this.dynamicCrosshair;
            copy.opacity = this.opacity;
            return copy;
        }
    }

    public enum CrosshairType {
        CROSS("Крест"),
        DOT("Точка"),
        CIRCLE("Круг"),
        SQUARE("Квадрат"),
        T_SHAPE("T-образный"),
        CUSTOM("Пользовательский");

        private final String displayName;

        CrosshairType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
