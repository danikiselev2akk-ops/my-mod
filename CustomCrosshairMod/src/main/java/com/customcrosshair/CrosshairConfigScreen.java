package com.customcrosshair;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CrosshairConfigScreen extends Screen {
    private final Screen lastScreen;
    private int selectedPresetIndex = -1;
    private EditBox presetNameBox;
    private List<Button> colorButtons = new ArrayList<>();
    private int scrollOffset = 0;

    public CrosshairConfigScreen(Screen lastScreen) {
        super(Component.literal("Настройки прицела"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int leftPanel = 10;
        int rightPanel = this.width - 210;
        int y = 30;

        // === ЛЕВАЯ ПАНЕЛЬ - НАСТРОЙКИ ===
        
        // Тип прицела
        addRenderableWidget(Button.builder(
                Component.literal("Тип: " + CrosshairConfig.currentSettings.crosshairType.getDisplayName()),
                button -> {
                    CrosshairConfig.CrosshairType[] types = CrosshairConfig.CrosshairType.values();
                    int currentIndex = CrosshairConfig.currentSettings.crosshairType.ordinal();
                    int nextIndex = (currentIndex + 1) % types.length;
                    CrosshairConfig.currentSettings.crosshairType = types[nextIndex];
                    button.setMessage(Component.literal("Тип: " + types[nextIndex].getDisplayName()));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Цвет
        addRenderableWidget(Button.builder(
                Component.literal("Цвет"),
                button -> cycleColor())
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Толщина
        addRenderableWidget(Button.builder(
                Component.literal("Толщина: " + CrosshairConfig.currentSettings.thickness),
                button -> {
                    CrosshairConfig.currentSettings.thickness++;
                    if (CrosshairConfig.currentSettings.thickness > 5) 
                        CrosshairConfig.currentSettings.thickness = 1;
                    button.setMessage(Component.literal("Толщина: " + CrosshairConfig.currentSettings.thickness));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Зазор
        addRenderableWidget(Button.builder(
                Component.literal("Зазор: " + CrosshairConfig.currentSettings.gap),
                button -> {
                    CrosshairConfig.currentSettings.gap++;
                    if (CrosshairConfig.currentSettings.gap > 10) 
                        CrosshairConfig.currentSettings.gap = 0;
                    button.setMessage(Component.literal("Зазор: " + CrosshairConfig.currentSettings.gap));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Длина
        addRenderableWidget(Button.builder(
                Component.literal("Длина: " + CrosshairConfig.currentSettings.length),
                button -> {
                    CrosshairConfig.currentSettings.length++;
                    if (CrosshairConfig.currentSettings.length > 15) 
                        CrosshairConfig.currentSettings.length = 1;
                    button.setMessage(Component.literal("Длина: " + CrosshairConfig.currentSettings.length));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Обводка
        addRenderableWidget(Button.builder(
                Component.literal("Обводка: " + (CrosshairConfig.currentSettings.outline ? "Вкл" : "Выкл")),
                button -> {
                    CrosshairConfig.currentSettings.outline = !CrosshairConfig.currentSettings.outline;
                    button.setMessage(Component.literal("Обводка: " + (CrosshairConfig.currentSettings.outline ? "Вкл" : "Выкл")));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Точка в центре
        addRenderableWidget(Button.builder(
                Component.literal("Центр точка: " + (CrosshairConfig.currentSettings.dot ? "Вкл" : "Выкл")),
                button -> {
                    CrosshairConfig.currentSettings.dot = !CrosshairConfig.currentSettings.dot;
                    button.setMessage(Component.literal("Центр точка: " + (CrosshairConfig.currentSettings.dot ? "Вкл" : "Выкл")));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Размер точки
        addRenderableWidget(Button.builder(
                Component.literal("Размер точки: " + CrosshairConfig.currentSettings.dotSize),
                button -> {
                    CrosshairConfig.currentSettings.dotSize++;
                    if (CrosshairConfig.currentSettings.dotSize > 5) 
                        CrosshairConfig.currentSettings.dotSize = 1;
                    button.setMessage(Component.literal("Размер точки: " + CrosshairConfig.currentSettings.dotSize));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Динамический прицел
        addRenderableWidget(Button.builder(
                Component.literal("Динамический: " + (CrosshairConfig.currentSettings.dynamicCrosshair ? "Вкл" : "Выкл")),
                button -> {
                    CrosshairConfig.currentSettings.dynamicCrosshair = !CrosshairConfig.currentSettings.dynamicCrosshair;
                    button.setMessage(Component.literal("Динамический: " + (CrosshairConfig.currentSettings.dynamicCrosshair ? "Вкл" : "Выкл")));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 25;

        // Прозрачность
        addRenderableWidget(Button.builder(
                Component.literal("Прозрачность: " + (int)(CrosshairConfig.currentSettings.opacity * 100) + "%"),
                button -> {
                    CrosshairConfig.currentSettings.opacity += 0.1f;
                    if (CrosshairConfig.currentSettings.opacity > 1.0f) 
                        CrosshairConfig.currentSettings.opacity = 0.1f;
                    button.setMessage(Component.literal("Прозрачность: " + (int)(CrosshairConfig.currentSettings.opacity * 100) + "%"));
                    CrosshairConfig.saveConfig();
                })
                .bounds(leftPanel, y, 200, 20)
                .build());
        y += 35;

        // Сохранить как пресет
        presetNameBox = new EditBox(this.font, leftPanel, y, 120, 20, Component.literal(""));
        presetNameBox.setHint(Component.literal("Имя пресета"));
        presetNameBox.setMaxLength(20);
        addRenderableWidget(presetNameBox);

        addRenderableWidget(Button.builder(
                Component.literal("Сохранить"),
                button -> {
                    if (!presetNameBox.getValue().isEmpty()) {
                        CrosshairConfig.saveAsPreset(presetNameBox.getValue());
                        presetNameBox.setValue("");
                        this.clearWidgets();
                        this.init();
                    }
                })
                .bounds(leftPanel + 125, y, 75, 20)
                .build());

        // === ПРАВАЯ ПАНЕЛЬ - ПРЕСЕТЫ ===
        int presetY = 30;
        for (int i = 0; i < CrosshairConfig.presets.size(); i++) {
            final int index = i;
            CrosshairConfig.CrosshairPreset preset = CrosshairConfig.presets.get(i);
            
            addRenderableWidget(Button.builder(
                    Component.literal(preset.name),
                    button -> {
                        CrosshairConfig.applyPreset(preset);
                        selectedPresetIndex = index;
                        this.clearWidgets();
                        this.init();
                    })
                    .bounds(rightPanel, presetY, 200, 20)
                    .build());
            presetY += 25;
        }

        // Кнопка закрытия
        addRenderableWidget(Button.builder(
                Component.literal("Готово"),
                button -> this.minecraft.setScreen(lastScreen))
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void cycleColor() {
        int[] colors = {0xFFFFFF, 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF, 0xFFA500};
        int currentColor = CrosshairConfig.currentSettings.color;
        
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == currentColor) {
                CrosshairConfig.currentSettings.color = colors[(i + 1) % colors.length];
                CrosshairConfig.currentSettings.dotColor = CrosshairConfig.currentSettings.color;
                CrosshairConfig.saveConfig();
                return;
            }
        }
        CrosshairConfig.currentSettings.color = colors[0];
        CrosshairConfig.currentSettings.dotColor = colors[0];
        CrosshairConfig.saveConfig();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        
        // Заголовки панелей
        guiGraphics.drawCenteredString(this.font, "Настройки", 110, 10, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "Пресеты", this.width - 110, 10, 0xFFFFFF);
        
        // Превью прицела в центре
        int previewX = this.width / 2;
        int previewY = this.height / 2;
        
        guiGraphics.drawString(this.font, "Превью:", previewX - 60, previewY - 40, 0xAAAAAA);
        
        // Здесь можно добавить рендер превью прицела
        
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        
        // Отображение текущего цвета
        int colorBoxX = 130;
        int colorBoxY = 80;
        guiGraphics.fill(colorBoxX, colorBoxY, colorBoxX + 20, colorBoxY + 20, 
                0xFF000000 | CrosshairConfig.currentSettings.color);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
