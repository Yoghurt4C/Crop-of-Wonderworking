package mods.coww.blocks;

import mods.coww.registry.CropWonderWorkingItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class SweedBlock extends CropWonderWorkingBlock {

    public SweedBlock(int delay, Settings settings){
        super(delay, settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0));
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.get(AGE) == getMaxAge() - 1 && !world.isClient) {
            sweedDeployment(world,pos);
        }
    }

    public void sweedDeployment(ServerWorld world, BlockPos pos){
        Direction direction = Direction.byId(2 + world.random.nextInt(4));
        int x1 = pos.getX() + direction.getOffsetX();
        int z1 = pos.getZ() + direction.getOffsetZ();
        int y1 = findSutableY(world, x1, pos.getY(), z1);
        BlockPos newPos = new BlockPos(x1,y1,z1);
        if (canPlantOnTop(world.getBlockState(new BlockPos(x1, y1 - 1, z1)),world,pos) && world.getBlockState(newPos).isAir()) {
            world.setBlockState(newPos, this.getDefaultState());
        }
    }

    public int findSutableY(World world, int x, int y, int z) {
        int bY=y;
        y+=1;
        while(!canPlantOnTop(world.getBlockState(new BlockPos(x, y, z)),world,new BlockPos(x, y, z)) && y > bY-2) {--y;}
        return y+1;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        Block block=floor.getBlock();
        return block !=null && TagRegistry.block(BlockTags.BAMBOO_PLANTABLE_ON.getId()).contains(block);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return CropWonderWorkingItems.SWEED_SEEDS;
    }

    public static final IntProperty AGE;
    public IntProperty getAgeProperty() {
        return AGE;
    }
    public int getMaxAge() {
        return 3;
    }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(new Property[]{AGE}); }
    static {AGE= Properties.AGE_3;}
}
