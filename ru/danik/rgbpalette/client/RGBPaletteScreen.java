package ru.danik.rgbpalette.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RGBPaletteScreen extends Screen {

    private ColorWheelWidget colorWheel;

    public RGBPaletteScreen() {
        super(Component.literal("RGB Block Palette"));
    }

    @Override
    protected void init() {
        colorWheel = new ColorWheelWidget(20, 20, 60);
        addRenderableWidget(colorWheel);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float delta) {
        renderBackground(gg);

        int color = colorWheel.getSelectedColor();

        gg.drawString(font, "Выбранный цвет:", 20, 150, 0xFFFFFF);
        gg.fill(20, 165, 80, 205, 0xFF000000 | color);

        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int b = color & 255;

        gg.drawString(font, "R: " + r, 110, 165, 0xFFFFFF);
        gg.drawString(font, "G: " + g, 110, 180, 0xFFFFFF);
        gg.drawString(font, "B: " + b, 110, 195, 0xFFFFFF);

        super.render(gg, mouseX, mouseY, delta);
    }
}