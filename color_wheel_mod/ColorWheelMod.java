package com.colorwheel.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mod("colorwheel")
public class ColorWheelMod {
    public static final String MOD_ID = "colorwheel";

    public ColorWheelMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    public static class KeyInputHandler {
        @SubscribeEvent
        public void onKeyInput(InputEvent.Key event) {
            if (event.getKey() == GLFW.GLFW_KEY_C && event.getAction() == GLFW.GLFW_PRESS) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen == null) {
                    mc.setScreen(new ColorWheelScreen());
                }
            }
        }
    }

    public static class ColorWheelScreen extends Screen {
        private static final int WHEEL_RADIUS = 100;
        private static final int CENTER_X = 200;
        private static final int CENTER_Y = 150;
        
        private int selectedHue = 0;
        private float selectedSaturation = 1.0f;
        private float selectedBrightness = 1.0f;
        private Color selectedColor = Color.RED;
        private List<BlockColor> similarColors = new ArrayList<>();

        protected ColorWheelScreen() {
            super(Component.literal("RGB Color Wheel"));
        }

        @Override
        protected void init() {
            super.init();
            updateSimilarColors();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.renderBackground(guiGraphics);
            
            // Рисуем цветовое колесо
            drawColorWheel(guiGraphics, CENTER_X, CENTER_Y, WHEEL_RADIUS);
            
            // Рисуем палитру насыщенности/яркости
            drawSaturationBrightnessBox(guiGraphics, CENTER_X + WHEEL_RADIUS + 40, 50);
            
            // Показываем выбранный цвет
            guiGraphics.fill(CENTER_X - 50, CENTER_Y + WHEEL_RADIUS + 20, 
                           CENTER_X + 50, CENTER_Y + WHEEL_RADIUS + 70, 
                           selectedColor.getRGB() | 0xFF000000);
            
            // Показываем RGB значения
            String rgbText = String.format("RGB: %d, %d, %d", 
                selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
            guiGraphics.drawString(this.font, rgbText, CENTER_X - 40, CENTER_Y + WHEEL_RADIUS + 80, 0xFFFFFF);
            
            // Показываем похожие цвета блоков
            drawSimilarBlocks(guiGraphics, 400, 50);
            
            super.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        private void drawColorWheel(GuiGraphics guiGraphics, int centerX, int centerY, int radius) {
            for (int angle = 0; angle < 360; angle++) {
                for (int r = 0; r < radius; r++) {
                    double rad = Math.toRadians(angle);
                    int x = centerX + (int)(r * Math.cos(rad));
                    int y = centerY + (int)(r * Math.sin(rad));
                    
                    float hue = angle / 360.0f;
                    Color color = Color.getHSBColor(hue, 1.0f, 1.0f);
                    guiGraphics.fill(x, y, x + 1, y + 1, color.getRGB() | 0xFF000000);
                }
            }
        }

        private void drawSaturationBrightnessBox(GuiGraphics guiGraphics, int x, int y) {
            int size = 150;
            
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    float saturation = i / (float)size;
                    float brightness = 1.0f - (j / (float)size);
                    
                    Color color = Color.getHSBColor(selectedHue / 360.0f, saturation, brightness);
                    guiGraphics.fill(x + i, y + j, x + i + 1, y + j + 1, color.getRGB() | 0xFF000000);
                }
            }
            
            guiGraphics.drawString(this.font, "Насыщенность/Яркость", x, y - 15, 0xFFFFFF);
        }

        private void drawSimilarBlocks(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.drawString(this.font, "Похожие блоки:", x, y - 15, 0xFFFFFF);
            
            int offsetY = 0;
            for (int i = 0; i < Math.min(similarColors.size(), 10); i++) {
                BlockColor blockColor = similarColors.get(i);
                
                // Рисуем цвет блока
                guiGraphics.fill(x, y + offsetY, x + 30, y + offsetY + 20, 
                               blockColor.color.getRGB() | 0xFF000000);
                
                // Название блока
                guiGraphics.drawString(this.font, blockColor.name, x + 35, y + offsetY + 6, 0xFFFFFF);
                
                // Показываем разницу в цвете
                int diff = (int)blockColor.difference;
                guiGraphics.drawString(this.font, "Δ" + diff, x + 200, y + offsetY + 6, 0xAAAAAA);
                
                offsetY += 25;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // Клик на цветовое колесо
            double dx = mouseX - CENTER_X;
            double dy = mouseY - CENTER_Y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance < WHEEL_RADIUS) {
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                if (angle < 0) angle += 360;
                
                selectedHue = (int)angle;
                updateSelectedColor();
                updateSimilarColors();
                return true;
            }
            
            // Клик на палитру насыщенности/яркости
            int boxX = CENTER_X + WHEEL_RADIUS + 40;
            int boxY = 50;
            int boxSize = 150;
            
            if (mouseX >= boxX && mouseX < boxX + boxSize && 
                mouseY >= boxY && mouseY < boxY + boxSize) {
                
                selectedSaturation = (float)(mouseX - boxX) / boxSize;
                selectedBrightness = 1.0f - (float)(mouseY - boxY) / boxSize;
                updateSelectedColor();
                updateSimilarColors();
                return true;
            }
            
            return super.mouseClicked(mouseX, mouseY, button);
        }

        private void updateSelectedColor() {
            selectedColor = Color.getHSBColor(selectedHue / 360.0f, selectedSaturation, selectedBrightness);
        }

        private void updateSimilarColors() {
            similarColors.clear();
            
            // Список популярных блоков с их цветами
            List<BlockColor> allBlocks = getMinecraftBlocks();
            
            // Сортируем по близости к выбранному цвету
            for (BlockColor block : allBlocks) {
                block.difference = calculateColorDifference(selectedColor, block.color);
            }
            
            allBlocks.sort((a, b) -> Double.compare(a.difference, b.difference));
            similarColors.addAll(allBlocks.subList(0, Math.min(10, allBlocks.size())));
        }

        private double calculateColorDifference(Color c1, Color c2) {
            // Используем формулу евклидова расстояния в RGB пространстве
            int dr = c1.getRed() - c2.getRed();
            int dg = c1.getGreen() - c2.getGreen();
            int db = c1.getBlue() - c2.getBlue();
            
            return Math.sqrt(dr * dr + dg * dg + db * db);
        }

        private List<BlockColor> getMinecraftBlocks() {
            List<BlockColor> blocks = new ArrayList<>();
            
            // Шерсть и терракота
            blocks.add(new BlockColor("Белая шерсть", new Color(233, 236, 236)));
            blocks.add(new BlockColor("Оранжевая шерсть", new Color(240, 118, 37)));
            blocks.add(new BlockColor("Пурпурная шерсть", new Color(189, 68, 179)));
            blocks.add(new BlockColor("Голубая шерсть", new Color(58, 175, 217)));
            blocks.add(new BlockColor("Желтая шерсть", new Color(254, 216, 61)));
            blocks.add(new BlockColor("Лаймовая шерсть", new Color(112, 185, 25)));
            blocks.add(new BlockColor("Розовая шерсть", new Color(237, 141, 172)));
            blocks.add(new BlockColor("Серая шерсть", new Color(62, 68, 71)));
            blocks.add(new BlockColor("Светло-серая шерсть", new Color(142, 142, 134)));
            blocks.add(new BlockColor("Бирюзовая шерсть", new Color(21, 119, 136)));
            blocks.add(new BlockColor("Фиолетовая шерсть", new Color(121, 42, 172)));
            blocks.add(new BlockColor("Синяя шерсть", new Color(53, 57, 157)));
            blocks.add(new BlockColor("Коричневая шерсть", new Color(114, 71, 40)));
            blocks.add(new BlockColor("Зеленая шерсть", new Color(84, 109, 27)));
            blocks.add(new BlockColor("Красная шерсть", new Color(160, 39, 34)));
            blocks.add(new BlockColor("Черная шерсть", new Color(20, 21, 25)));
            
            // Бетон
            blocks.add(new BlockColor("Белый бетон", new Color(207, 213, 214)));
            blocks.add(new BlockColor("Оранжевый бетон", new Color(224, 97, 1)));
            blocks.add(new BlockColor("Пурпурный бетон", new Color(169, 48, 159)));
            blocks.add(new BlockColor("Голубой бетон", new Color(36, 137, 199)));
            blocks.add(new BlockColor("Желтый бетон", new Color(240, 175, 21)));
            blocks.add(new BlockColor("Лаймовый бетон", new Color(94, 169, 24)));
            blocks.add(new BlockColor("Розовый бетон", new Color(214, 101, 143)));
            blocks.add(new BlockColor("Серый бетон", new Color(54, 57, 61)));
            blocks.add(new BlockColor("Светло-серый бетон", new Color(125, 125, 115)));
            blocks.add(new BlockColor("Бирюзовый бетон", new Color(21, 119, 136)));
            blocks.add(new BlockColor("Фиолетовый бетон", new Color(100, 31, 156)));
            blocks.add(new BlockColor("Синий бетон", new Color(44, 46, 143)));
            blocks.add(new BlockColor("Коричневый бетон", new Color(96, 60, 31)));
            blocks.add(new BlockColor("Зеленый бетон", new Color(73, 91, 36)));
            blocks.add(new BlockColor("Красный бетон", new Color(142, 32, 32)));
            blocks.add(new BlockColor("Черный бетон", new Color(8, 10, 15)));
            
            // Природные блоки
            blocks.add(new BlockColor("Камень", new Color(125, 125, 125)));
            blocks.add(new BlockColor("Гранит", new Color(146, 95, 81)));
            blocks.add(new BlockColor("Полированный гранит", new Color(154, 106, 79)));
            blocks.add(new BlockColor("Диорит", new Color(193, 193, 193)));
            blocks.add(new BlockColor("Андезит", new Color(132, 132, 132)));
            blocks.add(new BlockColor("Дубовые доски", new Color(162, 130, 78)));
            blocks.add(new BlockColor("Еловые доски", new Color(114, 84, 48)));
            blocks.add(new BlockColor("Березовые доски", new Color(192, 175, 121)));
            blocks.add(new BlockColor("Джунглевые доски", new Color(160, 115, 80)));
            blocks.add(new BlockColor("Акациевые доски", new Color(168, 90, 50)));
            blocks.add(new BlockColor("Доски темного дуба", new Color(66, 43, 20)));
            blocks.add(new BlockColor("Кварцевый блок", new Color(235, 229, 222)));
            blocks.add(new BlockColor("Пурпурный блок", new Color(169, 125, 169)));
            blocks.add(new BlockColor("Призмарин", new Color(99, 156, 151)));
            
            return blocks;
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }

    static class BlockColor {
        String name;
        Color color;
        double difference;

        BlockColor(String name, Color color) {
            this.name = name;
            this.color = color;
            this.difference = 0;
        }
    }
}
