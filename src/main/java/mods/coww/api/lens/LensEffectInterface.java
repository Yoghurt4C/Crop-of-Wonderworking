package mods.coww.api.lens;

import mods.coww.api.internal.PowerBurstInterface;
import mods.coww.api.power.BurstProperties;
import mods.coww.api.power.PowerReceiverInterface;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * Have an item implement this for it to count as a lens effect and
 * be able to change the properties of Mana Bursts.
 */
public interface LensEffectInterface {

    /**
     * Called when a mana spreader that has this focus shoots a burst. This is where
     * you change the properties of the burst.
     */
    public void apply(ItemStack stack, BurstProperties props);

    /**
     * Called when a mana burst fired from a mana spreader with this focus collides against
     * any block. This is called after the collision is handled.
     * @return True to kill the burst. False to keep it alive.
     */
    public boolean collideBurst(PowerBurstInterface burst, HitResult pos, boolean isManaBlock, boolean dead, ItemStack stack);

    /**
     * Called when a mana burst fired from a mana spreader with this focus is updated.
     * This is called before the update is handled.
     */
    public void updateBurst(PowerBurstInterface burst, ItemStack stack);

    /**
     * Called when the mana burst should do it's particles. Return false to not
     * do any particles.
     */
    public boolean doParticles(PowerBurstInterface burst, ItemStack stack);

    /**
     * Gets the amount of mana to transfer to the passed in mana receiver block.
     */
    public default int getPowerToTransfer(PowerBurstInterface burst, ThrownEntity entity, ItemStack stack, PowerReceiverInterface receiver) {
        return burst.getPower();
    }

}