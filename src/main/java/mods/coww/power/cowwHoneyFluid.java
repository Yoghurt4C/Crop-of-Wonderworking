package mods.coww.power;

import mods.coww.registry.cowwBlocks;
import mods.coww.registry.cowwItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public abstract class cowwHoneyFluid extends BaseFluid {

    @Override
    public Fluid getFlowing() {
        return cowwBlocks.FLOWING_THAUMATURGES_HONEY;
    }

    @Override
    public Fluid getStill() {
        return cowwBlocks.THAUMATURGES_HONEY;
    }

    @Override
    public Item getBucketItem() {
        return cowwItems.THAUMATURGES_HONEY_BUCKET;
    }

    @Override
    protected boolean isInfinite() {
        return false;
    }

    @Override
    protected void beforeBreakingBlock(IWorld world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world.getWorld(), pos, blockEntity);
    }

    //flow speed control
    @Override
    protected int method_15733(WorldView worldView) {
        return 3;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView worldView) {
        return 2;
    }

    @Override
    protected boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.matches(FluidTags.WATER);
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 40;
    }

    @Override
    protected float getBlastResistance() {
        return 50;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return cowwBlocks.THAUMATURGES_HONEY_FLUID.getDefaultState().with(FluidBlock.LEVEL, method_15741(fluidState));
    }

    @Override
    protected ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_HONEY;
    }


    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == cowwBlocks.FLOWING_THAUMATURGES_HONEY || fluid == cowwBlocks.THAUMATURGES_HONEY;
    }

    public static class Flowing extends cowwHoneyFluid {
        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }
    }

    public static class Still extends cowwHoneyFluid {
        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }
    }
}
