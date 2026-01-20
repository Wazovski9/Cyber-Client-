package dev.tr7zw.exordium.module.modules.misc;

import dev.tr7zw.exordium.event.events.TickListener;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.utils.EncryptedString;
import net.minecraft.client.util.math.MatrixStack;

public final class SP extends Module implements TickListener {
    public SP() {
        super(EncryptedString.of("Sprint"), -1, Category.MISC);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        super.onDisable();
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {

    }

    @Override
    public void onTick() {
        mc.player.setSprinting(mc.player.input.pressingForward);
    }
}
