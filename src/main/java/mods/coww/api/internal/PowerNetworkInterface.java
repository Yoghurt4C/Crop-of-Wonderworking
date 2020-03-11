/*
 *  Originates from Botania.
 *  Source: https://github.com/Vazkii/Botania
 */

package mods.coww.api.internal;

import mods.coww.api.power.BlockEntitySignature;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public interface PowerNetworkInterface {

    /**
     * Clears the entire M*na Network of all it's contents, you probably
     * don't want to call this unless you have a very good reason.
     */
    void clear();

    /**
     * Gets the closest M*na Collector (eg. M*na Spreader) in the network to the Chunk
     * Coordinates passed in, in the given dimension.<br>
     * A way of getting the dimension is via world.provider.dimensionId<br>
     * Note that this function *can* get performance intensive, it's recommended you
     * call it sparingly and take cache of the value returned.
     * @param limit The maximum distance the closest block can be, if the closest block
     * is farther away than that, null will be returned instead.
     */
    BlockEntity getClosestCollector(BlockPos pos, World world, int limit);

    /**
     * Gets the closest M*na Pool in the network to the Chunk Coordinates passed in,
     * in the given dimension.<br>
     * A way of getting the dimension is via world.provider.dimensionId<br>
     * Note that this function *can* get performance intensive, it's reccomended you
     * call it sparingly and take cache of the value returned.
     * @param limit The maximum distance the closest block can be, if the closest block
     * is farther away than that, null will be returned instead.
     */
    BlockEntity getClosestTank(BlockPos pos, World world, int limit);

    /**
     * Gets the set of all M*na Collectors (eg. M*na Spreader) in the dimension
     * passed in. Note that this is the actual set and not a copy, make sure to
     * clone the set if you intend to change it in any way.
     */
    Set<BlockEntitySignature> getAllCollectorsInWorld(World world);

    /**
     * Gets the set of all M*na Pools in the dimension passed in. Note that this
     * is the actual set and not a copy, make sure to clone the set if you intend
     * to change it in any way.
     */
    Set<BlockEntitySignature> getAllTanksInWorld(World world);
}
