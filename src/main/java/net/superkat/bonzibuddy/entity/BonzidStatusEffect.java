package net.superkat.bonzibuddy.entity;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.awt.*;

public class BonzidStatusEffect extends StatusEffect {
    protected BonzidStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, new Color(143, 108, 246, 255).getRGB());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return super.canApplyUpdateEffect(duration, amplifier);
    }
}
