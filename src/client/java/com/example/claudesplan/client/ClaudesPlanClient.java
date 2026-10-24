package com.example.claudesplan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class ClaudesPlanClient implements ClientModInitializer {

    private static KeyBinding toggleKey;
    private static boolean enabled = false;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.claudesplan.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                "category.claudesplan"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        while (toggleKey.wasPressed()) {
            enabled = !enabled;
            if (client.player != null) {
                client.player.sendMessage(Text.translatable(
                        enabled ? "message.claudesplan.on" : "message.claudesplan.off"), true);
            }
        }

        if (!enabled) return;

        client.options.pauseOnLostFocus = false;

        if (client.player == null || client.world == null || client.interactionManager == null) return;
        if (client.isWindowFocused()) return;
        if (client.currentScreen != null) return;

        if (client.crosshairTarget instanceof BlockHitResult hit
                && hit.getType() == HitResult.Type.BLOCK) {
            client.interactionManager.updateBlockBreakingProgress(hit.getBlockPos(), hit.getSide());
            client.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
