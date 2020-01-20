package mods.coww.blocks;

import mods.coww.registry.CropWonderWorkingItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LazulliaBlock extends CropWonderWorkingBlock {
    public LazulliaBlock(int delay, Settings settings){
        super(delay, settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0));
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return CropWonderWorkingItems.LAZULLIA_SEEDS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.playLevelEvent(player, 2001, pos, getRawIdFromState(state));
        if (state.get(AGE).equals(getMaxAge()))
            ItemScatterer.spawn(world,pos.getX(),pos.getY(),pos.getZ(),new ItemStack(CropWonderWorkingItems.LAZULLIA_SEEDS));
    }

    public static final IntProperty AGE;
    public IntProperty getAgeProperty() {
        return AGE;
    }
    public int getMaxAge() {
        return 7;
    }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(new Property[]{AGE}); }
    static {AGE= Properties.AGE_7;}
}
