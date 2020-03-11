package mods.coww.api.lens;

import mods.coww.api.power.PowerSpreaderInterface;
import net.minecraft.item.ItemStack;

public interface LensControlInterface extends LensInterface {
    public boolean isControlLens(ItemStack stack);

    public boolean allowBurstShooting(ItemStack stack, PowerSpreaderInterface spreader, boolean redstone);

    /**
     * Used for the tick of a non-redstone spreader.
     */
    public void onControlledSpreaderTick(ItemStack stack, PowerSpreaderInterface spreader, boolean redstone);

    /**
     * Used for when a redstone spreader gets a pulse.
     */
    public void onControlledSpreaderPulse(ItemStack stack, PowerSpreaderInterface spreader, boolean redstone);
}
