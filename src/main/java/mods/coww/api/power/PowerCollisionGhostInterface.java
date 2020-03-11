package mods.coww.api.power;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PowerCollisionGhostInterface {
    public boolean isGhost(BlockState state, World world, BlockPos pos);
}
