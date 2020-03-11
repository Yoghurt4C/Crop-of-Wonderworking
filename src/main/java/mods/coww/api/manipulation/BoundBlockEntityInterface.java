package mods.coww.api.manipulation;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Any blockentity that implements this is technically bound
 * to something, and the binding will be shown when hovering
 * over with a thing.
 */
public interface BoundBlockEntityInterface {

    /**
     * Gets where this block is bound to
     */
    @Nullable
    BlockPos getBinding();
}
