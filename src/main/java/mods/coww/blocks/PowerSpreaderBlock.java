package mods.coww.blocks;

import mods.coww.api.manipulation.WandBindableInterface;
import mods.coww.entity.power.PowerSpreaderBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PowerSpreaderBlock extends Block implements BlockEntityProvider {
    public PowerSpreaderBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        PowerSpreaderBlockEntity spreader = (PowerSpreaderBlockEntity) world.getBlockEntity(pos);
        spreader.receivePower(spreader.getMaxPower());
        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction orientation = Direction.getEntityFacingOrder(placer)[0].getOpposite();
        PowerSpreaderBlockEntity spreader = (PowerSpreaderBlockEntity) world.getBlockEntity(pos);

        switch(orientation) {
            case DOWN:
                spreader.rotationY = -90F;
                break;
            case UP:
                spreader.rotationY = 90F;
                break;
            case NORTH:
                spreader.rotationX = 270F;
                break;
            case SOUTH:
                spreader.rotationX = 90F;
                break;
            case WEST:
                break;
            default:
                spreader.rotationX = 180F;
                break;
        }
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new PowerSpreaderBlockEntity();
    }
}
