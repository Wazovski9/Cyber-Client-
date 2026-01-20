package dev.tr7zw.exordium.utils;

import dev.tr7zw.exordium.module.modules.client.CG;

public final class AnimationUtils {
	private double value;
	private final double originalValue;
    private double endValue;

	public AnimationUtils(double value) {
		this.value = value;
		this.originalValue = value;
	}

	public double animate(double delta, double end) {
		this.endValue = end;
		if (CG.animationMode.isMode(CG.AnimationMode.Normal)) {
			value = MathUtils.goodLerp((float) delta, value, end);
		} else if (CG.animationMode.isMode(CG.AnimationMode.Positive)) {
			value = MathUtils.smoothStepLerp(delta, value, end);
		} else if (CG.animationMode.isMode(CG.AnimationMode.Off)) {
			value = end;
		}

		return value;
	}

	public double getValue() {
		return value;
	}

	public double getOriginalValue() {
		return originalValue;
	}

	public double getEndValue() {
		return endValue;
	}

	public double getAnimationProgress() {
		return value / endValue;
	}

	public void reset(double delta) {
		value = MathUtils.smoothStepLerp(delta, value, originalValue);
	}
}


