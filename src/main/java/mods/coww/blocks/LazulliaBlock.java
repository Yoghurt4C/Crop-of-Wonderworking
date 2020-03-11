package mods.coww.blocks;

import mods.coww.registry.cowwItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;

public class LazulliaBlock extends CropWonderWorkingBlock {
    public LazulliaBlock(int delay, Settings settings){
        super(delay, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return cowwItems.LAZULLIA_SEEDS;
    }

    public static final IntProperty AGE;
    public IntProperty getAgeProperty() {
        return AGE;
    }
    public int getMaxAge() {
        return 7;
    }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(AGE); }
    static {AGE= Properties.AGE_7;}
}
