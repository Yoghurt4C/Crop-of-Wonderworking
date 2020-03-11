package mods.coww.blocks;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.mixin.api.IBucketItem;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import mods.coww.entity.CauldronBlockEntity;
import mods.coww.entity.IItemEntity;
import mods.coww.registry.cowwItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CauldronBlock extends net.minecraft.block.CauldronBlock implements BlockEntityProvider {

    public CauldronBlock(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecated")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        final CauldronBlockEntity cauldron = (CauldronBlockEntity) world.getBlockEntity(pos);
        if (cauldron!=null) { cauldron.handleInventory(cauldron,player,hand); }
        return ActionResult.SUCCESS;
    }

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
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CauldronBlockEntity();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        CauldronBlockEntity cauldron = (CauldronBlockEntity) world.getBlockEntity(pos);
        world.playLevelEvent(player, 2001, pos, getRawIdFromState(state));
        if (cauldron!=null)
        ItemScatterer.spawn(world,pos,cauldron);
    }
}
