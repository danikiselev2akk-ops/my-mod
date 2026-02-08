package ru.danik.rgbpalette.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ColorWheelWidget extends AbstractWidget {

    private float hue = 0f;
    private float saturation = 1f;
    private float value = 1f;

    private final int radius;

    public ColorWheelWidget(int x, int y, int radius) {
        super(x, y, radius * 2, radius * 2, Component.empty());
        this.radius = radius;
    }

    @Override
    protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        RenderSystem.disableTexture();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                double dist = Math.sqrt(x * x + y * y);
                if (dist <= radius) {
                    float h = (float) ((Math.atan2(y, x) / (2 * Math.PI)) + 0.5);
                    float s = (float) (dist / radius);
                    int rgb = Color.HSBtoRGB(h, s, value);
                    gg.fill(getX() + radius + x, getY() + radius + y,
                            getX() + radius + x + 1, getY() + radius + y + 1,
                            0xFF000000 | rgb);
                }
            }
        }

        RenderSystem.enableTexture();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double dx = mouseX - (getX() + radius);
        double dy = mouseY - (getY() + radius);
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= radius) {
            hue = (float) ((Math.atan2(dy, dx) / (2 * Math.PI)) + 0.5);
            saturation = (float) (dist / radius);
            return true;
        }
        return false;
    }

    public int getSelectedColor() {
        return Color.HSBtoRGB(hue, saturation, value);
    }
}