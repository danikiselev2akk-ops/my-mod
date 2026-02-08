package ru.danik.rgbpalette.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    public static KeyMapping OPEN_GUI;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        OPEN_GUI = new KeyMapping(
                "key.rgbpalette.open",
                GLFW.GLFW_KEY_P,
                "key.categories.misc"
        );
        event.register(OPEN_GUI);
    }

    @Mod.EventBusSubscriber
    public static class ClientTick {
        @SubscribeEvent
        public static void onTick(net.minecraftforge.event.TickEvent.ClientTickEvent e) {
            if (OPEN_GUI != null && OPEN_GUI.consumeClick()) {
                Minecraft.getInstance().setScreen(new RGBPaletteScreen());
            }
        }
    }
}