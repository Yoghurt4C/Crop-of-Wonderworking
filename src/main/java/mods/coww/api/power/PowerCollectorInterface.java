package mods.coww.api.power;

import mods.coww.api.internal.PowerBurstInterface;

public interface PowerCollectorInterface extends PowerReceiverInterface {
    /**
     * Called every tick on the client in case the player is holding a Wand of the Forest.
     */
    void onClientDisplayTick();

    /**
     * Get the multiplier of power to input into the blockentity, 1.0 is the original amount of power
     * in the burst. 0.9, for example, is 90%, so 10% of the power in the burst will suffer from entropy.
     */
    float getPowerYieldMultiplier(PowerBurstInterface burst);

    /**
     * Gets the maximum amount of power this collector can store.
     */
    int getMaxPower();
}
