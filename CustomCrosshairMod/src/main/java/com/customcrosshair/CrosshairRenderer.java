package com.customcrosshair;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = CustomCrosshairMod.MOD_ID)
public class CrosshairRenderer {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.options.getCameraType().isFirstPerson() && !mc.options.hideGui) {
            if (event.getOverlay().id().toString().equals("minecraft:crosshair")) {
                event.setCanceled(true);
                renderCustomCrosshair(event.getGuiGraphics());
            }
        }
    }

    private static void renderCustomCrosshair(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        CrosshairConfig.CrosshairSettings settings = CrosshairConfig.currentSettings;

        // Динамическое изменение gap при движении
        int dynamicGap = settings.gap;
        if (settings.dynamicCrosshair) {
            float movement = Math.abs(mc.player.getDeltaMovement().x) + 
                           Math.abs(mc.player.getDeltaMovement().z);
            dynamicGap += (int)(movement * 10);
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        switch (settings.crosshairType) {
            case CROSS:
                renderCross(bufferBuilder, poseStack, centerX, centerY, dynamicGap, settings);
                break;
            case DOT:
                renderDot(bufferBuilder, poseStack, centerX, centerY, settings);
                break;
            case CIRCLE:
                renderCircle(bufferBuilder, poseStack, centerX, centerY, dynamicGap, settings);
                break;
            case SQUARE:
                renderSquare(bufferBuilder, poseStack, centerX, centerY, dynamicGap, settings);
                break;
            case T_SHAPE:
                renderTShape(bufferBuilder, poseStack, centerX, centerY, dynamicGap, settings);
                break;
        }

        // Рендер точки в центре
        if (settings.dot && settings.crosshairType != CrosshairConfig.CrosshairType.DOT) {
            renderCenterDot(bufferBuilder, poseStack, centerX, centerY, settings);
        }

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void renderCross(BufferBuilder buffer, PoseStack poseStack, int x, int y, int gap, CrosshairConfig.CrosshairSettings settings) {
        // Обводка
        if (settings.outline) {
            // Верхняя линия
            drawLine(buffer, poseStack, x, y - gap - settings.length, x, y - gap, 
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Нижняя линия
            drawLine(buffer, poseStack, x, y + gap, x, y + gap + settings.length, 
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Левая линия
            drawLine(buffer, poseStack, x - gap - settings.length, y, x - gap, y, 
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Правая линия
            drawLine(buffer, poseStack, x + gap, y, x + gap + settings.length, y, 
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
        }

        // Основной крест
        // Верхняя линия
        drawLine(buffer, poseStack, x, y - gap - settings.length, x, y - gap, settings.thickness, settings.color, settings.opacity);
        // Нижняя линия
        drawLine(buffer, poseStack, x, y + gap, x, y + gap + settings.length, settings.thickness, settings.color, settings.opacity);
        // Левая линия
        drawLine(buffer, poseStack, x - gap - settings.length, y, x - gap, y, settings.thickness, settings.color, settings.opacity);
        // Правая линия
        drawLine(buffer, poseStack, x + gap, y, x + gap + settings.length, y, settings.thickness, settings.color, settings.opacity);
    }

    private static void renderDot(BufferBuilder buffer, PoseStack poseStack, int x, int y, CrosshairConfig.CrosshairSettings settings) {
        int size = settings.dotSize;
        if (settings.outline) {
            fillRect(buffer, poseStack, x - size - settings.outlineThickness, y - size - settings.outlineThickness,
                    x + size + settings.outlineThickness, y + size + settings.outlineThickness,
                    settings.outlineColor, settings.opacity);
        }
        fillRect(buffer, poseStack, x - size, y - size, x + size, y + size, settings.color, settings.opacity);
    }

    private static void renderCircle(BufferBuilder buffer, PoseStack poseStack, int x, int y, int gap, CrosshairConfig.CrosshairSettings settings) {
        int radius = gap + settings.length;
        int segments = 32;

        if (settings.outline) {
            drawCircleOutline(buffer, poseStack, x, y, radius, segments, 
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
        }
        drawCircleOutline(buffer, poseStack, x, y, radius, segments, settings.thickness, settings.color, settings.opacity);
    }

    private static void renderSquare(BufferBuilder buffer, PoseStack poseStack, int x, int y, int gap, CrosshairConfig.CrosshairSettings settings) {
        int size = gap + settings.length;

        if (settings.outline) {
            // Верхняя линия
            drawLine(buffer, poseStack, x - size, y - size, x + size, y - size,
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Нижняя линия
            drawLine(buffer, poseStack, x - size, y + size, x + size, y + size,
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Левая линия
            drawLine(buffer, poseStack, x - size, y - size, x - size, y + size,
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Правая линия
            drawLine(buffer, poseStack, x + size, y - size, x + size, y + size,
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
        }

        // Основной квадрат
        drawLine(buffer, poseStack, x - size, y - size, x + size, y - size, settings.thickness, settings.color, settings.opacity);
        drawLine(buffer, poseStack, x - size, y + size, x + size, y + size, settings.thickness, settings.color, settings.opacity);
        drawLine(buffer, poseStack, x - size, y - size, x - size, y + size, settings.thickness, settings.color, settings.opacity);
        drawLine(buffer, poseStack, x + size, y - size, x + size, y + size, settings.thickness, settings.color, settings.opacity);
    }

    private static void renderTShape(BufferBuilder buffer, PoseStack poseStack, int x, int y, int gap, CrosshairConfig.CrosshairSettings settings) {
        if (settings.outline) {
            // Верхняя линия
            drawLine(buffer, poseStack, x, y - gap - settings.length, x, y - gap,
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Левая линия
            drawLine(buffer, poseStack, x - gap - settings.length, y, x - gap, y,
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
            // Правая линия
            drawLine(buffer, poseStack, x + gap, y, x + gap + settings.length, y,
                    settings.thickness + settings.outlineThickness * 2, settings.outlineColor, settings.opacity);
        }

        // T-образный прицел
        drawLine(buffer, poseStack, x, y - gap - settings.length, x, y - gap, settings.thickness, settings.color, settings.opacity);
        drawLine(buffer, poseStack, x - gap - settings.length, y, x - gap, y, settings.thickness, settings.color, settings.opacity);
        drawLine(buffer, poseStack, x + gap, y, x + gap + settings.length, y, settings.thickness, settings.color, settings.opacity);
    }

    private static void renderCenterDot(BufferBuilder buffer, PoseStack poseStack, int x, int y, CrosshairConfig.CrosshairSettings settings) {
        int size = settings.dotSize;
        if (settings.outline) {
            fillRect(buffer, poseStack, x - size - 1, y - size - 1, x + size + 1, y + size + 1,
                    settings.outlineColor, settings.opacity);
        }
        fillRect(buffer, poseStack, x - size, y - size, x + size, y + size, settings.dotColor, settings.opacity);
    }

    private static void drawLine(BufferBuilder buffer, PoseStack poseStack, int x1, int y1, int x2, int y2, int thickness, int color, float opacity) {
        Matrix4f matrix = poseStack.last().pose();
        
        int halfThickness = thickness / 2;
        
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = opacity;

        if (x1 == x2) {
            // Вертикальная линия
            fillRect(buffer, poseStack, x1 - halfThickness, y1, x1 + halfThickness, y2, color, opacity);
        } else {
            // Горизонтальная линия
            fillRect(buffer, poseStack, x1, y1 - halfThickness, x2, y1 + halfThickness, color, opacity);
        }
    }

    private static void fillRect(BufferBuilder buffer, PoseStack poseStack, int x1, int y1, int x2, int y2, int color, float opacity) {
        Matrix4f matrix = poseStack.last().pose();
        
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = opacity;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, x1, y2, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y1, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void drawCircleOutline(BufferBuilder buffer, PoseStack poseStack, int x, int y, int radius, int segments, int thickness, int color, float opacity) {
        Matrix4f matrix = poseStack.last().pose();
        
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = opacity;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float)(2 * Math.PI * i / segments);
            float angle2 = (float)(2 * Math.PI * (i + 1) / segments);
            
            int x1 = (int)(x + Math.cos(angle1) * radius);
            int y1 = (int)(y + Math.sin(angle1) * radius);
            int x2 = (int)(x + Math.cos(angle2) * radius);
            int y2 = (int)(y + Math.sin(angle2) * radius);
            
            drawLine(buffer, poseStack, x1, y1, x2, y2, thickness, color, opacity);
        }
    }
}
