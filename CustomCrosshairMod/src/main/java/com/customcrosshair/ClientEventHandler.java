package com.customcrosshair;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CustomCrosshairMod.MOD_ID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            
            if (KeyBindings.OPEN_GUI.consumeClick() && mc.screen == null) {
                mc.setScreen(new CrosshairConfigScreen(null));
            }
        }
    }
}
