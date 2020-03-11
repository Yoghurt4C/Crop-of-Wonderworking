/*
 *  Originates from Botania.
 *  Source: https://github.com/Vazkii/Botania
 */

package mods.coww.api.power;

public final class BurstProperties {

    public int maxPower;
    public int ticksBeforePowerLoss;
    public float powerLossPerTick;
    public float gravity;
    public float motionModifier;

    public int color;

    public BurstProperties(int maxPower, int ticksBeforePowerLoss, float powerLossPerTick, float gravity, float motionModifier, int color) {
        this.maxPower = maxPower;
        this.ticksBeforePowerLoss = ticksBeforePowerLoss;
        this.powerLossPerTick = powerLossPerTick;
        this.gravity = gravity;
        this.motionModifier = motionModifier;
        this.color = color;
    }

}
