package mods.coww.api.power;

import mods.coww.api.internal.PowerBurstInterface;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PowerBurstTriggerInterface {
    public void onBurstCollision(PowerBurstInterface burst, World world, BlockPos pos);
}
