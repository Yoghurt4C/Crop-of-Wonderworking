/*
 *  Originates from Botania.
 *  Source: https://github.com/Vazkii/Botania
 */

package mods.coww.api.power;

import net.minecraft.block.entity.BlockEntity;

public class BlockEntitySignature {
    private final BlockEntity be;
    private final boolean remoteWorld;

    public BlockEntitySignature(BlockEntity be, boolean remoteWorld) {
        this.be = be;
        this.remoteWorld = remoteWorld;
    }

    public BlockEntity getTile() {
        return be;
    }

    public boolean isRemote() {
        return remoteWorld;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(remoteWorld) ^ System.identityHashCode(be);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlockEntitySignature
                && be == ((BlockEntitySignature) o).be
                && remoteWorld == ((BlockEntitySignature) o).remoteWorld;
    }
}
