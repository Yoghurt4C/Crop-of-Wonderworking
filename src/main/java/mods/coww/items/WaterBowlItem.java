package mods.coww.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WaterBowlItem extends BucketItem {
    public WaterBowlItem(Fluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {return TypedActionResult.pass(user.getStackInHand(hand));}

    @Override
    protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
        return !player.abilities.creativeMode ? new ItemStack(Items.BOWL) : stack;
    }

    @Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        return false;
    }
}
