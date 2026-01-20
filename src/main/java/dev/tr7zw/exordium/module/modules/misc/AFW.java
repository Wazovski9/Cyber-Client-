package dev.tr7zw.exordium.module.modules.misc;

import dev.tr7zw.exordium.event.events.ItemUseListener;
import dev.tr7zw.exordium.event.events.TickListener;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.module.setting.BooleanSetting;
import dev.tr7zw.exordium.module.setting.KeybindSetting;
import dev.tr7zw.exordium.module.setting.NumberSetting;
import dev.tr7zw.exordium.utils.EncryptedString;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public final class AFW extends Module implements TickListener, ItemUseListener {
    private final KeybindSetting activateKey = new KeybindSetting(EncryptedString.of("Activate Key"), -1, false);
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0, 20, 0, 1);
    private final BooleanSetting switchBack = new BooleanSetting(EncryptedString.of("Switch Back"), true);
    private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0, 20, 0, 1)
            .setDescription(EncryptedString.of("Delay after using firework before switching back"));

    private boolean active, hasActivated;
    private int clock, previousSlot, switchClock, speedClock;

    public AFW() {
        super(EncryptedString.of("Auto Firework"),
                -1,
                Category.MISC);
        addSettings(activateKey, delay, switchBack, switchDelay);
    }

    @Override
    public void onEnable() {
        reset();
        eventManager.add(TickListener.class, this);
        eventManager.add(ItemUseListener.class, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        eventManager.remove(ItemUseListener.class, this);
        super.onDisable();
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null)
            return;

        if (speedClock > 0) {
            speedClock--;
            return;
        }

        // Check if activation key is pressed
        int key = activateKey.getKey();
        boolean keyPressed = false;
        
        if (key != -1) {
            if (key >= 0 && key <= 4) {
                // Mouse button
                keyPressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
            } else {
                // Keyboard key
                keyPressed = GLFW.glfwGetKey(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
            }
        }

        // Check if player is eating or using any item (including right-clicking)
        boolean isUsingRightClick = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean isEating = mc.player.isUsingItem() || (isUsingRightClick && mc.player.getMainHandStack().isFood());

        // Automatically use firework when key is pressed and elytra flying
        if (mc.player != null
                && keyPressed
                && !isEating  // Don't activate while eating
                && mc.player.isFallFlying()
                && mc.player.getInventory().getArmorStack(2).isOf(Items.ELYTRA)
                && !mc.player.getInventory().getMainHandStack().isOf(Items.FIREWORK_ROCKET)
                && !(mc.player.getMainHandStack().getItem() instanceof ArmorItem)
        ) {
            active = true;
        }

        if (active) {
            // Cancel if player starts eating/using item
            if (mc.player.isUsingItem() || (isUsingRightClick && mc.player.getMainHandStack().isFood())) {
                reset();
                return;
            }
            if (previousSlot == -1)
                previousSlot = mc.player.getInventory().selectedSlot;

            if (!selectItemFromHotbar(Items.FIREWORK_ROCKET)) {
                reset();
                return;
            }

            if (clock < delay.getValueInt()) {
                clock++;
                return;
            }

            if (!hasActivated) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                hasActivated = true;
            }

            if (switchBack.getValue())
                switchBack();
            else reset();
        }
    }

    private boolean selectItemFromHotbar(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                mc.player.getInventory().selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    private void switchBack() {
        if (switchClock < switchDelay.getValueInt()) {
            switchClock++;
            return;
        }

        mc.player.getInventory().selectedSlot = previousSlot;
        reset();
    }

    private void reset() {
        previousSlot = -1;
        clock = 0;
        switchClock = 0;
        speedClock = 4;
        active = false;
        hasActivated = false;
    }

    @Override
    public void onItemUse(ItemUseEvent event) {
        if (mc.player.getMainHandStack().isOf(Items.FIREWORK_ROCKET))
            hasActivated = true;
        if (speedClock > 0)
            event.cancel();
    }
}