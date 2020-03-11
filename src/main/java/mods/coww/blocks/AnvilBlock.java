package mods.coww.blocks;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.mixin.api.IBucketItem;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import mods.coww.entity.AnvilBlockEntity;
import mods.coww.entity.CauldronBlockEntity;
import mods.coww.entity.IItemEntity;
import mods.coww.registry.cowwItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AnvilBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final DirectionProperty FACING;
    private static final VoxelShape X_BASE_SHAPE;
    private static final VoxelShape Z_BASE_SHAPE;
    private static final VoxelShape X_STAND_SHAPE;
    private static final VoxelShape X_FACE_SHAPE;
    private static final VoxelShape Z_STAND_SHAPE;
    private static final VoxelShape Z_FACE_SHAPE;
    private static final VoxelShape X_AXIS_SHAPE;
    private static final VoxelShape Z_AXIS_SHAPE;

    public AnvilBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        final AnvilBlockEntity anvil = (AnvilBlockEntity) world.getBlockEntity(pos);
        //if (anvil!=null) { anvil.handleInventory(anvil,player,hand); }
        return ActionResult.SUCCESS;
    }

    /*
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        final CauldronBlockEntity cauldron = (CauldronBlockEntity) world.getBlockEntity(pos);
        if (cauldron != null) {
            FluidKey cauldronFluid = cauldron.fluid.getInvFluid(0).fluidKey;
            if (world.isClient) {
                return;
            }

            if (cauldronFluid.equals(FluidKeys.LAVA) && !cauldron.fluid.getInvFluid(0).isEmpty()) {
                entity.setOnFireFor(4);
            } else {
                if (entity instanceof ItemEntity) {
                    ItemEntity ientity = (ItemEntity) entity;
                    ItemStack stack = ientity.getStack();
                    int level = state.get(LEVEL);
                    if (!((IItemEntity) ientity).coww_getSpawnedByCauldron()) {
                        if (stack.getItem() instanceof IBucketItem) { cauldron.interact(cauldron, stack); }
                        else if (stack.getItem().equals(Items.BOWL)) {
                            if (level == 3) {
                                stack.decrement(1);
                                cauldron.spawnCraftingResult(world, pos, new ItemStack(cowwItems.BOWL_OF_WATER)); }
                            cauldron.fluid.setInvFluid(0, FluidVolumeUtil.EMPTY, Simulation.ACTION);
                            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.1F, 10F);
                            world.setBlockState(pos,cauldron.getCachedState().with(LEVEL,0));
                        } else cauldron.handleInventory(cauldron, stack);
                        cauldron.sync();
                    }
                }
            }
        }
    } */

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        AnvilBlockEntity anvil = (AnvilBlockEntity) world.getBlockEntity(pos);
        world.playLevelEvent(player, 2001, pos, getRawIdFromState(state));
        if (anvil!=null)
            ItemScatterer.spawn(world,pos,anvil);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
        Direction direction = state.get(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    static {
        X_BASE_SHAPE = Block.createCuboidShape(4.0D, 0.0D, 2.0D, 12.0D, 2.0D, 14.0D);
        Z_BASE_SHAPE = Block.createCuboidShape(2.0D, 0.0D, 4.0D, 14.0D, 2.0D, 12.0D);
        X_STAND_SHAPE = Block.createCuboidShape(6.0D, 2.0D, 4.0D, 10.0D, 7.0D, 12.0D);
        Z_STAND_SHAPE = Block.createCuboidShape(4.0D, 2.0D, 6.0D, 12.0D, 7.0D, 10.0D);
        X_FACE_SHAPE = Block.createCuboidShape(3.0D, 7.0D, 0.0D, 13.0D, 13.0D, 16.0D);
        Z_FACE_SHAPE = Block.createCuboidShape(0.0D, 7.0D, 3.0D, 16.0D, 13.0D, 13.0D);
        X_AXIS_SHAPE = VoxelShapes.union(X_BASE_SHAPE, X_STAND_SHAPE, X_FACE_SHAPE);
        Z_AXIS_SHAPE = VoxelShapes.union(Z_BASE_SHAPE, Z_STAND_SHAPE, Z_FACE_SHAPE);
        FACING = Properties.HORIZONTAL_FACING;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new AnvilBlockEntity();
    }
}
