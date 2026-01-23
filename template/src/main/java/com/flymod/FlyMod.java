package com.flymod;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("flymod") // Это ID твоего мода
public class FlyMod {

    public FlyMod() {
        // Регистрируем наш класс в шине событий Forge
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fly")
                .requires(source -> source.hasPermission(2)) // Доступ для ОП
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    // Переключаем полет
                    boolean allow = !player.getAbilities().mayfly;
                    player.getAbilities().mayfly = allow;

                    if (!allow) {
                        player.getAbilities().flying = false;
                    }

                    // Синхронизация с клиентом
                    player.onUpdateAbilities();

                    // Сообщение (используем Component.literal для 1.20.1)
                    String status = allow ? "§aВКЛ" : "§cВЫКЛ";
                    player.sendSystemMessage(Component.literal("Полет: " + status));

                    return 1;
                })
        );
    }
}