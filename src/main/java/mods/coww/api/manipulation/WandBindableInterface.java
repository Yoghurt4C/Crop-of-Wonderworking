package mods.coww.api.manipulation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface WandBindableInterface extends BoundBlockEntityInterface {

    /**
     * Return true if the Wand can select this blockentity.
     */
    boolean canSelect(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side);

    /**
     * Call to bind the blockentity to where the player clicked. Return true to deselect
     * the blockentity for another bind or false case the blockentity should stay selected.
     */
    boolean bindTo(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side);
}
