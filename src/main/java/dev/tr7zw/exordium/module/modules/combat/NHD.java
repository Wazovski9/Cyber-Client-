package dev.tr7zw.exordium.module.modules.combat;

import dev.tr7zw.exordium.event.events.AttackListener;
import dev.tr7zw.exordium.event.events.BlockBreakingListener;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.module.setting.BooleanSetting;
import dev.tr7zw.exordium.utils.EncryptedString;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult;

public final class NHD extends Module implements AttackListener, BlockBreakingListener {
    private final BooleanSetting onlySword = new BooleanSetting(EncryptedString.of("Only Sword"), true);
    private final BooleanSetting air = new BooleanSetting(EncryptedString.of("Air"), true)
            .setDescription(EncryptedString.of("Whether to stop hits directed to the air"));
    private final BooleanSetting blocks = new BooleanSetting(EncryptedString.of("Blocks"), false)
            .setDescription(EncryptedString.of("Whether to stop hits directed to blocks"));

    public NHD() {
        super(EncryptedString.of("No Hit Delay"),
                -1,
                Category.COMBAT);
        addSettings(onlySword, air, blocks);
    }

    @Override
    public void onEnable() {
        eventManager.add(AttackListener.class, this);
        eventManager.add(BlockBreakingListener.class, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(AttackListener.class, this);
        eventManager.remove(BlockBreakingListener.class, this);
        super.onDisable();
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
    }

    private boolean shouldApplyLogic() {
        if (onlySword.getValue()) {
            var item = mc.player.getMainHandStack().getItem();
            return item instanceof SwordItem;
        }
        return true;
    }

    @Override
    public void onAttack(AttackEvent event) {
        if (!shouldApplyLogic()) return;

        switch (mc.crosshairTarget.getType()) {
            case MISS -> {
                if (air.getValue()) event.cancel();
            }
            case BLOCK -> {
                if (blocks.getValue()) event.cancel();
            }
        }
    }

    @Override
    public void onBlockBreaking(BlockBreakingEvent event) {
        if (!shouldApplyLogic()) return;

        if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            if (blocks.getValue()) event.cancel();
        }
    }
}
