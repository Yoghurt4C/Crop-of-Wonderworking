package mods.coww.power;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.mixin.api.IBucketItem;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import mods.coww.registry.cowwBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class cowwHoneyBucketItem extends BucketItem implements IBucketItem {

    public cowwHoneyBucketItem(Fluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    protected void playEmptyingSound(PlayerEntity player, IWorld world, BlockPos pos) {
        world.playSound(player, pos, SoundEvents.BLOCK_HONEY_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean libblockattributes__shouldExposeFluid() {
        return true;
    }

    @Override
    public FluidKey libblockattributes__getFluid(ItemStack stack) {
        return FluidKeys.get(cowwBlocks.THAUMATURGES_HONEY);
    }

    @Override
    public ItemStack libblockattributes__withFluid(FluidKey fluid) {
        return new ItemStack(this);
    }

    @Override
    public FluidAmount libblockattributes__getFluidVolumeAmount() {
        return FluidAmount.BUCKET;
    }
}
