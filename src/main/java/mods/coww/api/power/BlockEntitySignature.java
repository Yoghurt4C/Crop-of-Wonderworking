/*
 *  Originates from Botania.
 *  Source: https://github.com/Vazkii/Botania
 */

package mods.coww.api.power;

import net.minecraft.block.entity.BlockEntity;

public class BlockEntitySignature {
    private final BlockEntity tile;
    private final boolean remoteWorld;

    public BlockEntitySignature(BlockEntity tile, boolean remoteWorld) {
        this.tile = tile;
        this.remoteWorld = remoteWorld;
    }

    public BlockEntity getTile() {
        return tile;
    }

    public boolean isRemote() {
        return remoteWorld;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(remoteWorld) ^ System.identityHashCode(tile);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlockEntitySignature
                && tile == ((BlockEntitySignature) o).tile
                && remoteWorld == ((BlockEntitySignature) o).remoteWorld;
    }
}
