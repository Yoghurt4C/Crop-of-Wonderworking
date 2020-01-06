package mods.coww.blocks;

import mods.coww.registry.CropWonderWorkingItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.Random;

public class RedlonBlock extends PlantBlock implements Fertilizable {
    private final Block growableBlock;
    protected static final VoxelShape[] AGE_TO_SHAPE;

    public RedlonBlock(Block growableBlock, Settings settings){
        super(settings);
        this.growableBlock = growableBlock;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    protected Item getPickItem() {
        return CropWonderWorkingItems.REDLON_SEEDS;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
        return AGE_TO_SHAPE[(Integer)state.get(AGE)];
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        return floor.getBlock() == Blocks.FARMLAND;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            float f = getAvailableMoisture(this, world, pos);
            if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                int i = (Integer)state.get(AGE);
                if (i < 7) {
                    state = (BlockState)state.with(AGE, i + 1);
                    world.setBlockState(pos, state, 2);
                } else {
                    Direction direction = Direction.Type.HORIZONTAL.random(random);
                    BlockPos blockPos = pos.offset(direction);
                    if (world.getBlockState(blockPos).isAir()) {
                        world.setBlockState(blockPos, this.growableBlock.getDefaultState());
                        world.setBlockState(pos, ((AttachableRedstoneBlock)Blocks.REDSTONE_BLOCK).getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                    }
                }
            }

        }
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.isClient){
            world.addParticle(new DustParticleEffect(1f,0f,0f,1f),
                    pos.getX()+random.nextDouble(),
                    pos.getY()+random.nextDouble(),
                    pos.getZ()+random.nextDouble(),
                    0,0,0);
        }
    }

    protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockPos = pos.down();

        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float g = 0.0F;
                BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
                if (blockState.getBlock() == Blocks.FARMLAND) {
                    g = 1.0F;
                    if ((Integer)blockState.get(FarmlandBlock.MOISTURE) > 0) {
                        g = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    g /= 4.0F;
                }

                f += g;
            }
        }

        BlockPos blockPos2 = pos.north();
        BlockPos blockPos3 = pos.south();
        BlockPos blockPos4 = pos.west();
        BlockPos blockPos5 = pos.east();
        boolean bl = block == world.getBlockState(blockPos4).getBlock() || block == world.getBlockState(blockPos5).getBlock();
        boolean bl2 = block == world.getBlockState(blockPos2).getBlock() || block == world.getBlockState(blockPos3).getBlock();
        if (bl && bl2) {
            f /= 2.0F;
        } else {
            boolean bl3 = block == world.getBlockState(blockPos4.north()).getBlock() || block == world.getBlockState(blockPos5.north()).getBlock() || block == world.getBlockState(blockPos5.south()).getBlock() || block == world.getBlockState(blockPos4.south()).getBlock();
            if (bl3) {
                f /= 2.0F;
            }
        }

        return f;
    }


    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        Item item = this.getPickItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return (Integer)state.get(AGE) != getMaxAge();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(7, (Integer)state.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
        BlockState blockState = (BlockState)state.with(AGE, i);
        world.setBlockState(pos, blockState, 2);
        if (i == 7) {
            blockState.scheduledTick(world, pos, world.random);
        }
    }


    public Block getGrowableBlock() {
        return this.growableBlock;
    }

    public static final IntProperty AGE;
    public IntProperty getAgeProperty() { return AGE; }
    public int getMaxAge() { return 7; }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(new Property[]{AGE}); }

    static {
        AGE=Properties.AGE_7;
        AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
    }
}
