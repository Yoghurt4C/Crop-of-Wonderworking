package mods.coww.blocks;

import mods.coww.registry.CropWonderWorkingBlocks;
import mods.coww.registry.CropWonderWorkingItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public class BriarPlantBlock extends TallPlantBlock implements Fertilizable {
    public static final IntProperty AGE;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static int growthDelay;

    public BriarPlantBlock(int delay, Settings settings){
        super(settings);
        growthDelay=delay;
        this.setDefaultState(this.stateManager.getDefaultState().with(this.getAgeProperty(), 0).with(HALF, DoubleBlockHalf.LOWER));
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        return floor.getBlock() == Blocks.FARMLAND || BlockTags.BAMBOO_PLANTABLE_ON.contains(floor.getBlock());
    }

    public IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState state) {
        return (Integer)state.get(this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return (BlockState)this.getDefaultState().with(this.getAgeProperty(), age);
    }

    public boolean isMature(BlockState state) {
        return (Integer)state.get(this.getAgeProperty()) >= this.getMaxAge();
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.getBlock().equals(this)) {
            if (world.getLightLevel(pos.up()) >= 9) {
                int age = state.get(AGE);
                if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
                    if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                        world.setBlockState(pos, this.withAge(age + 1).with(HALF, DoubleBlockHalf.UPPER));
                        world.setBlockState(pos.down(), this.withAge(age + 1).with(HALF, DoubleBlockHalf.LOWER));
                    }
                } else {
                    if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                        world.setBlockState(pos.up(), this.withAge(age + 1).with(HALF, DoubleBlockHalf.UPPER));
                        world.setBlockState(pos, this.withAge(age + 1).with(HALF, DoubleBlockHalf.LOWER));
                    }
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(AGE).equals(getMaxAge())) {
            int count = world.random.nextInt(3)+1;
            ItemScatterer.spawn(world,pos.getX(),pos.getY(),pos.getZ(),new ItemStack(CropWonderWorkingItems.BRIAR_FRUIT,count));
            if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
                world.setBlockState(pos, this.withAge(2).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos.down(), this.withAge(2).with(HALF, DoubleBlockHalf.LOWER));
            } else {
                world.setBlockState(pos.up(), this.withAge(2).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos, this.withAge(2).with(HALF, DoubleBlockHalf.LOWER));
            }
        }
        return ActionResult.PASS;
    }

    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int age = state.get(AGE);
        int i = this.getAge(state) + this.getGrowthAmount(world);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
            if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                world.setBlockState(pos, this.withAge(i).with(HALF,DoubleBlockHalf.UPPER));
                world.setBlockState(pos.down(), this.withAge(i).with(HALF,DoubleBlockHalf.LOWER));
            }
        } else {
            if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                world.setBlockState(pos.up(), this.withAge(i).with(HALF,DoubleBlockHalf.UPPER));
                world.setBlockState(pos, this.withAge(i).with(HALF,DoubleBlockHalf.LOWER));
            }
        }
    }

    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof ItemEntity)) {
            entity.damage(DamageSource.CACTUS,1);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return CropWonderWorkingBlocks.BRIAR;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this.getSeedsItem());
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !this.isMature(state);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.applyGrowth(world, pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
        builder.add(AGE);
    }

    static {
        AGE = Properties.AGE_7;
        HALF = Properties.DOUBLE_BLOCK_HALF;
    }
}
