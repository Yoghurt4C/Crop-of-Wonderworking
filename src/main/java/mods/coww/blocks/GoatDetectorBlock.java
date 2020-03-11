package mods.coww.blocks;

import mods.coww.entity.GoatDetectorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class GoatDetectorBlock extends FacingBlock implements BlockEntityProvider {
    public static final BooleanProperty POWERED;

    public GoatDetectorBlock(Settings settings){
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH).with(POWERED, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        final GoatDetectorBlockEntity goatDetector = (GoatDetectorBlockEntity) world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);
        if (goatDetector!=null) {
            if (stack.getItem().equals(Items.NAME_TAG) && stack.hasTag() && stack.getSubTag("display")!=null) {
                goatDetector.goat = stack.getSubTag("display").get("Name").asString();
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, false), 2);
        } else {
            world.setBlockState(pos, state.with(POWERED, true), 2);
        }

        this.updateNeighbors(world, pos, state);
    }

    public void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return state.getWeakRedstonePower(view, pos, facing);
    }

    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return state.get(POWERED) && state.get(FACING) == facing ? 15 : 0;
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (state.getBlock() != oldState.getBlock()) {
            if (!world.isClient() && state.get(POWERED) && !world.getBlockTickScheduler().isScheduled(pos, this)) {
                BlockState blockState = state.with(POWERED, false);
                world.setBlockState(pos, blockState, 18);
                this.updateNeighbors(world, pos, blockState);
            }

        }
    }

    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (!world.isClient && state.get(POWERED) && world.getBlockTickScheduler().isScheduled(pos, this)) {
                this.updateNeighbors(world, pos, state.with(POWERED, false));
            }

        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean sneaky = ctx.getPlayer() != null && ctx.getPlayer().isSneaking();
        if (sneaky){ return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite()); }
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    static {
        POWERED = Properties.POWERED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new GoatDetectorBlockEntity();
    }
}
