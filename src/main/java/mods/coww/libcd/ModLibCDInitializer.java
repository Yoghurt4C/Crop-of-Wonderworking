package mods.coww.libcd;

import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.tweaker.TweakerManager;

public class ModLibCDInitializer implements LibCDInitializer {
    @Override
    public void initTweakers(TweakerManager manager) {
        TweakerManager.INSTANCE.addAssistant("mods.coww.libcd.CauldronRecipeTweaker", new CauldronRecipeTweaker());
    }

    @Override
    public void initConditions(ConditionManager manager) { }
}
