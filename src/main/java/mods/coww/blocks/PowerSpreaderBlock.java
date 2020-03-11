package mods.coww.blocks;

import mods.coww.entity.power.PowerSpreaderBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class PowerSpreaderBlock extends Block implements BlockEntityProvider {
    public PowerSpreaderBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new PowerSpreaderBlockEntity();
    }
}
