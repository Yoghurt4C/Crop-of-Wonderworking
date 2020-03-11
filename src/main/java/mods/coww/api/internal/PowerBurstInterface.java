/*
 *  Originates from Botania.
 *  Source: https://github.com/Vazkii/Botania
 */

package mods.coww.api.internal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Interface for the Power Burst entity. This can be safely casted to EntityThrowable.
 */

public interface PowerBurstInterface {
    boolean isFake();

    void setBurstMotion(double x, double y, double z);

    int getColor();

    void setColor(int color);

    int getPower();

    void setPower(int power);

    int getStartingPower();

    void setStartingPower(int power);

    int getMinPowerLoss();

    void setMinPowerLoss(int minPowerLoss);

    float getPowerLossPerTick();

    void setPowerLossPerTick(float power);

    float getGravity();

    void setGravity(float gravity);

    BlockPos getBurstSourceBlockPos();

    void setBurstSourceCoords(BlockPos pos);

    ItemStack getSourceLens();

    void setSourceLens(ItemStack lens);

    boolean hasAlreadyCollidedAt(BlockPos pos);

    void setCollidedAt(BlockPos pos);

    int getTicksExisted();

    void setFake(boolean fake);

    void setShooterUUID(UUID uuid);

    UUID getShooterUUID();

    void ping();
}
